package testSystem.lang.geom;

import geogebra.main.Application;
import ru.ipo.structurededitor.model.DSLBeanParams;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 17.03.11
 * Time: 15:12
 */
@DSLBeanParams(shortcut = ". лежит на ∘", description = "Точка лежит на окружности")
public class LaysOnCirclePred extends GeoPointGeoCircleBinPred {
    private static final Logger log = Logger.getLogger("LaysOnCirclePred");

    @Override
    public void init() {
        op = "лежит на";
        vert = false;
    }

    @Override
    public boolean verify(Application app) {
        log.severe("LaysOnCirclePred is not implemented");
        return false;
    }
}
