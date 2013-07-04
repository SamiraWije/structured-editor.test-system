package testSystem.lang.geom;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 18.03.11
 * Time: 14:26
 */
public abstract class GeoSegLineBinPred extends BinPred {
    public AbstractGeoSegLine getE1() {
        return e1;
    }

    public void setE1(AbstractGeoSegLine e1) {
        this.e1 = e1;
    }

    public AbstractGeoSegLine getE2() {
        return e2;
    }

    public void setE2(AbstractGeoSegLine e2) {
        this.e2 = e2;
    }

    private AbstractGeoSegLine e1;
    private AbstractGeoSegLine e2;
    public String toString() {
          return e1.toString()+" "+op+" "+e2.toString();
    }
}
