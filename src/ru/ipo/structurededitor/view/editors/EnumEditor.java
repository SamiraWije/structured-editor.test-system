package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.controller.FieldMask;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.elements.ComboBoxTextEditorElement;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 03.01.2010
 * Time: 23:49:11
 */
public class EnumEditor extends FieldEditor {

    //private ComboBoxTextEditorElement<Enum<?>> EnumSelectionElement;
    //private ContainerElement container;

    public EnumEditor(Object o, String fieldName, FieldMask mask, StructuredEditorModel model) {
        super(o, fieldName, mask, model);
        ComboBoxTextEditorElement<Enum<?>> EnumSelectionElement = createEnumSelectionElement(model);
        setModificationVector(model.getModificationVector());
        //container = new ContainerElement(model, EnumSelectionElement);
        Field[] possibleValues = new Field[20];
        PropertyDescriptor pd;
        Class<? extends Enum> eclass;
        try {


            if (mask != null) {
                eclass = (Class<? extends Enum>) (mask.getValueClass(getFieldType()));

            } else {
                eclass = (Class<? extends Enum>) (getFieldType());
            }
            possibleValues = eclass.getFields();
        } catch (Exception e) {
            throw new Error("Fail in EnumEditor.createElement()");
        }
        //Method[] possibleMethods = getObject().getClass().getMethods();
        String const_name;
        for (Field pv : possibleValues) {
            if (pv.isEnumConstant()) {
                const_name = pv.getName();
                EnumSelectionElement.addValue(const_name, "", Enum.valueOf(eclass, const_name));
            }
        }
        setElement(EnumSelectionElement);
        updateElement();
    }


    @Override
    protected void updateElement() {
        @SuppressWarnings("unchecked")
        ComboBoxTextEditorElement<Enum<?>> EnumSelectionElement = (ComboBoxTextEditorElement<Enum<?>>) getElement();
        EnumSelectionElement.forcedSetValue((Enum<?>) getValue());
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