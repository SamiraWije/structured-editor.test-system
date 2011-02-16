package ru.ipo.structurededitor.testLang.logic;

import ru.ipo.structurededitor.model.*;
import ru.ipo.structurededitor.testLang.comb.Expr;


/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 12.02.11
 * Time: 12:37
 * To change this template use File | Settings | File Templates.
 */
abstract public class LogicCondition implements DSLBean{
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
      return new Vert(new Horiz(new ConstantCell("Одновременно "+word), new FieldCell("num"),
                                new ConstantCell("утверждений из: ")),
                      new VertArray("items",' '));
    }
}
