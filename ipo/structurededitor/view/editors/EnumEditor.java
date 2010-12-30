package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.controller.FieldMask;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.elements.ComboBoxTextEditorElement;
import ru.ipo.structurededitor.view.elements.VisibleElement;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.beans.PropertyDescriptor;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 03.01.2010
 * Time: 23:49:11
 */
public class EnumEditor extends FieldEditor {

    private ComboBoxTextEditorElement<Enum<?>> EnumSelectionElement;
    //private ContainerElement container;

    public EnumEditor(Object o, String fieldName, FieldMask mask) {
        super(o, fieldName, mask);
    }

    @Override
    public VisibleElement createElement(StructuredEditorModel model) {
        EnumSelectionElement = createEnumSelectionElement(model);
        setModificationVector(model.getModificationVector());
        //container = new ContainerElement(model, EnumSelectionElement);
        Field[] possibleValues = new Field[20];
        PropertyDescriptor pd;
        Class<? extends Enum> eclass;
        try {


            FieldMask mask = getMask();
            if (mask!=null){
                eclass = (Class<? extends Enum>) (mask.getValueClass(getFieldType()));

            } else {
                eclass = (Class<? extends Enum>) (getFieldType());
            }
            possibleValues = eclass.getFields();
        }
        catch (Exception e) {
            throw new Error("Fail in EnumEditor.createElement()");
        }
        //Method[] possibleMethods = getObject().getClass().getMethods();
        String const_name;
        for (Field pv : possibleValues) {
            if (pv.isEnumConstant()) {
                const_name = pv.getName();
                EnumSelectionElement.addValue(const_name, "",Enum.valueOf(eclass, const_name));
            }
        }
        // else {
        //       System.out.println("This is not enum ");
        // }
        /*
        container.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
                //if ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0 && e.getKeyChar() == '\u0001') //Ctrl+a
                //    container.setSubElement(beanClassSelectionElement);
            }

            public void keyPressed(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
            }
        });
        */
        //return container;

        //updateElement();
        updateElement();
        return EnumSelectionElement;
    }

    @Override
    protected void updateElement() {
        /*if (EmptyFieldsRegistry.getInstance().isEmpty((DSLBean) getObject(), getFieldName())){
            EnumSelectionElement.forcedSetValue(null);
        }
        else {*/
            EnumSelectionElement.forcedSetValue((Enum<?>) getValue());
        //}
    }

    private ComboBoxTextEditorElement<Enum<?>> createEnumSelectionElement(final StructuredEditorModel model) {
        final ComboBoxTextEditorElement<Enum<?>> res = new ComboBoxTextEditorElement<Enum<?>>(model);
        res.setEmptyString("[Выберите вариант]");
        res.addPropertyChangeListener("text", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                Enum<?> value = res.getValue();
                //System.out.println("enum value =" + value);
                //if (value != null) {
                    setValue(value);
                //}

            }
        });
        /*res.addPropertyChangeListener("refresh", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                VisibleElement.RefreshProperties rp = (VisibleElement.RefreshProperties) evt.getNewValue();
                setEmpty(rp.getEmpty());
                setObject(rp.getObject());
                Enum<?> val = (Enum<?>) getValue();
                if (val == null)
                    res.setText("");
                else
                    res.setValue(val);
                res.resetPosition();
                res.resumeRefresh();

            }
        });*/
        return res;
    }

}