package testSystem;

import com.thoughtworks.xstream.converters.Converter;
import geogebra.euclidian.EuclidianConstants;
import geogebra.euclidian.EuclidianView;
import geogebra.gui.inputbar.AlgebraInput;
import geogebra.main.Application;
import ru.ipo.structurededitor.StructuredEditor;
import ru.ipo.structurededitor.controller.EditorsRegistry;
import ru.ipo.structurededitor.controller.ModificationHistory;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.model.EnumFieldParams;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.events.ImageLoadEvent;
import ru.ipo.structurededitor.view.events.ImageLoadListener;
import testSystem.lang.DSP.*;
import testSystem.lang.comb.Statement;
import testSystem.lang.geom.GeoStatement;
import testSystem.lang.geom.Instrum;
import testSystem.lang.logic.LogicStatement;
import testSystem.structureBuilder.XStreamBuilder;
import testSystem.structureSerializer.NodesRegistry;
import testSystem.structureSerializer.XStreamSerializer;
import testSystem.util.GeoElementConverter;
import testSystem.util.IOUtils;
import testSystem.view.PicturePanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.StyledDocument;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Олег
 * Date: 26.08.2010
 * Time: 16:15:33
 */
public class MenuHandlerFactory {
    private static final Logger log = Logger.getLogger(MenuHandlerFactory.class.getName());

    private static final HashMap<Instrum, Integer> INSTRUM_MODES = new HashMap<Instrum, Integer>();

    static {
        INSTRUM_MODES.put(Instrum.POINT, EuclidianView.MODE_POINT);
        INSTRUM_MODES.put(Instrum.LINE_PERPEND, EuclidianView.MODE_ORTHOGONAL);
        INSTRUM_MODES.put(Instrum.LINE_PARALL, EuclidianView.MODE_PARALLEL);
        INSTRUM_MODES.put(Instrum.LINE_TWO_POINTS, EuclidianView.MODE_JOIN);
        INSTRUM_MODES.put(Instrum.CIRCLE_CENTER_RAD, EuclidianView.MODE_CIRCLE_POINT_RADIUS);
        INSTRUM_MODES.put(Instrum.CIRCLE_CENTER_POINT, EuclidianView.MODE_CIRCLE_TWO_POINTS);
        INSTRUM_MODES.put(Instrum.MIDPOINT, EuclidianView.MODE_MIDPOINT);
        INSTRUM_MODES.put(Instrum.SEGMENT_TWO_POINTS, EuclidianView.MODE_SEGMENT);
        INSTRUM_MODES.put(Instrum.SEGMENT_FIXED, EuclidianView.MODE_SEGMENT_FIXED);
        INSTRUM_MODES.put(Instrum.ANGLE_THREE_POINTS, EuclidianView.MODE_ANGLE);
        INSTRUM_MODES.put(Instrum.ANGLE_FIXED, EuclidianView.MODE_ANGLE_FIXED);
        INSTRUM_MODES.put(Instrum.RAY_TWO_POINTS, EuclidianView.MODE_RAY);
    }

    private final Container f;
    //XMLViewer xmlV;
    private final StructuredEditor structuredEditor, answerEditor;
    private final NodesRegistry nodesRegistry;
    private final String subSystem;

    private DSLBean ans;
    private JTextField combAns;
    private StyledDocument styledDocument;
    private String openDir = "";
    private boolean algView = false;
    private String filename = "";
    private JPanel taskPanel;
    private PicturePanel picturePanel=null;
    private StructuredEditor panelEditor;
    //public MyMenuHandler(JFrame f, XMLViewer xmlV, StructuredEditor structuredEditor){

    public MenuHandlerFactory(final Container f, StructuredEditor structuredEditor, NodesRegistry nodesRegistry,
                              String subSystem, StructuredEditor answerEditor, JTextField combAns,
                              StyledDocument styledDocument, JPanel taskPanel, StructuredEditor panelEditor) {
        this(f, structuredEditor, nodesRegistry, subSystem, answerEditor, combAns, styledDocument);
        this.taskPanel = taskPanel;
        this.panelEditor = panelEditor;
    }

    public MenuHandlerFactory(final Container f, StructuredEditor structuredEditor, NodesRegistry nodesRegistry, String subSystem,
                              StructuredEditor answerEditor, JTextField combAns, StyledDocument styledDocument) {
        this(f, structuredEditor, nodesRegistry, subSystem, answerEditor, combAns);
        this.styledDocument = styledDocument;
    }

    private void installImageListeners(StructuredEditorModel model) {
        model.addImageLoadListener(new ImageLoadListener() {
            @Override
            public Image loadImage(ImageLoadEvent e) {
                String fullName = openDir;
                if (openDir.endsWith("\\")) {
                    fullName += e.getFileName();
                } else {
                    fullName += "\\" + e.getFileName();
                }
                log.info("Getting image: " + fullName);
                try {
                    return ImageIO.read(new File(fullName));
                } catch (IOException e1) {
                    log.log(Level.WARNING, "Error in creation of image" + fullName, e1);
                }
                return null;
                //return new BufferedImage(100,100,BufferedImage.TYPE_INT_RGB);
                //return structuredEditor.getToolkit().getImage(fullName);
            }

            @Override
            public String selectImage() {
                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle("Загрузка изображения");
                ImgFilter filter = new ImgFilter();
                fc.setFileFilter(filter);
                if (!openDir.equals(""))
                    fc.setCurrentDirectory(new File(openDir));
                else {
                    saveAs();
                    if (!openDir.equals("")) {
                        fc.setCurrentDirectory(new File(openDir));
                    } else {
                        return null;
                    }
                }

                int returnVal = fc.showOpenDialog(f);
                if (returnVal == JFileChooser.APPROVE_OPTION /*&& dir != null && fl != null*/) {
                    File picFile = fc.getSelectedFile();
                    String ffn = picFile.getAbsolutePath();
                    String fn = picFile.getName();
                    if (!picFile.getParent().equals(openDir)) {
                        if (!IOUtils.copyFile(ffn, openDir + "\\" + fn))
                            return null;
                    }
                    return fn;
                    //xmlV.setFileName(fn);


                    //EmptyFieldsRegistry.getInstance().clear();
                }
                return null;
            }
        });
    }

    public MenuHandlerFactory(final Container f, StructuredEditor structuredEditor, NodesRegistry nodesRegistry, String subSystem,
                              StructuredEditor answerEditor, JTextField combAns) {
        this.f = f;
        //this.xmlV=xmlV;
        this.structuredEditor = structuredEditor;
        this.nodesRegistry = nodesRegistry;
        this.subSystem = subSystem;
        this.answerEditor = answerEditor;
        this.combAns = combAns;

        if (answerEditor != null)
            this.ans = answerEditor.getModel().getObject();

        //Image Load Listener for PictureTextElement
        installImageListeners(structuredEditor.getModel());
    }

    private void refreshEditor(DSLBean st, ModificationHistory modificationHistory) {

        StructuredEditorModel model = new StructuredEditorModel(st, modificationHistory);
        installImageListeners(model);
        model.setBeansRegistry(structuredEditor.getModel().getBeansRegistry());
        model.setEditorsRegistry(structuredEditor.getModel().getEditorsRegistry());
        model.setView(structuredEditor.getModel().isView());
        model.setApp(structuredEditor.getModel().getApp());
        //structuredEditor.getModel().setFocusedElement(null); //commented out by iposov
        structuredEditor.setModel(model);
        structuredEditor.getUI().redrawEditor();
        if ((subSystem.equals("geom") || subSystem.equals("log") || subSystem.equals("DSP")) && styledDocument != null) {
            String title = "";
            String text = "";
            String imageFile = "";
            if (subSystem.equals("geom")) {
                title = ((GeoStatement) model.getObject()).getTitle();
                text = ((GeoStatement) model.getObject()).getStatement();
            } else if (subSystem.equals("log")) {
                title = ((LogicStatement) model.getObject()).getTitle();
                text = ((LogicStatement) model.getObject()).getStatement();
            } else if (subSystem.equals("DSP")) {
                title = ((DSPStatement) model.getObject()).getTitle();
                text = ((DSPStatement) model.getObject()).getStatement();
                imageFile = ((DSPStatement) model.getObject()).getPicture();
            }
            try {
                //((GeoStatement)model.getObject()).getTitle()
                styledDocument.remove(0, styledDocument.getLength());
                styledDocument.insertString(0, title + "\n", styledDocument.getStyle("Heading2"));
                styledDocument.insertString(styledDocument.getLength(), text + "\n", styledDocument.getStyle("Default"));
                //styledDocument.setParagraphAttributes(1, 1, styledDocument.getStyle("Default"), false);
            } catch (Exception e) {
                log.log(Level.SEVERE, "Text HTML error", e);
                throw new RuntimeException("Text HTML error", e);
            }
            if (subSystem.equals("DSP")) {
                //Picture - attachment to the problem text
                if (imageFile !=null && !imageFile.equals("")){
                    String fullImageFile = openDir;
                    if (openDir.endsWith("\\")) {
                        fullImageFile += imageFile;
                    } else {
                        fullImageFile += "\\" + imageFile;
                    }
                    picturePanel = new PicturePanel(fullImageFile);
                    taskPanel.add(picturePanel);
                    //picturePanel.loadImage(fullImageFile);
                } else if (picturePanel!=null){
                    taskPanel.remove(picturePanel);
                    picturePanel=null;
                }
                //Tool panel
                DSPPanel panel = (DSPPanel)(panelEditor.getModel().getObject());
                AbstractTool[] tools = ((DSPStatement) structuredEditor.getModel().getObject()).getTools();
                PanelTool[] panelTools = new PanelTool[tools.length];
                int i=0;
                for (AbstractTool tool : tools){
                   if (tool instanceof FunctTool){
                      panelTools[i] = new FunctionPanelTool();

                       try {
                           String displayText;
                           Funct value = ((FunctTool) tool).getTool();
                           Field field = Funct.class.getField(value.name());
                           EnumFieldParams fieldParams = field.getAnnotation(EnumFieldParams.class);
                           displayText = fieldParams == null ? value.name() : fieldParams.displayText();
                           ((FunctionPanelTool) panelTools[i]).setFunName(displayText);
                       } catch (NoSuchFieldException e) {
                           e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                           ((FunctionPanelTool) panelTools[i]).setFunName("bad_function");
                       }

                   } else if (tool instanceof ToolboxTool){
                        switch (((ToolboxTool) tool).getTool()){
                            case CS_TOOLBOX: panelTools[i] = new CSToolboxPanelTool();
                                             break;
                            case SP_TOOLBOX: panelTools[i] = new SPToolboxPanelTool();
                        }
                   } else if (tool instanceof BlockTool){

                   } else if (tool instanceof BlocksetTool){

                   }
                   i++;
                }
                panel.setTools(panelTools);
                StructuredEditorModel model1 = new StructuredEditorModel(panel);
                installImageListeners(model1);
                model1.setBeansRegistry(panelEditor.getModel().getBeansRegistry());
                model1.setEditorsRegistry(panelEditor.getModel().getEditorsRegistry());
                model1.setView(panelEditor.getModel().isView());
                //model.setApp(panelEditor.getModel().getApp());
                //structuredEditor.getModel().setFocusedElement(null); //commented out by iposov
                panelEditor.setModel(model1);
                panelEditor.getUI().redrawEditor();
            }
        }
    }

    private void saveAs() {
        final JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Сохранение задачи");
        fc.setFileFilter(new XMLFilter());
        if (!"".equals(openDir)) {
            fc.setCurrentDirectory(new File(openDir));
        }

        if (fc.showSaveDialog(f) == JFileChooser.APPROVE_OPTION) {
            save(fc.getSelectedFile().getAbsolutePath());
            openDir = fc.getSelectedFile().getParent();
        }
    }

    private void save(String fn) {
        if (!fn.endsWith(".xml")) {
            fn = fn + ".xml";
        }
        log.info("Saving statement to file: " + fn);

        final List<Converter> converters = new ArrayList<Converter>();
        if (subSystem.equals("geom")) {
            log.info("Adding converter for geogebra elements");
            final Application app = (Application) structuredEditor.getApp();
            converters.add(new GeoElementConverter(app));
        }
        final XStreamSerializer serializer = new XStreamSerializer(converters);

        serializer.saveStructure(fn, subSystem, structuredEditor.getModel().getObject());

        if (subSystem.equals("geom")) {
            final File file = new File(fn.substring(0, fn.lastIndexOf('.')) + ".ggb");
            log.info("Saving GGB part to " + file);
            boolean success = ((Application) structuredEditor.getApp()).saveGeoGebraFile(file);
            if (success) {
                log.info("Successfully saved to file: " + fn);
                ((Application) structuredEditor.getApp()).setCurrentFile(file);
            } else {
                log.warning("Failed to save to file: " + fn);
            }
        }
        filename = fn;
    }

    public void openTask(String fn) {
        log.info("Loading statement from file: " + fn);
        filename = fn;
        openDir = (new File(fn)).getParent(); //changed by iposov 04-08-2011

        if (subSystem.equals("geom")) {
            final File file = new File(fn.substring(0, fn.lastIndexOf('.')) + ".ggb");
            log.info("Loading GGB part from file: " + file);

            final Application app = (Application) structuredEditor.getApp();
            if (file.exists()) {
                app.getGuiManager().loadFile(file, false);
            } else {
                app.clearConstruction();
            }
        }

        final List<Converter> converters = new ArrayList<Converter>();
        if (subSystem.equals("geom")) {
            log.info("Added converter for geogebra elements");
            Application app = (Application) structuredEditor.getApp();
            converters.add(new GeoElementConverter(app));
        }
        final XStreamBuilder builder = new XStreamBuilder(converters);
        final DSLBean bean = builder.getStructure(filename);

        refreshEditor(bean, structuredEditor.getModel().getModificationHistory());

        structuredEditor.getModel().getModificationHistory().clearVector();
        if (answerEditor != null) {
            if (subSystem.equals("log")) {
                TaskVerifier verifier = new TaskVerifier(structuredEditor.getModel().getObject(), subSystem,
                        null, ans, "");
                //combAns.getText()
                verifier.makeLogAnswer();
            } else if (subSystem.equals("DSP")) {
                ans = new DSPAnswer();
            }
            final EditorsRegistry editorsRegistry = answerEditor.getModel().getEditorsRegistry();

            final StructuredEditorModel model = new StructuredEditorModel(ans);

            answerEditor.setModel(model);
            model.setEditorsRegistry(editorsRegistry);
            if (subSystem.equals("DSP")) {
                 answerEditor.setApp(panelEditor);
            }
            answerEditor.getUI().redrawEditor();
        }
        if (subSystem.equals("geom")) {
            final Application app = (Application) structuredEditor.getApp();
            if (structuredEditor.isView()) {
                final Instrum instruments[] = ((GeoStatement) bean).getInstrums();
                if (instruments != null && instruments.length != 0) {
                    log.info("Limiting available instrument list to " + Arrays.toString(instruments));
                    String toolStr;
                    toolStr = String.valueOf(EuclidianConstants.MODE_MOVE);
                    for (Instrum instrum : instruments) {
                        final Integer mode = INSTRUM_MODES.get(instrum);
                        if (mode != null)
                            toolStr += " | " + String.valueOf(mode);
                    }
                    app.getGuiManager().setToolBarDefinition(toolStr);

                    app.updateToolBar();
                }
            }
        }
        log.info("Loaded statement from file: " + fn);
    }

    public ActionListener createHandler() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filename = "";
                DSLBean bean;
                if (subSystem.equals("geom")) {
                    bean = new GeoStatement();
                    refreshEditor(bean, structuredEditor.getModel().getModificationHistory());

                    structuredEditor.getModel().getModificationHistory().clearVector();
                    final Application app = (Application) structuredEditor.getApp();
                    app.clearConstruction();

                    // clear input bar
                    if (app.hasGuiManager()) {
                        AlgebraInput ai = (AlgebraInput) app.getGuiManager()
                                .getAlgebraInput();
                        ai.replaceString(null);
                    }
                    //app.updateContentPane();
                } else if (subSystem.equals("log")) {
                    bean = new LogicStatement();
                    refreshEditor(bean, structuredEditor.getModel().getModificationHistory());
                } else if (subSystem.equals("comb")) {
                    bean = new Statement();
                    refreshEditor(bean, structuredEditor.getModel().getModificationHistory());
                } else if (subSystem.equals("DSP")) {
                    bean = new DSPStatement();
                    refreshEditor(bean, structuredEditor.getModel().getModificationHistory());
                }
            }
        };
    }

    public ActionListener openHandler() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser();
                fc.setDialogTitle("Загрузка задачи");
                fc.setFileFilter(new XMLFilter());
                if (!openDir.equals("")) {
                    fc.setCurrentDirectory(new File(openDir));
                }
                if (fc.showOpenDialog(f) == JFileChooser.APPROVE_OPTION) {
                    String fn = fc.getSelectedFile().getAbsolutePath();
                    openTask(fn);
                    //xmlV.setFileName(fn);

                    //EmptyFieldsRegistry.getInstance().clear();
                }
            }
        };
    }

    public ActionListener saveHandler() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (filename.equals(""))
                    saveAs();
                else
                    save(filename);
            }
        };
    }

    public ActionListener saveAsHandler() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAs();
            }
        };
    }

    public ActionListener exitHandler() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                f.setVisible(false);
                System.exit(0);
            }
        };
    }

    public ActionListener undoHandler() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                structuredEditor.getModel().hidePopup();
                structuredEditor.getModel().getModificationHistory().undo();
                refreshEditor(structuredEditor.getModel().getObject(),
                        structuredEditor.getModel().getModificationHistory());
            }
        };
    }

    public ActionListener redoHandler() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                structuredEditor.getModel().hidePopup();
                structuredEditor.getModel().getModificationHistory().redo();
                refreshEditor(structuredEditor.getModel().getObject(),
                        structuredEditor.getModel().getModificationHistory());
            }
        };
    }

    public ActionListener verifyHandler() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final Application app = subSystem.equals("geom")
                        ? (Application) structuredEditor.getApp()
                        : null;

                final TaskVerifier verifier = new TaskVerifier(structuredEditor.getModel().getObject(), subSystem,
                        app, ans, combAns == null ? null : combAns.getText());

                final String message, ansScore;
                if (verifier.verify()) {
                    message = "Ответ правильный!";
                    ansScore = "1";
                } else {
                    message = "Ответ неправильный!";
                    ansScore = "0";
                }
                if (f instanceof Applet) {
                    try {
                        ((Applet) f).getAppletContext().showDocument(
                                new URL("javascript:setDataValue(\"cmi.score.scaled\"," + ansScore + "); commitData()"));
                    } catch (MalformedURLException me) {
                        log.log(Level.WARNING, "", me);
                    }
                }
                JOptionPane.showMessageDialog(null, message, "Проверка", JOptionPane.PLAIN_MESSAGE);
            }
        };
    }

    public ActionListener helpHandler() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String mes = "Система контроля знаний";
                if (subSystem.equals("geom")) {
                    if (structuredEditor.isView()) {
                        mes = "Откройте нужную задачу (пункт меню \"Задача/Открыть\"). Выполните на полотне нужные построения " +
                                "и выберите пункт меню \"Задача/Проверить\"";
                    } else {
                        mes = "Введите условия задачи в текстовой и математической форме, постройте исходный чертеж на полотне.";
                    }
                } else if (subSystem.equals("log")) {
                    if (structuredEditor.isView()) {
                        mes = "Откройте нужную задачу (пункт меню \"Задача/Открыть\"). Задайте значения логических переменных " +
                                "и выберите пункт меню \"Задача/Проверить\"";
                    } else {
                        mes = "Введите условия задачи в текстовой и математической форме.";
                    }
                }
                JOptionPane.showMessageDialog(null, mes, "Помощь", JOptionPane.PLAIN_MESSAGE);
            }
        };
    }

    public ActionListener objectPanelHandler() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                algView = !algView;
                ((Application) structuredEditor.getApp()).getGuiManager().setShowAlgebraView(algView);
            }
        };
    }

    public ActionListener emptyHandler() {
        log.warning("Creating empty handler.");
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.warning("Empty handler fired for action " + e.getActionCommand());
            }
        };
    }
}
