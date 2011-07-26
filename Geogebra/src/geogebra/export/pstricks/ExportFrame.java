package geogebra.export.pstricks;
import geogebra.Plain;
import geogebra.main.Application;

import java.awt.event.*;
import java.io.*;

import javax.swing.*;

abstract public class ExportFrame extends JFrame {
  class ListenKey extends KeyAdapter {
    ExportFrame ef;
    ListenKey(ExportFrame ef) {
      this.ef = ef;
    }
    @Override
    public void keyReleased(KeyEvent e) {
      String cmd = e.getSource().toString();
      if (cmd.equals(TEXT_XUNIT))
        try {
          double value = textXUnit.getValue();
          ggb.setXunit(value);
          textwidth.setValue(value * width);
        } catch (NumberFormatException e1) {
        }
      else if (cmd.equals(TEXT_YUNIT))
        try {
          double value = textYUnit.getValue();
          ggb.setYunit(value);
          textheight.setValue(value * height);
        } catch (NumberFormatException e1) {
        }
      else if (cmd.equals(TEXT_WIDTH))
        try {
          double value = textwidth.getValue() / width;
          ggb.setXunit(value);
          textXUnit.setValue(value);
        } catch (NumberFormatException e1) {
        }
      else if (cmd.equals(TEXT_HEIGHT))
        try {
          double value = textheight.getValue() / height;
          ggb.setYunit(value);
          textYUnit.setValue(value);
        } catch (NumberFormatException e1) {
        }
      else if (cmd.equals(TEXT_XMIN))
        try {
          double xmax = ggb.getXmax();
          double m = textXmin.getValue();
          if (m > xmax) {
            ggb.setXmax(m);
            ggb.setXmin(xmax);
            width = m - xmax;
            int pos = textXmin.getCaretPosition();
            textXmin.setValue(xmax);
            textXmax.setValue(m);
            textXmax.setCaretPosition(pos);
            textXmax.requestFocus();
          } else {
            ggb.setXmin(m);
            width = xmax - m;
          }
          textwidth.setValue(width * ggb.getXunit());
          ggb.refreshSelectionRectangle();
        } catch (NumberFormatException e1) {
        }
      else if (cmd.equals(TEXT_XMAX))
        try {
          double xmin = ggb.getxmin();
          double m = textXmax.getValue();
          if (m < xmin) {
            ggb.setxmin(m);
            ggb.setxmax(xmin);
            width = xmin - m;
            int pos = textXmax.getCaretPosition();
            textXmin.setValue(m);
            textXmax.setValue(xmin);
            textXmin.setCaretPosition(pos);
            textXmin.requestFocus();
          } else {
            ggb.setxmax(m);
            width = m - xmin;
          }
          textwidth.setValue(width * ggb.xunit);
          ggb.refreshSelectionRectangle();
        } catch (NumberFormatException e1) {
        }
      else if (cmd.equals(TEXT_YMIN))
        try {
          double ymax = ggb.getymax();
          double m = textYmin.getValue();
          if (m > ymax) {
            ggb.setymax(m);
            ggb.setymin(ymax);
            height = m - ymax;
            int pos = textYmin.getCaretPosition();
            textYmin.setValue(ymax);
            textYmax.setValue(m);
            textYmax.setCaretPosition(pos);
            textYmax.requestFocus();

          } else {
            ggb.setymin(m);
            height = ymax - m;
          }
          textheight.setValue(height * ggb.yunit);
          ggb.refreshSelectionRectangle();
        } catch (NumberFormatException e1) {
        }
      else if (cmd.equals(TEXT_YMAX))
        try {
          double ymin = ggb.getymin();
          double m = textYmax.getValue();
          if (m < ymin) {
            ggb.setymin(m);
            ggb.setymax(ymin);
            height = ymin - m;
            int pos = textYmax.getCaretPosition();
            textYmin.setValue(m);
            textYmax.setValue(ymin);
            textYmin.setCaretPosition(pos);
            textYmin.requestFocus();
          } else {
            ggb.setymax(m);
            height = m - ymin;
          }
          textheight.setValue(height * ggb.yunit);
          ggb.refreshSelectionRectangle();
        } catch (NumberFormatException e1) {
        }

    }
  }
  private static final long serialVersionUID = 1L;
  private final String TEXT_XUNIT = "textxunit";
  private final String TEXT_YUNIT = "textyunit";
  private final String TEXT_WIDTH = "textwidth";
  private final String TEXT_HEIGHT = "textheight";
  private final String TEXT_XMAX = "textxmax";
  private final String TEXT_XMIN = "textxmin";
  private final String TEXT_YMAX = "textymax";
  private final String TEXT_YMIN = "textymin";
  protected TextValue textXUnit, textYUnit, textwidth, textheight;;
  protected JLabel labelwidth, labelheight, labelXUnit, labelYUnit,
      labelFontSize, labelFormat;
  protected TextValue textXmin, textXmax, textYmin, textYmax;
  protected JLabel labelXmin, labelXmax, labelYmin, labelYmax;
  private final String[] msg = {"10 pt", "11 pt", "12 pt"};
  protected JComboBox comboFontSize, comboFormat;
  protected JPanel panel;
  protected JButton button, button_copy;
  protected JCheckBox jcbPointSymbol, jcbGrayscale;
  protected JScrollPane js;
  protected JTextArea textarea;
  protected Application app;
  protected double width, height;
  protected JButton buttonSave;
  // private ExportFrame ef;
  protected File currentFile = null;
  private final GeoGebraExport ggb;
  private final ListenKey listenKey;
  // definition of the behaviour of the textValues corresponding
  // to xmin, xmax, ymin and ymax.
  // Explaination for xs:
  // if xmin is changed, then both xmin and xmax are changed
  // to be sure that everything is allright even though xmin is set
  // to a higher value than xmax
  // then the width is changed.
  protected ExportFrame(GeoGebraExport ggb, String action) {
    this.ggb = ggb;
    app = ggb.getApp();
    width = ggb.getXmax() - ggb.getXmin();
    height = ggb.getYmax() - ggb.getYmin();
    listenKey = new ListenKey(this);
    textXUnit = new TextValue(this, String.valueOf(ggb.getXunit()), false,
        TEXT_XUNIT);
    textYUnit = new TextValue(this, String.valueOf(ggb.getYunit()), false,
        TEXT_YUNIT);
    textwidth = new TextValue(this, String.valueOf(width), false, TEXT_WIDTH);
    textheight = new TextValue(this, String.valueOf(height), false, TEXT_HEIGHT);
    textXmin = new TextValue(this, String.valueOf(ggb.getXmin()), true,
        TEXT_XMIN);
    textXmax = new TextValue(this, String.valueOf(ggb.getxmax()), true,
        TEXT_XMAX);
    textYmin = new TextValue(this, String.valueOf(ggb.getymin()), true,
        TEXT_YMIN);
    textYmax = new TextValue(this, String.valueOf(ggb.getymax()), true,
        TEXT_YMAX);
    textXUnit.addKeyListener(listenKey);
    textYUnit.addKeyListener(listenKey);
    textXmin.addKeyListener(listenKey);
    textXmax.addKeyListener(listenKey);
    textwidth.addKeyListener(listenKey);
    textheight.addKeyListener(listenKey);
    textYmin.addKeyListener(listenKey);
    textYmax.addKeyListener(listenKey);

    panel = new JPanel();
    button = new JButton(app.getPlain1(action));
    button_copy = new JButton(Plain.CopyToClipboard);
    labelXUnit = new JLabel(Plain.XUnits);
    labelYUnit = new JLabel(Plain.YUnits);
    labelwidth = new JLabel(Plain.PictureWidth);
    labelheight = new JLabel(Plain.PictureHeight);
    labelFontSize = new JLabel(Plain.LatexFontSize);
    labelXmin = new JLabel(Plain.xmin);
    labelXmax = new JLabel(Plain.xmax);
    labelYmin = new JLabel(Plain.ymin);
    labelYmax = new JLabel(Plain.ymax);
    jcbPointSymbol = new JCheckBox(Plain.DisplayPointSymbol);
    jcbGrayscale = new JCheckBox(Plain.PGFExport_Grayscale);
    comboFontSize = new JComboBox(msg);
    jcbPointSymbol.setSelected(true);
    jcbGrayscale.setSelected(false);
    button.addActionListener(ggb);
    button_copy.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        textarea.copy();
      }
    });
    js = new JScrollPane();
    textarea = new JTextArea();
    buttonSave = new JButton(geogebra.Menu.SaveAs);
    buttonSave.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        currentFile = app.getGuiManager().showSaveDialog(
            Application.FILE_EXT_TEX, currentFile,
            "TeX " + geogebra.Menu.Files);
        if (currentFile == null)
          return;
        else
          try {

            FileOutputStream f = new FileOutputStream(currentFile);
            BufferedOutputStream b = new BufferedOutputStream(f);
            /*
             * java.util.Enumeration en=System.getProperties().keys();
             * while(en.hasMoreElements()){ String
             * s=en.nextElement().toString();
             * System.out.println(s+" "+System.getProperty(s)); }
             */
            OutputStreamWriter osw = new OutputStreamWriter(b, "UTF-8");
            StringBuffer sb = new StringBuffer(textarea.getText());
            if (isLaTeX()) {
              int id = sb.indexOf("\\usepackage{");
              if (id != -1)
                sb.insert(id, "\\usepackage[utf8]{inputenc}\n");
            } else if (isConTeXt()) {
              int id = sb.indexOf("\\usemodule[");
              if (id != -1)
                sb.insert(id, "\\enableregime[utf]\n");
            }
            osw.write(sb.toString());
            osw.close();
            b.close();
            f.close();
          } catch (FileNotFoundException e1) {
          } catch (UnsupportedEncodingException e2) {
          } catch (IOException e3) {
          }
      }
    });
  }
  protected void centerOnScreen() {
    // center on screen
    pack();
    setLocationRelativeTo(app.getFrame());
  }
  public boolean getExportPointSymbol() {
    return jcbPointSymbol.isSelected();
  }
  public int getFontSize() {
    switch (comboFontSize.getSelectedIndex()) {
      case 0 :
        return 10;
      case 1 :
        return 11;
      case 2 :
        return 12;
    }
    return 10;
  }
  protected int getFormat() {
    return comboFormat.getSelectedIndex();
  }
  public double getLatexHeight() {
    return textheight.getValue();
  }
  public double getXUnit() {
    double d;
    try {
      d = textXUnit.getValue();
    } catch (NumberFormatException e) {
      d = 1;
    }
    return d;
  }
  public double getYUnit() throws NumberFormatException {
    double d;
    try {
      d = textYUnit.getValue();
    } catch (NumberFormatException e) {
      d = 1;
    }
    return d;
  }
  protected abstract boolean isBeamer();
  protected abstract boolean isConTeXt();
  public boolean isGrayscale() {
    return jcbGrayscale.isSelected();
  }
  protected abstract boolean isLaTeX();

  protected abstract boolean isPlainTeX();
  protected void write(StringBuffer sb) {
    textarea.setText(new String(sb));
    textarea.selectAll();
  }
}