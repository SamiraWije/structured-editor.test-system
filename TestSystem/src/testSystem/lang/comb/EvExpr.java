package testSystem.lang.comb;

import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:29:20
 */
@DSLBeanParams(shortcut = "Чет", description = "Четное")
public class EvExpr extends UnExpr {
    public EvExpr() {
        fn = "Четное";
    }
}
