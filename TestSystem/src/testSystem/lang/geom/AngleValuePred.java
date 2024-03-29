package testSystem.lang.geom;

import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 09.07.11
 * Time: 11:44
 */
@DSLBeanParams(shortcut = "∠ = ?", description = "Величина угла")
public class AngleValuePred extends ValuePred {
    public AngleValuePred() {
        op = "=";
    }
    AbstractGeoAngle e;

    public AbstractGeoAngle getE() {
        return e;
    }

    public void setE(AbstractGeoAngle e) {
        this.e = e;
    }
}
