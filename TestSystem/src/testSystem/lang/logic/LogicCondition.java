package testSystem.lang.logic;

import testSystem.lang.comb.Expr;

import ru.ipo.structurededitor.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 12.02.11
 * Time: 12:37
 */
abstract public class LogicCondition implements DSLBean {
    protected String word;
    int num;
    Expr[] items;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public Expr[] getItems() {
        return items;
    }

    public void setItems(Expr[] items) {
        this.items = items;
    }

    public Cell getLayout() {
        return new Vert(new Horiz(new ConstantCell("Одновременно " + word), new FieldCell("num"),
                new ConstantCell("утверждений из: ")),
                new ArrayFieldCell("items", ArrayFieldCell.Orientation.Vertical).withSpaceChar(' '));
    }
}
