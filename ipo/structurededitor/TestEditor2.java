package ru.ipo.structurededitor;

import ru.ipo.structurededitor.controller.EditorsRegistry;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.model.DSLBeansRegistry;
import ru.ipo.structurededitor.testLang.*;
import ru.ipo.structurededitor.testLang.comb.*;
import ru.ipo.structurededitor.view.EditorRenderer;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.editors.*;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 02.01.2010
 * Time: 17:05:46
 */
public class TestEditor2 {

    /*static {
        UIManager.installLookAndFeel("UI for structured editor", ComponentUI.class.getName());
    }*/

    private StructuredEditorModel model;

    public TestEditor2() {
        JFrame f = new JFrame("Test Editor");

        //TODO think of the appropriate place to this default registrations
        /*EditorsRegistry editorsRegistry = EditorsRegistry.getInstance();
        editorsRegistry.setDefaultEditor(VoidEditor.class);
        //editorsRegistry.setNextArrayEditor(NextArrayDSLBeanEditor.class);
        editorsRegistry.registerEditor(String.class, StringEditor.class);
        editorsRegistry.registerEditor(int.class, IntEditor.class);
        editorsRegistry.registerEditor(double.class, DoubleEditor.class);
        editorsRegistry.registerEditor(Boolean.class, BooleanEditor.class);
        editorsRegistry.registerEditor(Count.class, EnumEditor.class);*/

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

        Statement st = new Statement();
        final StructuredEditorModel model = createModel(st);
//        f.add(new JScrollPane(new JTextArea("asdf")));
        StructuredEditor structuredEditor = new StructuredEditor(model);
        f.add(new JScrollPane(structuredEditor));

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(640, 480);
        f.setLocationRelativeTo(null);
        f.setVisible(true);

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
        return model;
    }
}
