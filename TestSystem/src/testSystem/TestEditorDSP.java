package testSystem;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.ipo.structurededitor.StructuredEditor;
import ru.ipo.structurededitor.StructuredEditorWithActions;
import ru.ipo.structurededitor.controller.ModificationHistory;
import ru.ipo.structurededitor.controller.ModificationListener;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.model.DSLBeansRegistry;
import ru.ipo.structurededitor.view.StatusBar;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.images.ImageGetter;
import testSystem.lang.DSP.*;
import testSystem.lang.comb.Expr;
import testSystem.lang.comb.LogAndExpr;
import testSystem.lang.comb.LogNotExpr;
import testSystem.lang.comb.LogOrExpr;
import testSystem.lang.logic.*;
import testSystem.structureBuilder.MyErrorHandler;
import testSystem.structureSerializer.NodesRegistry;

import javax.swing.*;
import javax.tools.Tool;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 02.01.2010
 * Time: 17:05:46
 */
public class TestEditorDSP {
    public static void main(String[] args) {
        StructuredEditor.initializeStructuredEditorUI();
        new TestEditorDSP();
    }
    /*static {
        UIManager.installLookAndFeel("UI for structured editor", ComponentUI.class.getName());
    }*/

    //private StructuredEditorModel model;

    public TestEditorDSP() {
        JFrame f = new JFrame("Модуль учителя");
        //f.setLayout(new GridLayout(2,1));
        BorderLayout br = new BorderLayout();
        f.setLayout(br);
        //TODO think of the appropriate place to this default registrations
        //-------Editors registry preparation
        /*EditorsRegistry editorsRegistry = EditorsRegistry.getInstance();
        editorsRegistry.setDefaultEditor(VoidEditor.class);
        // editorsRegistry.setNextArrayEditor(NextArrayDSLBeanEditor.class);
        editorsRegistry.registerEditor(String.class, StringEditor.class);
        editorsRegistry.registerEditor(int.class, IntEditor.class);
        editorsRegistry.registerEditor(double.class, DoubleEditor.class);
        editorsRegistry.registerEditor(Boolean.class, BooleanEditor.class);
        editorsRegistry.registerEditor(Count.class, EnumEditor.class);     */


        //-------Nodes registry preparation
        NodesRegistry nodesRegistry = nodesRegistryPrep();


        //------------Frame preparation

        DSPStatement st = new DSPStatement();
        final StructuredEditorModel model = createModel(st);

//        f.add(new JScrollPane(new JTextArea("asdf")));
        final StructuredEditor structuredEditor = new StructuredEditor(model,false);
        JScrollPane structuredEditorScrPane = new JScrollPane(structuredEditor);

        f.add(new StructuredEditorWithActions(structuredEditor), BorderLayout.CENTER);

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(640, 480);
        //structuredEditorScrPane.setSize(320,480);
        f.setLocationRelativeTo(null);


        /* //XMLViewer
        XMLViewer xmlV = new XMLViewer("f:\\dsl\\IDEA_DSL\\ru\\ipo\\structurededitor\\xmlViewer\\emptytask.xml");
        f.add(new JScrollPane(xmlV));*/


        // Menu
        MenuBar menuBar = new MenuBar();
        f.setMenuBar(menuBar);
        Menu file = new Menu("Файл");
        MenuItem item1, item2, item3, item4, item5,helpItem;
        file.add(item1 = new MenuItem("Создать"));
        file.add(item2 = new MenuItem("Открыть . . ."));
        file.add(item3 = new MenuItem("Сохранить"));
        file.add(item4 = new MenuItem("-"));
        file.add(item5 = new MenuItem("Выход"));
        menuBar.add(file);
        Menu edit = new Menu("Редактирование");
        final MenuItem undoItem, redoItem;
        edit.add(undoItem = new MenuItem("Отменить"));
        edit.add(redoItem = new MenuItem("Повторить"));
        undoItem.setEnabled(false);
        redoItem.setEnabled(false);
        menuBar.add(edit);
        Menu help = new Menu("Помощь");
        help.add(helpItem = new MenuItem("Работа"));

        //MyJMenuHandler handler = new MyJMenuHandler(f,xmlV,structuredEditor);
        helpItem.setActionCommand("Помощь");
        menuBar.add(help);
        //MyMenuHandler handler = new MyMenuHandler(f,xmlV,structuredEditor);
        MyMenuHandler handler = new MyMenuHandler(f, structuredEditor, nodesRegistry, "DSP",null,null);
        helpItem.addActionListener(handler);
        item1.addActionListener(handler);
        item2.addActionListener(handler);
        item3.addActionListener(handler);
        item4.addActionListener(handler);
        item5.addActionListener(handler);
        undoItem.addActionListener(handler);
        redoItem.addActionListener(handler);

        //Status Bar
        StatusBar statusBar = new StatusBar("Нажмите Ctrl+Пробел для выбора вариантов ввода");
        f.add(statusBar, BorderLayout.SOUTH);

        //ToolBar
        JToolBar toolBar = new JToolBar();
        addButtonToToolBar(toolBar, "menu-open.png", "Открыть . . .", true, handler);
        addButtonToToolBar(toolBar, "save.png", "Сохранить", true, handler);
        final JButton undoButton = addButtonToToolBar(toolBar, "undo.png", "Отменить", true, handler);
        final JButton redoButton = addButtonToToolBar(toolBar, "redo.png", "Повторить", true, handler);
        addButtonToToolBar(toolBar, "Примеры задач", "Примеры задач . . .", false, handler);
        addButtonToToolBar(toolBar, "help.png", "Помощь", true, handler);

        undoButton.setEnabled(false);
        redoButton.setEnabled(false);

        f.add(toolBar, BorderLayout.NORTH);

        structuredEditorScrPane.requestFocusInWindow();
        f.setVisible(true);
        final ModificationHistory modificationHistory = model.getModificationHistory();
        modificationHistory.addModificationListener(new ModificationListener() {
            public void modificationPerformed() {
                if (modificationHistory.canRedo()) {
                    redoButton.setEnabled(true);
                    redoItem.setEnabled(true);
                } else {
                    redoButton.setEnabled(false);
                    redoItem.setEnabled(false);
                }
                if (modificationHistory.canUndo()) {
                    undoButton.setEnabled(true);
                    undoItem.setEnabled(true);
                } else {
                    undoButton.setEnabled(false);
                    undoItem.setEnabled(false);
                }
            }
        });
        //model.setModificationHistory(modificationHistory);


        /*f.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                ((CompositeElement)model.getRootElement()).add(new TextElement(TestEditor.this.model, "!!!"),0);

                System.out.println("e.getKeyCode() = " + e.getKeyCode());
                System.out.println("e.getKeyChar() = '" + e.getKeyChar() + "' (" + (int)e.getKeyChar() + ")");
                System.out.println("e.getModifiers() = " + e.getModifiers());
                System.out.println("e.getModifiersEx() = " + e.getModifiersEx());

                System.out.println();
            }

            public void keyReleased(KeyEvent e) {
            }
        });*/

        //model.getRootElement().gainFocus(new TextPosition(0,0), false, false);
    }

    private NodesRegistry nodesRegistryPrep() {
        Document document;
        // obtain the default parser
        try {
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            DocumentBuilder builder = factory.newDocumentBuilder();

            // set error handler for validation errors
            builder.setErrorHandler(new MyErrorHandler());

            document = builder.newDocument();

            NodesRegistry nodesRegistry = new NodesRegistry();

            Element defaultNode = document.createElement("unknown");
            nodesRegistry.setDefaultNode(defaultNode);

            Element emptyNode = document.createElement("empty");
            nodesRegistry.setEmptyNode(emptyNode);


            Attr taskTitle = document.createAttribute("title");
            nodesRegistry.registerNode(DSPStatement.class, "title", taskTitle);

            Element taskDescription = document.createElement("description");
            nodesRegistry.registerNode(DSPStatement.class, "statement", taskDescription);

            //Tools
            Element blockToolEnum = document.createElement("tool");
            blockToolEnum.setAttribute("type", "Block");
            nodesRegistry.registerNode(Block.class, blockToolEnum);
            nodesRegistry.registerProp(BlockTool.class, "tool");

            Element blocksetToolEnum = document.createElement("tool");
            blocksetToolEnum.setAttribute("type", "Blockset");
            nodesRegistry.registerNode(Blockset.class, blocksetToolEnum);
            nodesRegistry.registerProp(BlocksetTool.class, "tool");

            Element funcToolEnum = document.createElement("tool");
            funcToolEnum.setAttribute("type", "Func");
            nodesRegistry.registerNode(Funct.class, funcToolEnum);
            nodesRegistry.registerProp(FunctTool.class, "tool");

            Element toolboxToolEnum = document.createElement("tool");
            toolboxToolEnum.setAttribute("type", "Toolbox");
            nodesRegistry.registerNode(Toolbox.class, toolboxToolEnum);
            nodesRegistry.registerProp(ToolboxTool.class, "tool");

            /*Element tools = document.createElement("tools");
            nodesRegistry.registerNode(DSPStatement.class, "tools", tools);*/

            Element verifier = document.createElement("verifier");
            nodesRegistry.registerNode(DSPStatement.class, "verifier", verifier);

            Element picture = document.createElement("picture");
            nodesRegistry.registerNode(DSPStatement.class, "picture", picture);

            return nodesRegistry;

        } catch (Exception e) {
            throw new Error("Failed to register nodes: ", e);
        }
    }

    private JButton addButtonToToolBar(JToolBar toolBar, String text, String cmd, Boolean pict, ActionListener handler) {
        JButton but;
        if (pict)
            but = new JButton(new ImageIcon(ImageGetter.class.getResource(text)));
        else
            but = new JButton(text);
        but.setActionCommand(cmd);
        but.addActionListener(handler);
        but.setFocusable(false);
        toolBar.add(but);
        return but;
    }

    private StructuredEditorModel createModel(DSLBean st) {
        /*CompositeElement root = new CompositeElement(model, CompositeElement.Orientation.Vertical);

        CompositeElement _1stLine = new CompositeElement(model, CompositeElement.Orientation.Horizontal);
        CompositeElement _2ndLine = new CompositeElement(model, CompositeElement.Orientation.Horizontal);
        VisibleElement _3rdLine = new TextElement(model, "The 3rd line");
        ComboBoxTextEditorElement<Integer> _4thLine = new ComboBoxTextEditorElement<Integer>(model);

        _1stLine.add(new TextElement(model, "text 1"));
        _1stLine.add(new TextElement(model, "text 2"));
        _1stLine.add(new TextElement(model, "text 3"));

        _2ndLine.add(new TextElement(model, "null value: "));
        _2ndLine.add(new TextEditorElement(model, "initial text"));
        _2ndLine.add(new TextElement(model));

        _4thLine.addValue("value 1", 1);
        _4thLine.addValue("value 2", 2);
        _4thLine.addValue("value 3", 3);

//        model.setRootElement(root);

        root.add(_1stLine);
        root.add(_2ndLine);
        root.add(_3rdLine);
        root.add(new ContainerElement(model, _4thLine));*/

        //Bean1 bean1 = new Bean1();
        DSLBeansRegistry reg = new DSLBeansRegistry();
        reg.clearRegistry();
        reg.registerBean(DSPStatement.class);
        reg.registerBean(AbstractTool.class);
        reg.registerBean(BlockTool.class);
        reg.registerBean(BlocksetTool.class);
        reg.registerBean(FunctTool.class);
        reg.registerBean(ToolboxTool.class);
        StructuredEditorModel model = new StructuredEditorModel(st);
        model.setBeansRegistry(reg);
        return model;
    }
}