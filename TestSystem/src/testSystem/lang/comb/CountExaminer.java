package testSystem.lang.comb;

import ru.ipo.structurededitor.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:15:32
 */
@DSLBeanParams(shortcut = "Считающий", description = "верификатор")
public class CountExaminer extends Examiner {

    private Expr expr;

    public Expr getExpr() {
        return expr;
    }

    public void setExpr(Expr expr) {
        this.expr = expr;
    }

    public Cell getLayout() {
        return new Vert(
                new ConstantCell("Считающий верификатор"),
                new Horiz(new ConstantCell("Предикат:"), new FieldCell("expr"))
        );
    }
}
