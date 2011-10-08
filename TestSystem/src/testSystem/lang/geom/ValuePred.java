package testSystem.lang.geom;

import ru.ipo.structurededitor.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:25:07
 */
public abstract class ValuePred extends Pred {

    protected String op;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    double value;

    public Cell getLayout() {
            return new Horiz(new FieldCell("e"), new ConstantCell(op),
                    new FieldCell("value"));
    }
}
