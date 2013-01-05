package testSystem.lang.geom;

import geogebra.kernel.GeoAngle;
import geogebra.main.Application;
import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 09.07.11
 * Time: 11:44
 */
@DSLBeanParams(shortcut = "∠ = ?", description = "Величина угла")
public class AngleValuePred extends ValuePred {
    @Override
    public void init() {
        op = "=";
    }

    private AbstractGeoAngle e;

    public AbstractGeoAngle getE() {
        return e;
    }

    public void setE(AbstractGeoAngle e) {
        this.e = e;
    }

    @Override
    public boolean verify(Application app) {
        final double value = getValue();
        final GeoAngle angle = getE().resolve(app);

        return angle != null
                && Math.round(angle.getRawAngle() / Math.PI * 180 * 10) == Math.round(value * 10);
    }
}
