package testSystem.lang.geom;

import geogebra.kernel.GeoConic;
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
@DSLBeanParams(shortcut = "∘ чертеж", description = "Окружность с чертежа")
public class GeoCircleLink extends AbstractGeoCircle {
    public GeoConic getGeo() {
        return geo;
    }

    public void setGeo(GeoConic geo) {
        this.geo = geo;
    }

    GeoConic geo;

    public Cell getLayout() {
        return new FieldCell("geo");
    }
}

