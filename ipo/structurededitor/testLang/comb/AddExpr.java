package ru.ipo.structurededitor.testLang.comb;

import ru.ipo.structurededitor.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:29:20
 */
@DSLBeanParams(shortcut = "+", description = "Сложение")
public class  AddExpr extends ArrayExpr {
    public AddExpr() {
        op = '+';
    }
}
