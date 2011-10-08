package testSystem.lang.geom;

import ru.ipo.structurededitor.model.*;
import ru.ipo.structurededitor.view.editors.settings.StringSettings;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 22.03.11
 * Time: 14:35
 */
@DSLBeanParams(shortcut = "∘ постр", description = "Окружность для построения")
public class CircleElement extends AbstractGeoCircle{
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public Cell getLayout() {
        return new Horiz(new ConstantCell("∘"), new FieldCell("name", new StringSettings()
                        .withNullAllowed(false)
                        .withEmptyText("[имя окружности]")
                        .withToolTipText("<html>Введите имя окружности, которую участник<br>должен будет построить на чертеже</html>")));
    }
}
