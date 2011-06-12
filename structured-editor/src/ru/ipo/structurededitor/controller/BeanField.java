package ru.ipo.structurededitor.controller;

import ru.ipo.structurededitor.model.DSLBean;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 09.11.2010
 * Time: 10:31:56
 * To change this template use File | Settings | File Templates.
 */
public class BeanField {
    private DSLBean bean;
    private String fieldName;

    public BeanField(DSLBean bean, String fieldName) {
        this.bean = bean;
        this.fieldName = fieldName;
    }

    public DSLBean getBean() {
        return bean;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BeanField &&
                ((BeanField) obj).getBean() == bean &&
                ((BeanField) obj).getFieldName().equals(fieldName);

    }
}
