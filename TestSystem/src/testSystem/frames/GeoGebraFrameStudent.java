package testSystem.frames;

import geogebra.gui.app.GeoGebraFrame;
import testSystem.TestEditorGeom;
import geogebra.main.Application;
import geogebra.main.DefaultApplication;
import geogebra.main.GeoGebraPreferences;
import geogebra.util.Util;
import ru.ipo.structurededitor.StructuredEditor;
import testSystem.structureSerializer.NodesRegistry;
import testSystem.lang.geom.GeoStatement;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import testSystem.util.LogConfigLoader;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.*;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 20.03.11
 * Time: 18:50
 */
public class GeoGebraFrameStudent extends GeoGebraFrame {
    private static final Logger log = Logger.getLogger(GeoGebraFrameStudent.class.getName());

    public static void main(String[] args) {
        LogConfigLoader.configureLogger();
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
        GeoGebraFrame wnd = new GeoGebraFrameStudent();
        wnd.setLayout(new BorderLayout());

        final DefaultApplication app = new DefaultApplication(args, wnd, true);

        log.info("Load GUI JAR");
        app.loadGUIJar();
        //app.getGuiManager().initMenubar();

        // init GUI
        log.info("init GUI");
        app.setShowMenuBar(false);
        wnd.setApplication(app);

        //StructuredEditor
        NodesRegistry nodesRegistry = TestEditorGeom.nodesRegistryPrep();


        //------------Frame preparation

        GeoStatement st = new GeoStatement();
        final StructuredEditorModel model = TestEditorGeom.createModel(st);

//        f.add(new JScrollPane(new JTextArea("asdf")));
        structuredEditor = new StructuredEditor(model,true);


        JPanel taskPanel= new JPanel(new BorderLayout());


        StyleContext sc = new StyleContext();
        final DefaultStyledDocument doc = new DefaultStyledDocument(sc);


        JTextPane textPane = new JTextPane(doc);
        taskPanel.add(textPane, BorderLayout.CENTER);

        textPane.setEditable(false);
        final Style heading2Style = sc.addStyle("Heading2", null);
        //heading2Style.addAttribute(StyleConstants.Foreground, Color.red);
        heading2Style.addAttribute(StyleConstants.FontSize, 16);
        heading2Style.addAttribute(StyleConstants.FontFamily, "serif");
        heading2Style.addAttribute(StyleConstants.Bold, true);
        heading2Style.addAttribute(StyleConstants.ALIGN_CENTER, true);

        final Style defaultStyle = sc.addStyle("Default", null);

        StyledDocument styledDocument =  textPane.getStyledDocument();

        try {
            //((GeoStatement)model.getObject()).getTitle()

            styledDocument.remove(0,styledDocument.getLength());
            styledDocument.insertString(0,"Откройте задачу\n Здесь будет условие\n",null);
            //doc.setParagraphAttributes(0, 1, heading2Style, false);

        }catch (Exception e) {
            throw new Error("Text HTML error"+e);
        }
        Border border = BorderFactory.createLineBorder(Color.BLACK);

        JScrollPane structuredEditorScrPane = new JScrollPane(structuredEditor);
        wnd.getContentPane().add(taskPanel, BorderLayout.BEFORE_FIRST_LINE);
        wnd.getContentPane().add(app.buildApplicationPanel(), BorderLayout.CENTER);
        textPane.setBorder(border);
        taskPanel.add(textPane, BorderLayout.CENTER);

        //wnd.getContentPane().add(structuredEditorScrPane, BorderLayout.CENTER);
        structuredEditor.requestFocusInWindow();
        structuredEditor.setApp(app);
        JToolBar toolBar = TestEditorGeom.createBars(wnd, structuredEditor, nodesRegistry,styledDocument);
        taskPanel.add(toolBar, BorderLayout.SOUTH);
        //---StructuredEditor
        wnd.setDropTarget(new DropTarget(wnd,
                new geogebra.gui.FileDropTargetListener(app)));
        wnd.addWindowFocusListener(wnd);
        updateAllTitles();
        wnd.setVisible(true);
        app.getGuiManager().setShowAlgebraView(false);

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

    /*public void updateSize() {
        super.updateSize();
        /*if (!structuredEditor.isView()){
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
    }  */

    private static StructuredEditor structuredEditor;


}
