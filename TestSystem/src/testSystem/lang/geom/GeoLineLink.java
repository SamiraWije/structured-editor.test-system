package testSystem.lang.geom;

import geogebra.kernel.GeoLine;
import geogebra.main.Application;
import ru.ipo.structurededitor.model.Cell;
import ru.ipo.structurededitor.model.DSLBeanParams;
import ru.ipo.structurededitor.model.FieldCell;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 18.03.11
 * Time: 14:18
 */
@DSLBeanParams(shortcut = "/ чертеж", description = "Прямая с чертежа")
public class GeoLineLink extends AbstractGeoLine {
    public GeoLine getGeo() {
        return geo;
    }

    public void setGeo(GeoLine geo) {
        this.geo = geo;
    }

    private GeoLine geo;

    @Override
    public GeoLine resolveLine(Application app) {
        return geo;
    }

    public Cell getLayout() {
        return new FieldCell("geo");
    }
}
