package ru.ipo.structurededitor.view.elements;

import ru.ipo.structurededitor.view.Display;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.TextProperties;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: Yura
 * Date: 09.03.2010
 * Time: 21:55:00
 * To change this template use File | Settings | File Templates.
 */
public class NextArrayElement extends VisibleElement {

    private final String NEXT_ARRAY_STRING = "...";
    private TextProperties nextArrayStringProperties = new TextProperties(Font.BOLD, Color.LIGHT_GRAY);

    public boolean isEmpty() {
        return true;
    }

    public NextArrayElement(StructuredEditorModel model) {
        super(model);

        setHeight(1);
    }

    public int countWidth() {
        return NEXT_ARRAY_STRING.length();
    }

    public void drawElement(int x0, int y0, Display d) {
        drawText(NEXT_ARRAY_STRING, nextArrayStringProperties, x0, y0, d);
    }

    private void drawText(String text, TextProperties textProperties, int x0, int y0, Display d) {
        d.drawString(text, x0, y0, textProperties);
    }
}
