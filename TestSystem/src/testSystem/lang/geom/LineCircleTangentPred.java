package testSystem.lang.geom;

import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 17.03.11
 * Time: 15:12
 */
@DSLBeanParams(shortcut = "/ кас ∘", description = "Прямая касается окружности")
public class LineCircleTangentPred extends GeoSegLineGeoCircleBinPred {
    public LineCircleTangentPred() {
        op = "касается";
        vert = false;
    }
}
