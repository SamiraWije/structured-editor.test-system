package ru.ipo.structurededitor.model;

/**
 * User: Ilya
 * Ячейка с редактором для поля JavaBean
 */
public class FieldCell implements Cell {
    /**
     * имя поля в JavaBean для редактирования
     */
    private String fieldName;

    public FieldCell(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

}
