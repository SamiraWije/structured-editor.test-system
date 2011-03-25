package ru.ipo.structurededitor.testLang.geom;

import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 22.03.11
 * Time: 14:35
 * To change this template use File | Settings | File Templates.
 */
@DSLBeanParams(shortcut = "Точка", description = "Точка")
public class PointElement extends Element {
    public PointElement() {
        elType="Точка";
    }
}
