package testSystem.lang.geom;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoSegment;
import geogebra.kernel.GeoVec3D;
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
@DSLBeanParams(shortcut = "/ постр", description = "Прямая для построения")
public class LineElement extends AbstractGeoLine {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    @Override
    public GeoLine resolveLine(Application app) {
        final GeoElement res = GeogebraUtils.getGeoByCaption(name, app);
        if (res instanceof GeoSegment){
            GeoLine geoLine1=new GeoLine(app.getKernel().getConstruction());
            GeoVec3D.lineThroughPoints(((GeoSegment) res).getStartPoint(), ((GeoSegment) res).getEndPoint(),
                    geoLine1);
            return geoLine1;
        }
        return res instanceof GeoLine ? (GeoLine) res : null;
    }

    public Cell getLayout() {
        return new Horiz(
                new ConstantCell("/"),
                new FieldCell("name", new StringSettings()
                        .withNullAllowed(false)
                        .withEmptyText("[имя прямой]")
                        .withToolTipText("<html>Введите имя прямой, которую участник<br>должен будет построить на чертеже</html>")
                ));
    }
}
