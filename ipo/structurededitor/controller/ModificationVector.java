package ru.ipo.structurededitor.controller;


import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.view.events.PopupListener;
import ru.ipo.structurededitor.view.events.RepaintListener;

import javax.swing.*;
import javax.swing.event.EventListenerList;
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
public class ModificationVector {
    private Vector<Modification> vector;
    private int position = -1;
    ModificationEventSupport mes=new ModificationEventSupport();
    public void addModificationListener(ModificationListener l) {
        mes.addModificationListener(l);
    }

    public void removeModificationListener(ModificationListener l) {
        mes.removeModificationListener(l);
    }

    public void add(Modification mod){
        if (vector == null) {
            vector = new Vector<Modification>();
            position = -1;
        }
        if (position < vector.size() - 1) {
            vector.setSize(position + 1);
        }
        position++;
        vector.add(mod);
        mes.fireModification();
    }

    /*public static void setMenus(JButton redoButton, JButton undoButton, MenuItem redoItem, MenuItem undoItem){
       ModificationVector.redoButton=redoButton;
       ModificationVector.undoButton=undoButton;
       ModificationVector.redoItem=redoItem;
       ModificationVector.undoItem=undoItem;
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
    } */
    private void setValue(DSLBean bean, String fieldName, Object value, FieldMask mask) {
        try {

            PropertyDescriptor pd = new PropertyDescriptor(fieldName, bean.getClass());
            Method rm = pd.getReadMethod();
            Method wm = pd.getWriteMethod();

            if (mask==null) {

                wm.invoke(bean, value);
            } else {
                Object val = rm.invoke(bean);
                mask.set(val, value);
                wm.invoke(bean, val);
            }
            //empty = false;


        } catch (Exception e1) {
            throw new Error("Fail in Modification.setValue()");
        }
    }

    public boolean canUndo(){
        return position > -1;
    }
    public  boolean canRedo(){
        return vector!=null && position < vector.size() - 1;
    }
    public void undo() {
        if (canUndo()) {
            Modification mod = vector.get(position);
            setValue(mod.getBean(), mod.getFieldName(), mod.getOldValue(), mod.getMask());
            position--;
            mes.fireModification();
        }
    }

    public void redo() {
        if (canRedo()) {
            position++;
            Modification mod = vector.get(position);
            setValue(mod.getBean(), mod.getFieldName(), mod.getNewValue(), mod.getMask());
            mes.fireModification();
        }
    }

    public void clearVector() {
        position = -1;
        if (vector != null) {
            vector.clear();
        }
        mes.fireModification();
    }

    public Vector getVector() {
        return vector;
    }

    public int getPosition() {
        return position;
    }
}
