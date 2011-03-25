package ru.ipo.structurededitor.testLang.geom;

import geogebra.kernel.GeoLine;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 18.03.11
 * Time: 14:18
 * To change this template use File | Settings | File Templates.
 */
public class GeoLineLink extends GeoElementLink{
     public GeoLine getGeoLine() {
        return geoLine;
    }

    public void setGeoLine(GeoLine geoLine) {
        this.geoLine = geoLine;
    }

    GeoLine geoLine;
}
