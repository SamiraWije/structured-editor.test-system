package testSystem.lang.geom;

import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoSegment;
import geogebra.kernel.Relation;
import geogebra.main.Application;
import ru.ipo.structurededitor.model.DSLBeanParams;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 17.03.11
 * Time: 15:12
 */
@DSLBeanParams(shortcut = ". лежит на |", description = "Точка лежит на отрезке")
public class LaysOnSegmentPred extends GeoPointGeoSegmentBinPred {
    private static final Logger log = Logger.getLogger(LaysOnSegmentPred.class.getName());

    @Override
    public void init() {
        op = "лежит на";
        vert = false;
    }

    @Override
    public boolean verify(Application app) {
        final GeoPoint point = getE1().resolve(app);
        final GeoSegment seg = getE2().resolveSegment(app);

        if (point == null || seg == null) {
            log.severe("Point or segment not defined: point = " + point + ", segment = " + seg);
            return false;
        }

        /*final Relation rel = new Relation(app.getKernel());
        final String relStr = rel.relation(point, seg);

        log.info(relStr);
        return !relStr.contains("не лежит на");*/
        boolean res = seg.isIntersectionPointIncident(point,0.0001);
        return res;
    }
}
