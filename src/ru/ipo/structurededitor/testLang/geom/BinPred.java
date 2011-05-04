package ru.ipo.structurededitor.testLang.geom;

import ru.ipo.structurededitor.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:25:07
 */
public abstract class BinPred extends Pred {

    protected String op;
    protected boolean vert = false;

    public Cell getLayout() {
        if (vert)
            return new Vert(new FieldCell("e1"), new ConstantCell(op), new FieldCell("e2"));
        else
            return new Horiz(new FieldCell("e1"), new ConstantCell(op),
                    new FieldCell("e2"));
    }
}
