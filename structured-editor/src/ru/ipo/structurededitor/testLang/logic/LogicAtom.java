package ru.ipo.structurededitor.testLang.logic;

import ru.ipo.structurededitor.model.Cell;
import ru.ipo.structurededitor.model.DSLBeanParams;
import ru.ipo.structurededitor.model.FieldCell;
import ru.ipo.structurededitor.model.Horiz;
import ru.ipo.structurededitor.testLang.comb.Expr;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:07:49
 */
@DSLBeanParams(shortcut = "Cуждение", description = "Простое суждение")
public class LogicAtom extends Expr {

    private String val;

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public Cell getLayout() {
        return new Horiz(new FieldCell("val", true));
    }
}
