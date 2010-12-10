package ru.ipo.structurededitor.view.elements;

import ru.ipo.structurededitor.view.Display;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.StructuredEditorUI;
import ru.ipo.structurededitor.view.elements.VisibleElement;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 03.01.2010
 * Time: 23:14:57
 */
// !!! Invalid due to deleting of editor from model !!!
public class IconElement extends VisibleElement {

    private Icon icon;

    protected IconElement(StructuredEditorModel model, Icon icon) {
        super(model);
        this.icon = icon;
    }

    public boolean isEmpty() {
        return icon == null;
    }

    @Override
    public void drawElement(int x0, int y0, Display d) {
        int x = d.xToPixels(x0);
        int y = d.yToPixels(y0);
        //icon.paintIcon(getModel().getEditor(), d.getGraphics(), x, y);
    }

    /*@Override
    public int getWidth() {
        StructuredEditorUI editorUI = getModel().getUI();
        return getSize(icon.getIconWidth(), editorUI.getCharWidth());
    } */

    /*@Override
    public int getHeight() {
        StructuredEditorUI editorUI = getModel().getUI();
        return getSize(icon.getIconHeight(), editorUI.getCharHeight());
    } */

    private int getSize(int icon, int chr) {
        if (icon % chr == 0)
            return icon / chr;
        else
            return icon / chr + 1;
    }
}
