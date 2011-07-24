package testSystem.lang.comb;

import ru.ipo.structurededitor.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:07:49
 */
@DSLBeanParams(shortcut = "{}", description = "Перечисление")
public class EnumKit extends Kit {

    private ConstantElement items[];

    public ConstantElement[] getItems() {
        return items;
    }

    public void setItems(ConstantElement items[]) {
        this.items = items;
    }

    public Cell getLayout() {
        return new Horiz(
                new ConstantCell("{"),
                new ArrayFieldCell("items", ArrayFieldCell.Orientation.Vertical).withSpaceChar(','),
                new ConstantCell("}")
        );
    }
}
