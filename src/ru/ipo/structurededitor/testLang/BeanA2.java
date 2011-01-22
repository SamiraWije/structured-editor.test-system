package ru.ipo.structurededitor.testLang;

import ru.ipo.structurededitor.model.Cell;
import ru.ipo.structurededitor.model.ConstantCell;
import ru.ipo.structurededitor.model.FieldCell;
import ru.ipo.structurededitor.model.Horiz;
import ru.ipo.structurededitor.model.Vert;

/**
 * Пример сложного поля из нескольких ячеек
 */
public class BeanA2 extends BeanA {

    private String x;

    public Cell getLayout() {
        return new Horiz(new Vert(new ConstantCell("BEAN 2"), new ConstantCell(
                "strFld ="), new ConstantCell("x =")), new Vert(new ConstantCell(" "),
                new FieldCell("strFld"), new FieldCell("x")));
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }
}
