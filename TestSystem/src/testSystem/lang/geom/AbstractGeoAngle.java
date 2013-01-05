package testSystem.lang.geom;

import geogebra.kernel.GeoAngle;
import geogebra.main.Application;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.model.EditorSettings;
import ru.ipo.structurededitor.view.editors.settings.AbstractDSLBeanSettings;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 31.03.11
 * Time: 17:53
 */
public abstract class AbstractGeoAngle implements DSLBean {

    public abstract GeoAngle resolve(Application app);

    public static EditorSettings getDefaultEditorSettings() {
        return new AbstractDSLBeanSettings()
                .withNullValueText("[Выберите тип угла]")
                .withSelectVariantActionText("Выбрать тип угла")
                .withSetNullActionText("Выбрать другой тип угла");
    }
}
