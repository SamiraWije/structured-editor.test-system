package testSystem.lang.geom;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 18.03.11
 * Time: 14:26
 */
public abstract class GeoSegmentBinPred extends BinPred {


    public AbstractGeoSegment getE1() {
        return e1;
    }

    public void setE1(AbstractGeoSegment e1) {
        this.e1 = e1;
    }

    public AbstractGeoSegment getE2() {
        return e2;
    }

    public void setE2(AbstractGeoSegment e2) {
        this.e2 = e2;
    }

    private AbstractGeoSegment e1;
    private AbstractGeoSegment e2;
}
