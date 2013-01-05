package testSystem.lang.geom;

import geogebra.kernel.GeoConic;
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
public abstract class AbstractGeoCircle implements DSLBean {

    public abstract GeoConic resolve(Application app);

    public static EditorSettings getDefaultEditorSettings() {
        return new AbstractDSLBeanSettings()
                .withNullValueText("[Выберите тип окружности]")
                .withSelectVariantActionText("Выбрать тип окружности")
                .withSetNullActionText("Выбрать другой тип окружности");
    }

}
