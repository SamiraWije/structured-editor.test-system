package testSystem.lang.geom;

import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:25:07
 */
@DSLBeanParams(shortcut = "∠ = ∠", description = "Углы равны")
public class AngleEqualPred extends GeoAngleBinPred {
    @Override
    public void init() {
        op = "=";
        vert = false;
    }
}
