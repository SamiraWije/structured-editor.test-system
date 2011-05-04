package ru.ipo.structurededitor.testLang.comb;

import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:29:20
 */
@DSLBeanParams(shortcut = "mod", description = "Остаток от деления")
public class RemExpr extends BinExpr {
    public RemExpr() {
        op = "mod";
    }
}
