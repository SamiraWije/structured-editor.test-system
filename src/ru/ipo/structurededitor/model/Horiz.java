package ru.ipo.structurededitor.model;

/**
 * Горизонтальная последовательность ячеек
 */
public class Horiz implements Cell {

    private final Cell[] cells;

    public Horiz(Cell... cells) {
        this.cells = cells;
    }

    public Cell[] getCells() {
        return cells;
    }
}