package ru.ipo.structurededitor.view.elements;

import ru.ipo.structurededitor.view.Display;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.TextPosition;
import ru.ipo.structurededitor.view.TextProperties;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 02.01.2010
 * Time: 15:58:23
 */
public class CompositeElement extends VisibleElement {

    private static class PositionedElement {
        public int x;
        public int y;
        public VisibleElement element;

        public PositionedElement(int x, int y, VisibleElement element) {
            this.x = x;
            this.y = y;
            this.element = element;
        }

        public PositionedElement(VisibleElement element) {
            this.element = element;
        }
    }

    private final ArrayList<PositionedElement> elements = new ArrayList<PositionedElement>();
    private Orientation orientation;
    private char spaceChar = 0;
    public final Color SPACE_CHAR_COLOR = Color.BLACK;
    //private int previousOutDirection = Integer.MAX_VALUE;

    public enum Orientation {
        Vertical,
        Horizontal,
    }

    public CompositeElement(StructuredEditorModel model, Orientation orientation) {
        super(model);
        this.orientation = orientation;
        reposition();
    }

    public CompositeElement(StructuredEditorModel model, Orientation orientation, char spaceChar) {
        super(model);
        this.orientation = orientation;
        this.spaceChar = spaceChar;
        reposition();
    }

    public boolean isEmpty() {
        for (PositionedElement el : elements) {
            if (!el.element.isEmpty())
                return false;
        }
        return true;
    }

    @Override
    public void processMouseEvent(MouseEvent evt) {
        for (PositionedElement el : elements) {
            TextPosition position = el.element.getAbsolutePosition();
            int x = position.getColumn();
            int y = position.getLine();
            if (evt.getX() >= x && evt.getX() < x + el.element.getWidth()
                    && evt.getY() >= y && evt.getY() < y + el.element.getHeight())
                el.element.processMouseEvent(evt);
        }
    }

    public void add(VisibleElement element) {
        add(element, elements.size());
    }

    public void add(VisibleElement element, int index) {
        elements.add(index, new PositionedElement(element));
        element.setParent(this);
        reposition();

        PropertyChangeListener sizeChangeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                reposition();
            }
        };
        element.addPropertyChangeListener("width", sizeChangeListener);
        element.addPropertyChangeListener("height", sizeChangeListener);
    }

    private void reposition() {
        if (elements.size() == 0) return;

        elements.get(0).x = 0;
        elements.get(0).y = 0;

        for (int i = 1; i < elements.size(); i++) {
            switch (orientation) {
                case Horizontal:
                    elements.get(i).x = elements.get(i - 1).x + elements.get(i - 1).element.getWidth() + 1;
                    if (spaceChar != 0)
                        elements.get(i).x += 2;
                    elements.get(i).y = elements.get(i - 1).y;
                    break;
                case Vertical:
                    elements.get(i).x = elements.get(i - 1).x;
                    /*if (spaceChar != 0)
                        elements.get(i).x += 2;*/
                    elements.get(i).y = elements.get(i - 1).y + elements.get(i - 1).element.getHeight();
                    break;
            }
        }

        setWidth(countWidth());
        setHeight(countHeight());
    }

    public void remove(VisibleElement element) {
        for (PositionedElement pe : elements) {
            if (pe.element == element) {
                elements.remove(pe);
                break;
            }
        }
        reposition();
    }

    public void remove(int index) {
        elements.remove(index);
        reposition();
    }

    public VisibleElement get(int index) {
        return elements.get(index).element;
    }

    public void drawElement(int x0, int y0, Display d) {

        for (int i = 0; i < elements.size(); i++) {
            PositionedElement pe = elements.get(i);
            pe.element.drawElement(x0 + pe.x, y0 + pe.y, d);
            if (spaceChar != 0 && i < elements.size() - 1)
                d.drawString(String.valueOf(spaceChar), x0 + pe.x + pe.element.getWidth() + 1, y0 + pe.y,
                        new TextProperties(Font.PLAIN, SPACE_CHAR_COLOR));
        }
    }

    public int countWidth() {
        switch (orientation) {
            case Horizontal:
                PositionedElement lastH = elements.get(elements.size() - 1);
                return lastH.x + lastH.element.getWidth();
            case Vertical:
                int maxWidth = 0;
                for (PositionedElement pe : elements) {
                    int w = pe.element.getWidth();
                    if (w > maxWidth)
                        maxWidth = w;
                }
                return maxWidth + 1;
        }

        return -1; //may not occur
    }

    public int countHeight() {
        switch (orientation) {
            case Horizontal:
                int maxHeight = 0;
                for (PositionedElement pe : elements) {
                    int h = pe.element.getHeight();
                    if (h > maxHeight)
                        maxHeight = h;
                }
                return maxHeight;
            case Vertical:
                PositionedElement lastV = elements.get(elements.size() - 1);
                return lastV.y + lastV.element.getHeight();
        }

        return -1; //may not occur
    }

    @Override
    public int getChildrenCount() {
        return elements.size();
    }

    @Override
    public VisibleElement getChild(int index) {
        return elements.get(index).element;
    }

    @Override
    public TextPosition getChildPosition(int index) {
        PositionedElement element = elements.get(index);
        return new TextPosition(element.y, element.x);
    }

    /*
    @Override
    public void gainFocus(TextPosition pos, boolean shift, boolean ctrl) {
        if (getChildrenCount() == 0) return;

        PositionedElement pe = null;

        switch (orientation) {
            case Vertical:
                int ind = -1;
                for (int i = 0; i < getChildrenCount() - 1; i++)
                    if (elements.get(i).y <= pos.getLine() && pos.getLine() <= elements.get(i + 1).y - 1) {
                        ind = i;
                        break;
                    }

                if (ind == -1)
                    ind = getChildrenCount() - 1;

                pe = elements.get(ind);

                break;
            case Horizontal:
                ind = -1;
                for (int i = 0; i < getChildrenCount() - 1; i++)
                    if (elements.get(i).x <= pos.getColumn() && pos.getColumn() <= elements.get(i + 1).x) {
                        ind = i;
                        break;
                    }

                if (ind == -1)
                    ind = getChildrenCount() - 1;

                pe = elements.get(ind);

                break;
        }

        int line = pos.getLine() - pe.y;
        int col = pos.getColumn() - pe.x;

        if (line >= pe.element.getHeight())
            line = pe.element.getHeight() - 1;
        if (col >= pe.element.getWidth())
            col = pe.element.getWidth() - 1;

        pe.element.gainFocus(new TextPosition(line, col), shift, ctrl);
    }

    private int findChildByAncestor(VisibleElement ancestor) {
        int i = 0;
        for (PositionedElement pe : elements) {
            if (pe.element.isParentOf(ancestor))
                return i;
            i++;
        }

        return -1;
    }
    */

    /*
    @Override
    public boolean processKeyEvent(KeyEvent e) {
        boolean shift = (e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != 0;
        boolean ctrl = (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (orientation == Orientation.Horizontal)
                    return moveCaretKeyPressed(shift, ctrl, -1);
                else
                    return moveCaretKeyPressed(shift, ctrl, -2);
            case KeyEvent.VK_RIGHT:
                if (orientation == Orientation.Horizontal)
                    return moveCaretKeyPressed(shift, ctrl, +1);
                else
                    return moveCaretKeyPressed(shift, ctrl, +2);
            case KeyEvent.VK_UP:
                if (orientation == Orientation.Horizontal)
                    return moveCaretKeyPressed(shift, ctrl, -2);
                else
                    return moveCaretKeyPressed(shift, ctrl, -1);
            case KeyEvent.VK_DOWN:
                if (orientation == Orientation.Horizontal)
                    return moveCaretKeyPressed(shift, ctrl, +2);
                else
                    return moveCaretKeyPressed(shift, ctrl, +1);
        }

        return true;
    }
    */

    /*
    private boolean moveCaretKeyPressed(boolean shift, boolean ctrl, int childInc) {
        VisibleElement focused = getModel().getElementUnderCaret();
        VisibleElement prevFocused = getModel().getPreviousFocusedElement();

        if (Math.abs(childInc) == 2)
            return false;

        int childInd;
        if (focused == this) {
            if (isParentOf(prevFocused) && previousOutDirection == childInc) { //move out
                previousOutDirection = childInc;
                return false;
            } else { //then go inside
                if (childInc < 0)
                    childInd = getChildrenCount() - 1;
                else
                    childInd = 0;
            }
        } else { //if focused is an ancestor, go Up/Left
            childInd = findChildByAncestor(focused);
            if (childInd == -1) return true;
            childInd += childInc;
        }

        //if it was the last child and need to select all component
        if (focused != this)
            if (Math.abs(childInc) == 2 || childInd < 0 || childInd >= getChildrenCount()) {
                previousOutDirection = childInc;
                getModel().setFocusedElement(this);
                return true;
            }

        //select next child
        VisibleElement child = getChild(childInd);
        child.gainFocus(
                childInc < 0 ?
                        new TextPosition(child.getHeight() - 1, child.getWidth())
                        :
                        new TextPosition(0, 0),
                shift, ctrl
        );
        return true;
    }
    */
}
