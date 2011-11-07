package testSystem.lang.comb;

import ru.ipo.structurededitor.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:29:20
 */
public abstract class ArrayExpr extends Expr {
    private Expr items[];
    protected char op;
    protected boolean vert = false;

    public Expr[] getItems() {
        return items;
    }

    public void setItems(Expr items[]) {
        this.items = items;
    }

    public Cell getLayout() {
        if (vert)
            return new ArrayFieldCell("items", ArrayFieldCell.Orientation.Vertical).withSpaceChar(op);
        else
            return new Horiz(new ConstantCell("("), new ArrayFieldCell("items", ArrayFieldCell.Orientation.Horizontal)
                    .withSpaceChar(op), new ConstantCell(")"));
    }
}
