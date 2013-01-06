package testSystem.lang.geom;

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
@DSLBeanParams(shortcut = "| = |", description = "Отрезки равны")
public class SegEqualPred extends GeoSegmentBinPred {
    private static final Logger log = Logger.getLogger(SegEqualPred.class.getName());

    @Override
    public void init() {
        op = "=";
        vert = false;
    }

    @Override
    public boolean verify(Application app) {
        final GeoSegment seg1 = getE1().resolveSegment(app);
        final GeoSegment seg2 = getE2().resolveSegment(app);

        if (seg1 == null || seg2 == null) {
            log.severe("Some segment not defined: first = " + seg1 + ", second = " + seg2);
            return false;
        }

        final Relation rel = new Relation(app.getKernel());
        final String relStr = rel.relation(seg1, seg2);

        log.info(relStr);
        return !relStr.contains("не равны");
    }
}
