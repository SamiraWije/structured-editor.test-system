package testSystem.lang.geom;

import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPoint;
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
@DSLBeanParams(shortcut = ". лежит на /", description = "Точка лежит на прямой")
public class LaysOnPred extends GeoPointGeoLineBinPred {
    private static final Logger log = Logger.getLogger(LaysOnPred.class.getName());

    @Override
    public void init() {
        op = "лежит на";
        vert = false;
    }

    @Override
    public boolean verify(Application app) {
        final GeoPoint point = getE1().resolve(app);
        final GeoLine line = getE2().resolveLine(app);
        if (point == null || line == null) {
            log.severe("Point or line not defined: point = " + point + " line = " + line);
            return false;
        }

        /*final Relation rel = new Relation(app.getKernel());
        final String relStr = rel.relation(point, line);
        log.info(relStr);
        return !(relStr.contains("не лежит на"));*/
        boolean res = line.isIntersectionPointIncident(point,0.0001);
        return res;
    }
}
