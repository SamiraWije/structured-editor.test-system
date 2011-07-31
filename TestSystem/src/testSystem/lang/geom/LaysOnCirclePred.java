package testSystem.lang.geom;

import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 17.03.11
 * Time: 15:12
 * To change this template use File | Settings | File Templates.
 */
@DSLBeanParams(shortcut = "лежит_на_окр", description = "Точка лежит на окружности")
public class LaysOnCirclePred extends GeoPointGeoCircleBinPred {
    public LaysOnCirclePred() {
        op = "лежит на";
        vert = false;
    }
}