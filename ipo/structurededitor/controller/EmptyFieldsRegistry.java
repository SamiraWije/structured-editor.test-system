package ru.ipo.structurededitor.controller;

import ru.ipo.structurededitor.model.DSLBean;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Vector;

/**
 * Register of empty fields
 */
public class EmptyFieldsRegistry {

    static EmptyFieldsRegistry instance;

    static public EmptyFieldsRegistry getInstance() {
        if (instance == null) {
            instance = new EmptyFieldsRegistry();
        }

        return instance;
    }

    /**
     * Vector of empty fields of concrete objects
     */
    private Vector<BeanField> emptyFields = new Vector<BeanField>();

    public void clear(){
        emptyFields.clear();
    }
    public void setEmpty(DSLBean bean, String fieldName, boolean empty) {
        BeanField beanField = new BeanField(bean, fieldName);
        if (empty) {
            emptyFields.addElement(beanField);
        } else {
            int i=0;
            while (i<emptyFields.size()) {
                BeanField item = emptyFields.get(i);
                if (beanField.equals(item)) {
                    emptyFields.remove(item);
                    return;
                }
                i++;
            }
        }
    }

    public boolean isEmpty(DSLBean bean, String fieldName) {
        BeanField beanField = new BeanField(bean, fieldName);
        for (int i=0; i<emptyFields.size();i++){
            if (beanField.equals(emptyFields.get(i))) {
                return true;

            }
        }
        return false;
    }
}

