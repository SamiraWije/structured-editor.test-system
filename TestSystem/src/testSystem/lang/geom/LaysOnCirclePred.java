package testSystem.lang.geom;

import geogebra.kernel.GeoConic;
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
@DSLBeanParams(shortcut = ". лежит на ∘", description = "Точка лежит на окружности")
public class LaysOnCirclePred extends GeoPointGeoCircleBinPred {
    private static final Logger log = Logger.getLogger(LaysOnCirclePred.class.getName());

    @Override
    public void init() {
        op = "лежит на";
        vert = false;
    }

    @Override
    public boolean verify(Application app) {
       // log.severe("LaysOnCirclePred is not implemented");
        final GeoPoint point = getE1().resolve(app);
        final GeoConic circle = getE2().resolve(app);

        if (point == null || circle == null) {
            log.severe("Point or line not defined: point = " + point + " line = " + circle);
            return false;
        }

        final Relation rel = new Relation(app.getKernel());
        final String relStr = rel.relation(point, circle);

        log.info(relStr);
        return !(relStr.contains("не лежит на"));
        //return false;
    }
}
