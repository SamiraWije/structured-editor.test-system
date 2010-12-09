package ru.ipo.structurededitor.testLang.comb;

import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:28:44
 */
@DSLBeanParams(shortcut = "=", description = "Равно")
public class  EqExpr extends BinExpr {

    public EqExpr() {
        op = "=";
        vert = true;
    }

}
