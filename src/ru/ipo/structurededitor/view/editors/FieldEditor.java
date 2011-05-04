package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.controller.FieldMask;
import ru.ipo.structurededitor.controller.Modification;
import ru.ipo.structurededitor.controller.ModificationVector;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.elements.VisibleElement;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;


/**
 * Базовый класс для компонент редактирования ПОЯ при помощи нового редактора
 */
public abstract class FieldEditor {

    private Object o;
    private String fieldName;
    private boolean singleLined = false;

    public VisibleElement getElement() {
        return editorElement;
    }

    protected void setElement(VisibleElement editorElement) {
        this.editorElement = editorElement;
    }

    private VisibleElement editorElement;

    public FieldMask getMask() {
        return mask;
    }

    public void setMask(FieldMask mask) {
        this.mask = mask;
        updateElement();
    }

    private FieldMask mask;
    //private boolean arrItem;
    //private boolean empty = true;

    public void setModificationVector(ModificationVector modificationVector) {
        this.modificationVector = modificationVector;
    }

    private ModificationVector modificationVector;

    /*ModificationEventSupport mes=new ModificationEventSupport();
   public void addModificationListener(ModificationListener l) {
       mes.addModificationListener(l);
   }

   public void removeModificationListener(ModificationListener l) {
       mes.removeModificationListener(l);
   } */
    public FieldEditor(Object o, String fieldName, FieldMask mask, boolean singleLined, StructuredEditorModel model) {
        this.o = o;
        this.fieldName = fieldName;
        this.mask = mask;
        this.singleLined = singleLined;
        //empty = forcedGetValue() == null;
    }

    public FieldEditor(Object o, String fieldName, FieldMask mask, StructuredEditorModel model) {
        this.o = o;
        this.fieldName = fieldName;
        this.mask = mask;

        //empty = forcedGetValue() == null;
    }

    /*public FieldEditor(Object o, String fieldName, int index) {
        this.o = o;
        this.fieldName = fieldName;
        this.index = index;
        this.arrItem = true;
        //empty = forcedGetValue() == null;
    } */

    protected Object getObject() {
        return o;
    }

    protected void setObject(Object o) {
        this.o = o;
    }

    protected String getFieldName() {
        return fieldName;
    }


    /*protected boolean isEmpty() {
        return empty;
    }   */

    /*protected void setEmpty(boolean empty) {
        this.empty = empty;
        Object val = getValue();
        if (!isArrItem())
            EmptyFieldsRegistry.getInstance().setEmpty((DSLBean) getObject(), getFieldName(), empty);
    } */

    public ModificationVector getModificationVector() {
        return modificationVector;
    }

    protected void setValue(Object value) {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(getFieldName(), getObject().getClass());
            Method rm = pd.getReadMethod();
            Object val = rm.invoke(getObject());
            Method wm = pd.getWriteMethod();
            if (value == null && mask == null) {
                if (val instanceof Integer || val instanceof Double)
                    value = 0;
                if (modificationVector != null)
                    modificationVector.add(
                            new Modification((DSLBean) getObject(), getFieldName(), val, value, null));
                wm.invoke(getObject(), value);
                return;
            }
            if (mask != null) {

                Object oldItem = mask.get(val);
                Object oldVal = val;
                val = mask.set(val, value);
                if (oldVal == val) {
                    if (modificationVector != null)
                        modificationVector.add(new Modification((DSLBean) getObject(), getFieldName(),
                                oldItem, value, mask));
                } else {
                    wm.invoke(getObject(), val);
                    if (modificationVector != null)
                        modificationVector.add(new Modification((DSLBean) getObject(), getFieldName(), oldVal, val, null));
                }
            } else {
                wm.invoke(getObject(), value);
                if (modificationVector != null)
                    modificationVector.add(new Modification((DSLBean) getObject(), getFieldName(), val, value, null));
            }

        } catch (Exception e1) {
            throw new Error("Fail in FieldEditor.setValue()");
        }

        //updateElement();
    }

    protected Object getValue() {
        //if (empty) return null;
        try {
            PropertyDescriptor pd = new PropertyDescriptor(getFieldName(), getObject().getClass());
            Method wm = pd.getReadMethod();
            Object value = wm.invoke(getObject());
            if (mask != null && value != null) {
                return mask.get(value);
            } else
                return value;
        } catch (Exception e1) {
            throw new Error("Fail in FieldEditor.getValue()");
        }
    }

    /*protected Object forcedGetValue() {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(getFieldName(), getObject().getClass());
            Method wm = pd.getReadMethod();
            Object value = wm.invoke(getObject());
            if (isArrItem() && value != null) {
                if (index < Array.getLength(value))
                    return Array.get(value, index);
                else
                    return null;
            } else
                return value;
        } catch (Exception e1) {
            throw new Error("Fail in FieldEditor.getValue()");
        }
    } */

    protected Class<?> getFieldType() {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(getFieldName(), getObject().getClass());
            return pd.getPropertyType();
        } catch (Exception e1) {
            throw new Error("Fail in FieldEditor.getValueType()");
        }
    }

    //public abstract VisibleElement createElement(StructuredEditorModel model);


    protected abstract void updateElement();
}
