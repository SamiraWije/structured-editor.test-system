package ru.ipo.structurededitor.testLang.comb;

import ru.ipo.structurededitor.model.Cell;
import ru.ipo.structurededitor.model.ConstantCell;
import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:46:50
 */
@DSLBeanParams(shortcut = "Текущий", description = "элемент")
public class CurElementExpr extends Expr {
    public Cell getLayout() {
        return new ConstantCell("Текущий элемент");
    }
}
