package geogebra.cas.view;

import geogebra.main.Application;

import java.awt.*;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 */
class CASOutputPanel extends JPanel {

  /** 
   * 
   */
  private static final long serialVersionUID = 1L;

  public static final int INDENT = 20; // pixel

  private static Color TEXT_COLOR = Color.blue;
  private static Color ERROR_COLOR = Color.red;

  private final JLabel outputSign;
  private final JLabel outputArea;
  private final LaTeXPanel latexPanel;

  public CASOutputPanel(Application app) {
    setBackground(Color.white);
    setLayout(new BorderLayout(5, 0));

    outputSign = new JLabel("\u2192");
    outputSign.setForeground(Color.lightGray);

    outputArea = new JLabel();
    latexPanel = new LaTeXPanel(app);
    latexPanel.setForeground(TEXT_COLOR);
    latexPanel.setBackground(Color.white);

    add(outputSign, BorderLayout.WEST);
    JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    centerPanel.setBackground(Color.white);
    centerPanel.add(outputArea);
    centerPanel.add(latexPanel);
    add(centerPanel, BorderLayout.CENTER);
  }

  public String getOutput() {
    return outputArea.getText();
  }

  @Override
  final public void setFont(Font ft) {
    super.setFont(ft);

    if (latexPanel != null)
      latexPanel.setFont(ft.deriveFont(ft.getSize() + 2f));

    if (outputArea != null)
      outputArea.setFont(ft);
    if (outputSign != null)
      outputSign.setFont(ft);
  }

  public void setOutput(String output, String latexOutput, boolean isError) {
    boolean useLaTeXpanel = latexOutput != null && !isError;
    outputArea.setVisible(!useLaTeXpanel);
    latexPanel.setVisible(useLaTeXpanel);

    if (useLaTeXpanel)
      latexPanel.setLaTeX(latexOutput);
    else {
      outputArea.setText(output);
      if (isError)
        outputArea.setForeground(ERROR_COLOR);
      else
        outputArea.setForeground(TEXT_COLOR);
    }
  }
}