package testSystem.lang.geom;

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
}
