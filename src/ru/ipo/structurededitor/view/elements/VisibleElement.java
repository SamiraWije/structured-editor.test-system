package ru.ipo.structurededitor.view.elements;

import ru.ipo.structurededitor.view.Display;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.TextPosition;
import ru.ipo.structurededitor.view.events.GeoSelectionChangedEvent;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

/**
 * View часть ячейки (Cell)
 */
public abstract class VisibleElement {

    //private ArrayList<ContentChangedEventListener> listeners = new ArrayList<ContentChangedEventListener>();
    private VisibleElement parent;
    private final StructuredEditorModel model;
    protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    protected ArrayList<KeyListener> keyListeners = new ArrayList<KeyListener>();

    private int width;
    private int height;


    protected VisibleElement(StructuredEditorModel model) {
        this.model = model;
    }


    //key listeners

    public void addKeyListener(KeyListener listener) {
        keyListeners.add(listener);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName,
                                          PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public abstract void drawElement(int x0, int y0, Display d);

    public abstract boolean isEmpty();

    /**
     * Обработка нажатия клавиш
     *
     * @param e
     */

    public void fireKeyEvent(KeyEvent e) {
        for (int i = keyListeners.size() - 1; i >= 0; i--) {
            KeyListener l = keyListeners.get(i);
            switch (e.getID()) {
                case KeyEvent.KEY_PRESSED:
                    l.keyPressed(e);
                    break;
                case KeyEvent.KEY_RELEASED:
                    l.keyReleased(e);
                    break;
                case KeyEvent.KEY_TYPED:
                    l.keyTyped(e);
                    break;
            }
            if (e.isConsumed())
                return;
        }

        if (e.getID() != KeyEvent.KEY_PRESSED)
            return;
        if (!e.isConsumed())
            processKeyEvent(e);
    }

    public void fireMouseEvent(MouseEvent e) {
        processMouseEvent(e);
    }

    public void fireGeoSelectionChangedEvent(GeoSelectionChangedEvent e) {
        processGeoSelectionChangedEvent(e);
    }

    public void processGeoSelectionChangedEvent(GeoSelectionChangedEvent e) {

    }

    public TextPosition getAbsolutePosition() {
        int line = 0;
        int column = 0;
        VisibleElement cur = this;
        while (cur != null) {
            VisibleElement parent = cur.getParent();
            int x = 0;
            int y = 0;
            if (parent != null) {
                TextPosition tp = parent.getChildPosition(cur);
                x = tp.getColumn();
                y = tp.getLine();
            }

            line += y;
            column += x;

            cur = parent;
        }

        return new TextPosition(line, column);
    }

    public VisibleElement getChild(int index) {
        return null;
    }


    public int getChildIndex(VisibleElement child) {
        for (int i = 0; i < getChildrenCount(); i++)
            if (getChild(i) == child)
                return i;

        return -1;
    }

    /**
     * This method should normally be overridden
     *
     * @param index child index
     * @return position of child
     */
    public TextPosition getChildPosition(int index) {
        return null;
    }

    public TextPosition getChildPosition(VisibleElement child) {
        for (int i = 0; i < getChildrenCount(); i++)
            if (getChild(i) == child)
                return getChildPosition(i);

        return null;
    }

    public int getChildrenCount() {
        return 0;
    }

    public int getHeight() {
        return height;
    }

    public StructuredEditorModel getModel() {
        return model;
    }

    public VisibleElement getParent() {
        return parent;
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return pcs.getPropertyChangeListeners();
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return pcs.getPropertyChangeListeners(propertyName);
    }

    public int getWidth() {
        return width;
    }

    public boolean isFocused() {
        return model.getFocusedElement() == this;
    }

    //TODO invent some better way for this
    public void fireFocusChanged(boolean oldFocused) {
        pcs.firePropertyChange("focused", oldFocused, isFocused());
    }

    public boolean isParentOf(VisibleElement element) {
        while (element != null) {
            if (element == this)
                return true;
            element = element.getParent();
        }

        return false;
    }

    //------------------- PropertyChangedSupport --------

    protected void processKeyEvent(KeyEvent e) {
    }

    protected void processMouseEvent(MouseEvent e) {
    }

    public void removeKeyListener(KeyListener listener) {
        keyListeners.remove(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName,
                                             PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    public void repaint() {
        getModel().repaint();
    }

    protected void setHeight(int height) {
        int oldValue = this.height;
        this.height = height;
        pcs.firePropertyChange("height", oldValue, height);
    }

    public void setParent(VisibleElement parent) {
        this.parent = parent;
    }

    protected void setWidth(int width) {
        int oldValue = this.width;
        this.width = width;
        pcs.firePropertyChange("width", oldValue, width);
    }
}