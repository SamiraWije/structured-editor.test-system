package testSystem.lang.geom;

import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 17.03.11
 * Time: 15:12
 */
@DSLBeanParams(shortcut = "/ кас ∘", description = "Линия касается окружности")
public class LineCircleTangentPred extends GeoSegLineGeoCircleBinPred {
    @Override
    public void init() {
        op = "касается";
        vert = false;
    }
}
