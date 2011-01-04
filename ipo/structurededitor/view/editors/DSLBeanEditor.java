package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.controller.FieldMask;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.model.DSLBeanParams;
import ru.ipo.structurededitor.model.DSLBeansRegistry;
import ru.ipo.structurededitor.view.EditorRenderer;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.elements.*;
import ru.ipo.structurededitor.view.events.ComboBoxSelectListener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 03.01.2010
 * Time: 23:49:11
 */
public class DSLBeanEditor extends FieldEditor {

    private TextElement innerElement;
    //private ContainerElement container;
    private StructuredEditorModel model;
    boolean isAbstract;



    private void setNonAbstractInnerElement() {
        final Class<?> beanType;
        FieldMask mask = getMask();
        if (mask != null)
            beanType = mask.getValueClass(getFieldType());
        else
            beanType = getFieldType();

        innerElement.addKeyListener(new KeyListener() {
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
    }

    public DSLBeanEditor(Object o, String fieldName, FieldMask mask, StructuredEditorModel model) {
        super(o, fieldName, mask, model);
        this.model = model;
        setModificationVector(model.getModificationVector());
        if (mask==null){
            isAbstract = Modifier.isAbstract(getFieldType().getModifiers());
        } else {
            isAbstract = Modifier.isAbstract(mask.getValueClass(getFieldType()).getModifiers());
        }
        final ComboBoxTextEditorElement<Class<? extends DSLBean>> beanClassSelectionElement;
        if (isAbstract) {
            beanClassSelectionElement = createBeanSelectionElement(model);
            innerElement = beanClassSelectionElement;
        } else {
            beanClassSelectionElement = null;
            innerElement = new TextElement(model, "");
            innerElement.setEmptyString("[Не заполнено]");
        }


        final ContainerElement container = new ContainerElement(model, innerElement);
        if (isAbstract) {
            setComboBoxList();
        } else {
            setNonAbstractInnerElement();
        }
        container.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
                if ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0 && e.getKeyChar() == '\u0001') { //Ctrl+a
                    if (getValue() != null) {
                        container.setSubElement(innerElement);
                        setValue(null);
                        e.consume();
                    }
                }
            }


            public void keyPressed(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
            }
        });

        setElement(container);
        updateElement();


    }


    private void setComboBoxList() {
        ComboBoxTextEditorElement<Class<? extends DSLBean>> beanClassSelectionElement =
                (ComboBoxTextEditorElement<Class<? extends DSLBean>>) innerElement;
        List<Class<? extends DSLBean>> classes;
        FieldMask mask = getMask();
        if (mask != null)
            classes = DSLBeansRegistry.getInstance().
                    getAllSubclasses((Class<? extends DSLBean>) mask.getValueClass(getFieldType()), true);
        else
            classes = DSLBeansRegistry.getInstance().getAllSubclasses((Class<? extends DSLBean>) getFieldType(), true);
        for (Class<? extends DSLBean> clazz : classes) {
            DSLBeanParams beanParams;
            beanParams = clazz.getAnnotation(DSLBeanParams.class);
            //String str;
            if (beanParams == null) {
                beanClassSelectionElement.addValue(clazz.getSimpleName(), "", clazz);
            } else {
                beanClassSelectionElement.addValue(beanParams.shortcut(), beanParams.description(), clazz);
            }

        }
    }

    private ComboBoxTextEditorElement<Class<? extends DSLBean>> createBeanSelectionElement(final StructuredEditorModel model) {
        final ComboBoxTextEditorElement<Class<? extends DSLBean>> res = new ComboBoxTextEditorElement<Class<? extends DSLBean>>(model);
        res.setEmptyString("[Выберите вариант]");

        res.addComboBoxSelectListener(new ComboBoxSelectListener() {
            public void itemSelected() {
                Class<? extends DSLBean> value = res.getValue();
                if (value != null) {
                    setNewBean(value, model);
                }
            }
        });
        res.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    Class<? extends DSLBean> value = res.getValue();
                    if (value != null) {
                        setNewBean(value, model);
                        e.consume();
                    }
                }

            }

            public void keyTyped(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
            }
        });
        return res;
    }

    private void setNewBean(Class<? extends DSLBean> beanClass, StructuredEditorModel model) {
        try {
            DSLBean bean = beanClass.newInstance();
            setValue(bean);
            updateElement();
        } catch (Exception e) {
            throw new Error("Failed to initialize DSL Bean");
        }
    }

    @Override
    protected void updateElement() {
        if (model == null)
            return;
        final ContainerElement container = (ContainerElement) getElement();

        final ComboBoxTextEditorElement<Class<? extends DSLBean>> beanClassSelectionElement;

        if (isAbstract) {
            beanClassSelectionElement =
                    (ComboBoxTextEditorElement<Class<? extends DSLBean>>) innerElement;
        }
        else{
           beanClassSelectionElement =null;
        }

        Object value = getValue();
        if (value == null /*||  EmptyFieldsRegistry.getInstance().isEmpty((DSLBean)getObject(), getFieldName())*/) {
            if (isAbstract)
                beanClassSelectionElement.setText("");
            container.setSubElement(innerElement);
        } else {
            EditorRenderer renderer = new EditorRenderer(model, (DSLBean) value);
            container.setSubElement(renderer.getRenderResult());
        }
    }

}
