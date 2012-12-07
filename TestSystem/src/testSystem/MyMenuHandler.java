package testSystem;

import geogebra.euclidian.EuclidianView;
import geogebra.gui.inputbar.AlgebraInput;
import geogebra.gui.layout.DockSplitPane;
import geogebra.main.Application;
import org.mathpiper.builtin.functions.core.FileSize;
import ru.ipo.structurededitor.StructuredEditor;
import ru.ipo.structurededitor.controller.EditorsRegistry;
import ru.ipo.structurededitor.controller.ModificationHistory;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.model.EnumFieldParams;
import ru.ipo.structurededitor.view.events.ImageLoadEvent;
import ru.ipo.structurededitor.view.events.ImageLoadListener;
import testSystem.lang.DSP.*;
import testSystem.lang.comb.Statement;
import testSystem.lang.logic.LogicStatement;
import testSystem.structureBuilder.StructureBuilder;
import testSystem.structureSerializer.NodesRegistry;
import testSystem.structureSerializer.StructureSerializer;
import testSystem.lang.geom.GeoStatement;
import testSystem.lang.geom.Instrum;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import testSystem.view.PicturePanel;

import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.StyledDocument;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Олег
 * Date: 26.08.2010
 * Time: 16:15:33
 */
public class MyMenuHandler implements ActionListener, ItemListener {
    final Container f;
    //XMLViewer xmlV;
    StructuredEditor structuredEditor, answerEditor;
    NodesRegistry nodesRegistry;
    String subSystem;
    HashMap<Instrum, Integer> instrumsModes;
    DSLBean ans;
    JTextField combAns;
    StyledDocument styledDocument;
    String openDir = "";
    boolean algView = false;
    String filename = "";
    private JPanel taskPanel;
    private PicturePanel picturePanel=null;
    private StructuredEditor panelEditor;
    //public MyMenuHandler(JFrame f, XMLViewer xmlV, StructuredEditor structuredEditor){

    public MyMenuHandler(final Container f, StructuredEditor structuredEditor, NodesRegistry nodesRegistry,
                         String subSystem, StructuredEditor answerEditor, JTextField combAns,
                         StyledDocument styledDocument, JPanel taskPanel, StructuredEditor panelEditor) {
        this(f, structuredEditor, nodesRegistry, subSystem, answerEditor, combAns, styledDocument);
        this.taskPanel = taskPanel;
        this.panelEditor = panelEditor;
    }

    public MyMenuHandler(final Container f, StructuredEditor structuredEditor, NodesRegistry nodesRegistry, String subSystem,
                         StructuredEditor answerEditor, JTextField combAns, StyledDocument styledDocument) {
        this(f, structuredEditor, nodesRegistry, subSystem, answerEditor, combAns);
        this.styledDocument = styledDocument;
    }

    private boolean fileCopy(String srFile, String dtFile) {
        try {
            File f1 = new File(srFile);
            File f2 = new File(dtFile);
            InputStream in = new FileInputStream(f1);

            //For Append the file.
//  OutputStream out = new FileOutputStream(f2,true);

            //For Overwrite the file.
            OutputStream out = new FileOutputStream(f2);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            System.out.println("File copied.");
            return true;
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage() + " in the specified directory.");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return false;
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
                System.out.println("Getting image: " + fullName);
                try {
                    return ImageIO.read(new File(fullName));
                } catch (IOException e1) {
                    System.out.println("Error in creating of image " + fullName);
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
                        if (!fileCopy(ffn, openDir + "\\" + fn))
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

    public MyMenuHandler(final Container f, StructuredEditor structuredEditor, NodesRegistry nodesRegistry, String subSystem,
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
        instrumsModes = new HashMap<Instrum, Integer>();
        instrumsModes.put(Instrum.POINT, EuclidianView.MODE_POINT);
        instrumsModes.put(Instrum.LINE_PERPEND, EuclidianView.MODE_ORTHOGONAL);
        instrumsModes.put(Instrum.LINE_PARALL, EuclidianView.MODE_PARALLEL);
        instrumsModes.put(Instrum.LINE_TWO_POINTS, EuclidianView.MODE_JOIN);
        instrumsModes.put(Instrum.CIRCLE_CENTER_RAD, EuclidianView.MODE_CIRCLE_POINT_RADIUS);
        instrumsModes.put(Instrum.CIRCLE_CENTER_POINT, EuclidianView.MODE_CIRCLE_TWO_POINTS);
        instrumsModes.put(Instrum.MIDPOINT, EuclidianView.MODE_MIDPOINT);
        instrumsModes.put(Instrum.SEGMENT_TWO_POINTS, EuclidianView.MODE_SEGMENT);
        instrumsModes.put(Instrum.SEGMENT_FIXED, EuclidianView.MODE_SEGMENT_FIXED);
        instrumsModes.put(Instrum.ANGLE_THREE_POINTS, EuclidianView.MODE_ANGLE);
        instrumsModes.put(Instrum.ANGLE_FIXED, EuclidianView.MODE_ANGLE_FIXED);
        instrumsModes.put(Instrum.RAY_TWO_POINTS, EuclidianView.MODE_RAY);


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
                throw new Error("Text HTML error" + e);
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
                }  catch (NoSuchFieldException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    ((FunctionPanelTool) panelTools[i]).setFunName("bad_function");
                }

                   } else if (tool instanceof ToolboxTool){
                        switch (((ToolboxTool) tool).getTool()){
                            case CS_TOOLBOX: panelTools[i] = new CSToolboxPanelTool();
                                             break;
                            case DSP_TOOLBOX: panelTools[i] = new DSPToolboxPanelTool();
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
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Сохранение задачи");
        XMLFilter filter = new XMLFilter();
        fc.setFileFilter(filter);
        if (!openDir.equals(""))
            fc.setCurrentDirectory(new File(openDir));
        int returnVal = fc.showSaveDialog(f);
        if (returnVal == JFileChooser.APPROVE_OPTION /*&& dir != null && fl != null*/) {
            save(fc.getSelectedFile().getAbsolutePath());
            openDir = fc.getSelectedFile().getParent();
        }

    }

    private void save(String fn) {
        if (!fn.endsWith(".xml"))
            fn = fn + ".xml";
        System.out.println("You begin to save the file: " + fn);

        StructureSerializer structureSerializer = new StructureSerializer(fn, nodesRegistry, subSystem);

        structureSerializer.saveStructure(structuredEditor.getModel().getObject());
        File file = new File(fn.substring(0, fn.lastIndexOf('.')) + ".ggb");

        if (subSystem.equals("geom")) {
            Application app = (Application) structuredEditor.getApp();
            System.out.println("You begin to save the GGB part");
            boolean success = ((Application) structuredEditor.getApp()).saveGeoGebraFile(file);
            if (success) {
                System.out.println("You've saved the file: " + fn);
                ((Application) structuredEditor.getApp()).setCurrentFile(file);
            } else {
               System.out.println("Error in saving of the file: " + fn);
            }
        }
        filename = fn;
    }

    public void openTask(String fn) {
        filename = fn;
        File file = new File(fn.substring(0, fn.lastIndexOf('.')) + ".ggb");
//                openDir=fn.substring(0, fn.lastIndexOf('\\'));
        openDir = (new File(fn)).getParent(); //changed by iposov 04-08-2011

        if (subSystem.equals("geom")) {
            Application app = (Application) structuredEditor.getApp();
            if (file.exists()){
                app.getGuiManager().loadFile(file, false);
            }
            else {

                app.clearConstruction();
            }

            // if (((Application)structuredEditor.getApp()).getGuiManager().getAlgebraView().isVisible())
            //     ((Application)structuredEditor.getApp()).getGuiManager().setShowAlgebraView(algView);
        }

        StructureBuilder structureBuilder;
        if (subSystem.equals("geom")){
            structureBuilder = new StructureBuilder(fn, subSystem, (Application) structuredEditor.getApp());
        } else {
            structureBuilder = new StructureBuilder(fn, subSystem, null);
        }
        DSLBean bean = structureBuilder.getStructure();
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
            EditorsRegistry editorsRegistry = answerEditor.getModel().getEditorsRegistry();

            StructuredEditorModel model = new StructuredEditorModel(ans);

            answerEditor.setModel(model);
            model.setEditorsRegistry(editorsRegistry);
            if (subSystem.equals("DSP")) {
                 answerEditor.setApp(panelEditor);
            }
            answerEditor.getUI().redrawEditor();
        }
        if (subSystem.equals("geom")) {
            Application app = (Application) structuredEditor.getApp();
            if (structuredEditor.isView()) {
                Instrum instrums[] = ((GeoStatement) bean).getInstrums();
                if (instrums != null && instrums.length != 0) {
                    String toolStr;
                    toolStr = "0";
                    for (int i = 0; i < instrums.length; i++) {
                        if (instrumsModes.get(instrums[i]) != null)
                            toolStr += " | " + String.valueOf(instrumsModes.get(instrums[i]));
                    }
                    app.getGuiManager().setToolBarDefinition(toolStr);

                    app.updateToolBar();
                }
            }
        }
        System.out.println("You've opened the file: " + fn);
    }

    public void actionPerformed(ActionEvent ae) {
        String arg = ae.getActionCommand();
        //System.out.println("You selected "+arg);
        if (arg.equals("Создать")) {
            filename = "";
            DSLBean bean;
            if (subSystem.equals("geom")) {
                bean = new GeoStatement();
                refreshEditor(bean, structuredEditor.getModel().getModificationHistory());
                structuredEditor.getModel().getModificationHistory().clearVector();
                Application app = (Application) structuredEditor.getApp();
                //app.clearConstruction();
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

        } else if (arg.equals("Open")) {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Загрузка задачи");
            XMLFilter filter = new XMLFilter();
            fc.setFileFilter(filter);
            if (!openDir.equals(""))
                fc.setCurrentDirectory(new File(openDir));
            int returnVal = fc.showOpenDialog(f);
            if (returnVal == JFileChooser.APPROVE_OPTION /*&& dir != null && fl != null*/) {
                String fn = fc.getSelectedFile().getAbsolutePath();
                openTask(fn);
                //xmlV.setFileName(fn);


                //EmptyFieldsRegistry.getInstance().clear();
            }

        } else if (arg.equals("Сохранить")) {
            if (filename.equals(""))
                saveAs();
            else
                save(filename);

        } else if (arg.equals("Сохранить как . . .")) {
            saveAs();
        }
        if (arg.equals("Выход")) {
            f.setVisible(false);
            System.exit(0);
        } else if (arg.equals("Отменить")) {
            structuredEditor.getModel().hidePopup();
            structuredEditor.getModel().getModificationHistory().undo();
            refreshEditor(structuredEditor.getModel().getObject(),
                    structuredEditor.getModel().getModificationHistory());
        } else if (arg.equals("Повторить")) {
            structuredEditor.getModel().hidePopup();
            structuredEditor.getModel().getModificationHistory().redo();
            refreshEditor(structuredEditor.getModel().getObject(),
                    structuredEditor.getModel().getModificationHistory());
        } else if (arg.equals("Проверить . . .")) {
            TaskVerifier verifier;
            if (subSystem.equals("geom")){
                verifier = new TaskVerifier(structuredEditor.getModel().getObject(), subSystem,
                    (Application) structuredEditor.getApp(), ans, combAns == null ? null : combAns.getText());
            }
            else {
                verifier = new TaskVerifier(structuredEditor.getModel().getObject(), subSystem,
                                    null, ans, combAns == null ? null : combAns.getText());

            }

            String mes, AnsScore;
            if (verifier.verify()) {
                mes = "Ответ правильный!";
                AnsScore = "1";
            } else {
                mes = "Ответ неправильный!";
                AnsScore = "0";
            }
            if (f instanceof Applet) {
                try {
                    ((Applet) f).getAppletContext().showDocument(
                            new URL("javascript:setDataValue(\"cmi.score.scaled\"," + AnsScore + "); commitData()"));
                } catch (MalformedURLException me) {
                    System.out.println("Bad JavaScript!");
                }
            }
            JOptionPane.showMessageDialog(null, mes, "Проверка", JOptionPane.PLAIN_MESSAGE);
        } else if (arg.equals("Помощь")) {
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
        } else if (arg.equals("Панель объектов") && subSystem.equals("geom")) {

            algView = !algView;
            ((Application) structuredEditor.getApp()).getGuiManager().setShowAlgebraView(algView);
        }
    }

    public void itemStateChanged(ItemEvent ie) {
        f.repaint();
    }
}
