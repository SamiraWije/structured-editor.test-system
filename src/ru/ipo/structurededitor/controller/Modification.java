package ru.ipo.structurededitor.controller;


import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.view.events.PopupListener;

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
    private DSLBean bean;
    private String fieldName;
    private Object oldValue;
    private Object newValue;
    private FieldMask mask;



    public Modification(DSLBean bean, String fieldName, Object oldValue, Object newValue, FieldMask mask) {
        this.bean = bean;
        this.fieldName = fieldName;
        this.newValue = newValue;
        this.oldValue = oldValue;
        this.mask = mask;

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


    public FieldMask getMask() {
        return mask;
    }
}
