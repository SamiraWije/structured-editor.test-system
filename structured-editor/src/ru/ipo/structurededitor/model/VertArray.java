package ru.ipo.structurededitor.model;

/**
 * User: Ilya
 * Ячейка с редактором для поля JavaBean типа массив с вертик. расположением элементов
 */
public class VertArray implements Cell {
    /**
     * имя поля в JavaBean для редактирования
     */
    private String fieldName;
    private char spaceChar = 0;

    public boolean getSingleLined() {
        return singleLined;
    }

    private boolean singleLined = false;

    public VertArray(String fieldName, char spaceChar, boolean singleLined) {
        this.fieldName = fieldName;
        this.spaceChar = spaceChar;
    }

    public VertArray(String fieldName, char spaceChar) {
        this.fieldName = fieldName;
        this.spaceChar = spaceChar;
    }

    public VertArray(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public char getSpaceChar() {
        return spaceChar;
    }
}