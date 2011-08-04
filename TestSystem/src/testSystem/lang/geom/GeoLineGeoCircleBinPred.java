package testSystem.lang.geom;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 18.03.11
 * Time: 14:26
 */
public abstract class GeoLineGeoCircleBinPred extends BinPred {


    public AbstractGeoLine getE1() {
        return e1;
    }

    public void setE1(AbstractGeoLine e1) {
        this.e1 = e1;
    }

    private AbstractGeoLine e1;


    public AbstractGeoCircle getE2() {
        return e2;
    }

    public void setE2(AbstractGeoCircle e2) {
        this.e2 = e2;
    }

    private AbstractGeoCircle e2;
}
