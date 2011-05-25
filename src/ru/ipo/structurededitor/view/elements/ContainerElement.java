package ru.ipo.structurededitor.view.elements;

import ru.ipo.structurededitor.view.Display;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.TextPosition;
import ru.ipo.structurededitor.view.events.GeoSelectionChangedEvent;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 03.01.2010
 * Time: 23:57:46
 */
public class ContainerElement extends VisibleElement {

    private VisibleElement subElement;

    public ContainerElement(StructuredEditorModel model, VisibleElement subElement) {
        super(model);
        setSubElement(subElement);
    }

    @Override
    public void drawElement(int x0, int y0, Display d) {
        subElement.drawElement(x0, y0, d);
    }

    @Override
    public TextPosition getChildPosition(int index) {
        return new TextPosition(0, 0);
    }

    @Override
    public int getChildrenCount() {
        return 1;
    }

    @Override
    public VisibleElement getChild(int index) {
        return subElement;
    }


    @Override
    public void processMouseEvent(MouseEvent evt) {
        /*TextPosition position = subElement.getAbsolutePosition();
        int x = position.getColumn();
        int y = position.getLine() + 1;
        if (evt.getX() >= x && evt.getX() < x + subElement.getWidth()
                && evt.getY() >= y && evt.getY() < y + subElement.getHeight())*/
        subElement.processMouseEvent(evt);

    }

    /*public void processGeoSelectionChangedEvent(GeoSelectionChangedEvent evt) {
        /*TextPosition position = subElement.getAbsolutePosition();
        int x = position.getColumn();
        int y = position.getLine() + 1;
        if (evt.getX() >= x && evt.getX() < x + subElement.getWidth()
                && evt.getY() >= y && evt.getY() < y + subElement.getHeight())** /
        subElement.processGeoSelectionChangedEvent(evt);
    }   */

    public boolean isEmpty() {
        return subElement == null || subElement.isEmpty();
    }

    public void setSubElement(final VisibleElement subElement) {
        if (subElement == null)
            throw new NullPointerException("Can not set null sub element");
        this.subElement = subElement;
        subElement.setParent(this);

        //size
        setWidth(subElement.getWidth());
        setHeight(subElement.getHeight());

        PropertyChangeListener sizeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                setWidth(subElement.getWidth());
                setHeight(subElement.getHeight());
            }
        };
        subElement.addPropertyChangeListener("width", sizeListener);
        subElement.addPropertyChangeListener("height", sizeListener);

        getModel().setFocusedElementAndCaret(subElement);
    }

    @Override
    protected void processKeyEvent(KeyEvent e) {
        super.processKeyEvent(e);    //To change body of overridden methods use File | Settings | File Templates.
    }


    /*
    @Override
    public void gainFocus(TextPosition pos, boolean shift, boolean ctrl) {
        subElement.gainFocus(pos, shift, ctrl);
    }
    */

}
