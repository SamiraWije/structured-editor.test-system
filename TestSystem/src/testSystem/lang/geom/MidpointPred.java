package testSystem.lang.geom;

import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoSegment;
import geogebra.kernel.Relation;
import geogebra.main.Application;
import ru.ipo.structurededitor.model.DSLBeanParams;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:25:07
 */
@DSLBeanParams(shortcut = "cер |", description = "Середина отрезка")
public class MidpointPred extends GeoPointGeoSegmentBinPred {
    private static final Logger log = Logger.getLogger(MidpointPred.class.getName());

    @Override
    public void init() {
        op = "середина";
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

        final Relation rel = new Relation(app.getKernel());
        final String relStr = rel.relation(point, seg);

        final GeoPoint lineStart = seg.getStartPoint();
        final GeoPoint lineEnd = seg.getEndPoint();

        final GeoSegment s1 = new GeoSegment(app.getKernel().getConstruction(), lineStart, point);
        final GeoSegment s2 = new GeoSegment(app.getKernel().getConstruction(), lineEnd, point);
        s1.calcLength();
        s2.calcLength();

        log.info(relStr);

        return !(relStr.contains("не лежит на") || (Math.round(s1.getLength() * 100) != Math.round(s2.getLength() * 100)));
    }
}
