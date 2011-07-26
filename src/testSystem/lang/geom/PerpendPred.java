package testSystem.lang.geom;

import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 17.03.11
 * Time: 15:12
 * To change this template use File | Settings | File Templates.
 */
@DSLBeanParams(shortcut = "_|_", description = "Прямые или отрезки перпендикулярны")
public class PerpendPred extends GeoSegLineBinPred {
    public PerpendPred() {
        op = "_|_";
        vert = false;
    }
}
