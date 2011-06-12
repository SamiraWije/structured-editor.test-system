package ru.ipo.structurededitor.view;

import java.awt.*;

/**
 * Обёртка для Graphics которая выводит текст в своей системе координат
 */
public class Display {

    private final Graphics g;
    private final StructuredEditorUI ui;

    public Display(Graphics g, StructuredEditorUI ui) {
        this.g = g;
        this.ui = ui;
        g.setFont(StructuredEditorUI.FONT);
    }

    /**
     * Вывод текста
     *
     * @param s  текст для вывода
     * @param x  столбец
     * @param y  строка
     * @param tp форматирование текста (цвет, жирность)
     */
    public void drawString(String s, int x, int y, TextProperties tp) {
        Font f = StructuredEditorUI.FONT;
        if (f.getStyle() != tp.getStyle())
            f = f.deriveFont(tp.getStyle());

        g.setFont(f);
        g.setColor(tp.getColor());
        //System.out.println("STRING: " + s + " " + xToPixels(x) + "," + yToPixels(y));        
        g.drawString(s, xToPixels(x), yToPixels(y) + ui.getCharAscent());
    }

    public Graphics getGraphics() {
        return g;
    }

    public StructuredEditorUI getUi() {
        return ui;
    }

    public int xToPixels(int x) {
        return ui.xToPixels(x);
    }

    public int yToPixels(int y) {
        return ui.yToPixels(y);
    }
}
