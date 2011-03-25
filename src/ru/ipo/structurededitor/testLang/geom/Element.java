package ru.ipo.structurededitor.testLang.geom;

import ru.ipo.structurededitor.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 22.03.11
 * Time: 14:28
 * To change this template use File | Settings | File Templates.
 */

public abstract class Element implements DSLBean {
    protected String elType;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
     public Cell getLayout() {
        return new Horiz(new ConstantCell(elType), new FieldCell("name",true));
     }
}
