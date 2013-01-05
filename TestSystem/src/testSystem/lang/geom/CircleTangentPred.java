package testSystem.lang.geom;

import geogebra.kernel.GeoConic;
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
@DSLBeanParams(shortcut = "∘ кас ∘", description = "Окружности касаются")
public class CircleTangentPred extends GeoCircleBinPred {
    private static final Logger log = Logger.getLogger("CircleTangentPred");

    @Override
    public void init() {
        op = "касается";
        vert = false;
    }

    @Override
    public boolean verify(Application app) {
        final GeoConic geo1 = getE1().resolve(app);
        final GeoConic geo2 = getE2().resolve(app);

        if (geo1 == null || geo2 == null) {
            log.severe("Some circle not defined: first = " + geo1 + ", second = " + geo2);
            return false;
        }

        final Relation rel = new Relation(app.getKernel());
        final String relStr = rel.relation(geo1, geo2);

        log.info(relStr);

        return !relStr.contains("пересекается");    //не работает
    }
}
