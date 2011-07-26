package testSystem.lang.geom;

import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 17.03.11
 * Time: 15:12
 */
@DSLBeanParams(shortcut = "окр кас окр", description = "Окружности касаются")
public class CircleTangentPred extends GeoCircleBinPred {
    public CircleTangentPred() {
        op = "касается";
        vert = false;
    }
}
