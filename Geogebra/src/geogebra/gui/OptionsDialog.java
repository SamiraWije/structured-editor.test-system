package geogebra.gui;

import geogebra.Plain;
import geogebra.main.Application;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.plaf.TabbedPaneUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

/**
 * A central dialog with all important options.
 * 
 * @author Florian Sonner
 */
public class OptionsDialog extends JDialog implements WindowListener {
  /**
   * Tabbed pane for the options dialog.
   * 
   * This tabbed pane will just use the special OptionsTabbedPaneUI class for
   * it's UI. It's required to reconstruct the UI object if updateUI() is called
   * (e.g. because the font size changed).
   * 
   * @author Florian Sonner
   */
  class OptionsTabbedPane extends JTabbedPane {
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Set the UI of this component to the OptionsTabbedPaneUI.
     */
    public OptionsTabbedPane() {
      setUI(new OptionsTabbedPaneUI());
    }

    /**
     * Ignore any non OptionsTabbedPaneUI objects.
     */
    @Override
    public void setUI(TabbedPaneUI ui) {
      if (ui instanceof OptionsTabbedPaneUI)
        super.setUI(ui);
    }

    /**
     * Update the UI of this component.
     * 
     * This will lead to an update of the fonts of the UI as just the font size
     * should change.
     */
    @Override
    public void updateUI() {
      if (ui instanceof OptionsTabbedPaneUI)
        ((OptionsTabbedPaneUI) getUI()).updateFont();
    }
  }

  /**
   * Custom UI for the tabs in the options dialog.
   * 
   * @author Florian Sonner
   */
  class OptionsTabbedPaneUI extends BasicTabbedPaneUI {
    /**
     * The background color for tabs which are neither active not hovered.
     */
    private Color bgColor;

    /**
     * The background color of active tabs (i.e. the content of this tab is
     * currently displayed).
     */
    private Color bgActiveColor;

    /**
     * The background color of tabs the mouse is over at the moment. Will not
     * apply to active tabs.
     */
    private Color bgHoverColor;

    /**
     * The tab should always have enough space for a 32x32 icon and the label.
     */
    @Override
    protected int calculateTabHeight(int tabPlacement, int tabIndex,
        int fontHeight) {
      if (!tabbedPane.isEnabledAt(tabIndex))
        return 0;

      return fontHeight + 45;
    }

    /**
     * Reduce the tab width by 32 as the icon is not drawn in one line with the
     * text.
     */
    @Override
    protected int calculateTabWidth(int tabPlacement, int tabIndex,
        FontMetrics metrics) {
      if (!tabbedPane.isEnabledAt(tabIndex))
        return 0;

      return super.calculateTabWidth(tabPlacement, tabIndex, metrics) - 32;
    }

    /**
     * Do not move the label if we select a tab (always return 0 as shift).
     */
    @Override
    protected int getTabLabelShiftY(int tabPlacement, int tabIndex,
        boolean isSelected) {
      return 0;
    }

    /**
     * Initialization of default values.
     */
    @Override
    protected void installDefaults() {
      super.installDefaults();
      tabAreaInsets = new Insets(0, 15, 0, 15);
      contentBorderInsets = new Insets(3, 3, 3, 3);
      tabInsets = new Insets(10, 10, 10, 10);
      selectedTabPadInsets = new Insets(0, 0, 0, 0);

      bgColor = Color.white;
      bgActiveColor = new Color(193, 210, 238);
      bgHoverColor = new Color(224, 232, 246);
    }

    /**
     * Use a custom layout for the label (icon centered, text below icon).
     * 
     * Copy 'n' paste from the original source of BasicTabbedPaneUI.
     */
    @Override
    protected void layoutLabel(int tabPlacement, FontMetrics metrics,
        int tabIndex, String title, Icon icon, Rectangle tabRect,
        Rectangle iconRect, Rectangle textRect, boolean isSelected) {
      textRect.x = 0;
      textRect.y = 0;
      textRect.width = 0;
      textRect.height = 0;
      iconRect.x = 0;
      iconRect.y = 0;
      iconRect.width = 0;
      iconRect.height = 0;

      // -- just this has to be changed to change the layout of the tabs --
      SwingUtilities.layoutCompoundLabel(tabPane, metrics, title, icon,
          SwingConstants.CENTER, SwingConstants.CENTER, SwingConstants.BOTTOM,
          SwingConstants.CENTER, tabRect, iconRect, textRect, textIconGap);

      int shiftX = getTabLabelShiftX(tabPlacement, tabIndex, isSelected);
      int shiftY = getTabLabelShiftY(tabPlacement, tabIndex, isSelected);

      iconRect.x += shiftX;
      iconRect.y += shiftY;

      textRect.x += shiftX;
      textRect.y += shiftY;
    }

    @Override
    protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement,
        int selectedIndex, int x, int y, int w, int h) {
      /* paint nothing */
    }

    @Override
    protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement,
        int selectedIndex, int x, int y, int w, int h) {
      /* paint nothing */
    }

    @Override
    protected void paintContentBorderRightEdge(Graphics g, int tabPlacement,
        int selectedIndex, int x, int y, int w, int h) {
      /* paint nothing */
    }

    /**
     * Paint the top border.
     */
    @Override
    protected void paintContentBorderTopEdge(Graphics g, int tabPlacement,
        int selectedIndex, int x, int y, int w, int h) {
      g.setColor(SystemColor.controlDkShadow);
      g.drawLine(x, y, x + w, y);
      g.setColor(SystemColor.controlLtHighlight);
      g.drawLine(x, y + 1, x + w, y + 1);
    }

    @Override
    protected void paintFocusIndicator(Graphics g, int tabPlacement,
        Rectangle[] rects, int tabIndex, Rectangle iconRect,
        Rectangle textRect, boolean isSelected) {
      /* paint nothing.. */
    }

    /**
     * Fill the background with white.
     */
    @Override
    protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {
      g.setColor(Color.white);

      g.fillRect(0, 0, tabPane.getBounds().width, calculateTabAreaHeight(
          tabPlacement, runCount, maxTabHeight));

      super.paintTabArea(g, tabPlacement, selectedIndex);
    }

    /**
     * Paint the background of the tabs.
     */
    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement,
        int tabIndex, int x, int y, int w, int h, boolean isSelected) {
      g.setColor(isSelected ? bgActiveColor : tabIndex == getRolloverTab()
          ? bgHoverColor
          : bgColor);
      g.fillRect(x, y, w, h);
    }

    /**
     * Paint the tab border.
     */
    @Override
    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
        int x, int y, int w, int h, boolean isSelected) {
      g.setColor(SystemColor.controlShadow);
      g.drawLine(x, y, x, y + h - 1);
      g.drawLine(x + w, y, x + w, y + h - 1);
    }

    /**
     * Repaint the tabbed pane if the mouse is hovering a new tab.
     */
    @Override
    protected void setRolloverTab(int index) {
      if (getRolloverTab() != index) {
        super.setRolloverTab(index);
        repaint();
      }
    }

    /**
     * Uninstall our custom defaults.
     */
    @Override
    protected void uninstallDefaults() {
      super.uninstallDefaults();

      bgColor = null;
      bgActiveColor = null;
      bgHoverColor = null;
    }

    /**
     * Update the font.
     */
    protected void updateFont() {
      LookAndFeel.installColorsAndFont(tabPane, "TabbedPane.background",
          "TabbedPane.foreground", "TabbedPane.font");
    }
  }

  /** */
  private static final long serialVersionUID = 1L;

  /**
   * An instance of the Application object of this window.
   */
  private final Application app;

  /**
   * The tabbed pane which is used to switch between the different pages of the
   * options menu.
   */
  private JTabbedPane tabbedPane;

  /**
   * The panel with general options.
   */
  private OptionsGeneral generalPanel;

  /**
   * The panel where the user can select new default values for certain objects.
   */
  private OptionsDefaults defaultsPanel;

  /**
   * The panel with all settings regarding font sizes & the current language.
   */
  private OptionsFont fontPanel;

  /**
   * The panel with all settings for the euclidian view. The
   * "Drawing Pad Properties" dialog is not longer used, all settings are stored
   * here for now.
   */
  private OptionsEuclidian euclidianPanel;

  /**
   * The button to apply settings without closing the window.
   */
  private JButton applyButton;

  /**
   * The button which closes the window and stores all changes.
   */
  private JButton closeButton;

  /**
   * Initialize the GUI and logics.
   * 
   * @param app
   */
  protected OptionsDialog(Application app) {
    super(app.getFrame(), true);

    this.app = app;

    setResizable(true);
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

    addWindowListener(this);

    initGUI();
    updateGUI();
  }

  /**
   * Apply settings which are not applied directly after changing a value.
   * 
   * TODO Save permanent settings
   */
  private void apply() {
    generalPanel.apply();
    fontPanel.apply();
    defaultsPanel.apply();
  }

  /**
   * Close the dialog.
   */
  private void closeDialog() {
    apply();

    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    app.storeUndoInfo();
    setCursor(Cursor.getDefaultCursor());
    setVisible(false);
  }

  /**
   * Initialize the GUI.
   */
  private void initGUI() {
    setLayout(new BorderLayout());

    // init tabs
    generalPanel = new OptionsGeneral(app);
    defaultsPanel = new OptionsDefaults(app);
    fontPanel = new OptionsFont(app);
    euclidianPanel = new OptionsEuclidian(app, app.getEuclidianView());

    // init scroll panes for tabs (show no extra borders)
    JScrollPane fontsAndLangPanelScroll = new JScrollPane(fontPanel);
    fontsAndLangPanelScroll.setBorder(BorderFactory.createEmptyBorder());
    JScrollPane euclidianPanelScroll = new JScrollPane(euclidianPanel);
    euclidianPanelScroll.setBorder(BorderFactory.createEmptyBorder());

    // init tabbing pane
    tabbedPane = new OptionsTabbedPane();

    // general
    tabbedPane.addTab("", app.getToolBarImage("mode_delete_32.gif", Color.RED),
        generalPanel);

    // defaults
    tabbedPane.addTab("", app.getToolBarImage("mode_delete_32.gif", Color.RED),
        defaultsPanel);

    // fonts & language
    tabbedPane.addTab("", app.getToolBarImage("mode_delete_32.gif", Color.RED),
        fontsAndLangPanelScroll);

    // euclidian properties
    tabbedPane.addTab("", app.getToolBarImage("mode_delete_32.gif", Color.RED),
        euclidianPanelScroll);

    // disable some tabs for applets
    if (app.isApplet()) {
      tabbedPane.setEnabledAt(1, false); // general
      tabbedPane.setEnabledAt(2, false); // default values

      // TODO: hide euclidian options in applets in certain cases
    }

    add(tabbedPane, BorderLayout.CENTER);

    // init close button
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
        SystemColor.controlDkShadow));
    buttonPanel.setBackground(Color.white);

    applyButton = new JButton();
    applyButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        apply();
      }
    });
    buttonPanel.add(applyButton);
    closeButton = new JButton();
    closeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        closeDialog();
      }
    });
    buttonPanel.add(closeButton);

    add(buttonPanel, BorderLayout.SOUTH);

    setLabels(); // update all labels

    setPreferredSize(new Dimension(640, 480));
    pack();

    setLocationRelativeTo(null);
  }

  /**
   * Update the labels of the components (e.g. if the language changed).
   * 
   * TODO use real phrases (F.S.)
   */
  protected void setLabels() {
    setTitle(geogebra.Menu.Options);

    closeButton.setText(geogebra.Menu.Close);
    applyButton.setText(Plain.Apply);

    tabbedPane.setTitleAt(0, geogebra.Menu.General);
    tabbedPane.setTitleAt(1, Plain.Defaults);
    tabbedPane.setTitleAt(2, Plain.FontsAndLanguage);
    tabbedPane.setTitleAt(3, Plain.DrawingPad);

    generalPanel.setLabels();
    euclidianPanel.setLabels();
    fontPanel.setLabels();
    defaultsPanel.setLabels();
  }

  /**
   * Select the tab which shows the euclidian view settings.
   */
  protected void showEuclidianTab() {
    tabbedPane.setSelectedIndex(3);
  }

  /**
   * Update the GUI.
   */
  protected void updateGUI() {
    generalPanel.updateGUI();
    defaultsPanel.updateGUI();
    fontPanel.updateGUI();
    euclidianPanel.updateGUI();
  }

  public void windowActivated(WindowEvent e) {
  }

  public void windowClosed(WindowEvent e) {
  }

  /**
   * Simulate the pressing of the close button if the window is closed.
   */
  public void windowClosing(WindowEvent e) {
    closeButton.doClick();
  }

  public void windowDeactivated(WindowEvent e) {
  }

  public void windowDeiconified(WindowEvent e) {
  }

  public void windowIconified(WindowEvent e) {
  }

  public void windowOpened(WindowEvent e) {
  }
}
