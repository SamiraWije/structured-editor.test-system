package testSystem.lang.logic;

import ru.ipo.structurededitor.model.DSLBeanParams;
import testSystem.lang.comb.BinExpr;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:29:20
 */
@DSLBeanParams(shortcut = "<=>", description = "Эквиваленция")
public class LogEquivExpr extends BinExpr {
    public LogEquivExpr() {
        op = "<=>";
    }
}
