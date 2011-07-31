package testSystem.lang.geom;

import ru.ipo.structurededitor.model.*;
import ru.ipo.structurededitor.view.editors.settings.AbstractDSLBeanSettings;
import ru.ipo.structurededitor.view.editors.settings.StringSettings;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 22.03.11
 * Time: 14:35
 */
@DSLBeanParams(shortcut = "Новый", description = "Угол для построения")
public class AngleElement extends AbstractGeoAngle{
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public Cell getLayout() {
        return new Horiz(
                new ConstantCell("∠"),
                new FieldCell("name", new StringSettings().withEmptyText("[Угол]"))
        );
    }
}
