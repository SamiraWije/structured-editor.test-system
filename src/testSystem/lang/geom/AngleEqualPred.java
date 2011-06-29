package testSystem.lang.geom;

import geogebra.kernel.GeoAngle;
import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:25:07
 */
@DSLBeanParams(shortcut = "у=у", description = "Углы равны")
public class AngleEqualPred extends GeoAngleBinPred {
    public AngleEqualPred() {
        op = "=";
        vert = false;
    }
}
