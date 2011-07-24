package geogebra.gui;

import geogebra.Plain;
import geogebra.main.Application;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * General options.
 * 
 * @author Florian Sonner
 */
public class OptionsGeneral extends JPanel {
  /** */
  private static final long serialVersionUID = 1L;

  /**
   * An instance of the GeoGebra application.
   */
  private final Application app;

  /**
   * The tabbed pane which contains the single areas which can be edited using
   * this panel.
   */
  private JTabbedPane tabbedPane;

  /**
   * Construct a panel for the general options which is divided using tabs.
   * 
   * @param app
   */
  public OptionsGeneral(Application app) {
    this.app = app;

    initGUI();
    updateGUI();
  }

  /**
   * Save the settings of this panel.
   */
  public void apply() {

  }

  /**
   * Initialize the GUI.
   */
  private void initGUI() {
    tabbedPane = new JTabbedPane();
    tabbedPane.addTab("", new JPanel());
    tabbedPane.addTab("", new JPanel());
    tabbedPane.addTab("", new JPanel());
    tabbedPane.addTab("", new JPanel());

    setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    setLayout(new BorderLayout());
    add(tabbedPane, BorderLayout.CENTER);
  }

  /**
   * Update the labels of the current panel. Should be applied if the language
   * was changed. Will be called after initialization automatically.
   */
  public void setLabels() {
    tabbedPane.setTitleAt(0, geogebra.Menu.General);
    tabbedPane.setTitleAt(1, Plain.Display);
    tabbedPane.setTitleAt(2, Plain.Spreadsheet);
    tabbedPane.setTitleAt(3, geogebra.Menu.Export);
  }

  /**
   * Update the GUI to take care of new settings which were applied.
   */
  public void updateGUI() {
    // TODO Hide tabs for applets (F.S.)
  }
}
