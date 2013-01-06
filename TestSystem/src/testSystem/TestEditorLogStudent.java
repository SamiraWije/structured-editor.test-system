package testSystem;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.ipo.structurededitor.StructuredEditor;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.model.DSLBeansRegistry;
import ru.ipo.structurededitor.view.StatusBar;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.images.ImageGetter;
import testSystem.lang.comb.Expr;
import testSystem.lang.comb.LogAndExpr;
import testSystem.lang.comb.LogNotExpr;
import testSystem.lang.comb.LogOrExpr;
import testSystem.lang.logic.*;
import testSystem.structureBuilder.MyErrorHandler;
import testSystem.structureSerializer.NodesRegistry;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.*;
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
public class TestEditorLogStudent {
    public static void main(String[] args) {
        StructuredEditor.initializeStructuredEditorUI();
        new TestEditorLogStudent();
    }
    /*static {
        UIManager.installLookAndFeel("UI for structured editor", ComponentUI.class.getName());
    }*/

    //private StructuredEditorModel model;
    public TestEditorLogStudent(JApplet f,String filename) {
        StructuredEditor.initializeStructuredEditorUI();
        makeContainer(f, filename);
    }

    public void makeContainer(Container f, String filename) {
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

        LogicStatement st = new LogicStatement();
        final StructuredEditorModel model = createModel(st);

//        f.add(new JScrollPane(new JTextArea("asdf")));
        final StructuredEditor structuredEditor = new StructuredEditor(model, true);
        JScrollPane structuredEditorScrPane = new JScrollPane(structuredEditor);
        LogicAnswer ans = new LogicAnswer();
        final StructuredEditor answerEditor = new StructuredEditor(new StructuredEditorModel(ans), false);
        JScrollPane answerEditorScrPane = new JScrollPane(answerEditor);

        JPanel taskPanel = new JPanel(new BorderLayout());


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

        StyledDocument styledDocument = textPane.getStyledDocument();

        try {
            //((GeoStatement)model.getObject()).getTitle()

            styledDocument.remove(0, styledDocument.getLength());
            styledDocument.insertString(0, "Откройте задачу\n Здесь будет условие\n", null);
            //doc.setParagraphAttributes(0, 1, heading2Style, false);

        } catch (Exception e) {
            throw new Error("Text HTML error" + e);
        }
        Border border = BorderFactory.createLineBorder(Color.GRAY);
        textPane.setBorder(border);

        f.add(taskPanel, BorderLayout.CENTER);
        f.add(answerEditorScrPane, BorderLayout.EAST);
        //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(640, 480);
        //structuredEditorScrPane.setSize(320,480);
        //f.setLocationRelativeTo(null);
        JMenuBar menuBar = new JMenuBar();
        if (f instanceof JFrame) {
            ((JFrame) f).setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ((JFrame) f).setLocationRelativeTo(null);
            ((JFrame) f).setJMenuBar(menuBar);
        } else if (f instanceof JApplet) {
            //((JApplet) f).setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            //((JApplet) f).setLocationRelativeTo(null);
            ((JApplet) f).setJMenuBar(menuBar);
        }

        /* //XMLViewer
        XMLViewer xmlV = new XMLViewer("f:\\dsl\\IDEA_DSL\\ru\\ipo\\structurededitor\\xmlViewer\\emptytask.xml");
        f.add(new JScrollPane(xmlV));*/


        // Menu

        //f.setMenuBar(menuBar);
        JMenu file = new JMenu("Файл");
        JMenuItem item1, item2, item3, item4, item5;
        //file.add(item1 = new MenuItem("Создать"));
        item2 = new JMenuItem("Open");
        item2.setName("Открыть . . .");
        item2.setActionCommand("Open");
        file.add(item2);
        //file.add(item3 = new MenuItem("Сохранить . . ."));
        file.addSeparator();
        file.add(item4 = new JMenuItem("Проверить . . ."));
        file.addSeparator();
        file.add(item5 = new JMenuItem("Выход"));
        menuBar.add(file);
        //Menu edit = new Menu("Редактирование");
        final JMenuItem undoItem, redoItem, helpItem;
        //edit.add(undoItem = new MenuItem("Отменить"));
        //edit.add(redoItem = new MenuItem("Повторить"));
        //undoItem.setEnabled(false);
        //redoItem.setEnabled(false);
        //menuBar.add(edit);
        JMenu help = new JMenu("Помощь");
        menuBar.add(help);
        help.add(helpItem = new JMenuItem("Работа"));
        helpItem.setActionCommand("Помощь");
        //MyMenuHandler handler = new MyMenuHandler(f,xmlV,structuredEditor);
        MenuHandlerFactory handler = new MenuHandlerFactory(f, structuredEditor, nodesRegistry, "log", answerEditor, null,
                styledDocument);
        //item1.addActionListener(handler);
        helpItem.addActionListener(handler.helpHandler());
        item2.addActionListener(handler.openHandler());
        //item3.addActionListener(handler);
        item4.addActionListener(handler.verifyHandler());
        item5.addActionListener(handler.exitHandler());
        //undoItem.addActionListener(handler);
        //redoItem.addActionListener(handler);

        //Status Bar
        StatusBar statusBar = new StatusBar("Для инвертирования значения логической переменной перейдите на него и нажмите Пробел");
        f.add(statusBar, BorderLayout.SOUTH);

        //ToolBar
        JToolBar toolBar = new JToolBar();
        addButtonToToolBar(toolBar, "menu-open.png", "Open", true, handler.openHandler());
        //addButtonToToolBar(toolBar, "save.png", "Сохранить . . .", true, handler);
        addButtonToToolBar(toolBar, "verify.png", "Проверить . . .", true, handler.verifyHandler());
        //final JButton undoButton = addButtonToToolBar(toolBar, "undo.png", "Отменить", true, handler);
        //final JButton redoButton = addButtonToToolBar(toolBar, "redo.png", "Повторить", true, handler);
        //addButtonToToolBar(toolBar, "Примеры задач", "Примеры задач . . .", false, handler);
        addButtonToToolBar(toolBar, "help.png", "Помощь", true, handler.helpHandler());

        //undoButton.setEnabled(false);
        //redoButton.setEnabled(false);

        f.add(toolBar, BorderLayout.NORTH);

        structuredEditorScrPane.requestFocusInWindow();
        f.setVisible(true);
        if (filename!=null && !filename.equals("")){
            handler.openTask(filename);
        }
    }

    public TestEditorLogStudent() {
        makeContainer(new JFrame("Модуль ученика"),"");

    }

    @Deprecated
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
            nodesRegistry.registerNode(LogicStatement.class, "title", taskTitle);

            Element taskDescription = document.createElement("description");
            nodesRegistry.registerNode(LogicStatement.class, "statement", taskDescription);

            //Logic conditions
            Element trueCondition = document.createElement("condition");
            trueCondition.setAttribute("type", "True");
            nodesRegistry.registerNode(TrueLogicCondition.class, trueCondition);
            Attr trueConditionNum = document.createAttribute("num");
            nodesRegistry.registerNode(TrueLogicCondition.class, "num", trueConditionNum);

            Element falseCondition = document.createElement("condition");
            falseCondition.setAttribute("type", "False");
            nodesRegistry.registerNode(FalseLogicCondition.class, falseCondition);
            Attr falseConditionNum = document.createAttribute("num");
            nodesRegistry.registerNode(FalseLogicCondition.class, "num", falseConditionNum);

            //Functions
            Element notFnc = document.createElement("function");
            notFnc.setAttribute("type", "Not");
            nodesRegistry.registerNode(LogNotExpr.class, notFnc);

            Element orFnc = document.createElement("function");
            orFnc.setAttribute("type", "Or");
            nodesRegistry.registerNode(LogOrExpr.class, orFnc);

            Element andFnc = document.createElement("function");
            andFnc.setAttribute("type", "And");
            nodesRegistry.registerNode(LogAndExpr.class, andFnc);

            //Atom
            Element atom = document.createElement("atom");
            nodesRegistry.registerNode(LogicAtom.class, atom);
            Attr atomName = document.createAttribute("name");
            nodesRegistry.registerNode(LogicAtom.class, "val", atomName);

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
        reg.registerBean(LogicStatement.class);
        reg.registerBean(LogicAtom.class);
        reg.registerBean(LogicCondition.class);
        reg.registerBean(FalseLogicCondition.class);
        reg.registerBean(TrueLogicCondition.class);
        reg.registerBean(LogAndExpr.class);
        reg.registerBean(LogOrExpr.class);
        reg.registerBean(LogNotExpr.class);
        reg.registerBean(Expr.class);

        StructuredEditorModel model = new StructuredEditorModel(st);
        model.setBeansRegistry(reg);
        return model;
    }
}
