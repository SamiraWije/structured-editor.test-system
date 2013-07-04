package testSystem.lang.geom;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 18.03.11
 * Time: 14:26
 */
public abstract class GeoAngleBinPred extends BinPred {

    public AbstractGeoAngle getE1() {
        return e1;
    }

    public void setE1(AbstractGeoAngle e1) {
        this.e1 = e1;
    }

    public AbstractGeoAngle getE2() {
        return e2;
    }

    public void setE2(AbstractGeoAngle e2) {
        this.e2 = e2;
    }

    private AbstractGeoAngle e1;
    private AbstractGeoAngle e2;
    public String toString() {
      return e1.toString()+" "+op+" "+e2.toString();
    }
}
