package testSystem.lang.geom;

import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 17.03.11
 * Time: 15:12
 */
@DSLBeanParams(shortcut = "||",     description = "Прямые или отрезки параллельны")
public class ParallPred extends GeoSegLineBinPred {
    public ParallPred() {
        op = "||";
        vert = false;
    }
}
