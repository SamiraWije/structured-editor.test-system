package testSystem.lang.comb;

import ru.ipo.structurededitor.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:07:49
 */
@DSLBeanParams(shortcut = "x", description = "Декартово произведение")
public class DescartesKit extends Kit {

    private Kit[] kit;

    public Kit[] getKit() {
        return kit;
    }

    public void setKit(Kit[] kit) {
        this.kit = kit;
    }

    public Cell getLayout() {
        return new ArrayFieldCell("kit",ArrayFieldCell.Orientation.Vertical).withSpaceChar('x');
    }
}