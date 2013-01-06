package testSystem.lang.geom;

import geogebra.kernel.GeoConic;
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
@DSLBeanParams(shortcut = "/ кас ∘", description = "Линия касается окружности")
public class LineCircleTangentPred extends GeoSegLineGeoCircleBinPred {
    private static final Logger log = Logger.getLogger(LineCircleTangentPred.class.getName());

    @Override
    public void init() {
        op = "касается";
        vert = false;
    }

    @Override
    public boolean verify(Application app) {
        final GeoLine line = getE1().resolveLine(app);
        final GeoConic circle = getE2().resolve(app);

        if (line == null || circle == null) {
            return false;
        }

        final Relation rel = new Relation(app.getKernel());
        final String relStr = rel.relation(line, circle);

        log.info(relStr);

        return !(relStr.contains("пересекается"));
    }
}
