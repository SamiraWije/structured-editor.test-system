package testSystem.lang.geom;

import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:25:07
 */
@DSLBeanParams(shortcut = "[]=[]", description = "Отрезки равны")
public class SegEqualPred extends GeoSegmentBinPred {
    public SegEqualPred() {
        op = "=";
        vert = false;
    }


    public AbstractGeoSegment getE1() {
        return e1;
    }

    public void setE1(AbstractGeoSegment e1) {
        this.e1 = e1;
    }

    private AbstractGeoSegment e1;

    public AbstractGeoSegment getE2() {
        return e2;
    }

    public void setE2(AbstractGeoSegment e2) {
        this.e2 = e2;
    }

    private AbstractGeoSegment e2;



}
