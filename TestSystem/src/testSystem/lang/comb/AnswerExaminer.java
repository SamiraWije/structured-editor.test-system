package testSystem.lang.comb;

import ru.ipo.structurededitor.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:15:32
 */
@DSLBeanParams(shortcut = "Непосредственный", description = "верификатор")
public class AnswerExaminer extends Examiner {

    private Expr expr;

    public Expr getExpr() {
        return expr;
    }

    public void setExpr(Expr expr) {
        this.expr = expr;
    }

    public Cell getLayout() {
        return new Vert(
                new ConstantCell("Непосредственный верификатор"),
                new Horiz(new ConstantCell("Предикат:"), new FieldCell("expr"))
        );
    }
}
