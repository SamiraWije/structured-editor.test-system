package testSystem.lang.geom;

import geogebra.kernel.GeoAngle;
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
@DSLBeanParams(shortcut = "∠ = ∠", description = "Углы равны")
public class AngleEqualPred extends GeoAngleBinPred {
    private static final Logger log = Logger.getLogger(AngleEqualPred.class.getName());

    @Override
    public void init() {
        op = "=";
        vert = false;
    }

    @Override
    public boolean verify(Application app) {
        final GeoAngle geo1 = getE1().resolve(app);
        final GeoAngle geo2 = getE2().resolve(app);

        if (geo1 == null || geo2 == null) {
            log.severe("Some angle not defined: first = " + geo1 + ", second = " + geo2);
            return false;
        }

        final GeoAngle inverted = new GeoAngle(app.getKernel().getConstruction(), 2 * Math.PI - geo2.getValue());

        final Relation rel = new Relation(app.getKernel());
        final String relStr = rel.relation(geo1, geo2);
        final String relStrInverted = rel.relation(geo1, inverted);

        log.info(relStr);
        log.info(relStrInverted);

        return !(relStr.contains("не идентичны") && relStrInverted.contains("не идентичны"));
    }
}
