package ru.ipo.structurededitor.testLang.comb;

import ru.ipo.structurededitor.model.Cell;
import ru.ipo.structurededitor.model.ConstantCell;
import ru.ipo.structurededitor.model.FieldCell;
import ru.ipo.structurededitor.model.Horiz;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:29:20
 */
public abstract class UnExpr extends Expr {
    private Expr expr;
    protected String fn;

    public Expr getExpr() {
        return expr;
    }

    public void setExpr(Expr expr) {
        this.expr = expr;
    }

    public Cell getLayout() {
        return new Horiz(new ConstantCell(fn + " ("), new FieldCell("expr"), new ConstantCell(")"));
    }
}
