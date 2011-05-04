package ru.ipo.structurededitor.testLang.geom;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 18.03.11
 * Time: 14:26
 * To change this template use File | Settings | File Templates.
 */
public abstract class GeoPointGeoLineBinPred extends BinPred {

    public AbstractGeoPoint getE1() {
        return e1;
    }

    public void setE1(AbstractGeoPoint e1) {
        this.e1 = e1;
    }

    public AbstractGeoLine getE2() {
        return e2;
    }

    public void setE2(AbstractGeoLine e2) {
        this.e2 = e2;
    }

    private AbstractGeoPoint e1;
    private AbstractGeoLine e2;
}
