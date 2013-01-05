package testSystem.lang.geom;

import ru.ipo.structurededitor.model.*;
import ru.ipo.structurededitor.view.editors.settings.NumberSettings;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:25:07
 */
public abstract class ValuePred extends Pred {

    protected transient String op;
    protected transient EditorSettings eSettings;

    protected abstract void init();

    protected ValuePred() {
        init();
    }

    private Object readResolve() {
        init();
        return this;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    double value;

    public Cell getLayout() {
        return new Horiz(new FieldCell("e", eSettings), new ConstantCell(op),
                new FieldCell(
                        "value",
                        new NumberSettings()
                                .withEmptyText("[Введите вещественное число]")
                ));
    }
}
