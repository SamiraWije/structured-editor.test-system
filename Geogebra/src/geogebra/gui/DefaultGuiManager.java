package geogebra.gui;

import geogebra.GeoGebra;
import geogebra.Menu;
import geogebra.Plain;
import geogebra.euclidian.EuclidianView;
import geogebra.gui.app.GeoGebraFrame;
import geogebra.gui.app.MyFileFilter;
import geogebra.gui.inputbar.AlgebraInput;
import geogebra.gui.layout.Layout;
import geogebra.gui.menubar.GeoGebraMenuBar;
import geogebra.gui.toolbar.MyToolbar;
import geogebra.gui.toolbar.ToolbarConfigDialog;
import geogebra.gui.util.BrowserLauncher;
import geogebra.gui.util.GeoGebraFileChooser;
import geogebra.gui.util.foxtrot.Job;
import geogebra.gui.util.foxtrot.Worker;
import geogebra.gui.view.algebra.AlgebraController;
import geogebra.gui.view.algebra.AlgebraView;
import geogebra.gui.view.consprotocol.ConstructionProtocol;
import geogebra.gui.view.consprotocol.ConstructionProtocolNavigation;
import geogebra.gui.view.spreadsheet.SpreadsheetView;
import geogebra.io.layout.Perspective;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoImage;
import geogebra.kernel.GeoJavaScriptButton;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoText;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;
import geogebra.main.GeoGebraPreferences;
import geogebra.main.GuiManager;
import geogebra.main.MyError;
import geogebra.main.MyResourceBundle;
import geogebra.util.Util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicFileChooserUI;
import javax.swing.text.JTextComponent;

/**
 * Handles all geogebra.gui package related objects and methods for Application.
 * This is done to be able to put class files of geogebra.gui.* packages into a
 * separate gui jar file.
 */
public class DefaultGuiManager implements GuiManager {

  // Added for Intergeo File Format (Yves Kreis) -->
  /*
   * PropertyChangeListener implementation to handle file filter changes
   */
  private class FileFilterChangedListener implements PropertyChangeListener {
    private String getFileName(String fileName) {
      if (fileChooser.getUI() instanceof BasicFileChooserUI) {
        BasicFileChooserUI ui = (BasicFileChooserUI) fileChooser.getUI();
        return ui.getFileName();
      } else if (fileName == null)
        Application.debug("Unknown UI in JFileChooser: "
            + fileChooser.getUI().getClass());
      return fileName;
    }

    private String getFileNameMAC(String fileName) {
      if (fileChooser.getUI() instanceof apple.laf.AquaFileChooserUI) {
        apple.laf.AquaFileChooserUI ui = (apple.laf.AquaFileChooserUI) fileChooser
            .getUI();
        return ui.getFileName();
      } else if (fileChooser.getUI() instanceof apple.laf.CUIAquaFileChooser) {
        apple.laf.CUIAquaFileChooser ui = (apple.laf.CUIAquaFileChooser) fileChooser
            .getUI();
        return ui.getFileName();
      } else if (fileName == null)
        Application.debug("Unknown UI in JFileChooser: "
            + fileChooser.getUI().getClass());
      return fileName;
    }

    public void propertyChange(PropertyChangeEvent evt) {
      if (fileChooser.getFileFilter() instanceof geogebra.gui.app.MyFileFilter) {
        String fileName = null;
        if (fileChooser.getSelectedFile() != null)
          fileName = fileChooser.getSelectedFile().getName();
        if (Application.MAC_OS)
          fileName = getFileNameMAC(fileName);
        else
          fileName = getFileName(fileName);
        if (fileName != null && fileName.indexOf(".") > -1) {
          fileName = fileName.substring(0, fileName.lastIndexOf(".")) + "."
              + ((MyFileFilter) fileChooser.getFileFilter()).getExtension();
          fileChooser.setSelectedFile(new File(fileChooser
              .getCurrentDirectory(), fileName));
        }
      }
    }
  }
  private class NumberInputHandler implements InputHandler {
    NumberValue num = null;

    public boolean processInput(String inputString) {
      GeoElement[] result = kernel.getAlgebraProcessor().processAlgebraCommand(
          inputString, false);
      boolean success = result != null && result[0].isNumberValue();
      if (success)
        num = (NumberValue) result[0];
      return success;
    }
  }

  private static final int SPREADSHEET_INI_COLS = 26;

  private static final int SPREADSHEET_INI_ROWS = 100;
  public static File addExtension(File file, String fileExtension) {
    if (file == null)
      return null;
    if (Application.getExtension(file).equals(fileExtension))
      return file;
    else
      return new File(file.getParentFile(), // path
          file.getName() + '.' + fileExtension); // filename
  }

  public static File removeExtension(File file) {
    if (file == null)
      return null;
    String fileName = file.getName();
    int dotPos = fileName.indexOf('.');

    if (dotPos <= 0)
      return file;
    else
      return new File(file.getParentFile(), // path
          fileName.substring(0, dotPos));
  }

  // Java user interface properties, for translation of JFileChooser
  private ResourceBundle rbJavaUI;
  private final Application app;
  private final Kernel kernel;

  private OptionsDialog optionsDialog;
  protected PropertiesDialog propDialog;
  private ConstructionProtocol constProtocol;
  protected ConstructionProtocolNavigation constProtocolNavigation;

  private AlgebraInput algebraInput;
  private AlgebraController algebraController;

  private AlgebraView algebraView;
  private SpreadsheetView spreadsheetView;
  private GeoGebraFileChooser fileChooser;

  private GeoGebraMenuBar menuBar;
   // Oleg Perchenok 7/05/2011
  public void setAppToolbarPanel(MyToolbar appToolbarPanel) {
        this.appToolbarPanel = appToolbarPanel;

  }
   //Oleg Perchenok
    private MyToolbar appToolbarPanel;

  private String strCustomToolbarDefinition;

  private Locale currentLocale;

  private final Layout layout;

  private boolean initialized = false;

  // Actions
  private AbstractAction showAxesAction, showGridAction, undoAction,
      redoAction;

  private final ArrayList<?> tempGeos = new ArrayList<Object>();

  public DefaultGuiManager(Application app) {
    this.app = app;
    kernel = app.getKernel();

    // the layout component
    layout = new Layout();

    // removed: we need the arrow keys to work in applets
    // if (!app.isApplet())

    initAlgebraController(); // needed for keyboard input in EuclidianView
  }

  public void addToToolbarDefinition(int mode) {
    if (strCustomToolbarDefinition != null)
      strCustomToolbarDefinition = strCustomToolbarDefinition + " | " + mode;
  }

  public void allowGUIToRefresh() {
    if (!SwingUtilities.isEventDispatchThread())
      return;

    // use Foxtrot to wait a bit until screen has refreshed
    Worker.post(new Job() {
      @Override
      public Object run() {
        try {
          Thread.sleep(10);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        return null;
      }
    });
  }

  public void attachAlgebraView() {
    getAlgebraView();
    algebraView.attachView();
  }

  public void attachSpreadsheetView() {
    getSpreadsheetView();
    spreadsheetView.attachView();
  }

  /**
   * Attach a view which by using the view ID.
   * 
   * @author Florian Sonner
   * @version 2008-10-21
   * 
   * @param viewId
   */
  public void attachView(int viewId) {
    switch (viewId) {
      case Application.VIEW_ALGEBRA :
        attachAlgebraView();
        break;
      case Application.VIEW_SPREADSHEET :
        attachSpreadsheetView();
        break;
    }
  }

  public void clearPreferences() {
    if (app.isSaved() || app.saveCurrentFile()) {
      app.setWaitCursor();
      GeoGebraPreferences.getPref().clearPreferences();

      // clear custom toolbar definition
      strCustomToolbarDefinition = null;

      GeoGebraPreferences.getPref().loadXMLPreferences(app); // this will load
      // the default
      // settings
      app.setLanguage(app.getMainComponent().getLocale());
      app.updateContentPaneAndSize();
      app.setDefaultCursor();
      app.setUndoActive(true);
    }
  }

  public void closeOpenDialogs() {
    // close open windows
    if (propDialog != null && propDialog.isShowing())
      propDialog.cancel();
    if (constProtocol != null && constProtocol.isShowing())
      constProtocol.setVisible(false);

  }

  public JFrame createFrame() {
    GeoGebraFrame wnd = new GeoGebraFrame();
    try {
      // TODO: throws null pointer exception
      wnd.setGlassPane(layout.getDockManager().getGlassPane());
    } catch (Exception e) {
      e.printStackTrace();
    }
    wnd.setApplication(app);

    return wnd;
  }

  public JDialog createTextDialog(GeoText text, GeoPoint startPoint) {
    TextInputDialog id = new TextInputDialog(app, Plain.Text, text, startPoint,
        30, 6);
    return id;
  }

  public void detachAlgebraView() {
    if (algebraView != null)
      algebraView.detachView();
  }

  public void detachSpreadsheetView() {
    if (spreadsheetView != null)
      spreadsheetView.detachView();
  }

  /**
   * Detach a view which by using the view ID.
   * 
   * @author Florian Sonner
   * @version 2008-10-21
   * 
   * @param viewId
   */
  public void detachView(int viewId) {
    switch (viewId) {
      case Application.VIEW_ALGEBRA :
        detachAlgebraView();
        break;
      case Application.VIEW_SPREADSHEET :
        detachSpreadsheetView();
        break;
    }
  }

  public void doAfterRedefine(GeoElement geo) {
    // select geoElement with label again
    if (propDialog != null && propDialog.isShowing())
      // propDialog.setViewActive(true);
      propDialog.geoElementSelected(geo, false);
  }

  public synchronized void doOpenFiles(File[] files,
      boolean allowOpeningInThisInstance) {
    // Added for Intergeo File Format (Yves Kreis) -->
    doOpenFiles(files, allowOpeningInThisInstance,
        Application.FILE_EXT_GEOGEBRA);
  }

  public synchronized void doOpenFiles(File[] files,
      boolean allowOpeningInThisInstance, String extension) {
    // <-- Added for Intergeo File Format (Yves Kreis)
    // there are selected files
    if (files != null) {
      File file;
      int counter = 0;
      for (File file2 : files) {
        file = file2;

        if (!file.exists()) {
          // Modified for Intergeo File Format (Yves Kreis) -->
          // file = addExtension(file, Application.FILE_EXT_GEOGEBRA);
          file = addExtension(file, extension);
          if (extension.equals(Application.FILE_EXT_GEOGEBRA) && !file.exists())
            file = addExtension(removeExtension(file),
                Application.FILE_EXT_GEOGEBRA_TOOL);
        }

        if (file.exists())
          if (Application.FILE_EXT_GEOGEBRA_TOOL.equals(Application
              .getExtension(file).toLowerCase(Locale.US)))
            // load macro file
            loadFile(file, true);
          else {
            // standard GeoGebra file
            GeoGebraFrame inst = GeoGebraFrame.getInstanceWithFile(file);
            if (inst == null) {
              counter++;
              if (counter == 1 && allowOpeningInThisInstance)
                // open first file in current window
                loadFile(file, false);
              else
                // create new window for file
                try {
                  String[] args = {file.getCanonicalPath()};
                  GeoGebraFrame wnd = GeoGebraFrame.createNewWindow(args);
                  wnd.toFront();
                  wnd.requestFocus();
                } catch (Exception e) {
                  e.printStackTrace();
                }
            } else if (counter == 0) {
              // there is an instance with this file opened
              inst.toFront();
              inst.requestFocus();
            }
          }
      }
    }

  }

  public synchronized void exitAll() {
    ArrayList<?> insts = GeoGebraFrame.getInstances();
    GeoGebraFrame[] instsCopy = new GeoGebraFrame[insts.size()];
    for (int i = 0; i < instsCopy.length; i++)
      instsCopy[i] = (GeoGebraFrame) insts.get(i);

    for (GeoGebraFrame element : instsCopy)
      element.getApplication().exit();
  }

  /**
   * Exports construction protocol as html
   */
  final public void exportConstructionProtocolHTML() {
    getConstructionProtocol();
    constProtocol.initProtocol();
    constProtocol.showHTMLExportDialog();
  }

  public JComponent getAlgebraInput() {
    if (algebraInput == null)
      algebraInput = new AlgebraInput(app);

    return algebraInput;
  }

  public int getAlgebraInputHeight() {
    if (app.showAlgebraInput() && algebraInput != null)
      return algebraInput.getHeight();
    else
      return 0;
  }

  public JTextComponent getAlgebraInputTextField() {
    getAlgebraInput();
    return algebraInput.getTextField();
  }

  public JComponent getAlgebraView() {
    if (algebraView == null) {
      initAlgebraController();
      algebraView = new AlgebraView(algebraController);
      if (!app.isApplet())
        // allow drag & drop of files on algebraView
        algebraView.setDropTarget(new DropTarget(algebraView,
            new FileDropTargetListener(app)));
    }

    return algebraView;
  }

  public String getConsProtocolXML() {
    StringBuffer sb = new StringBuffer();

    if (constProtocol != null)
      sb.append(constProtocol.getConsProtocolXML());

    // navigation bar of construction protocol
    if (app.showConsProtNavigation() && constProtocolNavigation != null) {
      sb.append("\t<consProtNavigationBar ");
      sb.append("show=\"");
      sb.append(app.showConsProtNavigation());
      sb.append("\"");
      sb.append(" playButton=\"");
      sb.append(constProtocolNavigation.isPlayButtonVisible());
      sb.append("\"");
      sb.append(" playDelay=\"");
      sb.append(constProtocolNavigation.getPlayDelay());
      sb.append("\"");
      sb.append(" protButton=\"");
      sb.append(constProtocolNavigation.isConsProtButtonVisible());
      sb.append("\"");
      sb.append(" consStep=\"");
      sb.append(kernel.getConstructionStep());
      sb.append("\"");
      sb.append("/>\n");
    }

    return sb.toString();
  }

  public JDialog getConstructionProtocol() {
    if (constProtocol == null)
      constProtocol = new ConstructionProtocol(app);
    return constProtocol;
  }

  public JComponent getConstructionProtocolNavigation() {
    if (constProtocolNavigation == null) {
      getConstructionProtocol();
      constProtocolNavigation = new ConstructionProtocolNavigation(
          constProtocol);
    }

    return constProtocolNavigation;
  }

  /**
   * Returns text "Created with <ApplicationName>" and link to application
   * homepage in html.
   */
  public String getCreatedWithHTML() {
    StringBuffer sb = new StringBuffer();
    sb.append(Util.toHTMLString(Plain.CreatedWith));
    sb.append(" ");
    sb.append("<a href=\"");
    sb.append(GeoGebra.GEOGEBRA_WEBSITE);
    sb.append("\" target=\"_blank\" >");
    sb.append("GeoGebra");
    sb.append("</a>");
    return sb.toString();
  }

  public final String getCustomToolbarDefinition() {
    return strCustomToolbarDefinition;
  }

  public String getDefaultToolbarString() {
    if (appToolbarPanel == null)
      return "";

    return appToolbarPanel.getDefaultToolbarString();
  }

  private URL getHelpURL(Locale locale, String command, String intCommand)
      throws Exception {
    // try to get help for current locale (language + country + variant)
    URL helpURL = getHelpURL(locale.toString(), command);

    if (helpURL != null)
      return helpURL;

    // last attempt: try to get English help
    helpURL = getHelpURL("en", intCommand);
    if (helpURL != null)
      return helpURL;

    // sorry, no help available
    throw new Exception("HelpNotFound");
  }

  private URL getHelpURL(String languageISOcode, String command) {
    // try to get help for given language
    String strFile;
    if (command == null)
      strFile = "docu" + languageISOcode + "/index.html";
    else
      // URL like http://www.geogebra.org/help/docuen/topics/UpperSum.html
      strFile = "docu" + languageISOcode + "/topics/" + command + ".html";
    String strURL = GeoGebra.GEOGEBRA_WEBSITE + "help/" + strFile;

    if (Application.MAC_OS) {
      String path = app.getCodeBase().getPath();
      int i = path.lastIndexOf("/Java/");
      if (i > -1)
        strFile = path.substring(0, i) + "/Help/" + strFile;
    }

    try {
      File f = new File(strFile);
      if (f.exists())
        return f.toURL();
      else { // web url
        URL url = new URL(strURL);
        if (Util.existsHttpURL(url))
          return url;
      }
    } catch (Exception e) {
    }
    return null;
  }

  public int getHighestUsedSpreadsheetColumn() {
    if (spreadsheetView != null)
      return spreadsheetView.getHighestUsedColumn();
    return -1;
  }

  /**
   * gets an image from the clipboard Then the image file is loaded and stored
   * in this application's imageManager. Michael Borcherds 2008-05-10
   * 
   * @return fileName of image stored in imageManager
   */
  public String getImageFromClipboard() {

    BufferedImage img = null;
    String fileName = null;
    try {
      app.setWaitCursor();

      // if (fromClipboard)
      {

        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transfer = clip.getContents(null);

        try {
          if (transfer.isDataFlavorSupported(DataFlavor.imageFlavor))
            img = (BufferedImage) transfer
                .getTransferData(DataFlavor.imageFlavor);
        } catch (UnsupportedFlavorException ufe) {
          app.showError("PasteImageFailed");
          return null;
          // ufe.printStackTrace();
        } catch (IOException ioe) {
          app.showError("PasteImageFailed");
          return null;
          // ioe.printStackTrace();
        }

        if (img == null) {
          app.showError("PasteImageFailed");
          return null;
        }

        fileName = "clipboard.png"; // extension determines what format
        // it will be in ggb file
      }
    } catch (Exception e) {
      app.setDefaultCursor();
      e.printStackTrace();
      app.showError("PasteImageFailed");
      return null;
    }

    return app.createImage(img, fileName);

  }

  /**
   * Shows a file open dialog to choose an image file, Then the image file is
   * loaded and stored in this application's imageManager.
   * 
   * @return fileName of image stored in imageManager
   */
  public String getImageFromFile() {

    BufferedImage img = null;
    String fileName = null;
    try {
      app.setWaitCursor();
      // else
      {

        initFileChooser();

        fileChooser.setCurrentDirectory(app.getCurrentImagePath());
        fileChooser.setMode(GeoGebraFileChooser.MODE_IMAGES);

        MyFileFilter fileFilter = new MyFileFilter();
        fileFilter.addExtension("jpg");
        fileFilter.addExtension("jpeg");
        fileFilter.addExtension("png");
        fileFilter.addExtension("gif");
        fileFilter.addExtension("tif");
        if (Util.getJavaVersion() >= 1.5)
          fileFilter.addExtension("bmp");
        fileFilter.setDescription(Plain.Image);
        fileChooser.resetChoosableFileFilters();
        fileChooser.setFileFilter(fileFilter);

        File imageFile = null;
        int returnVal = fileChooser.showOpenDialog(app.getMainComponent());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          imageFile = fileChooser.getSelectedFile();
          if (imageFile != null) {
            app.setCurrentImagePath(imageFile.getParentFile());
            if (!app.isApplet())
              GeoGebraPreferences.getPref().saveDefaultImagePath(
                  app.getCurrentImagePath());
          }
        }

        if (imageFile == null) {
          app.setDefaultCursor();
          return null;
        }

        // get file name
        fileName = imageFile.getCanonicalPath();

        // load image
        img = ImageIO.read(imageFile);
      }

      return app.createImage(img, fileName);

    } catch (Exception e) {
      app.setDefaultCursor();
      e.printStackTrace();
      app.showError("LoadFileFailed");
      return null;
    }

  }

  public Layout getLayout() {
    return layout;
  }

  public JComponent getLayoutRoot() {
    return layout.getDockManager().getRoot();
  }

  public String getLayoutXml(boolean isPreference) {
    return layout.getXml(isPreference);
  }

  public JMenuBar getMenuBar() {
    return menuBar;
  }

  public int getMenuBarHeight() {
    if (menuBar == null)
      return 0;
    else
      return ((JMenuBar) menuBar).getHeight();
  }

  public AbstractAction getRedoAction() {
    initActions();
    return redoAction;
  }

  public AbstractAction getShowAxesAction() {
    initActions();
    return showAxesAction;
  }

  public AbstractAction getShowGridAction() {
    initActions();
    return showGridAction;
  }

  public int getSpreadsheetTraceRow(int column) {
    if (spreadsheetView != null)
      return spreadsheetView.getTraceRow(column);
    return -1;
  }

  public JComponent getSpreadsheetView() {
    // init spreadsheet view
    if (spreadsheetView == null)
      spreadsheetView = new SpreadsheetView(app, SPREADSHEET_INI_COLS,
          SPREADSHEET_INI_ROWS);

    return spreadsheetView;
  }

  public String getSpreadsheetViewXML() {
    if (spreadsheetView != null)
      return spreadsheetView.getXML();
    else
      return "";
  }

  /**
   * gets String from clipboard Michael Borcherds 2008-04-09
   */
  public String getStringFromClipboard() {
    String selection = null;

    Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
    Transferable transfer = clip.getContents(null);

    try {
      if (transfer.isDataFlavorSupported(DataFlavor.stringFlavor))
        selection = (String) transfer.getTransferData(DataFlavor.stringFlavor);
      else if (transfer.isDataFlavorSupported(DataFlavor.plainTextFlavor)) {
        StringBuffer sbuf = new StringBuffer();
        InputStreamReader reader;
        char readBuf[] = new char[1024 * 64];
        int numChars;

        reader = new InputStreamReader((InputStream) transfer
            .getTransferData(DataFlavor.plainTextFlavor), "UNICODE");

        while (true) {
          numChars = reader.read(readBuf);
          if (numChars == -1)
            break;
          sbuf.append(readBuf, 0, numChars);
        }

        selection = new String(sbuf);
      }
    } catch (Exception e) {
    }

    return selection;
  }

  public String getToolBarDefinition() {
    if (strCustomToolbarDefinition == null && appToolbarPanel != null)
      return appToolbarPanel.getDefaultToolbarString();
    else
      return strCustomToolbarDefinition;
  }

  public int getToolBarHeight() {
    if (app.showToolBar() && appToolbarPanel != null)
      return appToolbarPanel.getHeight();
    else
      return 0;
  }

  public JComponent getToolbarPanel() {
    if (appToolbarPanel == null)
      appToolbarPanel = new MyToolbar(app);

    return appToolbarPanel;
  }

  public AbstractAction getUndoAction() {
    initActions();
    return undoAction;
  }

  public boolean hasSpreadsheetView() {
    return spreadsheetView != null;
  }

  private void initActions() {
    if (showAxesAction != null)
      return;

    showAxesAction = new AbstractAction(geogebra.Menu.Axes, app
        .getImageIcon("axes.gif")) {
      private static final long serialVersionUID = 1L;

      public void actionPerformed(ActionEvent e) {
        // toggle axes
        boolean bothAxesShown = app.getEuclidianView().getShowXaxis()
            && app.getEuclidianView().getShowYaxis();
        app.getEuclidianView().showAxes(!bothAxesShown, !bothAxesShown);
        app.getEuclidianView().repaint();
        app.storeUndoInfo();
        app.updateMenubar();
      }
    };

    showGridAction = new AbstractAction(geogebra.Menu.Grid, app
        .getImageIcon("grid.gif")) {
      private static final long serialVersionUID = 1L;

      public void actionPerformed(ActionEvent e) {
        // toggle grid
        app.getEuclidianView().showGrid(!app.getEuclidianView().getShowGrid());
        app.getEuclidianView().repaint();
        app.storeUndoInfo();
        app.updateMenubar();
      }
    };

    undoAction = new AbstractAction(geogebra.Menu.Undo, app
        .getImageIcon("edit-undo.png")) {
      private static final long serialVersionUID = 1L;

      public void actionPerformed(ActionEvent e) {
        if (propDialog != null && propDialog.isShowing())
          propDialog.cancel();

        undo();
      }
    };

    redoAction = new AbstractAction(geogebra.Menu.Redo, app
        .getImageIcon("edit-redo.png")) {
      private static final long serialVersionUID = 1L;

      public void actionPerformed(ActionEvent e) {
        if (propDialog != null && propDialog.isShowing())
          propDialog.cancel();

        redo();
      }
    };

    updateActions();
  }

  private void initAlgebraController() {
    if (algebraController == null)
      algebraController = new AlgebraController(app.getKernel());
  }

  public synchronized void initFileChooser() {
    if (fileChooser == null) {
      try {
        fileChooser = new GeoGebraFileChooser(app, app.getCurrentImagePath()); // non-restricted
        // Added for Intergeo File Format (Yves Kreis) -->
        fileChooser.addPropertyChangeListener(
            JFileChooser.FILE_FILTER_CHANGED_PROPERTY,
            new FileFilterChangedListener());
        // <-- Added for Intergeo File Format (Yves Kreis)
      } catch (Exception e) {
        // fix for java.io.IOException: Could not get shell folder ID list
        // Java bug http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6544857
        Application
            .debug("Error creating GeoGebraFileChooser - using fallback option");
        fileChooser = new GeoGebraFileChooser(app, app.getCurrentImagePath(),
            true); // restricted version
      }

      updateJavaUILanguage();
    }
  }

  public void initialize() {
    if (initialized)
      return;

    initialized = true;

    layout.initialize(app);
  }

  public void initMenubar() {
    if (menuBar == null)
      menuBar = new GeoGebraMenuBar(app, layout);
    menuBar.initMenubar();
  }

  public synchronized void initPropertiesDialog() {
    if (propDialog == null)
      propDialog = new PropertiesDialog(app);
  }

  public boolean isConsProtNavigationPlayButtonVisible() {
    if (constProtocolNavigation != null)
      return constProtocolNavigation.isPlayButtonVisible();
    else
      return true;
  }

  public boolean isConsProtNavigationProtButtonVisible() {
    if (constProtocolNavigation != null)
      return constProtocolNavigation.isConsProtButtonVisible();
    else
      return true;
  }

  public boolean isInputFieldSelectionListener() {
    return app.getCurrentSelectionListener() == algebraInput.getTextField();
  }

  public boolean isPropertiesDialogSelectionListener() {
    return app.getCurrentSelectionListener() == propDialog;
  }

  public boolean isUsingConstructionProtocol() {
    return constProtocol != null;
  }

  public boolean loadFile(final File file, final boolean isMacroFile) {
    if (!file.exists()) {
      // show file not found message
      JOptionPane.showConfirmDialog(app.getMainComponent(), app
          .getError("FileNotFound")
          + ":\n" + file.getAbsolutePath(), app.getError("Error"),
          JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
      return false;
    }

    app.setWaitCursor();
    if (!isMacroFile)
      // hide navigation bar for construction steps if visible
      app.setShowConstructionProtocolNavigation(false);

    boolean success = app.loadXML(file, isMacroFile);
    updateGUIafterLoadFile(success, isMacroFile);
    app.setDefaultCursor();
    return success;

  }

  /**
   * Creates a new image at the given location (real world coords).
   * 
   * @return whether a new image was create or not
   */
  public boolean loadImage(GeoPoint loc, boolean fromClipboard) {
    app.setWaitCursor();

    String fileName;
    if (fromClipboard)
      fileName = getImageFromClipboard();
    else
      fileName = getImageFromFile();

    boolean ret;
    if (fileName == null)
      ret = false;
    else {
      // create GeoImage object for this fileName
      GeoImage geoImage = new GeoImage(app.getKernel().getConstruction());
      geoImage.setFileName(fileName);
      geoImage.setCorner(loc, 0);
      geoImage.setLabel(null);

      GeoImage.updateInstances();
      ret = true;
    }

    app.setDefaultCursor();
    return ret;
  }

  public void openFile() {

    if (propDialog != null && propDialog.isShowing())
      propDialog.cancel();

    if (app.isSaved() || saveCurrentFile()) {
      app.setWaitCursor();
      File oldCurrentFile = app.getCurrentFile();
      app.setCurrentFile(null);

      initFileChooser();
      fileChooser.setMode(GeoGebraFileChooser.MODE_GEOGEBRA);
      fileChooser.setCurrentDirectory(app.getCurrentPath());
      fileChooser.setMultiSelectionEnabled(true);

      // GeoGebra File Filter
      MyFileFilter fileFilter = new MyFileFilter();
      fileFilter.addExtension(Application.FILE_EXT_GEOGEBRA);
      fileFilter.addExtension(Application.FILE_EXT_GEOGEBRA_TOOL);
      fileFilter.setDescription(Plain.ApplicationName + " "
          + geogebra.Menu.Files);
      fileChooser.resetChoosableFileFilters();
      // Modified for Intergeo File Format (Yves Kreis & Ingo Schandeler)
      // -->
      fileChooser.addChoosableFileFilter(fileFilter);

      // Intergeo File Filter
      MyFileFilter i2gFileFilter = new MyFileFilter();
      i2gFileFilter.addExtension(Application.FILE_EXT_INTERGEO);
      i2gFileFilter.setDescription("Intergeo " + geogebra.Menu.Files
          + " [Version " + GeoGebra.I2G_FILE_FORMAT + "]");
      fileChooser.addChoosableFileFilter(i2gFileFilter);

      // fileChooser.setFileFilter(fileFilter);
      if (Application.getExtension(oldCurrentFile).equals(
          Application.FILE_EXT_GEOGEBRA)
          || Application.getExtension(oldCurrentFile).equals(
              Application.FILE_EXT_GEOGEBRA_TOOL))
        fileChooser.setFileFilter(fileFilter);

      app.setDefaultCursor();
      int returnVal = fileChooser.showOpenDialog(app.getMainComponent());

      File[] files = null;
      if (returnVal == JFileChooser.APPROVE_OPTION)
        files = fileChooser.getSelectedFiles();
      // Modified for Intergeo File Format (Yves Kreis) -->
      if (fileChooser.getFileFilter() instanceof geogebra.gui.app.MyFileFilter) {
        fileFilter = (MyFileFilter) fileChooser.getFileFilter();
        doOpenFiles(files, true, fileFilter.getExtension());
      } else
        // doOpenFiles(files, true);
        doOpenFiles(files, true);

      if (app.getCurrentFile() == null)
        app.setCurrentFile(oldCurrentFile);
      fileChooser.setMultiSelectionEnabled(false);
    }
  }

  public void openHelp(String command) {
    String internalCmd = null;

    if (command != null)
      try { // convert eg uppersum to UpperSum
        internalCmd = app.translateCommand(command);
        String command2 = app.getCommand(internalCmd);
        if (command2 != null && command2 != "")
          command = command2;
      } catch (Exception e) {
      }

    try {
      URL helpURL = getHelpURL(app.getLocale(), command, internalCmd);
      showURLinBrowser(helpURL);
    } catch (MyError e) {
      app.showError(e);
    } catch (Exception e) {
      Application.debug("openHelp error: " + e.toString() + e.getMessage());
      app.showError(e.getMessage());
    }
  }

  public void redo() {
    app.setWaitCursor();
    kernel.redo();
    updateActions();
    app.setDefaultCursor();
    System.gc();
  }

  public synchronized void reinitPropertiesDialog() {
    propDialog = null;
    System.gc();
    propDialog = new PropertiesDialog(app);

  }

  public void removeFromToolbarDefinition(int mode) {
    if (strCustomToolbarDefinition != null) {
      // Application.debug("before: " + strCustomToolbarDefinition +
      // ",  delete " + mode);

      strCustomToolbarDefinition = strCustomToolbarDefinition.replaceAll(
          Integer.toString(mode), "");

      if (mode >= EuclidianView.MACRO_MODE_ID_OFFSET) {
        // if a macro mode is removed all higher macros get a new id (i.e. id-1)
        int lastID = kernel.getMacroNumber()
            + EuclidianView.MACRO_MODE_ID_OFFSET - 1;
        for (int id = mode + 1; id <= lastID; id++)
          strCustomToolbarDefinition = strCustomToolbarDefinition.replaceAll(
              Integer.toString(id), Integer.toString(id - 1));
      }

      // Application.debug("after: " + strCustomToolbarDefinition);
    }
  }

  public void resetSpreadsheet() {
    if (spreadsheetView != null)
      spreadsheetView.restart();
  }

  public boolean save() {
    app.setWaitCursor();

    // close properties dialog if open
    closeOpenDialogs();

    boolean success = false;
    if (app.getCurrentFile() != null) {
      // Mathieu Blossier - 2008-01-04
      // if the file is read-only, open save as
      if (!app.getCurrentFile().canWrite())
        success = saveAs();
      else
        success = app.saveGeoGebraFile(app.getCurrentFile());
    } else
      success = saveAs();

    app.setDefaultCursor();
    return success;
  }

  public boolean saveAs() {

    // Mathieu Blossier - 2008-01-04
    // if the file is hidden, set current file to null
    if (app.getCurrentFile() != null)
      if (!app.getCurrentFile().canWrite() && app.getCurrentFile().isHidden()) {
        app.setCurrentFile(null);
        app.setCurrentPath(null);
      }

    // Added for Intergeo File Format (Yves Kreis) -->
    String[] fileExtensions;
    String[] fileDescriptions;

    fileExtensions = new String[]{Application.FILE_EXT_GEOGEBRA,
        Application.FILE_EXT_INTERGEO};
    fileDescriptions = new String[]{
        Plain.ApplicationName + " " + geogebra.Menu.Files,
        "Intergeo " + geogebra.Menu.Files + " [Version "
            + GeoGebra.I2G_FILE_FORMAT + "]"};
    // <-- Added for Intergeo File Format (Yves Kreis)
    File file = showSaveDialog(
    // Modified for Intergeo File Format (Yves Kreis) -->
        // Application.FILE_EXT_GEOGEBRA, currentFile,
        // app.getPlain("ApplicationName") + " " + geogebra.Menu.Files);
        fileExtensions, app.getCurrentFile(), fileDescriptions);
    // <-- Modified for Intergeo File Format (Yves Kreis)
    if (file == null)
      return false;

    boolean success = app.saveGeoGebraFile(file);
    if (success)
      app.setCurrentFile(file);
    return success;
  }

  // returns true for YES or NO and false for CANCEL
  public boolean saveCurrentFile() {
    if (propDialog != null && propDialog.isShowing())
      propDialog.cancel();
    app.getEuclidianView().reset();

    // use null component for iconified frame
    GeoGebraFrame frame = (GeoGebraFrame) app.getFrame();
    Component comp = frame != null && !frame.isIconified() ? frame : null;

    // Michael Borcherds 2008-05-04
    Object[] options = {geogebra.Menu.Save, geogebra.Menu.DontSave,
        geogebra.Menu.Cancel};
    int returnVal = 1;

    //Commented out by Oleg Perchenok 2012-06-17
    /*JOptionPane.showOptionDialog(comp,
        Menu.DoYouWantToSaveYourChanges, geogebra.Menu.CloseFile,
        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,

        null, options, options[0]);*/

    /*
     * int returnVal = JOptionPane.showConfirmDialog( comp,
     * geogebra.Menu.SaveCurrentFileQuestion, app.getPlain("ApplicationName") +
     * " - " + app.getPlain("Question"), JOptionPane.YES_NO_CANCEL_OPTION,
     * JOptionPane.QUESTION_MESSAGE);
     */

    switch (returnVal) {
      case 0 :
        return save();

      case 1 :
        return true;

      default :
        return false;
    }
  }

  public void setColumnWidth(int column, int width) {
    ((SpreadsheetView) getSpreadsheetView()).setColumnWidth(column, width);
  }

  public void setConstructionStep(int step) {
    if (constProtocol != null)
      constProtocol.setConstructionStep(step);
  }

  public void setLabels() {
    // reinit actions to update labels
    showAxesAction = null;
    initActions();

    if (app.showMenuBar()) {
      initMenubar();
      if (app.isApplet())
        ((JApplet) app.getMainComponent()).setJMenuBar(menuBar);
      else
        ((JFrame) app.getMainComponent()).setJMenuBar(menuBar);
    }

    // update views
    if (algebraView != null)
      algebraView.setLabels();
    if (algebraInput != null)
      algebraInput.setLabels();

    // TODO don't reinit GUIs anymore! (performance!) (F.S.)
    if (appToolbarPanel != null)
      appToolbarPanel.initToolbar();

    if (propDialog != null)
      // changed to force all language strings to be updated
      reinitPropertiesDialog(); // was propDialog.initGUI();

    if (constProtocol != null)
      constProtocol.initGUI();
    if (constProtocolNavigation != null)
      constProtocolNavigation.setLabels();
    if (fileChooser != null)
      updateJavaUILanguage();
    if (optionsDialog != null)
      optionsDialog.setLabels();

    // layout.getDockManager().setLabels();
  }

  public void setMenubar(JMenuBar newMenuBar) {
    menuBar = (GeoGebraMenuBar) newMenuBar;
  }

  public void setMode(int mode) {
    // close properties dialog
    // if it is not the current selection listener
    if (propDialog != null && propDialog.isShowing()
        && propDialog != app.getCurrentSelectionListener())
      propDialog.setVisible(false);

    // reset algebra view
    if (algebraView != null)
      algebraView.reset();

    // tell EuclidianView
    app.getEuclidianView().setMode(mode);

    // select toolbar button
    setToolbarMode(mode);
  }

  public void setPerspectives(ArrayList<Perspective> perspectives) {
    layout.setPerspectives(perspectives);

    layout.setTitlebarVisible(app.isViewTitleBarVisible());
  }

  public void setScrollToShow(boolean scrollToShow) {
    if (spreadsheetView != null)
      spreadsheetView.setScrollToShow(scrollToShow);
  }

  public void setShowAlgebraView(boolean flag) {
    setShowView(flag, Application.VIEW_ALGEBRA);
  }

  public void setShowAuxiliaryObjects(boolean flag) {
    getAlgebraView();
    algebraView.setShowAuxiliaryObjects(flag);
  }

  public void setShowCASView(boolean flag) {
    setShowView(flag, Application.VIEW_CAS);
  }

  public void setShowConstructionProtocolNavigation(boolean show) {
    if (show) {
      if (app.getEuclidianView() != null)
        app.getEuclidianView().resetMode();
      getConstructionProtocolNavigation();
      constProtocolNavigation.register();
    } else if (constProtocolNavigation != null)
      constProtocolNavigation.unregister();
  }

  public void setShowConstructionProtocolNavigation(boolean show,
      boolean playButton, double playDelay, boolean showProtButton) {
    setShowConstructionProtocolNavigation(show);

    if (constProtocolNavigation != null) {
      constProtocolNavigation.setPlayButtonVisible(playButton);
      constProtocolNavigation.setPlayDelay(playDelay);
      constProtocolNavigation.setConsProtButtonVisible(showProtButton);
    }

  }

  public void setShowEuclidianView(boolean flag) {
    setShowView(flag, Application.VIEW_EUCLIDIAN);
  }

  public void setShowSpreadsheetView(boolean flag) {
    setShowView(flag, Application.VIEW_SPREADSHEET);
  }

  public void setShowToolBarHelp(boolean flag) {
    if (appToolbarPanel != null || flag == false) {
      getToolbarPanel();
      appToolbarPanel.setShowToolBarHelp(flag);
    }
  }

  private void setShowView(boolean flag, int viewId) {
    if (flag)
      layout.getDockManager().show(viewId);
    else
      layout.getDockManager().hide(viewId);
  }

  public void setToolBarDefinition(String toolBarDefinition) {
    strCustomToolbarDefinition = toolBarDefinition;
  }

  public void setToolbarMode(int mode) {
    if (appToolbarPanel == null)
      return;
    appToolbarPanel.setSelectedMode(mode);
  }

  public void showAboutDialog() {
    GeoGebraMenuBar.showAboutDialog(app);
  }

  public boolean showAlgebraView() {
    return showView(Application.VIEW_ALGEBRA);
  }

  /**
   * Shows a modal dialog to enter an angle or angle variable name.
   * 
   * @return: Object[] with { NumberValue, AngleInputDialog } pair
   */
  public Object[] showAngleInputDialog(String title, String message,
      String initText) {
    // avoid labeling of num
    Construction cons = kernel.getConstruction();
    boolean oldVal = cons.isSuppressLabelsActive();
    cons.setSuppressLabelCreation(true);

    NumberInputHandler handler = new NumberInputHandler();
    AngleInputDialog id = new AngleInputDialog(app, message, title, initText,
        false, handler, true);
    id.setVisible(true);

    cons.setSuppressLabelCreation(oldVal);
    Object[] ret = {handler.num, id};
    return ret;
  }

  /**
   * Creates a new text at given startPoint
   */
  public void showBooleanCheckboxCreationDialog(Point loc, GeoBoolean bool) {
    CheckboxCreationDialog d = new CheckboxCreationDialog(app, loc, bool);
    d.setVisible(true);
  }

  public boolean showCASView() {
    return showView(Application.VIEW_CAS);
  }

  public Color showColorChooser(Color currentColor) {
    // there seems to be a bug concerning ToolTips in JColorChooser
    // so we turn off ToolTips
    try {
      Color newColor = JColorChooser.showDialog(null, Plain.ChooseColor,
          currentColor);
      return newColor;
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Displays the construction protocol dialog
   */
  public void showConstructionProtocol() {
    app.getEuclidianView().resetMode();
    getConstructionProtocol();
    constProtocol.setVisible(true);
  }

  /**
   * Displays the zoom menu at the position p in the coordinate space of
   * euclidianView
   */
  public void showDrawingPadPopup(Component invoker, Point p) {
    // clear highlighting and selections in views
    app.getEuclidianView().resetMode();

    // menu for drawing pane context menu
    ContextMenuGraphicsWindow popupMenu = new ContextMenuGraphicsWindow(app,
        p.x, p.y);
    popupMenu.show(invoker, p.x, p.y);
  }

  /**
   * 
   * Displays the porperties dialog for the drawing pad
   */
  public void showDrawingPadPropertiesDialog() {
    if (!app.letShowPropertiesDialog())
      return;
    app.setWaitCursor();
    app.getEuclidianView().resetMode();
    PropertiesDialogGraphicsWindow euclidianViewDialog = new PropertiesDialogGraphicsWindow(
        app, app.getEuclidianView());
    euclidianViewDialog.setVisible(true);
    app.setDefaultCursor();
  }

  public boolean showEuclidianView() {
    return showView(Application.VIEW_EUCLIDIAN);
  }

  /**
   * Creates a new JavaScript button at given location (screen coords).
   * 
   * @return whether a new slider (number) was create or not
   */
  public boolean showJavaScriptButtonCreationDialog(int x, int y) {
    JavaScriptDialog dialog = new JavaScriptDialog(app, x, y);
    dialog.setVisible(true);
    GeoJavaScriptButton button = (GeoJavaScriptButton) dialog.getResult();
    if (button != null) {
      // make sure that we show name and value of slider
      button.setLabelMode(GeoElement.LABEL_NAME_VALUE);
      button.setLabelVisible(true);
      button.update();
    }
    return button != null;
  }

  /**
   * Shows a modal dialog to enter a number or number variable name.
   */
  public NumberValue showNumberInputDialog(String title, String message,
      String initText) {
    // avoid labeling of num
    Construction cons = kernel.getConstruction();
    boolean oldVal = cons.isSuppressLabelsActive();
    cons.setSuppressLabelCreation(true);

    NumberInputHandler handler = new NumberInputHandler();
    InputDialog id = new InputDialog(app, message, title, initText, false,
        handler, true, false, null);
    id.setVisible(true);

    cons.setSuppressLabelCreation(oldVal);
    return handler.num;
  }

  /**
   * Displays the options dialog.
   * 
   * @param showEuclidianTab
   *          If the tab with euclidian settings should be selected
   */
  public void showOptionsDialog(boolean showEuclidianTab) {
    if (optionsDialog == null)
      optionsDialog = new OptionsDialog(app);
    else
      optionsDialog.updateGUI();

    if (showEuclidianTab)
      optionsDialog.showEuclidianTab();

    optionsDialog.setVisible(true);
  }

  /**
   * Displays the popup menu for geo at the position p in the coordinate space
   * of the component invoker
   */
  public void showPopupMenu(GeoElement geo, Component invoker, Point p) {
    if (geo == null || !app.letShowPopupMenu())
      return;

    if (app.getKernel().isAxis(geo))
      showDrawingPadPopup(invoker, p);
    else {
      // clear highlighting and selections in views
      app.getEuclidianView().resetMode();
      Point screenPos = invoker.getLocationOnScreen();
      screenPos.translate(p.x, p.y);

      ContextMenuGeoElement popupMenu = new ContextMenuGeoElement(app, geo,
          screenPos);
      popupMenu.show(invoker, p.x, p.y);
    }

  }

  public void showPrintPreview() {
    GeoGebraMenuBar.showPrintPreview(app);
  }

  public void showPropertiesDialog() {
    showPropertiesDialog(null);
  }

  // <-- Added for Intergeo File Format (Yves Kreis)

  /**
   * Displays the porperties dialog for geos
   */
  public void showPropertiesDialog(ArrayList geos) {
    if (!app.letShowPropertiesDialog())
      return;

    // save the geos list: it will be cleared by setMoveMode()
    ArrayList selGeos = null;
    if (geos == null)
      geos = app.getSelectedGeos();

    if (geos != null) {
      tempGeos.clear();
      tempGeos.addAll(geos);
      selGeos = tempGeos;
    }

    app.setMoveMode();
    app.setWaitCursor();

    // open properties dialog
    initPropertiesDialog();
    propDialog.setVisibleWithGeos(selGeos);

    app.setDefaultCursor();
  }

  /**
   * Displays the redefine dialog for geo
   * 
   * @param allowTextDialog
   *          : whether text dialog should be used for texts
   */
  public void showRedefineDialog(GeoElement geo, boolean allowTextDialog) {
    // doBeforeRedefine();

    if (allowTextDialog && geo.isGeoText() && !geo.isTextCommand()) {
      showTextDialog((GeoText) geo);
      return;
    }

    String str = geo.getRedefineString(false, true);

    InputHandler handler = new RedefineInputHandler(app, geo, str);

    InputDialog id = new InputDialog(app, geo.getNameDescription(),
        Plain.Redefine, str, true, handler, geo);
    id.showSpecialCharacters(true);
    id.setVisible(true);
    // id.selectText();
  }

  /**
   * Displays the rename dialog for geo
   */
  public void showRenameDialog(GeoElement geo, boolean storeUndo,
      String initText, boolean selectInitText) {
    if (!app.isRightClickEnabled())
      return;

    geo.setLabelVisible(true);
    geo.updateRepaint();

    InputHandler handler = new RenameInputHandler(app, geo, storeUndo);

    InputDialog id = new InputDialog(app, "<html>"
        + app
            .getPlain("NewNameForA", "<b>" + geo.getNameDescription() + "</b>")
        + // eg New name for <b>Segment a</b>
        "</html>", Plain.Rename, initText, false, handler, true,
        selectInitText, null);
    id.setVisible(true);
  }

  public File showSaveDialog(String fileExtension, File selectedFile,
      String fileDescription) {
    // Added for Intergeo File Format (Yves Kreis) -->
    String[] fileExtensions = {fileExtension};
    String[] fileDescriptions = {fileDescription};
    return showSaveDialog(fileExtensions, selectedFile, fileDescriptions);
  }

  public File showSaveDialog(String[] fileExtensions, File selectedFile,
      String[] fileDescriptions) {
    // <-- Added for Intergeo File Format (Yves Kreis)
    boolean done = false;
    File file = null;

    // Added for Intergeo File Format (Yves Kreis) -->
    if (fileExtensions == null || fileExtensions.length == 0
        || fileDescriptions == null)
      return null;
    String fileExtension = fileExtensions[0];
    // <-- Added for Intergeo File Format (Yves Kreis)

    initFileChooser();
    fileChooser.setCurrentDirectory(app.getCurrentPath());

    // set selected file
    // Modified for Intergeo File Format (Yves Kreis) -->
    /*
     * if (selectedFile == null) { selectedFile =
     * removeExtension(fileChooser.getSelectedFile()); }
     */
    // <-- Modified for Intergeo File Format (Yves Kreis)
    if (selectedFile != null) {
      // Added for Intergeo File Format (Yves Kreis) -->
      fileExtension = Application.getExtension(selectedFile);
      int i = 0;
      while (i < fileExtensions.length
          && !fileExtension.equals(fileExtensions[i]))
        i++;
      if (i >= fileExtensions.length)
        fileExtension = fileExtensions[0];
      // <-- Added for Intergeo File Format (Yves Kreis)
      selectedFile = addExtension(selectedFile, fileExtension);
      fileChooser.setSelectedFile(selectedFile);
    }

    // Modified for Intergeo File Format (Yves Kreis) -->
    /*
     * MyFileFilter fileFilter = new MyFileFilter();
     * fileFilter.addExtension(fileExtension); if (fileDescription != null)
     * fileFilter.setDescription(fileDescription);
     * fileChooser.resetChoosableFileFilters();
     * fileChooser.setFileFilter(fileFilter);
     */
    fileChooser.resetChoosableFileFilters();
    MyFileFilter fileFilter;
    MyFileFilter mainFilter = null;
    for (int i = 0; i < fileExtensions.length; i++) {
      fileFilter = new MyFileFilter(fileExtensions[i]);
      if (fileDescriptions.length >= i && fileDescriptions[i] != null)
        fileFilter.setDescription(fileDescriptions[i]);
      fileChooser.addChoosableFileFilter(fileFilter);
      if (fileExtension.equals(fileExtensions[i]))
        mainFilter = fileFilter;
    }
    fileChooser.setFileFilter(mainFilter);
    // <-- Modified for Intergeo File Format (Yves Kreis)

    while (!done) {
      // show save dialog
      int returnVal = fileChooser.showSaveDialog(app.getMainComponent());
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        file = fileChooser.getSelectedFile();

        // Added for Intergeo File Format (Yves Kreis) -->
        if (fileChooser.getFileFilter() instanceof geogebra.gui.app.MyFileFilter) {
          fileFilter = (MyFileFilter) fileChooser.getFileFilter();
          fileExtension = fileFilter.getExtension();
        } else
          fileExtension = fileExtensions[0];

        // remove all special characters from HTML filename
        if (fileExtension == Application.FILE_EXT_HTML) {
          file = removeExtension(file);
          file = new File(file.getParent(), Util.keepOnlyLettersAndDigits(file
              .getName()));
        }

        // remove "*<>/\?|:
        file = new File(file.getParent(), Util.processFilename(file.getName())); // Michael
        // Borcherds
        // 2007-11-23

        // add file extension
        file = addExtension(file, fileExtension);
        fileChooser.setSelectedFile(file);

        if (file.exists()) {
          // ask overwrite question

          // Michael Borcherds 2008-05-04
          Object[] options = {geogebra.Menu.Overwrite,
              geogebra.Menu.DontOverwrite};
          int n = JOptionPane.showOptionDialog(app.getMainComponent(),
              Plain.OverwriteFile + "\n" + file.getName(), Plain.Question,
              JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
              options, options[1]);

          done = n == 0;
        } else
          done = true;
      } else {
        file = null;
        break;
      }
      // <-- Modified for Intergeo File Format (Yves Kreis)
    }

    return file;
  }

  /**
   * Creates a new slider at given location (screen coords).
   * 
   * @return whether a new slider (number) was create or not
   */
  public boolean showSliderCreationDialog(int x, int y) {
    app.setWaitCursor();

    SliderDialog dialog = new SliderDialog(app, x, y);
    dialog.setVisible(true);
    GeoNumeric num = (GeoNumeric) dialog.getResult();
    if (num != null) {
      // make sure that we show name and value of slider
      num.setLabelMode(GeoElement.LABEL_NAME_VALUE);
      num.setLabelVisible(true);
      num.update();
    }

    app.setDefaultCursor();

    return num != null;
  }

  public boolean showSpreadsheetView() {
    return showView(Application.VIEW_SPREADSHEET);
  }

  /**
   * Creates a new text at given startPoint
   */
  public void showTextCreationDialog(GeoPoint startPoint) {
    showTextDialog(null, startPoint);
  }

  /**
   * Displays the text dialog for a given text.
   */
  public void showTextDialog(GeoText text) {
    showTextDialog(text, null);
  }

  private void showTextDialog(GeoText text, GeoPoint startPoint) {
    app.setWaitCursor();
    JDialog dialog = createTextDialog(text, startPoint);
    dialog.setVisible(true);
    app.setDefaultCursor();
  }

  /**
   * Displays the configuration dialog for the toolbar
   */
  public void showToolbarConfigDialog() {
    app.getEuclidianView().resetMode();
    ToolbarConfigDialog dialog = new ToolbarConfigDialog(app);
    dialog.setVisible(true);
  }

  public void showURLinBrowser(String strURL) {
    try {
      URL url = new URL(strURL);
      showURLinBrowser(url);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void showURLinBrowser(URL url) {
    // if (applet != null) {
    // applet.getAppletContext().showDocument(url, "_blank");
    // } else {
    BrowserLauncher.openURL(url.toExternalForm());
    // }
  }

  private boolean showView(int viewId) {
    return layout.getDockManager().getPanel(viewId).getInfo().isVisible();
  }

  public void startCollectingSpreadsheetTraces() {
    if (spreadsheetView != null)
      spreadsheetView.startCollectingSpreadsheetTraces();
  }

  public void startEditing(GeoElement geo) {
    ((AlgebraView) getAlgebraView()).startEditing(geo);
  }

  public void stopCollectingSpreadsheetTraces() {
    if (spreadsheetView != null)
      spreadsheetView.stopCollectingSpreadsheetTraces();
  }

  public void traceToSpreadsheet(GeoElement geo) {
    if (spreadsheetView != null)
      spreadsheetView.traceToSpreadsheet(geo);
  }

  public void undo() {
    app.setWaitCursor();
    kernel.undo();
    updateActions();
    app.setDefaultCursor();
    System.gc();
  }

  public void updateActions() {
    if (app.isUndoActive() && undoAction != null) {
      undoAction.setEnabled(kernel.undoPossible());
      redoAction.setEnabled(kernel.redoPossible());
    }
  }

  public void updateAlgebraInput() {
    if (algebraInput != null)
      algebraInput.initGUI();
  }

  public void updateConstructionProtocol() {
    if (constProtocol != null)
      constProtocol.update();
  }

  public void updateFonts() {
    if (algebraView != null)
      algebraView.updateFonts();
    if (spreadsheetView != null)
      spreadsheetView.updateFonts();
    if (algebraInput != null)
      algebraInput.updateFonts();

    if (fileChooser != null) {
      fileChooser.setFont(app.getPlainFont());
      SwingUtilities.updateComponentTreeUI(fileChooser);
    }

    if (optionsDialog != null)
      SwingUtilities.updateComponentTreeUI(optionsDialog);

    if (appToolbarPanel != null)
      appToolbarPanel.initToolbar();

    if (propDialog != null)
      propDialog.initGUI();
    if (constProtocol != null)
      constProtocol.initGUI();
    if (constProtocolNavigation != null)
      constProtocolNavigation.initGUI();

    if (app.hasCasView())
      app.getCasView().updateFonts();

    SwingUtilities.updateComponentTreeUI(app.getMainComponent());
  }

  public void updateFrameSize() {
    JFrame fr = app.getFrame();
    if (fr != null)
      ((GeoGebraFrame) fr).updateSize();
  }

  public void updateFrameTitle() {
    GeoGebraFrame frame = (GeoGebraFrame) app.getFrame();

    StringBuffer sb = new StringBuffer();
    sb.append("GeoGebra");
    if (app.getCurrentFile() != null) {
      sb.append(" - ");
      sb.append(app.getCurrentFile().getName());
    } else if (GeoGebraFrame.getInstanceCount() > 1) {
      int nr = frame.getInstanceNumber();
      sb.append(" (");
      sb.append(nr + 1);
      sb.append(")");
    }
    frame.setTitle(sb.toString());
  }

  private void updateGUIafterLoadFile(boolean success, boolean isMacroFile) {
    if (success && !isMacroFile && !app.isIgnoringDocumentPerspective())
      setPerspectives(app.getTmpPerspectives());

    // force JavaScript ggbOnInit(); to be called
    if (!app.isApplet())
      app.getScriptManager().evalScript("ggbOnInit();");

    if (isMacroFile) {
      app.updateToolBar();
      app.updateContentPane();
    } else // update GUI
    if (app.getEuclidianView().hasPreferredSize()) {

      // Michael Borcherds 2008-04-27 BEGIN
      // Scale drawing pad down if it doesn't fit on the screen

      // calculate titlebar height
      // TODO is there a better way?
      // getFrame().getHeight() -
      // getFrame().getContentPane().getHeight(); doesn't seem to give
      // the right answer
      JFrame testFrame = new JFrame();
      JFrame testFrame2 = new JFrame();

      testFrame.setUndecorated(false);
      testFrame.setVisible(true);
      int height1 = testFrame.getHeight();
      testFrame.setVisible(false);
      testFrame2.setUndecorated(true);
      testFrame2.setVisible(true);
      int height2 = testFrame2.getHeight();
      testFrame2.setVisible(false);

      int titlebarHeight = height1 - height2 - 5;

      double height = app.getEuclidianView().getPreferredSize().height;
      double width = app.getEuclidianView().getPreferredSize().width;

      int furnitureWidth = app.getPreferredSize().width;
      int furnitureHeight = app.getPreferredSize().height - titlebarHeight;

      // GraphicsEnvironment env = GraphicsEnvironment
      // .getLocalGraphicsEnvironment();

      // Rectangle screenSize = env.getMaximumWindowBounds();
      Rectangle screenSize = app.getScreenSize();
      // takes
      // Windows
      // toolbar
      // (etc)
      // into
      // account

      // fake smaller screen for testing
      // screenSize.width=1024; screenSize.height=768;

      // Application.debug(width);
      // Application.debug(screenSize.width - furnitureWidth);
      // Application.debug(screenSize.width );
      // Application.debug(height);
      // Application.debug(screenSize.height-furnitureHeight);
      // Application.debug(screenSize.height);

      if (width > screenSize.width - furnitureWidth
          || height > screenSize.height - furnitureHeight) {

        Application.debug("Screen too small, resizing to fit" + "\nwidth = "
            + width + "\nscreenSize.width = " + screenSize.width
            + "\nfurnitureWidth = " + furnitureWidth + "\nheight = " + height
            + "\nscreenSize.height = " + screenSize.height
            + "\nfurnitureHeight = " + furnitureHeight);

        // close algebra and spreadsheet views
        // app.setShowAlgebraView(false);
        // app.setShowSpreadsheetView(false);

        double xscale = app.getEuclidianView().getXscale();
        double yscale = app.getEuclidianView().getYscale();
        double xZero = app.getEuclidianView().getXZero();
        double yZero = app.getEuclidianView().getYZero();
        double scale_down = Math.max(width
            / (screenSize.width - furnitureWidth), height
            / (screenSize.height - furnitureHeight));
        Application.debug(scale_down + "");
        app.getEuclidianView()
            .setCoordSystem(xZero / scale_down, yZero / scale_down,
                xscale / scale_down, yscale / scale_down, false);
      }

      // now check all absolute objects are still on screen
      Construction cons = kernel.getConstruction();
      TreeSet<GeoElement> geoSet = cons.getGeoSetConstructionOrder();

      int i = 0;
      Iterator<GeoElement> it = geoSet.iterator();
      while (it.hasNext()) { // iterate through all objects
        GeoElement geo = it.next();

        if (geo.isGeoText())
          if (((GeoText) geo).isAbsoluteScreenLocActive()) {
            GeoText geoText = (GeoText) geo;
            boolean fixed = geoText.isFixed();

            int x = geoText.getAbsoluteScreenLocX();
            int y = geoText.getAbsoluteScreenLocY();

            geoText.setFixed(false);
            if (x > screenSize.width)
              geoText.setAbsoluteScreenLoc(x = screenSize.width
                  - furnitureWidth - 100, y);
            if (y > screenSize.height)
              geoText.setAbsoluteScreenLoc(x, y = screenSize.height
                  - furnitureHeight);
            geoText.setFixed(fixed);
          }
        if (geo.isGeoNumeric())
          if (((GeoNumeric) geo).isAbsoluteScreenLocActive()) {
            GeoNumeric geoNum = (GeoNumeric) geo;
            boolean fixed = geoNum.isSliderFixed();

            int x = geoNum.getAbsoluteScreenLocX();
            int y = geoNum.getAbsoluteScreenLocY();

            int sliderWidth = 20, sliderHeight = 20;
            if (geoNum.isSliderHorizontal())
              sliderWidth = (int) geoNum.getSliderWidth(); // else
            // sliderHeight
            // =
            // (
            // int
            // )
            // geoNum
            // .
            // getSliderWidth
            // (
            // )
            // ;
            geoNum.setSliderFixed(false);

            if (x + sliderWidth > screenSize.width)
              geoNum.setAbsoluteScreenLoc(x = screenSize.width - sliderWidth
                  - furnitureWidth, y);
            if (y + sliderHeight > screenSize.height)
              geoNum.setAbsoluteScreenLoc(x, y = screenSize.height
                  - sliderHeight - furnitureHeight);
            geoNum.setSliderFixed(fixed);
          }

        i++;
      }

      // Michael Borcherds 2007-04-27 END

      // update GUI: size of euclidian view was set
        //OlegPerchenok
      //app.updateContentPaneAndSize();
    } else
     app.updateContentPane();
  }

  /**
   * Loads java-ui.properties and sets all key-value pairs using
   * UIManager.put(). This is needed to translate JFileChooser to languages not
   * supported by Java natively.
   */
  private void updateJavaUILanguage() {
    // load properties jar file
    if (currentLocale == app.getLocale() || !app.loadPropertiesJar())
      return;

    // update locale
    currentLocale = app.getLocale();

    // load javaui properties file for specific locale
    rbJavaUI = MyResourceBundle.loadSingleBundleFile(Application.RB_JAVA_UI
        + "_" + currentLocale);
    boolean foundLocaleFile = rbJavaUI != null;
    if (!foundLocaleFile)
      // fall back on English
      rbJavaUI = MyResourceBundle.loadSingleBundleFile(Application.RB_JAVA_UI);

    // set or delete all keys in UIManager
    Enumeration<?> keys = rbJavaUI.getKeys();
    while (keys.hasMoreElements()) {
      String key = (String) keys.nextElement();
      String value = foundLocaleFile ? rbJavaUI.getString(key) : null;

      // set or delete UIManager key entry (set values to null when locale file
      // not found)
      UIManager.put(key, value);
    }

    // update file chooser
    if (fileChooser != null) {
      fileChooser.setLocale(currentLocale);
      SwingUtilities.updateComponentTreeUI(fileChooser);
    }
  }

  /**
   * Make the title bar visible if the user is using an applet.
   * 
   * Active the glass pane if the application is changing from applet to frame
   * mode.
   */
  public void updateLayout() {
    layout.setTitlebarVisible(!app.isApplet());
    layout.getDockManager().updateGlassPane();
  }

  public void updateMenubar() {
    if (menuBar != null)
      menuBar.updateMenubar();
  }

  public void updateMenubarSelection() {
    if (menuBar != null)
      menuBar.updateSelection();
  }

  public void updateMenuFile() {
    if (menuBar != null)
      menuBar.updateMenuFile();
  }

  public void updateMenuWindow() {
    if (menuBar != null)
      menuBar.updateMenuWindow();
  }

  public void updateSpreadsheetColumnWidths() {
    if (spreadsheetView != null)
      spreadsheetView.updateColumnWidths();
  }

  public void updateToolbar() {
    if (appToolbarPanel != null)
      appToolbarPanel.initToolbar();
  }

}
