package ru.ipo.structurededitor.controller;


import ru.ipo.structurededitor.model.DSLBean;

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
