/**
 * This panel is for the input.
 */

package geogebra.cas.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

/**
 *
 */
class CASInputPanel extends JPanel {

  /** 
   *   
   */
  private static final long serialVersionUID = 1L;
  private final JTextField inputArea;

  public CASInputPanel() {
    setBackground(Color.white);
    setLayout(new BorderLayout(0, 0));

    inputArea = new JTextField(20);
    inputArea.setBorder(BorderFactory.createEmptyBorder());
    add(inputArea, BorderLayout.CENTER);
  }

  public String getInput() {
    return inputArea.getText();
  }

  public JTextComponent getInputArea() {
    return inputArea;
  }

  @Override
  final public void setFont(final Font ft) {
    super.setFont(ft);
    if (inputArea != null)
      inputArea.setFont(ft);
  }

  public void setInput(final String inValue) {
    inputArea.setText(inValue);
  }

  public void setInputAreaFocused() {
    inputArea.requestFocus();
    final String text = inputArea.getText();
    if (text != null)
      inputArea.setCaretPosition(text.length());
  }

}
