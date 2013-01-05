package testSystem.lang.geom;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoSegment;
import geogebra.main.Application;
import ru.ipo.structurededitor.model.*;
import ru.ipo.structurededitor.view.editors.settings.StringSettings;
import testSystem.util.GeogebraUtils;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 22.03.11
 * Time: 14:35
 */
@DSLBeanParams(shortcut = "| постр", description = "Отрезок для построения")
public class SegmentElement extends AbstractGeoSegment {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    @Override
    public GeoSegment resolveSegment(Application app) {
        final GeoElement res = GeogebraUtils.getGeoByCaption(name, app);
        return res instanceof GeoSegment ? (GeoSegment) res : null;
    }

    public Cell getLayout() {
        return new Horiz(
                new ConstantCell("|"),
                new FieldCell("name", new StringSettings()
                        .withNullAllowed(false)
                        .withEmptyText("[имя отрезка]")
                        .withToolTipText("<html>Введите имя отрезка, которую участник<br>должен будет построить на чертеже</html>"))
        );
    }
}
