package testSystem.lang.geom;

import ru.ipo.structurededitor.model.Cell;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.model.FieldCell;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 18.03.11
 * Time: 14:06
 */
public abstract class Link implements DSLBean {
    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    String linkName;

    public Cell getLayout() {
        return new FieldCell("linkName");
    }
}
