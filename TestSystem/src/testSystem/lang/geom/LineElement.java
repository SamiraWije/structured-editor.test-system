package testSystem.lang.geom;

import ru.ipo.structurededitor.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 22.03.11
 * Time: 14:35
 */
@DSLBeanParams(shortcut = "Новая_пр", description = "Прямая для построения")
public class LineElement extends AbstractGeoLine {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public Cell getLayout() {
        return new Horiz(/*new ConstantCell("Прямая"),*/ new FieldCell("name"));
    }
}