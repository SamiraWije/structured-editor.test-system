package geogebra.main;

import geogebra.io.layout.Perspective;
import geogebra.kernel.*;
import geogebra.kernel.arithmetic.NumberValue;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.text.JTextComponent;

/**
 * Handles all geogebra.gui package related objects and methods for Application.
 * This is done to be able to put class files of geogebra.gui.* packages into a
 * separate gui jar file.
 */
public interface GuiManager {
  public void addToToolbarDefinition(int mode);

  public void allowGUIToRefresh();
  public void attachAlgebraView();
  public void attachSpreadsheetView();
  public void attachView(int viewId);
  public void clearPreferences();
  public void closeOpenDialogs();
  public JFrame createFrame();

  public JDialog createTextDialog(GeoText text, GeoPoint startPoint);
  public void detachAlgebraView();

  public void detachSpreadsheetView();
  public void detachView(int viewId);

  public void doAfterRedefine(GeoElement geo);
  public void doOpenFiles(File[] files, boolean allowOpeningInThisInstance);
  public void doOpenFiles(File[] files, boolean allowOpeningInThisInstance,
      String extension);
  public void exitAll();
  /**
   * Exports construction protocol as html
   */
  public void exportConstructionProtocolHTML();
  public JComponent getAlgebraInput();

  public int getAlgebraInputHeight();
  public JTextComponent getAlgebraInputTextField();
  public JComponent getAlgebraView();
  public String getConsProtocolXML();

  public JDialog getConstructionProtocol();
  public JComponent getConstructionProtocolNavigation();
  /**
   * Returns text "Created with <ApplicationName>" and link to application
   * homepage in html.
   */
  public String getCreatedWithHTML();
  public String getCustomToolbarDefinition();

  public String getDefaultToolbarString();
  public int getHighestUsedSpreadsheetColumn();

  /**
   * gets an image from the clipboard Then the image file is loaded and stored
   * in this application's imageManager. Michael Borcherds 2008-05-10
   * 
   * @return fileName of image stored in imageManager
   */
  public String getImageFromClipboard();
  /**
   * Shows a file open dialog to choose an image file, Then the image file is
   * loaded and stored in this application's imageManager.
   * 
   * @return fileName of image stored in imageManager
   */
  public String getImageFromFile();

  public JComponent getLayoutRoot();

  // Layout methods - Florian Sonner 2008-11-02
  public String getLayoutXml(boolean isPreference);
  public JMenuBar getMenuBar();
  public int getMenuBarHeight();

  public AbstractAction getRedoAction();

  public AbstractAction getShowAxesAction();
  public AbstractAction getShowGridAction();

  public int getSpreadsheetTraceRow(int traceColumn);

  public JComponent getSpreadsheetView();

  public String getSpreadsheetViewXML();
  /**
   * gets String from clipboard Michael Borcherds 2008-04-09
   */
  public String getStringFromClipboard();

  public String getToolBarDefinition();
  public int getToolBarHeight();
  public JComponent getToolbarPanel();

  public AbstractAction getUndoAction();

  public boolean hasSpreadsheetView();

  public void initFileChooser();
  public void initialize();

  public void initMenubar();

  public void initPropertiesDialog();

  public boolean isConsProtNavigationPlayButtonVisible();

  public boolean isConsProtNavigationProtButtonVisible();
  public boolean isInputFieldSelectionListener();
  public boolean isPropertiesDialogSelectionListener();

  public boolean isUsingConstructionProtocol();

  public boolean loadFile(final File file, boolean isMacroFile);

  /**
   * Creates a new image at the given location (real world coords).
   * 
   * @return whether a new image was create or not
   */
  public boolean loadImage(GeoPoint loc, boolean fromClipboard);
  public void openFile();

  public void openHelp(String command);
  public void redo();
  public void removeFromToolbarDefinition(int mode);
  public void resetSpreadsheet();
  public boolean save();
  public boolean saveAs();

  // returns true for YES or NO and false for CANCEL
  public boolean saveCurrentFile();

  public void setColumnWidth(int column, int width);

  public void setConstructionStep(int step);

  public void setLabels();

  public void setMenubar(JMenuBar newMenuBar);

  public void setMode(int mode);

  public void setPerspectives(ArrayList<Perspective> perspectives);

  public void setScrollToShow(boolean scrollToShow);

  public void setShowAlgebraView(boolean flag);

  public void setShowAuxiliaryObjects(boolean flag);

  public void setShowCASView(boolean flag);

  public void setShowConstructionProtocolNavigation(boolean show);
  public void setShowConstructionProtocolNavigation(boolean show,
      boolean playButton, double playDelay, boolean protButton);

  public void setShowEuclidianView(boolean flag);

  public void setShowSpreadsheetView(boolean flag);

  public void setShowToolBarHelp(boolean flag);

  public void setToolBarDefinition(String toolBarDefinition);

  public void setToolbarMode(int mode);

  public void showAboutDialog();

  public boolean showAlgebraView();
  /**
   * Shows a modal dialog to enter an angle or angle variable name.
   * 
   * @return: Object[] with { NumberValue, AngleInputDialog } pair
   */
  public Object[] showAngleInputDialog(String title, String message,
      String initText);

  /**
   * Creates a new text at given startPoint
   */
  public void showBooleanCheckboxCreationDialog(Point loc, GeoBoolean bool);
  public boolean showCASView();

  public Color showColorChooser(Color currentColor);

  /**
   * Displays the construction protocol dialog
   */
  public void showConstructionProtocol();

  public void showDrawingPadPopup(Component invoker, Point p);

  public boolean showEuclidianView();

  /**
   * Creates a new slider at given location (screen coords).
   * 
   * @return whether a new slider (number) was create or not
   */
  public boolean showJavaScriptButtonCreationDialog(int x, int y);

  /**
   * Shows a modal dialog to enter a number or number variable name.
   */
  public NumberValue showNumberInputDialog(String title, String message,
      String initText);

  /**
   * Shows the options dialog.
   */
  public void showOptionsDialog(boolean showEuclidianTab);
  /**
   * Displays the popup menu for geo at the position p in the coordinate space
   * of the component invoker
   */
  public void showPopupMenu(GeoElement geo, Component invoker, Point p);

  public void showPrintPreview();

  public void showPropertiesDialog();

  /**
   * Displays the porperties dialog for geos
   */
  public void showPropertiesDialog(ArrayList geos);

  /**
   * Displays the redefine dialog for geo
   * 
   * @param allowTextDialog
   *          : whether text dialog should be used for texts
   */
  public void showRedefineDialog(GeoElement geo, boolean allowTextDialog);

  /**
   * Displays the rename dialog for geo
   */
  public void showRenameDialog(GeoElement geo, boolean storeUndo,
      String initText, boolean selectInitText);

  public File showSaveDialog(String fileExtension, File selectedFile,
      String fileDescription);
  public File showSaveDialog(String[] fileExtensions, File selectedFile,
      String[] fileDescriptions);

  /**
   * Creates a new slider at given location (screen coords).
   * 
   * @return whether a new slider (number) was create or not
   */
  public boolean showSliderCreationDialog(int x, int y);

  public boolean showSpreadsheetView();

  /**
   * Creates a new text at given startPoint
   */
  public void showTextCreationDialog(GeoPoint startPoint);

  /**
   * Displays the text dialog for a given text.
   */
  public void showTextDialog(GeoText text);

  /**
   * Displays the configuration dialog for the toolbar
   */
  public void showToolbarConfigDialog();

  public void showURLinBrowser(String strURL);

  public void showURLinBrowser(URL url);

  public void startCollectingSpreadsheetTraces();

  public void startEditing(GeoElement geo);

  public void stopCollectingSpreadsheetTraces();

  public void traceToSpreadsheet(GeoElement p);

  public void undo();
  public void updateActions();

  public void updateAlgebraInput();

  public void updateConstructionProtocol();

  public void updateFonts();

  public void updateFrameSize();

  public void updateFrameTitle();

  public void updateLayout();

  public void updateMenubar();

  public void updateMenubarSelection();
  public void updateMenuFile();
  public void updateMenuWindow();
  public void updateSpreadsheetColumnWidths();
  public void updateToolbar();

}
