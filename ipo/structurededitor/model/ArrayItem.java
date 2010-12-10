package ru.ipo.structurededitor.model;

/**
 * User: Ilya
 * Ячейка с редактором для элемента массива
 */
public class ArrayItem extends FieldCell implements Cell {
    /**
     * Индекс элемента массива
     */
    private int Index;

    public ArrayItem(String fieldName, int Index) {
        super(fieldName);
        this.Index = Index;
    }
}
