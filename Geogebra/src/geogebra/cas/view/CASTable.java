/**
 * 
 */
package geogebra.cas.view;

import geogebra.gui.view.spreadsheet.MyTable;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseListener;

import javax.swing.CellEditor;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;

/**
 *
 */
class CASTable extends JTable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public final static int COL_CAS_CELLS = 0;

  private final CASTableModel tableModel;
  protected Kernel kernel;
  protected Application app;
  private final CASView view;
  private boolean showCellSeparator = false;

  private final CASTableCellEditor editor;
  private final CASTableCellRenderer renderer;

  public static final Color SELECTED_BACKGROUND_COLOR_HEADER = new Color(185,
      185, 210);

  public CASTable(final CASView view) {
    this.view = view;
    app = view.getApp();
    kernel = app.getKernel();

    setShowGrid(true);
    setGridColor(MyTable.TABLE_GRID_COLOR);
    setBackground(Color.white);

    tableModel = new CASTableModel(this, app);
    setModel(tableModel);
    setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

    // init editor and renderer
    editor = new CASTableCellEditor(view);
    renderer = new CASTableCellRenderer(view);
    getColumnModel().getColumn(COL_CAS_CELLS).setCellEditor(editor);
    getColumnModel().getColumn(COL_CAS_CELLS).setCellRenderer(renderer);
    setTableHeader(null);

    // remove all default mouse listeners
    final MouseListener[] ml = getMouseListeners();
    if (ml != null)
      for (final MouseListener element : ml)
        removeMouseListener(element);

    // Set the width of the index column;
    // this.getColumn(this.getColumnName(CASPara.indexCol)).setMinWidth(30);
    // this.getColumn(this.getColumnName(CASPara.indexCol)).setMaxWidth(30);

    // this.sizeColumnsToFit(0);
    // this.setSurrendersFocusOnKeystroke(true);

    // addFocusListener(new FocusListener() {
    // public void focusGained(FocusEvent arg0) {
    // // TODO: remove
    // System.out.println("table GAINED focus");
    // // startEditingRow(getSelectedRow());
    // }
    //
    // public void focusLost(FocusEvent arg0) {
    // // TODO: remove
    // System.out.println("table LOST focus");
    // }
    // });

  }

  /*
   * Function: Delete a rolw, and set the focus at the right position
   */
  public void deleteAllRow() {
    final int row = tableModel.getRowCount();

    for (int i = row - 1; i >= 0; i--)
      tableModel.removeRow(i);
    this.repaint();

    getRowCount();
    // if (tableModel.getRowCount() == 0)
    // insertRow(-1, CASPara.contCol);
  }

  /*
   * Function: Delete a rolw, and set the focus at the right position
   */
  public void deleteRow(final int row) {
    // TODO:remove
    System.out.println("tableModel.removeRow " + row);

    tableModel.removeRow(row);

    final int rowCount = tableModel.getRowCount();
    if (rowCount == 0)
      insertRowAfter(-1, null);
    else
      startEditingRow(Math.min(row, rowCount - 1));
  }

  @Override
  public boolean editCellAt(final int editRow, final int editCol) {
    final boolean success = super.editCellAt(editRow, editCol);
    if (success && editCol == COL_CAS_CELLS)
      editor.setInputAreaFocused();
    return success;
  }

  public CASTableCellValue getCASTableCellValue(final int row) {
    return (CASTableCellValue) tableModel.getValueAt(row, COL_CAS_CELLS);
  }

  public CASView getCASView() {
    return view;
  }

  public CASTableCellEditor getEditor() {
    return editor;
  }

  /**
   * Returns the preferred height of a row. The result is equal to the tallest
   * cell in the row.
   * 
   * @see http://www.exampledepot.com/egs/javax.swing.table/RowHeight.html
   */
  public int getPreferredRowHeight(final int rowIndex) {
    // Get the current default height for all rows
    int height = getRowHeight();

    // Determine highest cell in the row
    for (int c = 0; c < getColumnCount(); c++) {
      final TableCellRenderer renderer = getCellRenderer(rowIndex, c);
      final Component comp = prepareRenderer(renderer, rowIndex, c);
      final int h = comp.getPreferredSize().height; // + 2*margin;
      height = Math.max(height, h);
    }
    return height;
  }

  /**
   * Inserts a row at the end and starts editing the new row.
   */
  public void insertRow(final CASTableCellValue newValue) {
    insertRowAfter(tableModel.getRowCount() - 1, newValue);
  }

  /**
   * Inserts a row after selectedRow and starts editing the new row.
   */
  public void insertRowAfter(final int selectedRow, CASTableCellValue newValue) {
    // TODO: remove
    System.out.println("insertRowAfter: " + selectedRow);

    if (newValue == null)
      newValue = new CASTableCellValue(view);
    tableModel.insertRow(selectedRow + 1, new Object[]{newValue});

    // update height of new row
    startEditingRow(selectedRow + 1);
  }

  public boolean isRowEmpty(final int row) {
    final CASTableCellValue value = (CASTableCellValue) tableModel.getValueAt(
        row, 0);
    final String input = value.getInput();
    final String output = value.getOutput();
    return (input == null || input.length() == 0)
        && (output == null || output.length() == 0);
  }

  public final boolean isShowCellSeparator() {
    return showCellSeparator;
  }

  /**
   * The height of each row is set to the preferred height of the tallest cell
   * in that row.
   */
  public void packRows() {
    packRows(0, getRowCount());
  }

  /**
   * For each row >= start and < end, the height of a row is set to the
   * preferred height of the tallest cell in that row.
   */
  public void packRows(final int start, final int end) {
    for (int r = start; r < end; r++) {
      // Get the preferred height
      final int h = getPreferredRowHeight(r);

      // Now set the row height using the preferred height
      if (getRowHeight(r) != h)
        setRowHeight(r, h);
    }
  }

  @Override
  public void setFont(final Font ft) {
    super.setFont(ft);
    if (editor != null)
      editor.setFont(getFont());
    if (renderer != null)
      renderer.setFont(getFont());
  }

  public final void setShowCellSeparator(final boolean showCellSeparator) {
    this.showCellSeparator = showCellSeparator;
  }

  /*
   * Function: Set the focus on the specified row
   */
  public void startEditingRow(final int editRow) {
    // TODO: remove
    System.out.println("startEditingRow: " + editRow);

    if (editRow >= tableModel.getRowCount())
      // insert new row, this starts editing
      insertRow(null);
    else {
      // start editing
      setRowSelectionInterval(editRow, editRow);
      scrollRectToVisible(getCellRect(editRow, COL_CAS_CELLS, false));
      editCellAt(editRow, COL_CAS_CELLS);
    }
  }

  public void stopEditing() {
    // stop editing
    final CellEditor editor = (CellEditor) getEditorComponent();
    if (editor != null)
      editor.stopCellEditing();
  }

  public void updateRow(final int row) {
    // stopEditing();

    // TODO: remove
    final CASTableCellValue value = getCASTableCellValue(row);
    System.out.println("update row: " + row + ", input: " + value.getInput()
        + ", output: " + value.getOutput());

    tableModel.fireTableRowsUpdated(row, row);
  }

}
