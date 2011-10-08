package testSystem.lang.geom;

import ru.ipo.structurededitor.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:25:07
 */
@DSLBeanParams(shortcut = "cер |", description = "Середина отрезка")
public class MidpointPred extends GeoPointGeoSegmentBinPred {
    public MidpointPred() {
        op = "середина";
        vert = false;
    }
}
