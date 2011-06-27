package testSystem.lang.geom;

import ru.ipo.structurededitor.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:25:07
 */
@DSLBeanParams(shortcut = "Середина", description = "отрезка")
public class MidpointPred extends GeoPointGeoSegmentBinPred {
    public MidpointPred() {
        op = "середина";
        vert = false;
    }

    public AbstractGeoPoint getE1() {
        return e1;
    }

    public void setE1(AbstractGeoPoint e1) {
        this.e1 = e1;
    }



    private AbstractGeoPoint e1;

    public AbstractGeoSegment getE2() {
        return e2;
    }

    public void setE2(AbstractGeoSegment e2) {
        this.e2 = e2;
    }

    private AbstractGeoSegment e2;



}
