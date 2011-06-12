package ru.ipo.structurededitor.testLang;

import ru.ipo.structurededitor.model.DSLBean;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 04.01.2010
 * Time: 2:22:26
 */
public abstract class BeanA implements DSLBean {

    private String strFld;

    public String getStrFld() {
        return strFld;
    }

    public void setStrFld(String strFld) {
        this.strFld = strFld;
    }
}
