package ru.ipo.structurededitor.testLang.comb;

import ru.ipo.structurededitor.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:09:17
 */
@DSLBeanParams(shortcut = "[]", description = "Отрезок")
public class  IntSegment extends Kit {

    private int from;
    private int to;

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public Cell getLayout() {
        return new Horiz(
                new ConstantCell("["), new FieldCell("from"), new ConstantCell(".."),
                new FieldCell("to"), new ConstantCell("]")
        );
    }
}
