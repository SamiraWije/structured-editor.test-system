package ru.ipo.structurededitor.testLang;

import ru.ipo.structurededitor.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 04.01.2010
 * Time: 2:09:56
 */
public class Bean2 implements DSLBean {

    private String field1;
    private int field2;
    private BeanA field3;
    private double field4;
    private boolean field5;
    private Count field6;
    private int[] field7;

    public BeanA2[] getField8() {
        return field8;
    }

    public void setField8(BeanA2[] field8) {
        this.field8 = field8;
    }

    private BeanA2[] field8;


    public Cell getLayout() {
        return new Vert(
                new ConstantCell("Пример редактора (это нередактируемый текст)"),
                new Horiz(new ConstantCell("Строка текста: "), new FieldCell("field1")),
                new Horiz(new ConstantCell("Целое число: "), new FieldCell("field2")),
                new Horiz(new ConstantCell("Структурные данные: "), new FieldCell("field3")),
                new Horiz(new ConstantCell("Вещественное число: "), new FieldCell("field4")),
                new Horiz(new ConstantCell("Логический флаг: "), new FieldCell("field5")),
                new Horiz(new ConstantCell("Поле с перечислимыми значениями: "), new FieldCell("field6")),
                new Horiz(new ConstantCell("Массив целых чисел: "), new HorizArray("field7", ';')),
                new Horiz(new ConstantCell("Массив структурных данных: "), new HorizArray("field8", ';'))
        );
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        System.out.println("field1 = " + field1);
        this.field1 = field1;
    }

    public int getField2() {
        return field2;
    }

    public void setField2(int field2) {
        System.out.println("field2 = " + field2);
        this.field2 = field2;
    }

    public BeanA getField3() {
        return field3;
    }

    public void setField3(BeanA field3) {
        this.field3 = field3;
    }

    public double getField4() {
        return field4;
    }

    public void setField4(double field4) {
        System.out.println("field4 = " + field4);
        this.field4 = field4;
    }

    public boolean getField5() {
        return field5;
    }

    public void setField5(boolean field5) {
        System.out.println("field5 = " + field5);
        this.field5 = field5;
    }

    public Count getField6() {
        return field6;
    }

    public void setField6(Count field6) {
        System.out.println("field6 = " + field6);
        this.field6 = field6;
    }

    public int[] getField7() {
        return field7;
    }

    public void setField7(int[] field7) {
        System.out.println("field7 = {");
        for (int ai : field7)
            System.out.println(ai);
        System.out.println("}");
        this.field7 = field7;
    }
}
