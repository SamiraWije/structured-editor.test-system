package ru.ipo.structurededitor.testLang;

import ru.ipo.structurededitor.model.*;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 04.01.2010
 * Time: 2:09:56
 */
public class  Bean1 implements DSLBean {

    private String field1;
    private int field2;
    private BeanA field3;
    private double field4;
    private boolean field5;
    private Count field6;
    private int[] field7;
    private Count[] field8;
    private double[] field9;
    private boolean[] field10;
    private String[] field11;
    private BeanA2[] field12;
    private BeanA[] field13;

    public Cell getLayout() {
        return new Vert(
                new ConstantCell("bean 1"),
                new Horiz(new ConstantCell("String Field 1: "), new FieldCell("field1")),
                new Horiz(new ConstantCell("Integer Field 2: "), new FieldCell("field2")),
                new Horiz(new ConstantCell("Bean Field 3: "), new FieldCell("field3")),
                new Horiz(new ConstantCell("Double Field 4: "), new FieldCell("field4")),
                new Horiz(new ConstantCell("Boolean Field 5: "), new FieldCell("field5")),
                new Horiz(new ConstantCell("Enum Field 6: "), new FieldCell("field6")),
                new Horiz(new ConstantCell("Int Array Field 7: "), new HorizArray("field7", '+')),
                new Horiz(new ConstantCell("Enum Array Field 8: "), new HorizArray("field8", ',')),
                new Horiz(new ConstantCell("Double Array Field 9: "), new HorizArray("field9")),
                new Horiz(new ConstantCell("Boolean Array Field 10: "), new HorizArray("field10")),
                new Horiz(new ConstantCell("String Array Field 11: "), new HorizArray("field11")),
                new Horiz(new ConstantCell("BeanA2 Array Field 12: "), new HorizArray("field12")),
                new Horiz(new ConstantCell("Abstract BeanA Array Field 13: "), new VertArray("field13"))
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

    public Count[] getField8() {
        return field8;
    }

    public void setField8(Count[] field8) {
        System.out.println("field8 = {");
        for (Count ai : field8)
            System.out.println(ai);
        System.out.println("}");
        this.field8 = field8;
    }

    public double[] getField9() {
        return field9;
    }

    public void setField9(double[] field9) {
        System.out.println("field9 = {");
        for (double ai : field9)
            System.out.println(ai);
        System.out.println("}");
        this.field9 = field9;
    }

    public boolean[] getField10() {
        return field10;
    }

    public void setField10(boolean[] field10) {
        System.out.println("field10 = {");
        for (boolean ai : field10)
            System.out.println(ai);
        System.out.println("}");
        this.field10 = field10;
    }

    public String[] getField11() {
        return field11;
    }

    public void setField11(String[] field11) {
        System.out.println("field11 = {");
        for (String ai : field11)
            System.out.println(ai);
        System.out.println("}");
        this.field11 = field11;
    }

    public BeanA2[] getField12() {
        return field12;
    }

    public void setField12(BeanA2[] field12) {
        this.field12 = field12;
    }

    public BeanA[] getField13() {
        return field13;
    }

    public void setField13(BeanA[] field13) {
        this.field13 = field13;
    }

}
