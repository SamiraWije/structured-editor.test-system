package testSystem.lang.geom;

import geogebra.kernel.GeoLine;
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
@DSLBeanParams(shortcut = "||",     description = "Прямые или отрезки параллельны")
public class ParallPred extends GeoSegLineBinPred {
    private static final Logger log = Logger.getLogger(ParallPred.class.getName());

    @Override
    public void init() {
        op = "||";
        vert = false;
    }

    @Override
    public boolean verify(Application app) {
        final GeoLine line1 = getE1().resolveLine(app);
        final GeoLine line2 = getE2().resolveLine(app);

        if (line1 == null || line2 == null) {
            log.severe("Some line not defined: first = " + line1 + ", second = " + line2);
            return false;
        }

        /*final Relation rel = new Relation(app.getKernel());
        final String relStr = rel.relation(line1, line2);

        log.info(relStr);
        return relStr.contains("параллельны");           */
        boolean res = line1.isParallel(line2);
        return res;
    }
}
