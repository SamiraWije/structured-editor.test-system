package ru.ipo.structurededitor.model;

/**
 * Ячейка с константным текстом
 */
public class ConstantCell implements Cell {

    private String text;

    public ConstantCell(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
