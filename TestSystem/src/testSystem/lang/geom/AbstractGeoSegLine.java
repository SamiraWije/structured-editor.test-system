package testSystem.lang.geom;

import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.model.EditorSettings;
import ru.ipo.structurededitor.view.editors.settings.AbstractDSLBeanSettings;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 31.03.11
 * Time: 17:53
 */
public abstract class AbstractGeoSegLine implements DSLBean {

    public static EditorSettings getDefaultEditorSettings() {
        return new AbstractDSLBeanSettings()
                .withNullValueText("[Выберите тип линии]")
                .withSelectVariantActionText("Выбрать тип линии")
                .withSetNullActionText("Выбрать другой тип линии");
    }

}
