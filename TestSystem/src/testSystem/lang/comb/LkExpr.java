package testSystem.lang.comb;

import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:28:44
 */
@DSLBeanParams(shortcut = "~", description = "Равны без учета порядка")
public class LkExpr extends BinExpr {

    public LkExpr() {
        op = "~";
        vert = true;
    }

}
