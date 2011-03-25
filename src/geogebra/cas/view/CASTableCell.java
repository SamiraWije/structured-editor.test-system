package geogebra.cas.view;

import geogebra.main.Application;

import java.awt.*;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.text.JTextComponent;

/**
 *
 */
abstract class CASTableCell extends JPanel {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  protected CASInputPanel inputPanel;
  protected CASOutputPanel outputPanel;
  private final CASTable consoleTable;
  protected Application app;
  protected CASView view;

  public CASTableCell(CASView view) {
    this.view = view;
    app = view.getApp();
    consoleTable = view.getConsoleTable();

    setLayout(new BorderLayout(5, 5));
    setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
    setBackground(Color.white);

    inputPanel = new CASInputPanel();
    outputPanel = new CASOutputPanel(view.getApp());
    add(inputPanel, BorderLayout.NORTH);
    add(outputPanel, BorderLayout.CENTER);
    return;
  }

  public String getInput() {
    return inputPanel.getInput();
  }

  public JTextComponent getInputArea() {
    return inputPanel.getInputArea();
  }

  public int getInputPanelHeight() {
    return inputPanel.getHeight();
  }

  public String getOutput() {
    return outputPanel.getOutput();
  }

  public int getOutputPanelHeight() {
    return outputPanel.getHeight();
  }

  @Override
  public void setFont(Font ft) {
    super.setFont(ft);
    if (inputPanel != null)
      inputPanel.setFont(ft);
    if (outputPanel != null)
      outputPanel.setFont(ft);
  }

  public void setInputAreaFocused() {
    inputPanel.setInputAreaFocused();
  }

  public void setValue(CASTableCellValue cellValue) {
    inputPanel.setInput(cellValue.getInput());

    // output panel
    if (cellValue.isOutputEmpty())
      outputPanel.setVisible(false);
    else {
      outputPanel.setVisible(true);
      outputPanel.setOutput(cellValue.getOutput(), cellValue.getLaTeXOutput(),
          cellValue.isOutputError());
    }
  }

  void updateTableRowHeight(JTable table, int row) {
    if (isVisible()) {
      Dimension prefSize = getPreferredSize();

      if (prefSize != null) {
        setSize(prefSize);
        if (table.getRowHeight(row) != prefSize.height)
          table.setRowHeight(row, prefSize.height);
      }
    }
  }

}
