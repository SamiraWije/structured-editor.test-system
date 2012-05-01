package testSystem.lang.DSP;

import ru.ipo.structurededitor.model.*;
import testSystem.lang.comb.CalculableExpr;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:29:20
 */
public abstract class AbstractTool implements DSLBean{
    String toolType;
    public Cell getLayout() {
        return new Horiz(new ConstantCell(toolType), new FieldCell("tool"));
    }
}
