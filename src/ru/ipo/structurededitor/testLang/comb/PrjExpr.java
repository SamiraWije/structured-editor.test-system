package ru.ipo.structurededitor.testLang.comb;

import ru.ipo.structurededitor.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:42:04
 */
@DSLBeanParams(shortcut = "Проекция", description = "на ось")
public class  PrjExpr extends Expr {

    private Expr expr;
    private int ind;

    public int getInd() {
        return ind;
    }

    public void setInd(int ind) {
        this.ind = ind;
    }

    public Expr getExpr() {
        return expr;
    }

    public void setExpr(Expr expr) {
        this.expr = expr;
    }

    public Cell getLayout() {
        return new Vert(
                new ConstantCell("Проекция"),
                new Horiz(new FieldCell("expr"), new ConstantCell("на"), new FieldCell("ind"))
        );
    }
}
