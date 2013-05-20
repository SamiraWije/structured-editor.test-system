package testSystem.lang.geom;

import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoSegment;
import geogebra.kernel.GeoVec3D;
import geogebra.main.Application;
import ru.ipo.structurededitor.model.EditorSettings;
import ru.ipo.structurededitor.view.editors.settings.AbstractDSLBeanSettings;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 31.03.11
 * Time: 17:53
 */
public abstract class AbstractGeoSegment extends AbstractGeoSegLine {

    public abstract GeoSegment resolveSegment(Application app);

    @Override
    public GeoLine resolveLine(Application app) {
        final GeoSegment seg = resolveSegment(app);
        if (seg == null)
            return null;
        final GeoLine line = new GeoLine(app.getKernel().getConstruction());
        GeoVec3D.lineThroughPoints(seg.getStartPoint(), seg.getEndPoint(), line);
        return line;
    }

    public static EditorSettings getDefaultEditorSettings() {
        return new AbstractDSLBeanSettings()
                .withNullValueText("[Выберите тип отрезка]")
                .withSelectVariantActionText("Выбрать тип отрезка")
                .withSetNullActionText("Выбрать другой тип отрезка");
    }

}
