package testSystem.lang.geom;

import geogebra.kernel.GeoSegment;
import ru.ipo.structurededitor.model.Cell;
import ru.ipo.structurededitor.model.DSLBeanParams;
import ru.ipo.structurededitor.model.FieldCell;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 18.03.11
 * Time: 14:18
 */
@DSLBeanParams(shortcut = "| чертеж", description = "Отрезок с чертежа")
public class GeoSegmentLink extends AbstractGeoSegment {

    public GeoSegment getGeo() {
        return geo;
    }

    public void setGeo(GeoSegment geo) {
        this.geo = geo;
    }

    private GeoSegment geo;

    public Cell getLayout() {
        return new FieldCell("geo");
    }
}
