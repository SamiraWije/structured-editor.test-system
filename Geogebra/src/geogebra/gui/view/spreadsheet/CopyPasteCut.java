package geogebra.gui.view.spreadsheet;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

public class CopyPasteCut {

  class Record {
    int id, x1, y1, x2, y2;
    public Record(final int id, final int x1, final int y1, final int x2,
        final int y2) {
      this.id = id;
      this.x1 = x1;
      this.x2 = x2;
      this.y1 = y1;
      this.y2 = y2;
    }

    public int compareTo(final Object o) {
      Application.debug(o.getClass() + "");
      // int id = ((Record) o).getId();
      // return id - this.id;
      return 0;
    }
    public int getId() {
      return id;
    }
    public int getx1() {
      return x1;
    }
    public int getx2() {
      return x2;
    }
    public int gety1() {
      return y1;
    }
    public int gety2() {
      return y2;
    }
  }
  protected Kernel kernel;
  protected Application app;

  protected MyTable table;
  protected String externalBuf;
  protected GeoElement[][] internalBuf;
  protected int bufColumn;

  protected int bufRow;

  Object[] constructionIndexes;

  // protected static Pattern pattern =
  // Pattern.compile("\\s*(\\\"([^\\\"]+)\\\")|([^,\\t\\\"]+)");
  // protected static Pattern pattern =
  // Pattern.compile("\\s*(\\\"([^\\\"]+)\\\")|([^\\t\\\"]+)");
  protected static Pattern pattern1 = Pattern
      .compile("((\\\"([^\\\"]+)\\\")|([^\\t\\\"\\(]+)|(\\([^)]+\\)))?(\\t|$)");

  protected static Pattern pattern2 = Pattern
      .compile("((\\\"([^\\\"]+)\\\")|([^,\\\"\\(]+)|(\\([^)]+\\)))?(,|$)");

  private static Comparator<Object> comparator;

  /*
   * change 3,4 to 3.4 leave {3,4,5} alone
   */
  private static String checkDecimalComma(String str) {
    if (str.indexOf("{") == -1 && str.indexOf(",") == str.lastIndexOf(","))
      str = str.replaceAll(",", "."); // allow decimal comma

    return str;
  }

  /**
   * used to sort Records based on the id (which is the construction index)
   */
  public static Comparator<Object> getComparator() {
    if (comparator == null)
      comparator = new Comparator<Object>() {
        public int compare(final Object a, final Object b) {
          final Record itemA = (Record) a;
          final Record itemB = (Record) b;

          return itemA.id - itemB.id;
        }

      };

    return comparator;
  }
  public static String[][] parseData(final String input) {

    // Application.debug("parse data: "+input);

    final String[] lines = input.split("\\r*\\n", -1);
    final String[][] data = new String[lines.length][];
    for (int i = 0; i < lines.length; ++i) {
      lines[i] = lines[i].trim();
      Matcher matcher = null;
      if (lines[i].indexOf('\t') != -1)
        matcher = pattern1.matcher(lines[i]);
      else
        matcher = pattern2.matcher(lines[i]);
      final LinkedList<String> list = new LinkedList<String>();
      while (matcher.find()) {
        String data1 = matcher.group(3);
        String data2 = matcher.group(4);
        String data3 = matcher.group(5);

        // Application.debug("data1: "+data1);
        // Application.debug("data2: "+data2);
        // Application.debug("data3: "+data3);

        if (data1 != null) {
          data1 = data1.trim();
          data1 = checkDecimalComma(data1); // allow decimal comma
          list.addLast(data1);
        } else if (data2 != null) {
          data2 = data2.trim();
          data2 = checkDecimalComma(data2); // allow decimal comma
          list.addLast(data2);
        } else if (data3 != null) {
          data3 = data3.trim();
          list.addLast(data3);
        } else
          list.addLast("");
      }
      if (list.size() > 0 && list.getLast().equals(""))
        list.removeLast();
      data[i] = list.toArray(new String[0]);
    }
    return data;
  }

  public CopyPasteCut(final JTable table0, final Kernel kernel0) {
    table = (MyTable) table0;
    kernel = kernel0;
    app = kernel.getApplication();
  }

  public void copy(final int column1, final int row1, final int column2,
      final int row2, final boolean skipInternalCopy) {
    // external
    externalBuf = "";
    for (int row = row1; row <= row2; ++row) {
      for (int column = column1; column <= column2; ++column) {
        final GeoElement value = RelativeCopy.getValue(table, column, row);
        if (value != null)
          externalBuf += value.toValueString();
        // if (value.isChangeable()) {
        // externalBuf += value.toValueString();
        // }
        // else {
        // String def = value.getDefinitionDescription();
        // def = def.replaceAll("\\s+", "");
        // externalBuf += def;
        // }
        if (column != column2)
          externalBuf += "\t";
      }
      if (row != row2)
        externalBuf += "\n";
    }
    final Toolkit toolkit = Toolkit.getDefaultToolkit();
    final Clipboard clipboard = toolkit.getSystemClipboard();
    final StringSelection stringSelection = new StringSelection(externalBuf);
    clipboard.setContents(stringSelection, null);

    // internal
    if (skipInternalCopy)
      internalBuf = null;
    else {
      bufColumn = column1;
      bufRow = row1;
      internalBuf = RelativeCopy.getValues(table, column1, row1, column2, row2);
    }
  }

  public void createPointsAndAList1(final GeoElement[][] values)
      throws Exception {
    final LinkedList<String> list = new LinkedList<String>();
    if (values.length == 1 && values[0].length > 0)
      for (int i = 0; i < values[0].length; ++i) {
        final GeoElement v1 = values[0][i];
        if (v1 != null && v1.isGeoPoint())
          list.addLast(v1.getLabel());
      }
    if (values.length > 0 && values[0].length == 1)
      for (int i = 0; i < values.length; ++i) {
        final GeoElement v1 = values[i][0];
        if (v1 != null && v1.isGeoPoint())
          list.addLast(v1.getLabel());
      }

    if (list.size() > 0) {
      final String[] points = list.toArray(new String[0]);
      String text = "={";
      for (int i = 0; i < points.length; ++i) {
        text += points[i];
        if (i != points.length - 1)
          text += ",";
      }
      text += "}";
      final GeoElement[] geos = table.kernel.getAlgebraProcessor()
          .processAlgebraCommandNoExceptionHandling(text, false);

      // set list name
      final String listName = geos[0].getIndexLabel("L");
      geos[0].setLabel(listName);
    }
  }

  public void createPointsAndAList2(final GeoElement[][] values)
      throws Exception {
    final LinkedList<String> list = new LinkedList<String>();

    /*
     * Markus Hohenwarter, 2008-08-24, I think this is not needed...
     * 
     * if (values.length == 2) { for (int i = 0; i < values[0].length && i <
     * values[1].length; ++ i) { GeoElement v1 = values[0][i]; GeoElement v2 =
     * values[1][i]; if (v1 != null && v2 != null && v1.isGeoNumeric() &&
     * v2.isGeoNumeric()) { String text = "(" + v1.getLabel() + "," +
     * v2.getLabel() + ")"; GeoElement [] geos =
     * table.kernel.getAlgebraProcessor
     * ().processAlgebraCommandNoExceptionHandling(text, false);
     * 
     * // set label P_1, P_2, etc. String pointName =
     * geos[0].getIndexLabel("P"); geos[0].setLabel(pointName);
     * 
     * list.addLast(pointName); } } }
     */

    // create points
    if (values.length > 0)
      for (int i = 0; i < values.length; ++i) {
        if (values[i].length != 2)
          continue;
        final GeoElement v1 = values[i][0];
        final GeoElement v2 = values[i][1];
        if (v1 != null && v2 != null && v1.isGeoNumeric() && v2.isGeoNumeric()) {
          final String text = "(" + v1.getLabel() + "," + v2.getLabel() + ")";
          final GeoElement[] geos = table.kernel.getAlgebraProcessor()
              .processAlgebraCommandNoExceptionHandling(text, false);

          // set label P_1, P_2, etc.
          final String pointName = geos[0].getIndexLabel("P");
          geos[0].setLabel(pointName);

          list.addLast(geos[0].getLabel());
        }
      }

    // create list of points
    if (list.size() > 0) {
      final String[] points = list.toArray(new String[0]);
      String text = "{";
      for (int i = 0; i < points.length; ++i) {
        text += points[i];
        if (i != points.length - 1)
          text += ",";
      }
      text += "}";

      final GeoElement[] geos = table.kernel.getAlgebraProcessor()
          .processAlgebraCommandNoExceptionHandling(text, false);

      // set list name
      final String listName = geos[0].getIndexLabel("L");
      geos[0].setLabel(listName);
    }
  }

  public boolean cut(final int column1, final int row1, final int column2,
      final int row2) {

    copy(column1, row1, column2, row2, false);
    externalBuf = null;
    return delete(column1, row1, column2, row2);
  }

  public boolean delete(final int column1, final int row1, final int column2,
      final int row2) {
    boolean succ = false;
    for (int column = column1; column <= column2; ++column)
      // int column3 = table.convertColumnIndexToModel(column);
      for (int row = row1; row <= row2; ++row) {
        final GeoElement value0 = RelativeCopy.getValue(table, column, row);
        if (value0 != null && !value0.isFixed()) {
          // value0.remove();
          value0.removeOrSetUndefinedIfHasFixedDescendent();
          succ = true;
        }
        // try {
        // MyCellEditor.prepareAddingValueToTable(kernel, table, null, value0,
        // column3, row);
        // } catch (Exception e) {
        // Application.debug("spreadsheet.delete: " + e.getMessage());
        // }
      }
    return succ;
  }

  public boolean paste(final int column1, final int row1, final int column2,
      final int row2) {
    final Clipboard clipboard = Toolkit.getDefaultToolkit()
        .getSystemClipboard();
    final Transferable contents = clipboard.getContents(null);
    String buf = null;
    boolean succ = false;

    /*
     * // print available data formats on clipboard StringBuffer sb = new
     * StringBuffer(); for (int i = 0; i <
     * contents.getTransferDataFlavors().length; i++) {
     * sb.append(contents.getTransferDataFlavors()[i]); sb.append("\n"); }
     * Application.debug(sb.toString());
     */

    try {
      final DataFlavor HTMLflavor = new DataFlavor(
          "text/html;class=java.lang.String");
      final String str = (String) contents.getTransferData(HTMLflavor);

      final StringBuffer sbHTML = new StringBuffer();

      // convert HTML table into CSV
      final HTMLEditorKit.ParserCallback callback = new HTMLEditorKit.ParserCallback() {
        boolean foundTable = false;
        boolean firstInRow = true;
        boolean firstColumn = true;
        boolean finished = false;
        @Override
        public void handleStartTag(final HTML.Tag tag,
            final MutableAttributeSet attrSet, final int pos) {
          if (tag == HTML.Tag.TABLE) {
            // Application.debug("table");
            if (foundTable)
              finished = true;
            foundTable = true;
            firstColumn = true;
            sbHTML.setLength(0);
          } else if (foundTable && tag == HTML.Tag.TR) {
            // Application.debug("TR");
            if (!firstColumn)
              sbHTML.append("\n");
            firstInRow = true;
            firstColumn = false;
          } else if (foundTable && (tag == HTML.Tag.TD || tag == HTML.Tag.TH)) {
            // Application.debug("TD");
            if (!firstInRow)
              sbHTML.append(",");
            firstInRow = false;
          } else if (!foundTable) {
            // Application.debug("TR without table");
            sbHTML.setLength(0);
            if (tag == HTML.Tag.TR) {
              foundTable = true; // HTML fragment without <TABLE>
              firstInRow = true;
              firstColumn = false;
            }
          }

        }
        @Override
        public void handleText(final char[] data, final int pos) {

          if (foundTable && !finished) {

            // if string contains a comma, surround the string with quotes ""
            boolean containsComma = false;
            boolean appendQuotes = false;
            for (final char element : data)
              if (element == ',')
                containsComma = true;

            if (containsComma
                && (data[0] != '"' || data[data.length - 1] != '"'))
              appendQuotes = true;

            if (containsComma) {
              boolean isNumber = true;
              int noOfCommas = 0;
              for (final char element : data)
                if (element == ',')
                  noOfCommas++;
                else if (element < '0' || element > '9')
                  isNumber = false;

              // check for European-style decimal comma
              if (isNumber && noOfCommas == 1)
                for (int i = 0; i < data.length; i++)
                  if (data[i] == ',')
                    // Application.debug("replacing , with .");
                    data[i] = '.';
            }

            if (appendQuotes)
              sbHTML.append('"');
            for (final char element : data)
              sbHTML.append(element);
            if (appendQuotes)
              sbHTML.append('"');
          }
          // System.out.println(data);
        }

      };
      final Reader reader = new StringReader(str);
      new ParserDelegator().parse(reader, callback, true);

      if (sbHTML.length() != 0) {
        // found HTML table to paste (as CSV)
        buf = sbHTML.toString();
        Application.debug("pasting from HTML <table>: " + buf);
      }

    } catch (final Exception e) {
      Application.debug("clipboard: no HTML");
    }

    // Application.debug("paste: "+row1+" "+row2+" "+column1+" "+column2);

    // no HTML found, try plain text
    if (buf == null && contents != null
        && contents.isDataFlavorSupported(DataFlavor.stringFlavor))
      try {
        buf = (String) contents.getTransferData(DataFlavor.stringFlavor);
        Application.debug("pasting from String: " + buf);
      } catch (final Exception ex) {
        Application.debug("clipboard: no String");
        // ex.printStackTrace();
        // app.showError(ex.getMessage());
        // Util.handleException(table, ex);
      }

    if (buf != null && externalBuf != null && buf.equals(externalBuf)
        && internalBuf != null) {
      final Construction cons = kernel.getConstruction();
      kernel.getApplication().setWaitCursor();
      try {
        succ = true;
        final int columnStep = internalBuf.length;
        final int rowStep = internalBuf[0].length;

        int maxColumn = column2;
        int maxRow = row2;
        // paste all data if just one cell selected
        // ie overflow selection rectangle
        if (row2 == row1 && column2 == column1) {
          maxColumn = column1 + columnStep;
          maxRow = row1 + rowStep;
        }

        // collect all redefine operations
        cons.startCollectingRedefineCalls();

        // paste data multiple times to fill in the selection rectangle (and
        // maybe overflow a bit)
        for (int c = column1; c <= column2; c += columnStep)
          for (int r = row1; r <= row2; r += rowStep)
            succ = succ && pasteInternal(c, r, maxColumn, maxRow);

        // now do all redefining and build new construction
        cons.processCollectedRedefineCalls();

      } catch (final Exception ex) {
        ex.printStackTrace(System.out);
        app.showError(ex.getMessage());

        // for (int c = column1 ; c <= column2 ; c++)
        // for (int r = row1 ; r <= row2 ; r++)
        // pasteExternal(buf, c, r);

        // paste data multiple times to fill in the selection rectangle (and
        // maybe overflow a bit)
        succ = pasteExternalMultiple(buf, column1, row1, column2, row2);

        // Util.handleException(table, ex);
      } finally {
        cons.stopCollectingRedefineCalls();
        kernel.getApplication().setDefaultCursor();
      }
    } else if (buf != null)
      // paste data multiple times to fill in the selection rectangle (and maybe
      // overflow a bit)
      succ = pasteExternalMultiple(buf, column1, row1, column2, row2);

    return succ;
  }
  public boolean pasteExternal(final String[][] data, final int column1,
      final int row1, final int maxColumn, final int maxRow) {
    app.setWaitCursor();
    boolean succ = false;

    try {
      final DefaultTableModel model = (DefaultTableModel) table.getModel();
      if (model.getRowCount() < row1 + data.length)
        model.setRowCount(row1 + data.length);
      final GeoElement[][] values2 = new GeoElement[data.length][];
      int maxLen = -1;
      for (int row = row1; row < row1 + data.length; ++row) {
        if (row < 0 || row > maxRow)
          continue;
        final int iy = row - row1;
        values2[iy] = new GeoElement[data[iy].length];
        if (maxLen < data[iy].length)
          maxLen = data[iy].length;
        if (model.getColumnCount() < column1 + data[iy].length)
          table.setMyColumnCount(column1 + data[iy].length);
        for (int column = column1; column < column1 + data[iy].length; ++column) {
          if (column < 0 || column > maxColumn)
            continue;
          final int ix = column - column1;
          // Application.debug(iy + " " + ix + " [" + data[iy][ix] + "]");
          data[iy][ix] = data[iy][ix].trim();
          if (data[iy][ix].length() == 0) {
            final GeoElement value0 = RelativeCopy.getValue(table, column, row);
            if (value0 != null)
              // Application.debug(value0.toValueString());
              // MyCellEditor.prepareAddingValueToTable(kernel, table, null,
              // value0, column, row);
              // value0.remove();
              value0.removeOrSetUndefinedIfHasFixedDescendent();
          } else {
            final GeoElement value0 = RelativeCopy.getValue(table, column, row);
            values2[iy][ix] = MyCellEditor
                .prepareAddingValueToTableNoStoringUndoInfo(kernel, table,
                    data[iy][ix], value0, column, row);
            values2[iy][ix].setAuxiliaryObject(values2[iy][ix].isGeoNumeric());
            table.setValueAt(values2[iy][ix], row, column);
          }
        }
      }
      // Application.debug("maxLen=" + maxLen);
      table.getView().repaintView();

      /*
       * if (values2.length == 1 || maxLen == 1) {
       * createPointsAndAList1(values2); } if (values2.length == 2 || maxLen ==
       * 2) { createPointsAndAList2(values2); }
       */

      succ = true;
    } catch (final Exception ex) {
      // app.showError(ex.getMessage());
      // Util.handleException(table, ex);
      ex.printStackTrace();
    } finally {
      app.setDefaultCursor();
    }

    return succ;
  }

  private boolean pasteExternalMultiple(final String buf, final int column1,
      final int row1, final int column2, final int row2) {
    /*
     * int newlineIndex = buf.indexOf("\n"); int rowStep = 1; if ( newlineIndex
     * == -1 || newlineIndex == buf.length()-1) { rowStep = 1; // no linefeeds
     * in string } else { for (int i = 0; i < buf.length()-1 ; i++) { // -1 :
     * don't want to count a newline if it's the last char char c =
     * buf.charAt(i); if (c == '\n') rowStep++; // count no of linefeeds in
     * string } }
     */
    boolean succ = true;
    final String[][] data = parseData(buf);
    final int rowStep = data.length;
    final int columnStep = data[0].length;

    int maxColumn = column2;
    int maxRow = row2;
    // paste all data if just one cell selected
    // ie overflow selection rectangle
    if (row2 == row1 && column2 == column1) {
      maxColumn = column1 + columnStep;
      maxRow = row1 + rowStep;
    }

    // paste data multiple times to fill in the selection rectangle (and maybe
    // overflow a bit)
    for (int c = column1; c <= column2; c += columnStep)
      for (int r = row1; r <= row2; r += rowStep)
        succ = succ && pasteExternal(data, c, r, maxColumn, maxRow);

    return succ;

  }
  public boolean pasteInternal(final int column1, final int row1,
      final int maxColumn, final int maxRow) throws Exception {
    final int width = internalBuf.length;
    if (width == 0)
      return false;
    final int height = internalBuf[0].length;
    if (height == 0)
      return false;

    app.setWaitCursor();
    boolean succ = false;

    // Application.debug("height = " + height+" width = "+width);
    final int x1 = bufColumn;
    final int y1 = bufRow;
    final int x2 = bufColumn + width - 1;
    final int y2 = bufRow + height - 1;
    final int x3 = column1;
    final int y3 = row1;
    final int x4 = column1 + width - 1;
    final int y4 = row1 + height - 1;
    final GeoElement[][] values2 = RelativeCopy
        .getValues(table, x3, y3, x4, y4);
    /*
     * for (int i = 0; i < values2.length; ++ i) { for (int j = 0; j <
     * values2[i].length; ++ j) { if (values2[i][j] != null) {
     * values2[i][j].remove(); values2[i][j] = null; } } } /*
     */

    final int size = (x2 - x1 + 1) * (y2 - y1 + 1);
    if (constructionIndexes == null || constructionIndexes.length < size)
      constructionIndexes = new Object[size];

    int count = 0;

    final DefaultTableModel model = (DefaultTableModel) table.getModel();
    if (model.getRowCount() < y4 + 1)
      model.setRowCount(y4 + 1);
    if (model.getColumnCount() < x4 + 1)
      table.setMyColumnCount(x4 + 1);
    final GeoElement[][] values1 = internalBuf;// RelativeCopy.getValues(table,
    // x1, y1, x2, y2);
    try {
      for (int x = x1; x <= x2; ++x) {
        final int ix = x - x1;
        for (int y = y1; y <= y2; ++y) {
          final int iy = y - y1;
          if (ix + column1 <= maxColumn && iy + row1 <= maxRow)
            if (values1[ix][iy] != null) {

              // just record the coordinates for pasting
              constructionIndexes[count] = new Record(values1[ix][iy]
                  .getConstructionIndex(), ix, iy, x3 - x1, y3 - y1);
              count++;
            }
          // values2[ix][iy] = RelativeCopy.doCopyNoStoringUndoInfo0(kernel,
          // table, values1[ix][iy], values2[ix][iy], x3 - x1, y3 - y1);
          // if (values1[ix][iy] != null && values2[ix][iy] != null)
          // values2[ix][iy].setAllVisualProperties(values1[ix][iy]);
        }
      }

      // sort according to the construction index
      // so that objects are pasted in the correct order
      Arrays.sort(constructionIndexes, 0, count, getComparator());

      // do the pasting
      for (int i = 0; i < count; i++) {
        final Record r = (Record) constructionIndexes[i];
        final int ix = r.getx1();
        final int iy = r.gety1();
        values2[ix][iy] = RelativeCopy.doCopyNoStoringUndoInfo0(kernel, table,
            values1[ix][iy], values2[ix][iy], r.getx2(), r.gety2());

      }

      succ = true;
    } catch (final Exception e) {
      e.printStackTrace();
    } finally {
      app.setDefaultCursor();
    }

    return succ;
  }

}
