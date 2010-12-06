package ru.ipo.structurededitor;

import ru.ipo.structurededitor.StructuredEditor;
import ru.ipo.structurededitor.controller.EditorsRegistry;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.model.DSLBeansRegistry;
import ru.ipo.structurededitor.testLang.*;
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
public class TestEditor4 {

    /*static {
        UIManager.installLookAndFeel("UI for structured editor", ComponentUI.class.getName());
    }*/

    private StructuredEditorModel model = new StructuredEditorModel();

    public TestEditor4() {
        JFrame f = new JFrame("Test Editor");

        //TODO think of the appropriate place to this default registrations
        EditorsRegistry<FieldEditor> editorsRegistry = EditorsRegistry.getInstance(FieldEditor.class);
        editorsRegistry.setDefaultEditor(VoidEditor.class);
        editorsRegistry.setNextArrayEditor(NextArrayDSLBeanEditor.class);
        editorsRegistry.setEnumEditor(EnumEditor.class);
        editorsRegistry.registerEditor(String.class, StringEditor.class);
        editorsRegistry.registerEditor(int.class, IntEditor.class);
        editorsRegistry.registerEditor(double.class, DoubleEditor.class);
        editorsRegistry.registerEditor(boolean.class, BooleanEditor.class);
        //editorsRegistry.registerEditor(Count.class, EnumEditor.class);

        DSLBeansRegistry.getInstance().registerBean(Bean1.class);
        DSLBeansRegistry.getInstance().registerBean(Bean2.class);
        DSLBeansRegistry.getInstance().registerBean(BeanA.class);
        DSLBeansRegistry.getInstance().registerBean(BeanA1.class);
        DSLBeansRegistry.getInstance().registerBean(BeanA2.class);
        DSLBeansRegistry.getInstance().registerBean(BeanA3.class);

        Bean2 bean2 = new Bean2();
        final StructuredEditorModel model = createModel(bean2);
//        f.add(new JScrollPane(new JTextArea("asdf")));
        StructuredEditor structuredEditor = new StructuredEditor(model, bean2);
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

    private StructuredEditorModel createModel(DSLBean bean2) {
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


        model.setRootElement(new EditorRenderer(model, bean2).getRenderResult());
        return model;
    }
}
