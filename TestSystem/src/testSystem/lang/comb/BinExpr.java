package testSystem.lang.comb;

import ru.ipo.structurededitor.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:25:07
 */
public abstract class BinExpr extends Expr {

    private Expr e1;
    private Expr e2;
    protected String op;
    protected boolean vert = false;

    public Expr getE1() {
        return e1;
    }

    public void setE1(Expr e1) {
        this.e1 = e1;
    }

    public Expr getE2() {
        return e2;
    }

    public void setE2(Expr e2) {
        this.e2 = e2;
    }

    public Cell getLayout() {
        if (vert)
            return new Vert(new FieldCell("e1"), new ConstantCell(op), new FieldCell("e2"));
        else
            return new Horiz(new ConstantCell("("), new FieldCell("e1"), new ConstantCell(op),
                    new FieldCell("e2"), new ConstantCell(")"));
    }
}
