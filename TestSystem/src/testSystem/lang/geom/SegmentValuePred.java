package testSystem.lang.geom;

import geogebra.kernel.GeoSegment;
import geogebra.main.Application;
import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 09.07.11
 * Time: 11:44
 */
@DSLBeanParams(shortcut = "| = ?", description = "Длина отрезка")
public class SegmentValuePred extends ValuePred {
    @Override
    public void init() {
        op = "=";
    }

    private AbstractGeoSegment e;

    public AbstractGeoSegment getE() {
        return e;
    }

    public void setE(AbstractGeoSegment e) {
        this.e = e;
    }

    @Override
    public boolean verify(Application app) {
        final double value = getValue();
        final GeoSegment seg = getE().resolveSegment(app);

        return seg != null
                && Math.round(seg.getLength() * 100) == Math.round(value * 100);
    }

    public String toString() {
                return e.toString()+" = "+Double.toString(value);
    }
}
