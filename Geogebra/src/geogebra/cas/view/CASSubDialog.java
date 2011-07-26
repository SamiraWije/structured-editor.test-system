package geogebra.cas.view;

import geogebra.Plain;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * Dialog to substitude a string in a CAS input.
 */
public class CASSubDialog extends JDialog implements ActionListener {

  private static final long serialVersionUID = 1L;

  private JButton btSub, btEval, btCancel;
  private JPanel optionPane, btPanel, captionPanel;
  private JTextField valueTextField;

  private final CASView casView;
  private final Application app;
  private final int editRow;
  private final String evalText;
  private JTextField subStrfield;

  /**
   * Substitute dialog for CAS.
   */
  protected CASSubDialog(CASView casView, String prefix, String evalText,
      String postfix, int editRow) {
    setModal(false);

    this.casView = casView;
    app = casView.getApp();
    this.evalText = evalText;
    this.editRow = editRow;

    createGUI();
    pack();
    setLocationRelativeTo(null);
  }

  public void actionPerformed(ActionEvent ae) {
    Object src = ae.getSource();

    if (src == btCancel)
      setVisible(false);
    else if (src == btEval) {
      if (apply(btEval.getActionCommand()))
        setVisible(false);
    } else if (src == btSub)
      if (apply(btSub.getActionCommand()))
        setVisible(false);
  }

  private boolean apply(String actionCommand) {

    CASTable table = casView.getConsoleTable();

    // substitute from
    String fromExp = subStrfield.getText();
    String toExp = valueTextField.getText();
    if (fromExp.length() == 0 || toExp.length() == 0)
      return false;

    // substitute command
    String subCmd = "Substitute[" + evalText + "," + fromExp + ", " + toExp
        + "]";
    if (actionCommand.equals("Eval"))
      subCmd = "Eval[" + subCmd + "]";

    try {
      CASTableCellValue currCell = table.getCASTableCellValue(editRow);
      String result = casView.getCAS().processCASInput(subCmd,
          casView.isUseGeoGebraVariableValues());
      currCell.setOutput(result);
      table.startEditingRow(editRow + 1);
      return true;
    } catch (Throwable e) {
      e.printStackTrace();
      return false;
    }
  }

  private void createGUI() {
    setTitle(Plain.Substitute);
    setResizable(false);

    // create label panel
    JPanel subTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    String temp = app.getPlain("SubstituteForAinB", "ThisIsJustTheSplitString",
        evalText);
    String[] strLabel = temp.split("ThisIsJustTheSplitString");
    JLabel subLabel = new JLabel(strLabel[0]);
    subStrfield = new JTextField(4);
    JLabel subLabel2 = new JLabel(strLabel[1]);
    subTitlePanel.add(subLabel);
    subTitlePanel.add(subStrfield);
    subTitlePanel.add(subLabel2);

    // create caption panel
    JLabel captionLabel = new JLabel(Plain.NewExpression + ":");
    valueTextField = new JTextField();
    valueTextField.setColumns(20);
    captionLabel.setLabelFor(valueTextField);

    JPanel subPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    subPanel.add(captionLabel);
    subPanel.add(valueTextField);

    captionPanel = new JPanel(new BorderLayout(5, 5));
    captionPanel.add(subTitlePanel, BorderLayout.CENTER);
    captionPanel.add(subPanel, BorderLayout.SOUTH);

    // buttons
    btSub = new JButton(Plain.Substitute);
    btSub.setActionCommand("Substitute");
    btSub.addActionListener(this);

    btEval = new JButton("=");
    btEval.setActionCommand("Eval");
    btEval.addActionListener(this);

    btCancel = new JButton(Plain.Cancel);
    btCancel.setActionCommand("Cancel");
    btCancel.addActionListener(this);
    btPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

    btPanel.add(btSub);
    btPanel.add(btEval);
    btPanel.add(btCancel);

    // Create the JOptionPane.
    optionPane = new JPanel(new BorderLayout(5, 5));

    // create object list
    optionPane.add(captionPanel, BorderLayout.NORTH);

    optionPane.add(btPanel, BorderLayout.SOUTH);
    optionPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    // Make this dialog display it.
    setContentPane(optionPane);
  }
}