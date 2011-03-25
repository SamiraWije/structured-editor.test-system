package geogebra.gui.menubar;

import geogebra.Plain;
import geogebra.gui.app.GeoGebraFrame;
import geogebra.gui.inputbar.AlgebraInput;
import geogebra.gui.util.ImageSelection;
import geogebra.main.Application;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * The "File" menu.
 */
class FileMenu extends BaseMenu {
  private static final long serialVersionUID = -5154067739481481835L;

  private AbstractAction newWindowAction, deleteAll, saveAction, saveAsAction,
      loadAction, exportWorksheet, exportGraphicAction, exportPgfAction,
      exportPSTricksAction, drawingPadToClipboardAction,
      printEuclidianViewAction, exitAction, exitAllAction;

  protected FileMenu(Application app) {
    super(app, geogebra.Menu.File);

    initActions();
    update();
  }

  /**
   * Initialize all actions of this menu.
   */
  private void initActions() {
    deleteAll = new AbstractAction(geogebra.Menu.New, app.getEmptyIcon()) {
      private static final long serialVersionUID = 1L;

      public void actionPerformed(ActionEvent e) {
        app.clearConstruction();

        // clear input bar
        if (app.hasGuiManager()) {
          AlgebraInput ai = (AlgebraInput) app.getGuiManager()
              .getAlgebraInput();
          ai.replaceString(null);
        }
      }
    };

    newWindowAction = new AbstractAction(geogebra.Menu.NewWindow, app
        .getImageIcon("document-new.png")) {
      private static final long serialVersionUID = 1L;

      public void actionPerformed(ActionEvent e) {
        Thread runner = new Thread() {
          @Override
          public void run() {
            app.setWaitCursor();
            GeoGebraFrame.createNewWindow(null);
            app.setDefaultCursor();
          }
        };
        runner.start();
      }
    };

    saveAction = new AbstractAction(geogebra.Menu.Save, app
        .getImageIcon("document-save.png")) {
      private static final long serialVersionUID = 1L;

      public void actionPerformed(ActionEvent e) {
        app.getGuiManager().save();
      }
    };

    saveAsAction = new AbstractAction(geogebra.Menu.SaveAs + " ...", app
        .getEmptyIcon()) {
      private static final long serialVersionUID = 1L;

      public void actionPerformed(ActionEvent e) {
        app.getGuiManager().saveAs();
      }
    };

    printEuclidianViewAction = new AbstractAction(Plain.DrawingPad + " ...") {
      private static final long serialVersionUID = 1L;

      public void actionPerformed(ActionEvent e) {
        GeoGebraMenuBar.showPrintPreview(app);
      }
    };

    exitAction = new AbstractAction(geogebra.Menu.Close, app
        .getImageIcon("exit.png")) {
      private static final long serialVersionUID = 1L;

      public void actionPerformed(ActionEvent e) {
        app.exit();
      }
    };

    exitAllAction = new AbstractAction(geogebra.Menu.CloseAll, app
        .getEmptyIcon()) {
      private static final long serialVersionUID = 1L;

      public void actionPerformed(ActionEvent e) {
        app.exitAll();
      }
    };

    loadAction = new AbstractAction(geogebra.Menu.Load + " ...", app
        .getImageIcon("document-open.png")) {
      private static final long serialVersionUID = 1L;

      public void actionPerformed(ActionEvent e) {
        app.getGuiManager().openFile();
      }
    };

    drawingPadToClipboardAction = new AbstractAction(
        geogebra.Menu.DrawingPadToClipboard, app.getImageIcon("edit-copy.png")) {
      private static final long serialVersionUID = 1L;

      public void actionPerformed(ActionEvent e) {
        app.clearSelectedGeos();

        Thread runner = new Thread() {
          @Override
          public void run() {
            app.setWaitCursor();
            // copy drawing pad to the system clipboard
            Image img = app.getEuclidianView().getExportImage(1d);
            ImageSelection imgSel = new ImageSelection(img);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                imgSel, null);
            app.setDefaultCursor();
          }
        };
        runner.start();
      }
    };

    /*
     * updateAction = new AbstractAction(geogebra.Menu.Update, getEmptyIcon()) {
     * private static final long serialVersionUID = 1L; public void
     * actionPerformed(ActionEvent e) { Thread runner = new Thread() { public
     * void run() { updateGeoGebra(); } }; runner.start(); } };
     */

    exportGraphicAction = new AbstractAction(Plain.DrawingPadAsPicture + " ("
        + Application.FILE_EXT_PNG + ", " + Application.FILE_EXT_EPS + ") ...",
        app.getImageIcon("image-x-generic.png")) {
      private static final long serialVersionUID = 1L;

      public void actionPerformed(ActionEvent e) {
        app.loadExportJar();

        try {
          Thread runner = new Thread() {
            @Override
            public void run() {
              app.setWaitCursor();
              try {
                app.clearSelectedGeos();

                // use reflection for
                JDialog d = new geogebra.export.GraphicExportDialog(app);
                // Class casViewClass =
                // Class.forName("geogebra.export.GraphicExportDialog");
                // Object[] args = new Object[] { app };
                // Class [] types = new Class[]
                // {Application.class};
                // Constructor constructor =
                // casViewClass.getDeclaredConstructor(types);
                // JDialog d = (JDialog)
                // constructor.newInstance(args);

                d.setVisible(true);

              } catch (Exception e) {
                Application.debug("GraphicExportDialog not available");
              }
              app.setDefaultCursor();
            }
          };
          runner.start();
        }

        catch (java.lang.NoClassDefFoundError ee) {
          app.showErrorDialog(app.getError("ExportJarMissing"));
          ee.printStackTrace();
        }
      }
    };

    exportPSTricksAction = new AbstractAction(Plain.DrawingPadAsPSTricks
        + " ...", app.getEmptyIcon()) {
      private static final long serialVersionUID = 1L;

      public void actionPerformed(ActionEvent e) {
        try {
          app.loadExportJar();
          new geogebra.export.pstricks.GeoGebraToPstricks(app);
        } catch (Exception ex) {
          Application.debug("GeoGebraToPstricks not available");
        } catch (java.lang.NoClassDefFoundError ee) {
          app.showErrorDialog(app.getError("ExportJarMissing"));
          ee.printStackTrace();
        }
      }
    };
    exportPgfAction = new AbstractAction(Plain.DrawingPagAsPGF + " ...", app
        .getEmptyIcon()) {
      private static final long serialVersionUID = 1L;

      public void actionPerformed(ActionEvent e) {
        try {
          app.loadExportJar();
          new geogebra.export.pstricks.GeoGebraToPgf(app);
        } catch (Exception ex) {
          Application.debug("GeoGebraToPGF not available");
        } catch (java.lang.NoClassDefFoundError ee) {
          app.showErrorDialog(app.getError("ExportJarMissing"));
          ee.printStackTrace();
        }
      }
    };

    exportWorksheet = new AbstractAction(Plain.DynamicWorksheetAsWebpage + " ("
        + Application.FILE_EXT_HTML + ") ...", app
        .getImageIcon("text-html.png")) {
      private static final long serialVersionUID = 1L;

      public void actionPerformed(ActionEvent e) {
        app.loadExportJar();

        try {

          Thread runner = new Thread() {
            @Override
            public void run() {
              app.setWaitCursor();
              try {
                app.clearSelectedGeos();
                geogebra.export.WorksheetExportDialog d = new geogebra.export.WorksheetExportDialog(
                    app);

                d.setVisible(true);
              } catch (Exception e) {
                Application.debug("WorksheetExportDialog not available");
                e.printStackTrace();
              }
              app.setDefaultCursor();
            }
          };
          runner.start();
        }

        catch (java.lang.NoClassDefFoundError ee) {
          app.showErrorDialog(app.getError("ExportJarMissing"));
          ee.printStackTrace();
        }
      }
    };
  }
  @Override
  public void update() {
    // TODO Auto-generated method stub

    updateItems();
  }

  /**
   * Initialize all items.
   */
  private void updateItems() {
    removeAll();

    JMenuItem mi;

    if (!app.isApplet()) {
      // "New" in application: new window
      mi = new JMenuItem(newWindowAction);
      setMenuShortCutAccelerator(mi, 'N');
      add(mi);
    }

    // "New": reset
    mi = add(deleteAll);

    mi = add(loadAction);
    setMenuShortCutAccelerator(mi, 'O'); // open
    addSeparator();
    mi = add(saveAction);
    setMenuShortCutAccelerator(mi, 'S');
    mi = add(saveAsAction);
    // now assigned to spreadsheet: setMenuShortCutShiftAccelerator(mi,
    // 'S');
    addSeparator();
    // submenu = new JMenu(geogebra.Menu.PrintPreview);
    // submenu.setIcon(app.getImageIcon("document-print-preview.png"));
    // submenu.add(printEuclidianViewAction);
    // // submenu.add(printProtocolAction);
    // menu.add(submenu);
    mi = add(printEuclidianViewAction);
    mi.setText(geogebra.Menu.PrintPreview);
    mi.setIcon(app.getImageIcon("document-print-preview.png"));
    setMenuShortCutAccelerator(mi, 'P');

    // export
    JMenu submenu = new JMenu(geogebra.Menu.Export);
    submenu.setIcon(app.getEmptyIcon());
    add(submenu);
    mi = submenu.add(exportWorksheet);
    setMenuShortCutShiftAccelerator(mi, 'W');

    submenu.addSeparator();
    // submenu.add(htmlCPAction);
    mi = submenu.add(exportGraphicAction);
    setMenuShortCutShiftAccelerator(mi, 'P');

    mi = submenu.add(drawingPadToClipboardAction);
    setMenuShortCutShiftAccelerator(mi, 'C');

    submenu.addSeparator();
    mi = submenu.add(exportPSTricksAction);
    setMenuShortCutShiftAccelerator(mi, 'T');

    // Added by Loïc Le Coq
    mi = submenu.add(exportPgfAction);
    // End

    // DONE HERE WHEN APPLET
    if (app.isApplet())
      return;

    // LAST FILES list
    int size = Application.getFileListSize();
    if (size > 0) {
      addSeparator();
      for (int i = 0; i < 4; i++) {
        File file = Application.getFromFileList(i);
        if (file != null) {
          mi = new JMenuItem(file.getName());
          mi.setIcon(app.getImageIcon("geogebra.gif"));
          ActionListener al = new LoadFileListener(app, file);
          mi.addActionListener(al);
          add(mi);
        }
      }
    }

    // close
    addSeparator();
    mi = add(exitAction);
    if (Application.MAC_OS)
      setMenuShortCutAccelerator(mi, 'W');
    else {
      // Alt + F4
      KeyStroke ks = KeyStroke
          .getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK);
      mi.setAccelerator(ks);
    }

    // close all
    if (GeoGebraFrame.getInstanceCount() > 1)
      add(exitAllAction);
  }
}
