package ru.ipo.structurededitor.testLang.comb;

import ru.ipo.structurededitor.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:07:49
 */
@DSLBeanParams(shortcut = "Целочисленный", description = "постоянный элемент")
public class  IntConstantElement extends ConstantElement {

    private int val;

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    public Cell getLayout() {
        return new Horiz(new FieldCell("val"));
    }
}
