package ru.ipo.structurededitor.testLang;

import ru.ipo.structurededitor.model.*;

/**
 * Пример сложного поля из нескольких ячеек
 */
public class  BeanA3 extends BeanA {

    private int x, y;

    public Cell getLayout() {
        return new Horiz(new Vert(new ConstantCell("Введите координаты:"), new ConstantCell(
                "x ="), new ConstantCell("y =")), new Vert(new ConstantCell(" "),
                new FieldCell("x"), new FieldCell("y")));
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
