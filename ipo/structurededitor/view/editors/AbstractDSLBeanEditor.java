package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.controller.FieldMask;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.model.DSLBeanParams;
import ru.ipo.structurededitor.model.DSLBeansRegistry;
import ru.ipo.structurededitor.view.EditorRenderer;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.elements.ComboBoxTextEditorElement;
import ru.ipo.structurededitor.view.elements.ContainerElement;
import ru.ipo.structurededitor.view.elements.VisibleElement;
import ru.ipo.structurededitor.view.events.ComboBoxSelectListener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 03.01.2010
 * Time: 23:49:11
 */
public class AbstractDSLBeanEditor extends FieldEditor {

    private ComboBoxTextEditorElement<Class<? extends DSLBean>> beanClassSelectionElement;
    private ContainerElement container;
    private StructuredEditorModel model;

    /*public AbstractDSLBeanEditor(Object o, String fieldName) {
        super(o, fieldName);
    } */

    public AbstractDSLBeanEditor(Object o, String fieldName, FieldMask mask) {
        super(o, fieldName, mask);
    }

    @Override
    public VisibleElement createElement(final StructuredEditorModel model) {
        this.model = model;
        setModificationVector(model.getModificationVector());

        beanClassSelectionElement = createBeanSelectionElement(model);

        container = new ContainerElement(model, beanClassSelectionElement);
        setComboBoxList();


        container.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
                if ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0 && e.getKeyChar() == '\u0001') { //Ctrl+a
                    if (getValue() != null) {
                        container.setSubElement(beanClassSelectionElement);
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

        updateElement();

        return container;
    }



    private void setComboBoxList(){
        List<Class<? extends DSLBean>> classes;
        FieldMask mask = getMask();
        if (mask!=null)
            classes = DSLBeansRegistry.getInstance().
                    getAllSubclasses((Class<? extends DSLBean>) mask.getValueClass(getFieldType()), true);
        else
            classes = DSLBeansRegistry.getInstance().getAllSubclasses((Class<? extends DSLBean>) getFieldType(), true);
        for (Class<? extends DSLBean> clazz : classes){
            DSLBeanParams beanParams;
            beanParams = clazz.getAnnotation(DSLBeanParams.class);
            //String str;
            if (beanParams==null){
                beanClassSelectionElement.addValue(clazz.getSimpleName(),"", clazz);
            }
            else {
                beanClassSelectionElement.addValue(beanParams.shortcut(),beanParams.description(), clazz);
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
       /* res.addPropertyChangeListener("text", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {

            }
        });
        res.addPropertyChangeListener("refresh", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                VisibleElement.RefreshProperties rp = (VisibleElement.RefreshProperties) evt.getNewValue();
                setEmpty(rp.getEmpty());
                setObject(rp.getObject());
                Object val = getValue();
                String str;
                if (val == null){
                    res.setText("");
                    str="";
                }
                else{
                    res.forcedSetValue((Class<? extends DSLBean>) (val.getClass()));
                    str=res.getText();
                }
                res.resetPosition();
                res.resumeRefresh();
            }
        });  */
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

        Object value = getValue();
        if (value == null /*||  EmptyFieldsRegistry.getInstance().isEmpty((DSLBean)getObject(), getFieldName())*/){
            beanClassSelectionElement.setText("");
            container.setSubElement(beanClassSelectionElement);
        }
        else {
            EditorRenderer renderer = new EditorRenderer(model, (DSLBean) value);
            container.setSubElement(renderer.getRenderResult());
        }
    }
}
