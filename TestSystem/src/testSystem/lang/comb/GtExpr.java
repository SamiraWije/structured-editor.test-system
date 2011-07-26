package testSystem.lang.comb;

import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:28:44
 */
@DSLBeanParams(shortcut = ">", description = "Больше")
public class GtExpr extends BinExpr {

    public GtExpr() {
        op = ">";
        vert = true;
    }

}
