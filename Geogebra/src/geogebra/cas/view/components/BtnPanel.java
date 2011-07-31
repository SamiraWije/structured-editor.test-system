package geogebra.cas.view.components;

import geogebra.cas.view.CASView;

import java.awt.FlowLayout;

import javax.swing.JPanel;

/**
 * JPanel with buttons
 */
class BtnPanel extends JPanel {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private static BtnPanel singleton = null;

  /** Singleton constructor */
  public static BtnPanel getInstance(CASView casview) {
    if (singleton == null)
      singleton = new BtnPanel();
    singleton.setLayout(new FlowLayout(FlowLayout.LEFT));

    // Set up buttons:

    CommandButton_ABS btn;
    btn = CmdBtnEval.getInstance(); // Make button
    btn.setText("=");
    btn.setCommand(CmdEval.getInstance(casview)); // Set command
    singleton.add(btn);

    btn = CmdBtnExpand.getInstance();
    btn.setText("Expand");
    btn.setCommand(CmdExpand.getInstance(casview));
    singleton.add(btn);

    // ...

    return singleton;
  }// getInstance()

  // / --- Interface --- ///
  /** Enforcing singleton */
  private BtnPanel() {
  }
}// class BtnPanel
