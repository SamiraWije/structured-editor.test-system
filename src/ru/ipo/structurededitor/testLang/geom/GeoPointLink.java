package ru.ipo.structurededitor.testLang.geom;

import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPoint;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 18.03.11
 * Time: 14:18
 * To change this template use File | Settings | File Templates.
 */
public class GeoPointLink extends GeoElementLink{
    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    GeoPoint geoPoint;
}
