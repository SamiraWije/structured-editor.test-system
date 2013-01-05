package testSystem.lang.geom;

import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 17.03.11
 * Time: 15:12
 */
@DSLBeanParams(shortcut = ". лежит на /", description = "Точка лежит на прямой")
public class LaysOnPred extends GeoPointGeoLineBinPred {
    @Override
    public void init() {
        op = "лежит на";
        vert = false;
    }
}
