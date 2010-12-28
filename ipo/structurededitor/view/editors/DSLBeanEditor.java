package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.view.EditorRenderer;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.elements.ContainerElement;
import ru.ipo.structurededitor.view.elements.TextElement;
import ru.ipo.structurededitor.view.elements.VisibleElement;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 03.01.2010
 * Time: 22:46:22
 */
public class DSLBeanEditor extends FieldEditor {
    //TODO remove this class and use only AbstractDSLBeanEditor 

    public DSLBeanEditor(Object o, String fieldName) {
        super(o, fieldName);
    }

    public DSLBeanEditor(Object o, String fieldName, int index) {
        super(o, fieldName, index);
    }

    /*public DSLBeanEditor(Object o, String fieldName, int Index) {
        super(o, fieldName, Index);
    }

    /*
    @Override
    public VisibleElement createElement(StructuredEditorModel model) {
        DSLBean bean = (DSLBean) getValue();
        if (bean == null)
            return new TextElement(model, "null");
        EditorRenderer renderer = new EditorRenderer(model, (DSLBean)getValue());
        return renderer.getRenderResult();
    }
    */

    private TextElement nullElement;
    private ContainerElement container;
    private StructuredEditorModel model;

    @Override
    public VisibleElement createElement(final StructuredEditorModel model) {
        this.model = model;
        setModificationVector(model.getModificationVector());

        nullElement = new TextElement(model, "");
        nullElement.setEmptyString("[Не заполнено]");
        container = new ContainerElement(model, nullElement);
        final Class<?> beanType;
        if (isArrItem())
            beanType = getFieldType().getComponentType();
        else
            beanType = getFieldType();

        nullElement.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
                if (Character.isLetterOrDigit(e.getKeyChar())) {
                    try {
                        DSLBean o;
                        o = (DSLBean) beanType.newInstance();
                        setValue(o);
                        updateElement();
                    } catch (Exception e1) {
                        throw new Error("Failed to instantiate bean: " + e1);
                    }
                }
            }

            public void keyPressed(KeyEvent keyEvent) {
            }

            public void keyReleased(KeyEvent keyEvent) {
            }
        });

        container.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
                if ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0 && e.getKeyChar() == '\u0001') { //Ctrl+a
                    if (getValue() != null) {
                        setValue(null);
                        updateElement();
                        e.consume();
                    }
                }
            }

            public void keyPressed(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
            }
        });
        /*container.addPropertyChangeListener("refresh", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                VisibleElement.RefreshProperties rp = (VisibleElement.RefreshProperties) evt.getNewValue();
                setEmpty(rp.getEmpty());
                setObject(rp.getObject());
                if (rp.getEmpty()) {
                    setValue(null);
                } else {
                    if (container.getChild(0) == nullElement) {
                        try {
                            DSLBean o;
                            o = (DSLBean) getValue();
                            EditorRenderer renderer = new EditorRenderer(model, o);
                            container.setSubElement(renderer.getRenderResult());

                        } catch (Exception e1) {
                            throw new Error("Failed to instantiate bean: " + e1);
                        }
                    }
                    container.getChild(0).Refresh(new VisibleElement.RefreshProperties(false, getValue()));
                }
            }
        });*/

        updateElement();
        return container;
    }

    protected void updateElement() {
        Object value = getValue();

        if (model == null)
            return;

        if (value == null)
            container.setSubElement(nullElement);
        else {
            EditorRenderer renderer = new EditorRenderer(model, (DSLBean)value);
            container.setSubElement(renderer.getRenderResult());
        }
    }
}