package testSystem.frames;

import geogebra.gui.app.GeoGebraFrame;
import ru.ipo.structurededitor.StructuredEditorWithActions;
import testSystem.TestEditorGeom;
import geogebra.main.Application;
import geogebra.main.DefaultApplication;
import geogebra.main.GeoGebraPreferences;
import geogebra.util.Util;
import ru.ipo.structurededitor.StructuredEditor;
import testSystem.structureSerializer.NodesRegistry;
import testSystem.lang.geom.GeoStatement;
import ru.ipo.structurededitor.view.StructuredEditorModel;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 20.03.11
 * Time: 18:50
 */
public class GeoGebraFrameWithStructEd extends GeoGebraFrame {
    private static final Logger log = Logger.getLogger(GeoGebraFrameWithStructEd.class.getName());

    public static void main(String[] args) {
        log.fine("check java version");
        double javaVersion = Util.getJavaVersion();
        if (javaVersion < 1.42) {
            JOptionPane
                    .showMessageDialog(
                            null,
                            "Sorry, GeoGebra cannot be used with your Java version "
                                    + javaVersion
                                    + "\nPlease visit http://www.java.com to get a newer version of Java.");
            return;
        }

        if (Application.MAC_OS)
            initMacSpecifics();

        // set system look and feel
        log.fine("set system look and feel");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            Application.debug(e + "");
        }

        log.fine("load list of previously used files");
        GeoGebraPreferences.getPref().loadFileList();

        StructuredEditor.initializeStructuredEditorUI();

        // create first window and show it
        log.fine("create first window and show it");
        createNewWindow(args);
    }

    public static synchronized GeoGebraFrame createNewWindow(String[] args) {
        // set Application's size, position and font size
        // TODO: Add layout glass pane (F.S.)
        GeoGebraFrame wnd = new GeoGebraFrameWithStructEd();
        wnd.setLayout(new BorderLayout());

        final DefaultApplication app = new DefaultApplication(args, wnd, true);

        log.info("Load GUI JAR");
        app.loadGUIJar();
        app.getGuiManager().initMenubar();

        // init GUI
        log.info("init GUI");
        //app.setShowMenuBar(false);
        //app.setShowMenuBar(true);
        wnd.setApplication(app);

        NodesRegistry nodesRegistry = TestEditorGeom.nodesRegistryPrep();

        //StructuredEditor

        //------------Frame preparation
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(8);
        wnd.add(splitPane, BorderLayout.CENTER);

        splitPane.setRightComponent(app.buildApplicationPanel());

        GeoStatement st = new GeoStatement();
        final StructuredEditorModel model = TestEditorGeom.createModel(st);

        structuredEditor = new StructuredEditor(model);

        splitPane.setLeftComponent(new StructuredEditorWithActions(structuredEditor));
        structuredEditor.requestFocusInWindow();
        structuredEditor.setApp(app);

        app.getGuiManager().setShowAlgebraView(false);
        wnd.add(TestEditorGeom.createBars(wnd, structuredEditor, nodesRegistry, null), BorderLayout.NORTH);

        //---StructuredEditor
        wnd.setDropTarget(new DropTarget(wnd,
                new geogebra.gui.FileDropTargetListener(app)));
        wnd.addWindowFocusListener(wnd);

        updateAllTitles();
        wnd.setVisible(true);

        // init some things in the background
        log.info("init some things in the background");
        if (!app.isApplet()) {
            Thread runner = new Thread() {
                @Override
                public void run() {
                    log.info("init CAS");
                    app.initCAS();

                    log.info("init properties dialog");
                    app.getGuiManager().initPropertiesDialog();

                    log.info("init file chooser");
                    app.getGuiManager().initFileChooser();

                    log.info("copy Jar files to temp directory");
                    app.downloadJarFiles();
                }
            };
            runner.start();
        }
        //structuredEditor.getApp().getEuclidianView().setSelectionRectangle(new Rectangle(
        //                structuredEditor.getApp().getEuclidianView().getSize()));
        //structuredEditor.getApp().selectAll(0);
        return wnd;
    }

    @Override
    public void setVisible(boolean flag) {
        super.setVisible(flag);
        structuredEditor.setSize(structuredEditor.getPreferredSize());
        structuredEditor.repaint();
    }

     public void updateSize() {
        super.updateSize();
        Dimension dim = getSize(),
                dim1 = structuredEditor.getPreferredSize();
        structuredEditor.setSize(dim1);

        dim.setSize(dim.getWidth() + dim1.getWidth(), dim.getHeight() + dim1.getHeight());
        Rectangle screenSize = app.getScreenSize();

        if (dim.width > screenSize.width
                || dim.height > screenSize.height) {
            dim.width = screenSize.width;
            dim.height = screenSize.height;
            setLocation(0, 0);
        }
        setSize(dim);
    }

    private static StructuredEditor structuredEditor;


}
