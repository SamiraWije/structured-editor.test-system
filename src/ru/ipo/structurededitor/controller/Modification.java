package ru.ipo.structurededitor.controller;


import ru.ipo.structurededitor.model.DSLBean;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Vector;

import static ru.ipo.structurededitor.view.editors.ArrayEditor.resizeArray;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 08.11.2010
 * Time: 15:09:59
 * To change this template use File | Settings | File Templates.
 */
public class Modification {
    private static Vector<Modification> vector;
    private static int position = -1;
    private static JButton redoButton,undoButton;
    private static MenuItem redoItem,undoItem;
    private DSLBean bean;
    private String fieldName;
    private Object oldValue;
    private Object newValue;
    private int index;

    public Modification(DSLBean bean, String fieldName, Object oldValue, Object newValue, int index) {
        this.bean = bean;
        this.fieldName = fieldName;
        this.newValue = newValue;
        this.oldValue = oldValue;
        this.index = index;
        if (vector == null) {
            vector = new Vector<Modification>();
            position = -1;
        }
        if (position < vector.size() - 1) {
            vector.setSize(position + 1);
        }
        position++;
        vector.add(this);
        checkMenus();
    }

    public static void setMenus(JButton redoButton, JButton undoButton, MenuItem redoItem, MenuItem undoItem){
       Modification.redoButton=redoButton;
       Modification.undoButton=undoButton;
       Modification.redoItem=redoItem;
       Modification.undoItem=undoItem;
    }
    private static void checkMenus(){
        if (canRedo()){
           redoButton.setEnabled(true);
           redoItem.setEnabled(true);
        }
        else {
           redoButton.setEnabled(false);
           redoItem.setEnabled(false);
        }
        if (canUndo()){
           undoButton.setEnabled(true);
           undoItem.setEnabled(true);
        }
        else {
           undoButton.setEnabled(false);
           undoItem.setEnabled(false);
        }
    }
    private static void setValue(DSLBean bean, String fieldName, Object value, int index) {
        try {
            if (value == null && index == -1) {
                EmptyFieldsRegistry.getInstance().setEmpty(bean, fieldName, true);
                return;
            }
            PropertyDescriptor pd = new PropertyDescriptor(fieldName, bean.getClass());
            Method rm = pd.getReadMethod();
            Method wm = pd.getWriteMethod();

            if (index == -1) {
                EmptyFieldsRegistry.getInstance().setEmpty(bean, fieldName, false);
                wm.invoke(bean, value);
            } else {
                Object val = rm.invoke(bean);
                if (val == null) {
                    val = Array.newInstance(pd.getPropertyType().getComponentType(), 1);

                }
                if (Array.getLength(val) <= index)
                    val = resizeArray(val, index + 1);

                Array.set(val, index, value);
                wm.invoke(bean, val);
            }
            //empty = false;


        } catch (Exception e1) {
            throw new Error("Fail in Modification.setValue()");
        }
    }

    public static boolean canUndo(){
        return position > -1;
    }
    public static boolean canRedo(){
        return vector!=null && position < vector.size() - 1;
    }
    public static void undo() {
        if (canUndo()) {
            Modification mod = vector.get(position);
            setValue(mod.getBean(), mod.getFieldName(), mod.getOldValue(), mod.getIndex());
            position--;
            checkMenus();
        }
    }

    public static void redo() {
        if (canRedo()) {
            position++;
            Modification mod = vector.get(position);
            setValue(mod.getBean(), mod.getFieldName(), mod.getNewValue(), mod.getIndex());
            checkMenus();
        }
    }

    public static void clearVector() {
        position = -1;
        if (vector != null) {
            vector.clear();
        }
        checkMenus();
    }

    public static Vector getVector() {
        return vector;
    }

    public static int getPosition() {
        return position;
    }

    public DSLBean getBean() {
        return bean;
    }


    public String getFieldName() {
        return fieldName;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }

    public int getIndex() {
        return index;
    }
}
