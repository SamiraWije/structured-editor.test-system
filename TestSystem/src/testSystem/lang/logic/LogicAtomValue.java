package testSystem.lang.logic;

import ru.ipo.structurededitor.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:07:49
 */
public class LogicAtomValue implements DSLBean{

    private boolean val;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
    public boolean getVal() {
        return val;
    }

    public void setVal(boolean val) {
        this.val = val;
    }

    public Cell getLayout() {
        return new Horiz(new FieldCell("name"), new ConstantCell("="), new FieldCell("val"));
    }
}
