package geogebra.cas.view.components;

import geogebra.main.Application;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

/**
 * Abstract CommandButton_ABS
 * 
 * <pre>
 * Parent for buttons who
 *      -listens to themselves, implementing ActionListener
 *      -implements Command_IF
 *      -should be singletons
 * Saves a long case-statement.
 * Can adjust all visual properties in this class.
 * </pre>
 */
abstract class CommandButton_ABS extends JButton implements ActionListener {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Command_ABS command = null;

  /** Constructor with command setting */

  /** Implementing ActionListener */
  public void actionPerformed(ActionEvent ae) {
    if (command != null)
      command.execute();
    else
      Application.debug("No command installed!");
  }

  /**
   * To set the command for the button.
   * 
   * @param command
   */
  public void setCommand(Command_ABS command) {
    this.command = command;
    addActionListener(this);
  }

}
