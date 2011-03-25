package ru.ipo.structurededitor.model;

/**
 * User: Ilya
 * Ячейка с редактором для поля JavaBean типа массив с гориз. расположением элементов
 */
public class HorizArray implements Cell {
    /**
     * имя поля в JavaBean для редактирования
     */
    private String fieldName;
    private char spaceChar=0;
    boolean singleLined;

    public boolean getSingleLined() {
            return singleLined;
    }

    public HorizArray(String fieldName, char spaceChar, boolean singleLined) {
        this.fieldName = fieldName;
        this.spaceChar = spaceChar;
        this.singleLined=singleLined;
    }

    public HorizArray(String fieldName, char spaceChar) {
        this.fieldName = fieldName;
        this.spaceChar = spaceChar;
    }
    public HorizArray(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
    public char getSpaceChar() {
        return spaceChar;
    }
}