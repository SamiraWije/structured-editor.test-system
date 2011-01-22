package ru.ipo.structurededitor.testLang.comb;

import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:29:20
 */
@DSLBeanParams(shortcut = "|", description = "Логическое ИЛИ")
public class  LogOrExpr extends ArrayExpr {
    public LogOrExpr() {
        op = '|';
    }
}
