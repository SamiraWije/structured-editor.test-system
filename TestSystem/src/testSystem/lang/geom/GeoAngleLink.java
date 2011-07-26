package testSystem.lang.geom;

import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoLine;
import ru.ipo.structurededitor.model.Cell;
import ru.ipo.structurededitor.model.DSLBeanParams;
import ru.ipo.structurededitor.model.FieldCell;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 18.03.11
 * Time: 14:18
 * To change this template use File | Settings | File Templates.
 */
@DSLBeanParams(shortcut = "Ссылка", description = "Угол с чертежа")
public class GeoAngleLink extends AbstractGeoAngle {


    public GeoAngle getGeo() {
        return geo;
    }

    public void setGeo(GeoAngle geo) {
        this.geo = geo;
    }

    GeoAngle geo;

    public Cell getLayout() {
        return new FieldCell("geo");
    }
}
