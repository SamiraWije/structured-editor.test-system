package testSystem.lang.geom;

import geogebra.kernel.GeoPoint;
import ru.ipo.structurededitor.model.Cell;
import ru.ipo.structurededitor.model.DSLBeanParams;
import ru.ipo.structurededitor.model.FieldCell;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 18.03.11
 * Time: 14:18
 */
@DSLBeanParams(shortcut = ". чертеж", description = "Точка с чертежа")
public class GeoPointLink extends AbstractGeoPoint {
    public GeoPoint getGeo() {
        return geo;
    }

    public void setGeo(GeoPoint geo) {
        this.geo = geo;
    }

    GeoPoint geo;

    public Cell getLayout() {
        return new FieldCell("geo");
    }
}

