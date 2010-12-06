package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.controller.EmptyFieldsRegistry;
import ru.ipo.structurededitor.controller.Modification;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.elements.VisibleElement;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;

import static ru.ipo.structurededitor.view.editors.ArrayEditor.resizeArray;

/**
 * Базовый класс для компонент редактирования ПОЯ при помощи нового редактора
 */
public abstract class FieldEditor {

    private Object o;
    private String fieldName;
    private int index;
    private boolean arrItem;
    private boolean empty = true;

    public FieldEditor(Object o, String fieldName) {
        this.o = o;
        this.fieldName = fieldName;
        this.index = 0;
        this.arrItem = false;
        empty = forcedGetValue() == null;
    }

    public FieldEditor(Object o, String fieldName, int index) {
        this.o = o;
        this.fieldName = fieldName;
        this.index = index;
        this.arrItem = true;
        empty = forcedGetValue() == null;
    }

    protected Object getObject() {
        return o;
    }

    protected void setObject(Object o) {
        this.o = o;
    }

    protected String getFieldName() {
        return fieldName;
    }

    protected int getIndex() {
        return index;
    }

    protected void setIndex(int index){
        this.index=index;
        updateElement();
    }

    protected boolean isArrItem() {
        return arrItem;
    }

    protected boolean isEmpty() {
        return empty;
    }

    protected void setEmpty(boolean empty) {
        this.empty = empty;
        Object val = getValue();
        if (!isArrItem())
            EmptyFieldsRegistry.getInstance().setEmpty((DSLBean) getObject(), getFieldName(), empty);
    }

    protected void setValue(Object value) {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(getFieldName(), getObject().getClass());
            Method rm = pd.getReadMethod();
            Object val = rm.invoke(getObject());
            Method wm = pd.getWriteMethod();
            if (value == null) {
                            empty = true;
                            if (!isArrItem()){
                                EmptyFieldsRegistry.getInstance().setEmpty((DSLBean) getObject(), getFieldName(), true);
                                new Modification((DSLBean)getObject(),getFieldName(),val,value,-1);
                                //Next lines throws IrregularArgumentException
                                //wm.invoke(getObject(), new Object[]{null});
                                //wm.invoke(getObject(), null);
                            } else{
                              Object oldItem= Array.get(val,getIndex());
                              val = ArrayEditor.delItem(val,index);
                              wm.invoke(getObject(),val);
                              new Modification((DSLBean)getObject(),getFieldName(),
                                      oldItem,value,getIndex());
                            }

                            return;
                        }

            if (EmptyFieldsRegistry.getInstance().isEmpty((DSLBean) getObject(), getFieldName())){
                val=null;
            }
            EmptyFieldsRegistry.getInstance().setEmpty((DSLBean) getObject(), getFieldName(), false);
            if (empty){
                
                empty = false;
            }
            if (isArrItem()) {

                if (val == null) {
                    val = Array.newInstance(pd.getPropertyType().getComponentType(), 1);

                }
                if (Array.getLength(val) <= index)
                    val = resizeArray(val, index + 1);

                Object oldItem=Array.get(val, index);
                Array.set(val, index, value);
                wm.invoke(getObject(), val);
                new Modification((DSLBean)getObject(),getFieldName(),
                                      oldItem,value,getIndex());
            } else{
                wm.invoke(getObject(), new Object[]{value});
                new Modification((DSLBean)getObject(),getFieldName(),val,value,-1);
            }

        } catch (Exception e1) {
            throw new Error("Fail in FieldEditor.setValue()");
        }

        //updateElement();
    }

    protected Object getValue() {
        if (empty) return null;
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
    }

    protected Object forcedGetValue() {
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
    }

    protected Class<?> getFieldType() {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(getFieldName(), getObject().getClass());
            return pd.getPropertyType();
        } catch (Exception e1) {
            throw new Error("Fail in FieldEditor.getValueType()");
        }
    }

    public abstract VisibleElement createElement(StructuredEditorModel model);

    protected abstract void updateElement();
}
