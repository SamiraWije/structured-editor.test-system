package ru.ipo.structurededitor.model;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 28.12.10
 * Time: 15:01
 * To change this template use File | Settings | File Templates.
 */
public class DefaultDSLBean implements DSLBean {
    public Cell getLayout() {
        return new ConstantCell("Пусто");
    }
}
