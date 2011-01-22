package ru.ipo.structurededitor.model;

/**
 * Вертикальное расположение ячеек
 */
public class Vert implements Cell {

    private Cell[] cells;

    public Vert(Cell... cells) {
        this.cells = cells;
    }

    public Cell[] getCells() {
        return cells;
    }
}
