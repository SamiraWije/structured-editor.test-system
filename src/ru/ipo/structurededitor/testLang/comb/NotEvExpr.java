package ru.ipo.structurededitor.testLang.comb;

import ru.ipo.structurededitor.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:29:20
 */
@DSLBeanParams(shortcut = "Неч", description = "Нечетное")
public class  NotEvExpr extends UnExpr {
    public NotEvExpr() {
        fn = "Нечетное";
    }
}
