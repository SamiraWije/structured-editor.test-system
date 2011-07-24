package testSystem.lang.logic;

import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 12.02.11
 * Time: 12:59
 * To change this template use File | Settings | File Templates.
 */
@DSLBeanParams(shortcut = "Ложь", description = "Одновременно ложны")
public class FalseLogicCondition extends LogicCondition {
    public FalseLogicCondition() {
        word = "ложны";
    }
}
