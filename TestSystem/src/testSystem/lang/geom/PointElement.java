package testSystem.lang.geom;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
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
@DSLBeanParams(shortcut = ". постр", description = "Точка для построения")
public class PointElement extends AbstractGeoPoint {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    @Override
    public GeoPoint resolve(Application app) {
        final GeoElement res = GeogebraUtils.getGeoByCaption(name, app);
        return res instanceof GeoPoint ? (GeoPoint) res : null;
    }

    public Cell getLayout() {
        return new Horiz(
                new ConstantCell("."),
                new FieldCell(
                        "name",
                        new StringSettings()
                                .withNullAllowed(false)
                                .withEmptyText("[имя точки]")
                                .withToolTipText("<html>Введите имя точки, которую участник<br>должен будет построить на чертеже</html>")
                ));
    }
}
