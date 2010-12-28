package ru.ipo.structurededitor;

import org.w3c.dom.*;
import ru.ipo.structurededitor.controller.*;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.model.DSLBeansRegistry;
import ru.ipo.structurededitor.structureBuilder.MyErrorHandler;
import ru.ipo.structurededitor.structureSerializer.NodesRegistry;
import ru.ipo.structurededitor.testLang.*;
import ru.ipo.structurededitor.testLang.comb.*;
import ru.ipo.structurededitor.view.EditorRenderer;
import ru.ipo.structurededitor.view.StatusBar;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.editors.*;
import ru.ipo.structurededitor.structureBuilder.StructureBuilder;
import ru.ipo.structurededitor.view.images.ImageGetter;
import ru.ipo.structurededitor.xmlViewer.XMLViewer;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.EventListener;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 02.01.2010
 * Time: 17:05:46
 */
public class TestEditor3 {

    /*static {
        UIManager.installLookAndFeel("UI for structured editor", ComponentUI.class.getName());
    }*/

    private StructuredEditorModel model;

    public TestEditor3() {
        JFrame f = new JFrame("Модуль учителя");
        //f.setLayout(new GridLayout(2,1));
        BorderLayout br = new BorderLayout();
        f.setLayout(br);
        //TODO think of the appropriate place to this default registrations
        //-------Editors registry preparation
        EditorsRegistry<FieldEditor> editorsRegistry = EditorsRegistry.getInstance(FieldEditor.class);
        editorsRegistry.setDefaultEditor(VoidEditor.class);
        // editorsRegistry.setNextArrayEditor(NextArrayDSLBeanEditor.class);
        editorsRegistry.registerEditor(String.class, StringEditor.class);
        editorsRegistry.registerEditor(int.class, IntEditor.class);
        editorsRegistry.registerEditor(double.class, DoubleEditor.class);
        editorsRegistry.registerEditor(Boolean.class, BooleanEditor.class);
        editorsRegistry.registerEditor(Count.class, EnumEditor.class);

        //-------DSL beans registry preparation
        DSLBeansRegistry.getInstance().registerBean(Bean1.class);
        DSLBeansRegistry.getInstance().registerBean(BeanA.class);
        DSLBeansRegistry.getInstance().registerBean(BeanA1.class);
        DSLBeansRegistry.getInstance().registerBean(BeanA2.class);

        DSLBeansRegistry.getInstance().registerBean(ArrayExpr.class);
        DSLBeansRegistry.getInstance().registerBean(BinExpr.class);
        DSLBeansRegistry.getInstance().registerBean(CountExaminer.class);
        DSLBeansRegistry.getInstance().registerBean(IndexExaminer.class);
        DSLBeansRegistry.getInstance().registerBean(ListExaminer.class);
        DSLBeansRegistry.getInstance().registerBean(AnswerExaminer.class);
        DSLBeansRegistry.getInstance().registerBean(CurElementExpr.class);
        DSLBeansRegistry.getInstance().registerBean(DescartesPower.class);
        DSLBeansRegistry.getInstance().registerBean(EqExpr.class);
        DSLBeansRegistry.getInstance().registerBean(Expr.class);
        DSLBeansRegistry.getInstance().registerBean(IntSegment.class);
        DSLBeansRegistry.getInstance().registerBean(PrjExpr.class);
        DSLBeansRegistry.getInstance().registerBean(Kit.class);
        DSLBeansRegistry.getInstance().registerBean(Statement.class);
        DSLBeansRegistry.getInstance().registerBean(Examiner.class);
        DSLBeansRegistry.getInstance().registerBean(CombKit.class);
        DSLBeansRegistry.getInstance().registerBean(LayoutKit.class);
        DSLBeansRegistry.getInstance().registerBean(EnumKit.class);
        DSLBeansRegistry.getInstance().registerBean(ConstantElement.class);
        DSLBeansRegistry.getInstance().registerBean(InnerConstantElement.class);
        DSLBeansRegistry.getInstance().registerBean(IntConstantElement.class);
        DSLBeansRegistry.getInstance().registerBean(AddExpr.class);
        DSLBeansRegistry.getInstance().registerBean(DiffExpr.class);
        DSLBeansRegistry.getInstance().registerBean(RemExpr.class);
        DSLBeansRegistry.getInstance().registerBean(IntDivExpr.class);
        DSLBeansRegistry.getInstance().registerBean(EvExpr.class);
        DSLBeansRegistry.getInstance().registerBean(NotEvExpr.class);
        DSLBeansRegistry.getInstance().registerBean(LogAndExpr.class);
        DSLBeansRegistry.getInstance().registerBean(LogNotExpr.class);
        DSLBeansRegistry.getInstance().registerBean(LkExpr.class);
        DSLBeansRegistry.getInstance().registerBean(LogOrExpr.class);
        DSLBeansRegistry.getInstance().registerBean(GtExpr.class);
        DSLBeansRegistry.getInstance().registerBean(SlExpr.class);
        DSLBeansRegistry.getInstance().registerBean(ToNumExpr.class);
        DSLBeansRegistry.getInstance().registerBean(CalcExpr.class);
        DSLBeansRegistry.getInstance().registerBean(CalculableExpr.class);
        //DSLBeansRegistry.getInstance().registerBean(SimpleCalculableExpr.class);
        DSLBeansRegistry.getInstance().registerBean(ModCalculableExpr.class);


        //-------Nodes registry preparation
        nodesRegistryPrep();


        //------------Frame preparation

        Statement st = new Statement();
        final StructuredEditorModel model = createModel(st);

//        f.add(new JScrollPane(new JTextArea("asdf")));
        final StructuredEditor structuredEditor = new StructuredEditor(model);
        JScrollPane structuredEditorScrPane = new JScrollPane(structuredEditor);
        f.add(structuredEditorScrPane, BorderLayout.CENTER);
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
        MenuItem item1, item2, item3, item4, item5;
        file.add(item1 = new MenuItem("Создать"));
        file.add(item2 = new MenuItem("Открыть . . ."));
        file.add(item3 = new MenuItem("Сохранить . . ."));
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
        menuBar.add(help);
        //MyMenuHandler handler = new MyMenuHandler(f,xmlV,structuredEditor);
        MyMenuHandler handler = new MyMenuHandler(f, structuredEditor);
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
        addButtonToToolBar(toolBar, "save.png", "Сохранить . . .", true, handler);
        final JButton undoButton = addButtonToToolBar(toolBar, "undo.png", "Отменить", true, handler);
        final JButton redoButton = addButtonToToolBar(toolBar, "redo.png", "Повторить", true, handler);
        addButtonToToolBar(toolBar, "Примеры задач", "Примеры задач . . .", false, handler);
        addButtonToToolBar(toolBar, "help.png", "Помощь", true, handler);

        undoButton.setEnabled(false);
        redoButton.setEnabled(false);

        f.add(toolBar, BorderLayout.NORTH);

        structuredEditorScrPane.requestFocusInWindow();
        f.setVisible(true);
        final ModificationVector modificationVector=model.getModificationVector();
        modificationVector.addModificationListener(new ModificationListener() {
            public void modificationPerformed() {
                if (modificationVector.canRedo()) {
                    redoButton.setEnabled(true);
                    redoItem.setEnabled(true);
                } else {
                    redoButton.setEnabled(false);
                    redoItem.setEnabled(false);
                }
                if (modificationVector.canUndo()) {
                    undoButton.setEnabled(true);
                    undoItem.setEnabled(true);
                } else {
                    undoButton.setEnabled(false);
                    undoItem.setEnabled(false);
                }
            }
        });
        //model.setModificationVector(modificationVector);


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

    private void nodesRegistryPrep() {
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

            Element defaultNode = document.createElement("unknown");
            NodesRegistry.getInstance().setDefaultNode(defaultNode);

            Element emptyNode = document.createElement("empty");
            NodesRegistry.getInstance().setEmptyNode(emptyNode);


            Attr taskTitle = document.createAttribute("title");
            NodesRegistry.getInstance().registerNode(Statement.class, "title", taskTitle);

            Element taskDescription = document.createElement("description");
            NodesRegistry.getInstance().registerNode(Statement.class, "statement", taskDescription);

            //Verifiers
            Element countVerifier = document.createElement("verifier");
            countVerifier.setAttribute("type", "CountVerifier");
            NodesRegistry.getInstance().registerNode(CountExaminer.class, countVerifier);

            Element indexVerifier = document.createElement("verifier");
            indexVerifier.setAttribute("type", "IndexVerifier");
            NodesRegistry.getInstance().registerNode(IndexExaminer.class, indexVerifier);

            Element listVerifier = document.createElement("verifier");
            listVerifier.setAttribute("type", "ListVerifier");
            NodesRegistry.getInstance().registerNode(ListExaminer.class, listVerifier);

            Element answerVerifier = document.createElement("verifier");
            answerVerifier.setAttribute("type", "AnswerVerifier");
            NodesRegistry.getInstance().registerNode(AnswerExaminer.class, answerVerifier);

            //Sets
            Element numericSet = document.createElement("set");
            numericSet.setAttribute("type", "NumericSet");
            NodesRegistry.getInstance().registerNode(IntSegment.class, numericSet);
            Attr numericSetFirst = document.createAttribute("first");
            NodesRegistry.getInstance().registerNode(IntSegment.class, "from", numericSetFirst);
            Attr numericSetLast = document.createAttribute("last");
            NodesRegistry.getInstance().registerNode(IntSegment.class, "to", numericSetLast);

            Element decartSet = document.createElement("set");
            decartSet.setAttribute("type", "DecartSet");
            NodesRegistry.getInstance().registerNode(DescartesPower.class, decartSet);
            Attr decartSetPower = document.createAttribute("power");
            NodesRegistry.getInstance().registerNode(DescartesPower.class, "pow", decartSetPower);

            Element combinationSet = document.createElement("set");
            combinationSet.setAttribute("type", "CombinationSet");
            NodesRegistry.getInstance().registerNode(CombKit.class, combinationSet);
            Attr combinationSetLength = document.createAttribute("length");
            NodesRegistry.getInstance().registerNode(CombKit.class, "k", combinationSetLength);

            Element layoutSet = document.createElement("set");
            layoutSet.setAttribute("type", "LayoutSet");
            NodesRegistry.getInstance().registerNode(LayoutKit.class, layoutSet);
            Attr layoutSetLength = document.createAttribute("length");
            NodesRegistry.getInstance().registerNode(LayoutKit.class, "k", layoutSetLength);

            Element enumerationSet = document.createElement("set");
            enumerationSet.setAttribute("type", "EnumerationSet");
            NodesRegistry.getInstance().registerNode(EnumKit.class, enumerationSet);

            Element constElement = document.createElement("constElement");
            NodesRegistry.getInstance().registerNode(InnerConstantElement.class, constElement);
            NodesRegistry.getInstance().registerNode(IntConstantElement.class, constElement);


            //Functions
            Element evenFnc = document.createElement("function");
            evenFnc.setAttribute("type", "Even");
            NodesRegistry.getInstance().registerNode(EvExpr.class, evenFnc);

            Element oddFnc = document.createElement("function");
            oddFnc.setAttribute("type", "Odd");
            NodesRegistry.getInstance().registerNode(NotEvExpr.class, oddFnc);

            Element notFnc = document.createElement("function");
            notFnc.setAttribute("type", "Not");
            NodesRegistry.getInstance().registerNode(LogNotExpr.class, notFnc);

            Element toDigitFnc = document.createElement("function");
            toDigitFnc.setAttribute("type", "ToDigit");
            NodesRegistry.getInstance().registerNode(ToNumExpr.class, toDigitFnc);

            Element equalsFnc = document.createElement("function");
            equalsFnc.setAttribute("type", "Equals");
            NodesRegistry.getInstance().registerNode(EqExpr.class, equalsFnc);

            Element greaterFnc = document.createElement("function");
            greaterFnc.setAttribute("type", "Greater");
            NodesRegistry.getInstance().registerNode(GtExpr.class, greaterFnc);

            Element divFnc = document.createElement("function");
            divFnc.setAttribute("type", "Div");
            NodesRegistry.getInstance().registerNode(IntDivExpr.class, divFnc);

            Element likeFnc = document.createElement("function");
            likeFnc.setAttribute("type", "Like");
            NodesRegistry.getInstance().registerNode(LkExpr.class, likeFnc);

            Element modFnc = document.createElement("function");
            modFnc.setAttribute("type", "Mod");
            NodesRegistry.getInstance().registerNode(RemExpr.class, modFnc);

            Element smallerFnc = document.createElement("function");
            smallerFnc.setAttribute("type", "Smaller");
            NodesRegistry.getInstance().registerNode(SlExpr.class, smallerFnc);

            Element parserFnc = document.createElement("function");
            parserFnc.setAttribute("type", "Parser");
            NodesRegistry.getInstance().registerNode(ModCalculableExpr.class, parserFnc);
            NodesRegistry.getInstance().registerNode(CalcExpr.class, parserFnc);
            Attr parserExp = document.createAttribute("exp");
            NodesRegistry.getInstance().registerNode(ModCalculableExpr.class, "ce", parserExp);
            NodesRegistry.getInstance().registerNode(CalcExpr.class, "ce", parserExp);
            Attr parserFncMod = document.createAttribute("mod");
            NodesRegistry.getInstance().registerNode(ModCalculableExpr.class, "mod", parserFncMod);

            Element projectionFnc = document.createElement("function");
            projectionFnc.setAttribute("type", "Projection");
            NodesRegistry.getInstance().registerNode(PrjExpr.class, projectionFnc);
            Attr projectionFncAxis = document.createAttribute("axis");
            NodesRegistry.getInstance().registerNode(PrjExpr.class, "ind", projectionFncAxis);

            Element sumFnc = document.createElement("function");
            sumFnc.setAttribute("type", "Sum");
            NodesRegistry.getInstance().registerNode(AddExpr.class, sumFnc);

            Element subFnc = document.createElement("function");
            subFnc.setAttribute("type", "Sub");
            NodesRegistry.getInstance().registerNode(DiffExpr.class, subFnc);

            Element orFnc = document.createElement("function");
            orFnc.setAttribute("type", "Or");
            NodesRegistry.getInstance().registerNode(LogOrExpr.class, orFnc);

            Element andFnc = document.createElement("function");
            andFnc.setAttribute("type", "And");
            NodesRegistry.getInstance().registerNode(LogAndExpr.class, andFnc);

            Element currentSetElement = document.createElement("current-set-element");
            NodesRegistry.getInstance().registerNode(CurElementExpr.class, currentSetElement);

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
        model = new StructuredEditorModel(st);
        /*final ModificationVector modificationVector = new ModificationVector();
        model.setModificationVector(modificationVector);*/

        return model;
    }
}