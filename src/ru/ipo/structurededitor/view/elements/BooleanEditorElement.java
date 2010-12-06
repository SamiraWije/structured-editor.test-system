package ru.ipo.structurededitor.view.elements;

import ru.ipo.structurededitor.view.Display;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.TextProperties;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 02.01.2010
 * Time: 21:24:49
 */
public class BooleanEditorElement extends TextElement {

    //Color constants
    public static final Color BOOL_TEXT_COLOR = Color.MAGENTA;
    public static final Color BOOL_EDIT_TEXT_COLOR = Color.RED;

    //Contants of Boolean values
    public static final String RUS_TRUE = "да";
    public static final String RUS_FALSE = "нет";

    public BooleanEditorElement(StructuredEditorModel model, String text) {
        super(model, text);
    }

    public BooleanEditorElement(StructuredEditorModel model) {
        super(model);
    }

    @Override
    public void drawElement(int x0, int y0, Display d) {
        if (!isFocused()) {
            settextProperties(new TextProperties(Font.PLAIN, BOOL_TEXT_COLOR));
            super.drawElement(x0, y0, d);
            return;
        }

        settextProperties(new TextProperties(Font.PLAIN, BOOL_EDIT_TEXT_COLOR));
        super.drawElement(x0, y0, d);
    }

    @Override
    public void processKeyEvent(KeyEvent e) {
        boolean ctrl = (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_INSERT:
                return;
            default:
                if (!ctrl) {
                    buttonChar(e.getKeyChar());
                    e.consume();
                }
        }
    }

    private void buttonChar(char c) {
        String text = getText();
        if (text.equals(RUS_TRUE))
            setText(RUS_FALSE);
        else
            setText(RUS_TRUE);
        repaint();
    }
}