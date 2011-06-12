package ru.ipo.structurededitor.view.elements;

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
public class DoubleEditorElement extends TextEditorElement {

    //public 

    //Color constants
    public static final Color DOUBLE_USER_TEXT_COLOR = Color.GREEN;

    public DoubleEditorElement(StructuredEditorModel model, String text) {
        super(model, text);
    }

    public DoubleEditorElement(StructuredEditorModel model) {
        super(model);
    }

    @Override
    public void setUnfocusedElementProps() {
        settextProperties(new TextProperties(Font.PLAIN, DOUBLE_USER_TEXT_COLOR));
    }

    @Override
    public void processKeyEvent(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                return;
        }
        super.processKeyEvent(e);
    }
}