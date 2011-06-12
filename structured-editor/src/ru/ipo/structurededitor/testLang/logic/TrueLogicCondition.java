package ru.ipo.structurededitor.testLang.logic;

import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 12.02.11
 * Time: 12:59
 * To change this template use File | Settings | File Templates.
 */
@DSLBeanParams(shortcut = "Истина", description = "Одновременно истинны")

public class TrueLogicCondition extends LogicCondition {
    public TrueLogicCondition() {
        word = "истинны";
    }
}
