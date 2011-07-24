package geogebra.cas.view;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * 
 */
class CASTableCellRenderer extends CASTableCell implements TableCellRenderer {

  /** 
   *  
   */
  private static final long serialVersionUID = 1L;

  CASTableCellRenderer(final CASView view) {
    super(view);
  }

  public Component getTableCellRendererComponent(final JTable table,
      final Object value, final boolean isSelected, final boolean hasFocus,
      final int row, final int column) {

    if (value instanceof CASTableCellValue) {
      inputPanel.setFont(view.getFont());

      final CASTableCellValue tempV = (CASTableCellValue) value;
      setValue(tempV);

      // update row height
      updateTableRowHeight(table, row);
    }
    return this;
  }

}
