package ru.ipo.structurededitor.view;

import java.awt.*;

/**
 * Свойства текста для вывода
 */
public class TextProperties {
    private final int style;
    private final Color color;
    private final Color bgcolor;

    public TextProperties(int style, Color color) {
        this(style, color, Color.WHITE);
    }

    public TextProperties(int style, Color color, Color bgcolor) {
        this.style = style;
        this.color = color;
        this.bgcolor = bgcolor;
    }

    /**
     * Цвет фона
     *
     * @return
     */
    public Color getBgcolor() {
        return bgcolor;
    }

    /**
     * Цвет текста
     *
     * @return
     */
    public Color getColor() {
        return color;
    }

    /**
     * Стиль шрифта (жирность, курсив). Константа из класса Font
     *
     * @return стиль
     */
    public int getStyle() {
        return style;
    }
}
