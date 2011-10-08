package testSystem.lang.geom;

import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:25:07
 */
@DSLBeanParams(shortcut = "| = |", description = "Отрезки равны")
public class SegEqualPred extends GeoSegmentBinPred {
    public SegEqualPred() {
        op = "=";
        vert = false;
    }
}
