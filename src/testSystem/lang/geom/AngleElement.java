package testSystem.lang.geom;

import ru.ipo.structurededitor.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 22.03.11
 * Time: 14:35
 * To change this template use File | Settings | File Templates.
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
        return new Horiz(new ConstantCell("/_"), new FieldCell("name", true));
    }
}
