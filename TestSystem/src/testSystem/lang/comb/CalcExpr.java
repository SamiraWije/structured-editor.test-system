package testSystem.lang.comb;

import ru.ipo.structurededitor.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:29:20
 */
@DSLBeanParams(shortcut = "Вычислить")
public class CalcExpr extends CalculableExpr {

    public Cell getLayout() {
        return new Horiz(new ConstantCell("Вычислить ("), new FieldCell("ce"), new ConstantCell(")"));
    }


}
