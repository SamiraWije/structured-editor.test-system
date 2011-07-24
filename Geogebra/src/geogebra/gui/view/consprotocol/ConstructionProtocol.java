/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.gui.view.consprotocol;

import geogebra.Plain;
import geogebra.euclidian.Drawable;
import geogebra.gui.view.spreadsheet.MyTable;
import geogebra.kernel.*;
import geogebra.main.Application;
import geogebra.main.View;
import geogebra.util.FastHashMapKeyless;
import geogebra.util.Util;

import java.awt.*;
import java.awt.dnd.DragSource;
import java.awt.event.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class ConstructionProtocol extends JDialog implements Printable {

  class ColumnData {
    String title;
    boolean isVisible; // column is shown in table
    private final int prefWidth, minWidth;
    private final int alignment;
    private final boolean initShow; // should be shown from the beginning

    protected ColumnData(String title, int prefWidth, int minWidth,
        int alignment, boolean initShow) {
      this.title = title;
      this.prefWidth = prefWidth;
      this.minWidth = minWidth;
      this.alignment = alignment;
      this.initShow = initShow;

      isVisible = initShow;
    }

    public int getAlignment() {
      return alignment;
    }

    public boolean getInitShow() {
      // algebra column should only be shown at startup if algebraview is shown
      // in app
      if (title.equals("Algebra") && app.isUsingLayout()
          && !app.getGuiManager().showAlgebraView())
        return false;

      return initShow;
    }

    public int getMinWidth() {
      return minWidth;
    }

    public int getPreferredWidth() {
      return prefWidth;
    }

    public String getTitle() {
      return app.getPlain1(title);
    }
  }
  class ColumnKeeper implements ActionListener {
    protected TableColumn column;
    protected ColumnData colData;

    private final boolean isBreakPointColumn;

    protected ColumnKeeper(TableColumn column, ColumnData colData) {
      this.column = column;
      this.colData = colData;

      isBreakPointColumn = colData.title.equals("Breakpoint");
    }

    public void actionPerformed(ActionEvent e) {
      JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
      TableColumnModel model = table.getColumnModel();

      if (item.isSelected()) {
        colData.isVisible = true;
        model.addColumn(column);
        // column is added at right end of model
        // move column to its default place
        int lastPos = model.getColumnCount() - 1;
        int pos = data.getColumnNumber(colData);
        if (pos >= 0 && pos < lastPos)
          model.moveColumn(lastPos, pos);
        setSize(getWidth() + column.getPreferredWidth(), getHeight());

        // show breakPointColumn => show all lines
        if (isBreakPointColumn) {
          kernel.setShowOnlyBreakpoints(false);
          cbShowOnlyBreakpoints.setSelected(false);
        }
      } else {
        colData.isVisible = false;
        model.removeColumn(column);
        setSize(getWidth() - column.getWidth(), getHeight());
      }
      table.tableChanged(new TableModelEvent(data));

      // reinit view to update possible breakpoint changes
      data.initView();

      SwingUtilities.updateComponentTreeUI(thisDialog);
    }
  }
  class ConstructionKeyListener extends KeyAdapter {
    @Override
    public void keyPressed(KeyEvent event) {
      // SPECIAL KEYS
      int keyCode = event.getKeyCode();
      switch (keyCode) {
        case KeyEvent.VK_DELETE :
          ConstructionElement ce = kernel.getConstructionElement(kernel
              .getConstructionStep());
          if (ce != null) {
            ce.remove();
            app.storeUndoInfo();
          }
          break;

        case KeyEvent.VK_UP :
        case KeyEvent.VK_RIGHT :
          previousStep();
          scrollToConstructionStep();
          break;

        case KeyEvent.VK_DOWN :
        case KeyEvent.VK_LEFT :
          nextStep();
          scrollToConstructionStep();
          break;

        case KeyEvent.VK_HOME :
        case KeyEvent.VK_PAGE_UP :
          setConstructionStep(-1);
          scrollToConstructionStep();
          break;

        case KeyEvent.VK_END :
        case KeyEvent.VK_PAGE_DOWN :
          setConstructionStep(kernel.getLastConstructionStep());
          scrollToConstructionStep();
          break;
      }
    }
  }
  class ConstructionMouseListener implements MouseListener, MouseMotionListener {

    // smallest and larges possible construction index for dragging
    private int minIndex, maxIndex;

    public void mouseClicked(MouseEvent e) {
      Object ob = e.getSource();
      if (ob == table) {
        Point origin = e.getPoint();
        int row = table.rowAtPoint(origin);
        if (row < 0)
          return;

        // right click
        if (Application.isRightClick(e)) {
          GeoElement geo = data.getGeoElement(row);
          app.getGuiManager().showPopupMenu(geo, table, origin);
        } else { // left click

          if (e.getClickCount() == 1) {

            // click on breakpoint column?
            int column = table.columnAtPoint(origin);
            String colName = table.getColumnName(column);

            if (colName.equals(Plain.Breakpoint)) {
              RowData rd = data.getRow(row);
              GeoElement geo = rd.geo;
              boolean newVal = !geo.isConsProtocolBreakpoint();
              geo.setConsProtocolBreakpoint(newVal);

              // update only current row
              rd.updateAll();

              if (kernel.showOnlyBreakpoints() && !newVal)
                data.remove(geo);

              /*
               * // update geo and all siblings GeoElement [] siblings =
               * geo.getSiblings(); if (siblings != null) { data.updateAll(); }
               * else { // update only current row rd.updateAll(); }
               * 
               * // no longer a breakpoint: hide it if
               * (kernel.showOnlyBreakpoints() && !newVal) { if (siblings ==
               * null) data.remove(geo); else { for (int i=0; i <
               * siblings.length; i++) { data.remove(siblings[i]); } } }
               */
            }
          }

          // double click
          if (e.getClickCount() == 2) {
            data.setConstructionStepForRow(row);
            table.repaint();
          }
        }
      } else if (ob == table.getTableHeader())
        if (e.getClickCount() == 2) {
          setConstructionStep(-1);
          table.repaint();
        }
    }

    public void mouseDragged(MouseEvent e) {
      if (e.getSource() != table || dragIndex == -1)
        return;

      int row = table.rowAtPoint(e.getPoint());
      int index = row < 0 ? -1 : data.getConstructionIndex(row);
      // drop possible
      if (minIndex <= index && index <= maxIndex)
        table.setCursor(DragSource.DefaultMoveDrop);
      else
        table.setCursor(DragSource.DefaultMoveNoDrop);

      if (index != dropIndex) {
        dragging = true;
        dropIndex = index;
        table.repaint();
      }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent arg0) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
      if (e.getSource() != table)
        return;
      int row = table.rowAtPoint(e.getPoint());
      if (row >= 0) { // init drag
        GeoElement geo = data.getGeoElement(row);
        dragIndex = geo.getConstructionIndex();
        minIndex = geo.getMinConstructionIndex();
        maxIndex = geo.getMaxConstructionIndex();
      }
    }

    public void mouseReleased(MouseEvent e) {
      if (e.getSource() != table)
        return;
      // drop
      int row = table.rowAtPoint(e.getPoint());
      if (row >= 0) {
        dropIndex = data.getConstructionIndex(row);
        boolean kernelChanged = data.moveInConstructionList(dragIndex,
            dropIndex);
        if (kernelChanged)
          app.storeUndoInfo();
      }
      // reinit vars
      dragging = false;
      dragIndex = -1;
      dropIndex = -1;
      table.setCursor(Cursor.getDefaultCursor());
      table.repaint();
    }
  }

  class ConstructionTableCellRenderer extends DefaultTableCellRenderer {

    /**
		 * 
		 */
    private static final long serialVersionUID = -9165858653728142643L;

    private final JCheckBox cbTemp = new JCheckBox();

    public ConstructionTableCellRenderer() {
      setOpaque(true);
      setVerticalAlignment(TOP);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {

      // Boolean value: show as checkbox
      boolean isBoolean = value instanceof Boolean;
      Component comp = isBoolean ? cbTemp : (Component) this;
      int step = kernel.getConstructionStep();
      RowData rd = data.getRow(row);
      int index = rd.geo.getConstructionIndex();
      if (useColors)
        comp.setForeground(rd.geo.getObjectColor());
      else
        comp.setForeground(Color.black);

      if (index == step)
        comp.setBackground(COLOR_STEP_HIGHLIGHT);
      else if (index < step)
        comp.setBackground(Color.white);
      else {
        comp.setForeground(Color.gray);
        comp.setBackground(Color.white);
      }

      // set background color
      if (dragging)
        if (index == dragIndex)
          comp.setBackground(COLOR_DRAG_HIGHLIGHT);
        else if (index == dropIndex)
          comp.setBackground(COLOR_DROP_HIGHLIGHT);

      comp.setFont(table.getFont());

      if (isBoolean) {
        cbTemp.setSelected(((Boolean) value).booleanValue());
        cbTemp.setEnabled(true);
        return cbTemp;
      } else {
        setText(value == null ? "" : value.toString());
        return this;
      }

    }
  }
  class ConstructionTableData extends AbstractTableModel implements View {

    private class ColumnMovementListener implements TableColumnModelListener {
      public void columnAdded(TableColumnModelEvent e) {
        columnsCount++;
      }

      public void columnMarginChanged(ChangeEvent e) {
      }

      public void columnMoved(TableColumnModelEvent e) {
      }
      public void columnRemoved(TableColumnModelEvent e) {
        columnsCount--;
      }
      public void columnSelectionChanged(ListSelectionEvent e) {
      }
    }

    /**
 * 
 */
    private static final long serialVersionUID = -6933858200673625046L;

    final protected ColumnData columns[] = {
        new ColumnData("No.", 35, 35, SwingConstants.CENTER, true),
        new ColumnData("Name", 80, 50, SwingConstants.LEFT, true),
        new ColumnData("Definition", 150, 50, SwingConstants.LEFT, true),
        new ColumnData("Command", 150, 50, SwingConstants.LEFT, false),
        new ColumnData("Algebra", 150, 50, SwingConstants.LEFT, true),
        new ColumnData("Breakpoint", 70, 35, SwingConstants.CENTER, false)};
    private final ArrayList<RowData> rowList;
    // map for (GeoElement, RowData) pairs
    private final FastHashMapKeyless geoMap;

    private int columnsCount = columns.length;

    public ConstructionTableData() {
      rowList = new ArrayList<RowData>();
      geoMap = new FastHashMapKeyless();
    }

    public void add(GeoElement geo) {
      if (!geo.isLabelSet() || kernel.showOnlyBreakpoints()
          && !geo.isConsProtocolBreakpoint())
        return;

      RowData row = (RowData) geoMap.get(geo); // lookup row for geo
      if (row == null) { // new row
        int index = geo.getConstructionIndex();
        int pos = 0; // there may be more rows with same index
        int size = rowList.size();
        while (pos < size
            && index >= rowList.get(pos).geo.getConstructionIndex())
          pos++;

        row = new RowData(geo);
        if (pos < size)
          rowList.add(pos, row);
        else {
          pos = size;
          rowList.add(row);
        }

        // insert new row
        geoMap.put(geo, row); // insert (geo, row) pair in map
        updateRowNumbers(pos);
        updateIndices();
        fireTableRowsInserted(pos, pos);
        updateAll();
        updateNavigationBars();
      }
    }

    protected void attachView() {
      if (!isViewAttached) {
        kernel.attach(this);
        initView();
        isViewAttached = true;
      }

      scrollToConstructionStep();
    }

    public void clearView() {
      rowList.clear();
      geoMap.clear();
      updateNavigationBars();
    }

    protected void detachView() {
      // only detach view if there are
      // no registered navitagion bars
      if (isViewAttached && navigationBars.size() == 0) {
        // clear view
        rowList.clear();
        geoMap.clear();
        kernel.detach(this);
        isViewAttached = false;

        // side effect: go to last construction step
        setConstructionStep(kernel.getLastConstructionStep());
      }
    }

    private Color getColorAt(int nRow, int nCol) {
      try {
        if (useColors)
          return rowList.get(nRow).geo.getObjectColor();
        else
          return Color.black;
      } catch (Exception e) {
        return Color.black;
      }
    }

    public int getColumnCount() {
      return columnsCount;
    }

    @Override
    public String getColumnName(int column) {
      return columns[column].getTitle();
    }

    protected int getColumnNumber(ColumnData column) {
      int pos = -1;
      for (int i = 0; i < columns.length; i++)
        if (columns[i] == column) {
          pos = i;
          break;
        }
      return pos;
    }

    protected int getConstructionIndex(int row) {
      return rowList.get(row).geo.getConstructionIndex();
    }

    /**
     * Returns the number of the current construction step shown in the
     * construction protocol's table.
     */
    public int getCurrentStepNumber() {
      int step = kernel.getConstructionStep();

      // search the current construction step in the rowList
      int size = rowList.size();
      for (int i = 0; i < size; i++) {
        RowData rd = rowList.get(i);
        if (rd.geo.getConstructionIndex() == step)
          return rd.index;
      }
      return 0;
    }

    protected GeoElement getGeoElement(int row) {
      return rowList.get(row).geo;
    }

    /**
     * Returns the number of the last construction step shown in the
     * construction protocol's table.
     */
    public int getLastStepNumber() {
      int pos = rowList.size() - 1;
      if (pos >= 0)
        return rowList.get(pos).index;
      else
        return 0;
    }

    // html code without <html> tags
    protected String getPlainHTMLAt(int nRow, int nCol) {
      if (nRow < 0 || nRow >= getRowCount())
        return "";
      switch (nCol) {
        case 0 :
          return "" + (rowList.get(nRow).geo.getConstructionIndex() + 1);
        case 1 :
          return rowList.get(nRow).geo.getNameDescriptionHTML(false, false);
        case 2 :
          return rowList.get(nRow).geo.getDefinitionDescriptionHTML(false);
        case 3 :
          return rowList.get(nRow).geo.getCommandDescriptionHTML(false);
        case 4 :
          return rowList.get(nRow).geo.getAlgebraDescriptionHTML(false);
        case 5 :
          return rowList.get(nRow).consProtocolVisible.toString();
      }
      return "";
    }

    // no html code but plain text
    protected String getPlainTextAt(int nRow, int nCol) {
      if (nRow < 0 || nRow >= getRowCount())
        return "";
      switch (nCol) {
        case 0 :
          return "" + (rowList.get(nRow).geo.getConstructionIndex() + 1);
        case 1 :
          return rowList.get(nRow).geo.getNameDescription();
        case 2 :
          return rowList.get(nRow).geo.getDefinitionDescription();
        case 3 :
          return rowList.get(nRow).geo.getCommandDescription();
        case 4 :
          return rowList.get(nRow).geo.getAlgebraDescription();
        case 5 :
          return rowList.get(nRow).consProtocolVisible.toString();
      }
      return "";
    }

    protected RowData getRow(int row) {
      return rowList.get(row);
    }

    public int getRowCount() {
      return rowList.size();
    }

    public int getRowIndex(RowData row) {
      return rowList.indexOf(row);
    }

    public Object getValueAt(int nRow, int nCol) {
      if (nRow < 0 || nRow >= getRowCount())
        return "";
      switch (nCol) {
        case 0 :
          return rowList.get(nRow).index + "";
        case 1 :
          return rowList.get(nRow).name;
        case 2 :
          return rowList.get(nRow).definition;
        case 3 :
          return rowList.get(nRow).command;
        case 4 :
          return rowList.get(nRow).algebra;
        case 5 :
          return rowList.get(nRow).consProtocolVisible;
      }
      return "";
    }

    protected void initView() {
      // init view
      rowList.clear();
      geoMap.clear();
      kernel.notifyAddAll(this, kernel.getLastConstructionStep());
    }

    /* ************************
     * View Implementation ***********************
     */

    @Override
    public boolean isCellEditable(int nRow, int nCol) {
      return false;
    }

    boolean moveInConstructionList(int fromIndex, int toIndex) {
      kernel.detach(this);
      boolean changed = kernel.moveInConstructionList(fromIndex, toIndex);
      kernel.attach(this);

      // reorder rows in this view
      ConstructionElement ce = kernel.getConstructionElement(toIndex);
      GeoElement[] geos = ce.getGeoElements();
      for (int i = 0; i < geos.length; ++i) {
        remove(geos[i]);
        add(geos[i]);
      }
      return changed;
    }

    public void remove(GeoElement geo) {
      RowData row = (RowData) geoMap.get(geo);
      // lookup row for GeoElemen
      if (row != null) {
        rowList.remove(row); // remove row
        geoMap.remove(geo); // remove (geo, row) pair from map
        updateRowNumbers(row.rowNumber);
        updateIndices();
        fireTableRowsDeleted(row.rowNumber, row.rowNumber);
        updateAll();
        updateNavigationBars();
      }
    }

    public void rename(GeoElement geo) {
      // renaming may affect multiple rows
      // so let's update whole table
      updateAll();
    }

    protected void repaint() {
      table.repaint();
    }

    final public void repaintView() {
      repaint();
    }

    public void reset() {
      repaint();
    }

    public void setConstructionStepForRow(int row) {
      if (row >= 0)
        setConstructionStep(getConstructionIndex(row));
      else
        setConstructionStep(-1);
    }

    final public void update(GeoElement geo) {
      RowData row = (RowData) geoMap.get(geo);
      if (row != null) {
        // remove row if only breakpoints
        // are shown and this is no longer a breakpoint (while loading a
        // construction)
        if (!geo.isConsProtocolBreakpoint() && kernel.showOnlyBreakpoints())
          remove(geo);
        else {
          row.updateAlgebraAndName();
          fireTableRowsUpdated(row.rowNumber, row.rowNumber);
        }
      } else // missing row: should be added if only breakpoints
      // are shown and this became a breakpoint (while loading a construction)
      if (kernel.showOnlyBreakpoints() && geo.isConsProtocolBreakpoint())
        add(geo);
    }

    private void updateAll() {
      int size = rowList.size();
      for (int i = 0; i < size; ++i) {
        RowData row = rowList.get(i);
        row.updateAll();
        if (row.includesIndex)
          table.setRowHeight(i, table.getFont().getSize() * 2);
        else
          table.setRowHeight(i, (table.getFont().getSize() + 8));
      }
      fireTableRowsUpdated(0, size - 1);
    }

    final public void updateAuxiliaryObject(GeoElement geo) {
      // update(geo);
    }

    // update all indices
    private void updateIndices() {
      int size = rowList.size();
      if (size == 0)
        return;

      int lastIndex = -1;
      int count = 0;
      RowData row;
      for (int i = 0; i < size; ++i) {
        row = rowList.get(i);
        int newIndex = row.geo.getConstructionIndex();
        if (lastIndex != newIndex) {
          lastIndex = newIndex;
          count++;
        }
        row.index = count;
      }
    }

    // update all row numbers >= row
    private void updateRowNumbers(int row) {
      if (row < 0)
        return;
      int size = rowList.size();
      for (int i = row; i < size; ++i)
        rowList.get(i).rowNumber = i;
    }
  }
  class HeaderRenderer extends JLabel implements TableCellRenderer {
    /**
		 * 
		 */
    private static final long serialVersionUID = 8149210120003929698L;

    public HeaderRenderer() {
      setOpaque(true);
      // setForeground(UIManager.getColor("TableHeader.foreground"));
      // setBackground(UIManager.getColor("TableHeader.background"));
      // setBorder(UIManager.getBorder("TableHeader.cellBorder"));
      // better for Macs?
      setForeground(Color.black);
      setBackground(MyTable.BACKGROUND_COLOR_HEADER);
      // setBorder(BorderFactory.createBevelBorder(0));
      setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1,
          MyTable.TABLE_GRID_COLOR));

    }

    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
      setFont(table.getFont());
      setText(value == null ? "" : " " + value.toString());
      return this;
    }
  }
  class RowData {
    int rowNumber;
    int index; // construction index of line: may be different
    // to geo.getConstructionIndex() as not every
    // geo is shown in the protocol
    GeoElement geo;
    String name, algebra, definition, command;
    boolean includesIndex;
    Boolean consProtocolVisible;

    protected RowData(GeoElement geo) {
      this.geo = geo;
      updateAll();
    }

    protected void updateAlgebraAndName() {
      algebra = geo.getAlgebraDescriptionTextOrHTML();
      // name description changes if type changes, e.g. ellipse becomes
      // hyperbola
      name = geo.getNameDescriptionTextOrHTML();
      // name = geo.getNameDescriptionHTML(true, true);
    }

    protected void updateAll() {
      // name = geo.getNameDescriptionHTML(true, true);
      name = geo.getNameDescriptionTextOrHTML();
      algebra = geo.getAlgebraDescriptionTextOrHTML();
      definition = geo.getDefinitionDescriptionHTML(true);
      command = geo.getCommandDescriptionHTML(true);
      consProtocolVisible = new Boolean(geo.isConsProtocolBreakpoint());

      // does this line include an index?
      includesIndex = name.indexOf("<sub>") >= 0
          || algebra.indexOf("<sub>") >= 0 || definition.indexOf("<sub>") >= 0
          || command.indexOf("<sub>") >= 0;
    }
  }

  /**
	 * 
	 */
  private static final long serialVersionUID = 1152223555575098008L;
  private static Color COLOR_STEP_HIGHLIGHT = Application.COLOR_SELECTION;
  private static Color COLOR_DRAG_HIGHLIGHT = new Color(250, 250, 200);
  private static Color COLOR_DROP_HIGHLIGHT = Color.lightGray;

  private final JTable table;

  private final ConstructionTableData data;

  private final Application app;
  private final Kernel kernel;
  private final JMenuBar menuBar = new JMenuBar();

  private JCheckBoxMenuItem cbUseColors, cbShowOnlyBreakpoints;

  private final TableColumn[] tableColumns;
  private final JDialog thisDialog;
  private AbstractAction printPreviewAction, exportHtmlAction;

  private boolean useColors;

  // for drag & drop
  boolean dragging = false;

  private int dragIndex = -1; // dragged construction index

  private int dropIndex = -1;

  // for printing
  private int maxNumPage = 1;

  // registered navigation bars that should be informed about updates of the
  // protocol
  private boolean isViewAttached;

  private final ArrayList<ConstructionProtocolNavigation> navigationBars = new ArrayList<ConstructionProtocolNavigation>();

  private final ConstructionProtocolNavigation protNavBar; // navigation bar of
  // protocol window

  public ConstructionProtocol(Application app) {
    super(app.getFrame());

    this.app = app;
    kernel = app.getKernel();
    thisDialog = this;
    data = new ConstructionTableData();
    useColors = true;

    table = new JTable();
    table.setAutoCreateColumnsFromModel(false);
    table.setModel(data);
    table.setRowSelectionAllowed(true);
    table.setGridColor(Color.lightGray);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

    // header
    JTableHeader header = table.getTableHeader();
    // header.setUpdateTableInRealTime(true);
    header.setReorderingAllowed(false);

    // init model
    ConstructionTableCellRenderer renderer;
    HeaderRenderer headerRend = new HeaderRenderer();
    tableColumns = new TableColumn[data.columns.length];
    for (int k = 0; k < data.columns.length; k++) {
      renderer = new ConstructionTableCellRenderer();
      renderer.setHorizontalAlignment(data.columns[k].getAlignment());
      tableColumns[k] = new TableColumn(k, data.columns[k].getPreferredWidth(),
          renderer, null);
      tableColumns[k].setMinWidth(data.columns[k].getMinWidth());
      tableColumns[k].setHeaderRenderer(headerRend);
      if (data.columns[k].getInitShow())
        table.addColumn(tableColumns[k]);
    }
    // first column "No." should have fixed width
    tableColumns[0].setMaxWidth(tableColumns[0].getMinWidth());

    table.getColumnModel().addColumnModelListener(
        data.new ColumnMovementListener());

    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.getViewport().setBackground(Color.white);
    getContentPane().add(scrollPane, BorderLayout.CENTER);

    // clicking
    ConstructionMouseListener ml = new ConstructionMouseListener();
    table.addMouseListener(ml);
    table.addMouseMotionListener(ml);
    header.addMouseListener(ml); // double clicking

    // keys
    ConstructionKeyListener keyListener = new ConstructionKeyListener();
    table.addKeyListener(keyListener);

    // navigation bar
    protNavBar = new ConstructionProtocolNavigation(this);
    protNavBar.setPlayButtonVisible(false);
    protNavBar.setConsProtButtonVisible(false);
    getContentPane().add(protNavBar, BorderLayout.SOUTH);
    Util.addKeyListenerToAll(protNavBar, keyListener);

    initGUI();

    // setSize(500, 200);
    pack();

    // center dialog
    Dimension d1 = getSize();
    Dimension d2 = app.getMainComponent().getSize();
    int x = Math.max((d2.width - d1.width) / 2, 0);
    int y = Math.max((d2.width - d1.width) / 2, 0);
    setBounds(x, y, d1.width, d1.height);
  }

  protected void firstStep() {
    if (isViewAttached)
      kernel.detach(data);
    kernel.firstStep();
    if (isViewAttached)
      kernel.attach(data);
    updateNavigationBars();
  }

  public Application getApplication() {
    return app;
  }

  public String getConsProtocolXML() {
    StringBuffer sb = new StringBuffer();

    // COLUMNS
    sb.append("\t<consProtColumns ");
    for (int i = 0; i < data.columns.length; i++) {
      sb.append(" col");
      sb.append(i);
      sb.append("=\"");
      sb.append(data.columns[i].isVisible);
      sb.append("\"");
    }
    sb.append("/>\n");

    // consProtocol
    sb.append("\t<consProtocol ");
    sb.append("useColors=\"");
    sb.append(useColors);
    sb.append("\"");
    sb.append(" showOnlyBreakpoints=\"");
    sb.append(kernel.showOnlyBreakpoints());
    sb.append("\"");
    sb.append("/>\n");

    return sb.toString();
  }

  /**
   * Returns the number of the current construction step shown in the
   * construction protocol's table.
   */
  public int getCurrentStepNumber() {
    return data.getCurrentStepNumber();
  }

  /**
   * Returns a html representation of the construction protocol.
   * 
   * @param imgFile
   *          : image file to be included
   */
  public String getHTML(File imgFile) {
    StringBuffer sb = new StringBuffer();

    sb.append("<html>\n");
    sb.append("<head>\n");
    sb.append("<title>");
    sb.append(Util.toHTMLString(Plain.ApplicationName + " - "
        + Plain.ConstructionProtocol));
    sb.append("</title>\n");
    sb.append("<meta keywords = \"");
    sb.append(Util.toHTMLString(Plain.ApplicationName));
    sb.append(" export\">");
    String css = app.getSetting("cssConstructionProtocol");
    if (css != null) {
      sb.append(css);
      sb.append("\n");
    }
    sb.append("</head>\n");

    sb.append("<body>\n");

    // header with title
    Construction cons = kernel.getConstruction();
    String title = cons.getTitle();
    if (!title.equals("")) {
      sb.append("<h1>");
      sb.append(Util.toHTMLString(title));
      sb.append("</h1>\n");
    }

    // header with author and date
    String author = cons.getAuthor();
    String date = cons.getDate();
    String line = null;
    if (!author.equals(""))
      line = author;
    if (!date.equals(""))
      if (line == null)
        line = date;
      else
        line = line + " - " + date;
    if (line != null) {
      sb.append("<h3>");
      sb.append(Util.toHTMLString(line));
      sb.append("</h3>\n");
    }

    // include image file
    if (imgFile != null) {
      sb.append("<p>\n");
      sb.append("<img src=\"");
      sb.append(imgFile.getName());
      sb.append("\" alt=\"");
      sb.append(Util.toHTMLString(Plain.ApplicationName));
      sb.append(' ');
      sb.append(Util.toHTMLString(Plain.DrawingPad));
      sb.append("\" border=\"1\">\n");
      sb.append("</p>\n");
    }

    // table
    sb.append("<table border=\"1\">\n");

    // table headers
    sb.append("<tr>\n");
    TableColumnModel colModel = table.getColumnModel();
    int nColumns = colModel.getColumnCount();
    for (int nCol = 0; nCol < nColumns; nCol++) {
      TableColumn tk = colModel.getColumn(nCol);
      title = (String) tk.getIdentifier();
      sb.append("<th>");
      sb.append(Util.toHTMLString(title));
      sb.append("</th>\n");
    }
    sb.append("</tr>\n");

    // table rows
    int endRow = table.getRowCount();
    for (int nRow = 0; nRow < endRow; nRow++) {
      sb.append("<tr  valign=\"baseline\">\n");
      for (int nCol = 0; nCol < nColumns; nCol++) {
        int col = table.getColumnModel().getColumn(nCol).getModelIndex();
        String str = data.getPlainHTMLAt(nRow, col);
        sb.append("<td>");
        if (str.equals(""))
          sb.append("&nbsp;"); // space
        else {
          Color color = data.getColorAt(nRow, col);
          if (color != Color.black) {
            sb.append("<span style=\"color:#");
            sb.append(Util.toHexString(color));
            sb.append("\">");
            sb.append(str);
            sb.append("</span>");
          } else
            sb.append(str);
        }
        sb.append("</td>\n");
      }
      sb.append("</tr>\n");
    }

    sb.append("</table>\n");

    // footer
    sb.append(app.getGuiManager().getCreatedWithHTML());

    sb.append("</body>\n");
    sb.append("</html>");

    return sb.toString();
  }

  /**
   * Returns the number of the last construction step shown in the construction
   * protocol's table.
   */
  public int getLastStepNumber() {
    return data.getLastStepNumber();
  }

  private void initActions() {

    printPreviewAction = new AbstractAction(
        geogebra.Menu.PrintPreview + "...", app
            .getImageIcon("document-print-preview.png")) {
      private static final long serialVersionUID = 1L;

      public void actionPerformed(ActionEvent e) {
        app.setWaitCursor();
        app.loadExportJar();

        Thread runner = new Thread() {
          @Override
          public void run() {
            new geogebra.export.PrintPreview(app, ConstructionProtocol.this,
                PageFormat.PORTRAIT);
          }
        };
        runner.start();

        app.setDefaultCursor();
      }
    };

    exportHtmlAction = new AbstractAction(Plain.ExportAsWebpage + " ("
        + Application.FILE_EXT_HTML + ") ...") {
      private static final long serialVersionUID = 1L;

      public void actionPerformed(ActionEvent e) {
        app.setWaitCursor();
        app.loadExportJar();

        Thread runner = new Thread() {
          @Override
          public void run() {
            JDialog d = new geogebra.export.ConstructionProtocolExportDialog(
                ConstructionProtocol.this);
            d.setVisible(true);
          }
        };
        runner.start();

        app.setDefaultCursor();
      }
    };

  }
  /**
   * inits GUI with labels of current language
   */
  public void initGUI() {
    setTitle(Plain.ConstructionProtocol);
    setFont(app.getPlainFont());
    setMenuBar();

    // set header values (language may have changed)
    for (int k = 0; k < tableColumns.length; k++)
      tableColumns[k].setHeaderValue(data.columns[k].getTitle());
    table.setFont(app.getPlainFont());
    data.updateAll();
  }

  public void initProtocol() {
    if (!isViewAttached)
      data.initView();

    updateMenubar();
  }

  private boolean isColumnInModel(TableColumn col) {
    boolean ret = false;
    TableColumnModel model = table.getColumnModel();
    int size = model.getColumnCount();
    for (int i = 0; i < size; ++i)
      if (model.getColumn(i) == col) {
        ret = true;
        break;
      }
    return ret;
  }

  protected void lastStep() {
    if (isViewAttached)
      kernel.detach(data);
    kernel.lastStep();
    if (isViewAttached)
      kernel.attach(data);
    updateNavigationBars();
  }

  protected void nextStep() {
    if (isViewAttached)
      kernel.detach(data);
    kernel.nextStep();
    if (isViewAttached)
      kernel.attach(data);
    updateNavigationBars();
    repaint();
  }

  protected void previousStep() {
    if (isViewAttached)
      kernel.detach(data);
    kernel.previousStep();
    if (isViewAttached)
      kernel.attach(data);
    updateNavigationBars();
  }

  public int print(Graphics pg, PageFormat pageFormat, int pageIndex)
      throws PrinterException {
    if (pageIndex >= maxNumPage)
      return NO_SUCH_PAGE;

    pg.translate((int) pageFormat.getImageableX(), (int) pageFormat
        .getImageableY());
    int wPage = (int) pageFormat.getImageableWidth();
    int hPage = (int) pageFormat.getImageableHeight();
    pg.setClip(0, 0, wPage, hPage);

    // construction title
    int y = 0;
    Font titleFont = table.getFont().deriveFont(Font.BOLD,
        table.getFont().getSize() + 2);
    pg.setFont(titleFont);
    pg.setColor(Color.black);
    // Font fn = pg.getFont();
    FontMetrics fm = pg.getFontMetrics();

    // title
    Construction cons = kernel.getConstruction();
    String title = cons.getTitle();
    if (!title.equals("")) {
      y += fm.getAscent();
      pg.drawString(title, 0, y);
    }

    // construction author and date
    String author = cons.getAuthor();
    String date = cons.getDate();
    String line = null;
    if (!author.equals(""))
      line = author;
    if (!date.equals(""))
      if (line == null)
        line = date;
      else
        line = line + " - " + date;
    if (line != null) {
      pg.setFont(table.getFont());
      // fn = pg.getFont();
      fm = pg.getFontMetrics();
      y += fm.getHeight();
      pg.drawString(line, 0, y);
    }

    y += 20; // space between title and table headers

    Font headerFont = table.getFont().deriveFont(Font.BOLD);
    pg.setFont(headerFont);
    fm = pg.getFontMetrics();

    TableColumnModel colModel = table.getColumnModel();
    int nColumns = colModel.getColumnCount();
    int x[] = new int[nColumns];
    x[0] = 0;

    int h = fm.getAscent();
    y += h; // add ascent of header font because of baseline

    int nRow, nCol;
    for (nCol = 0; nCol < nColumns; nCol++) {
      TableColumn tk = colModel.getColumn(nCol);
      int width = tk.getWidth();
      // only print column if there is enough space for it
      // if (x[nCol] + width > wPage) {
      // nColumns = nCol;
      // break;
      // }
      if (nCol + 1 < nColumns)
        x[nCol + 1] = x[nCol] + width;
      title = (String) tk.getIdentifier();
      pg.drawString(title, x[nCol], y);
    }

    Font tableFont = table.getFont();
    pg.setFont(tableFont);
    fm = pg.getFontMetrics();

    int header = y;
    h = fm.getHeight();
    int rowH = Math.max(h, 10);
    int rowPerPage = (hPage - header) / rowH;
    maxNumPage = Math.max((int) Math.ceil(table.getRowCount()
        / (double) rowPerPage), 1);

    // TableModel tblModel = table.getModel();
    int iniRow = pageIndex * rowPerPage;
    int endRow = Math.min(table.getRowCount(), iniRow + rowPerPage);
    int yAdd, maxYadd = 0;

    for (nRow = iniRow; nRow < endRow; nRow++) {
      y = y + h + maxYadd; // maxYadd is additional space for indices of last
      // line
      maxYadd = 0;
      for (nCol = 0; nCol < nColumns; nCol++) {
        int col = table.getColumnModel().getColumn(nCol).getModelIndex();
        // Object obj = data.getValueAt(nRow, col);
        // String str = obj.toString();
        // pg.drawString(str, x[nCol], y);
        String str = data.getPlainTextAt(nRow, col);
        pg.setColor(data.getColorAt(nRow, col));
        yAdd = Drawable.drawIndexedString((Graphics2D) pg, str, x[nCol], y).y;
        if (yAdd > maxYadd)
          maxYadd = yAdd;
      }
    }

    System.gc();
    return PAGE_EXISTS;
  }

  protected void registerNavigationBar(ConstructionProtocolNavigation nb) {
    if (!navigationBars.contains(nb)) {
      navigationBars.add(nb);
      data.attachView();
    }
  }

  protected void scrollToConstructionStep() {
    int rowCount = table.getRowCount();
    if (rowCount == 0)
      return;

    int step = kernel.getConstructionStep();
    int row = 0;
    for (int i = Math.max(step, 0); i < rowCount; i++)
      if (data.getConstructionIndex(i) <= step)
        row = i;
      else
        break;

    table.setRowSelectionInterval(row, row);
    table.repaint();
  }

  public void setConstructionStep(int step) {
    if (isViewAttached)
      kernel.detach(data);
    kernel.setConstructionStep(step);
    if (isViewAttached)
      kernel.attach(data);
    updateNavigationBars();
  }

  private void setMenuBar() {
    menuBar.removeAll();

    initActions();

    JMenu mFile = new JMenu(geogebra.Menu.File);
    mFile.add(printPreviewAction);
    mFile.add(exportHtmlAction);

    mFile.addSeparator();

    JMenuItem mExit = new JMenuItem(geogebra.Menu.Close, app.getEmptyIcon());
    ActionListener lstExit = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
      }
    };
    mExit.addActionListener(lstExit);
    mFile.add(mExit);
    menuBar.add(mFile);

    JMenu mView = new JMenu(geogebra.Menu.View);
    // TableColumnModel model = table.getColumnModel();

    for (int k = 1; k < tableColumns.length; k++) {
      JCheckBoxMenuItem item = new JCheckBoxMenuItem(data.columns[k].getTitle());
      TableColumn column = tableColumns[k];
      ColumnKeeper colKeeper = new ColumnKeeper(column, data.columns[k]);
      item.setSelected(isColumnInModel(column));
      item.addActionListener(colKeeper);
      mView.add(item);
    }
    mView.addSeparator();

    cbShowOnlyBreakpoints = new JCheckBoxMenuItem(Plain.ShowOnlyBreakpoints);

    cbShowOnlyBreakpoints.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        kernel.setShowOnlyBreakpoints(cbShowOnlyBreakpoints.isSelected());
        data.initView();
        repaint();
      }
    });
    mView.add(cbShowOnlyBreakpoints);

    cbUseColors = new JCheckBoxMenuItem(Plain.ColorfulConstructionProtocol);
    cbUseColors.setSelected(useColors);
    cbUseColors.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        useColors = cbUseColors.isSelected();
        data.updateAll();
      }
    });
    mView.add(cbUseColors);
    menuBar.add(mView);

    JMenu mHelp = new JMenu(geogebra.Menu.Help);
    JMenuItem mi = new JMenuItem(geogebra.Menu.FastHelp, app
        .getImageIcon("help.png"));
    ActionListener lstHelp = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        app.showHelp("ConstructionProtocolHelp");
        requestFocus();
      }
    };
    mi.addActionListener(lstHelp);
    mHelp.add(mi);
    menuBar.add(mHelp);

    setJMenuBar(menuBar);
    updateMenubar();
  }

  public void setUseColors(boolean flag) {
    useColors = flag;
    cbUseColors.setSelected(flag);
    data.updateAll();
  }

  /**
   * shows this dialog centered on screen
   */
  @Override
  public void setVisible(boolean flag) {
    if (flag)
      data.attachView();
    else
      data.detachView();
    super.setVisible(flag);
  }

  public void showHTMLExportDialog() {
    exportHtmlAction.actionPerformed(null);
  }

  /* *******************
   * PRINTING ******************
   */

  protected void unregisterNavigationBar(ConstructionProtocolNavigation nb) {
    navigationBars.remove(nb);
    data.detachView(); // only done if there are no more navigation bars
  }

  /* *******************
   * HTML export ******************
   */

  // Michael Borcherds 2008-05-15
  public void update() {
    data.updateAll();
  }

  public void updateMenubar() {
    cbShowOnlyBreakpoints.setSelected(kernel.showOnlyBreakpoints());
  }

  private void updateNavigationBars() {
    // update the navigation bar of the protocol window
    protNavBar.update();

    // update all registered navigation bars
    int size = navigationBars.size();
    for (int i = 0; i < size; i++)
      navigationBars.get(i).update();
  }

}
