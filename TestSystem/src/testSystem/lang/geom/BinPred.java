package testSystem.lang.geom;

import ru.ipo.structurededitor.model.*;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:25:07
 */
public abstract class BinPred extends Pred {

    protected transient String op;
    protected transient boolean vert = false;

    protected BinPred() {
        init();
    }

    protected abstract void init();

    private Object readResolve() {
        init();
        return this;
    }

    public Cell getLayout() {
        EditorSettings s1 = getSettingsForField("e1");
        EditorSettings s2 = getSettingsForField("e2");

        if (vert)
            return new Vert(new FieldCell("e1", s1), new ConstantCell(op), new FieldCell("e2", s2));
        else
            return new Horiz(new FieldCell("e1", s1), new ConstantCell(op), new FieldCell("e2", s2));
    }

    private EditorSettings getSettingsForField(String propertyName) {
        Class<?> propertyType;
        try {
            PropertyDescriptor pd = new PropertyDescriptor(propertyName, getClass());
            propertyType = pd.getPropertyType();

        } catch (IntrospectionException e) {
            throw new Error("Fail in getting settings for field");
        }

        return null;
    }
}
