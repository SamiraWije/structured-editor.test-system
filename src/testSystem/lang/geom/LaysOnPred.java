package testSystem.lang.geom;

import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 17.03.11
 * Time: 15:12
 * To change this template use File | Settings | File Templates.
 */
@DSLBeanParams(shortcut = "E", description = "Точка лежит на прямой")
public class LaysOnPred extends GeoPointGeoLineBinPred {
    public LaysOnPred() {
        op = "E";
        vert = false;
    }
}
