package geogebra.cas.view.components;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;

/**
 * TestFrame_TMP
 * 
 * <pre>
 * For testing of menues, buttons and buttonmodel in GeoGebra CAS
 * Temporary; to be erased before final release
 * 
 * &#064;author      Hans-Petter Ulven
 * @version     01.03.09
 */
public class TestFrame_TMP extends JFrame {

  /**
   * 
   */
  private static final long serialVersionUID = 4621859929933129186L;

  private final static boolean DEBUG = true;

  // / --- Properties --- ///

  // main til uttesting:
  public static void main(String[] args) {
    new TestFrame_TMP();
  }// main()

  // / --- Interface --- ///

  Container cp = null; // Panel inne i JFrame

  /** Constructor */
  public TestFrame_TMP() {
    cp = getContentPane();
    cp.setLayout(new BorderLayout());

    setTitle("TestFrame_TMP");
    setSize(500, 300);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    gui(); // Ordner GUI...
    setVisible(true);
    toFront();
    requestFocus();
  }// Constructor

  // / --- Private: --- ///
  private void gui() {
    BtnPanel btnpanel = BtnPanel.getInstance(null);
    cp.add(btnpanel, BorderLayout.NORTH);
  }// gui()

}// class TestFrame_TMP