package ru.ipo.structurededitor.view.elements;

import ru.ipo.structurededitor.view.Display;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.TextPosition;
import ru.ipo.structurededitor.view.TextProperties;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 02.01.2010
 * Time: 21:24:49
 */
public class TextEditorElement extends TextElement {

    private int xCaretPosition = 0;
    private int yCaretPosition = 0;
    private int xMarkPosition = -1;
    private int yMarkPosition = -1;
    //public 

    //Color constants
    public static final Color USER_TEXT_COLOR = Color.BLUE;
    public static final Color USER_EDIT_TEXT_COLOR = Color.RED;

    public TextEditorElement(StructuredEditorModel model, String text) {
        super(model, text);
        //System.out.println("Text was created with value: ");
    }


    public TextEditorElement(StructuredEditorModel model) {
        super(model);
    }

    public void resetPosition() {
        xCaretPosition = 0;
        yCaretPosition = 0;
        xMarkPosition = -1;
        yMarkPosition = -1;
    }

    public void setUnfocusedElementProps() {
        settextProperties(new TextProperties(Font.PLAIN, USER_TEXT_COLOR));
    }

    @Override
    public void drawElement(int x0, int y0, Display d) {
        if (!isFocused()) {

            setUnfocusedElementProps();
            super.drawElement(x0, y0, d);
            return;
        }

        //draw selection
        if (isFocused() && xMarkPosition >= 0 && xCaretPosition >= 0 &&
                !(xMarkPosition == xCaretPosition && yMarkPosition == yCaretPosition)) {
            d.getGraphics().setColor(Color.BLUE);
            if (yMarkPosition == yCaretPosition) {
                int begin = Math.min(xCaretPosition, xMarkPosition);
                int end = Math.max(xCaretPosition, xMarkPosition);
                int x1 = d.xToPixels(x0 + begin), y1 = d.yToPixels(y0 + yCaretPosition - 1);
                int x2 = d.xToPixels(x0 + end), y2 = d.yToPixels(y0 + yCaretPosition);
                d.getGraphics().fillRect(x1, y2, x2 - x1, y2 - y1);
            } else {
                int xBegin, yBegin, xEnd, yEnd;
                if (yMarkPosition < yCaretPosition) {
                    xBegin = xMarkPosition;
                    yBegin = yMarkPosition;
                    xEnd = xCaretPosition;
                    yEnd = yCaretPosition;
                } else {
                    xBegin = xCaretPosition;
                    yBegin = yCaretPosition;
                    xEnd = xMarkPosition;
                    yEnd = yMarkPosition;
                }
                int x1 = d.xToPixels(x0 + xBegin), y1 = d.yToPixels(y0 + yBegin - 1);
                int x2 = d.xToPixels(x0 + ((String) getLines().get(yBegin)).length()),
                        y2 = d.yToPixels(y0 + yBegin);
                d.getGraphics().fillRect(x1, y2, x2 - x1, y2 - y1);
                int delta = yEnd - yBegin;
                if (delta > 1) {
                    for (int i = yBegin + 1; i < yEnd; i++) {
                        x1 = d.xToPixels(x0);
                        y1 = d.yToPixels(y0 + i - 1);
                        x2 = d.xToPixels(x0 + ((String) getLines().get(i)).length());
                        y2 = d.yToPixels(y0 + i);
                        d.getGraphics().fillRect(x1, y2, x2 - x1, y2 - y1);
                    }
                }
                x1 = d.xToPixels(x0);
                y1 = d.yToPixels(y0 + yEnd - 1);
                x2 = d.xToPixels(x0 + xEnd);
                y2 = d.yToPixels(y0 + yEnd);
                d.getGraphics().fillRect(x1, y2, x2 - x1, y2 - y1);
            }
        }

        settextProperties(new TextProperties(Font.PLAIN, USER_EDIT_TEXT_COLOR));
        super.drawElement(x0, y0, d);

        //draw caret
        if (isFocused())
            getModel().showCaret(x0 + xCaretPosition, y0 + yCaretPosition, d);

    }

    @Override
    public void processMouseEvent(MouseEvent e) {
        super.processMouseEvent(e);
        if (e.getClickCount() >= 1 && !isEmpty()) {
            TextPosition position = getAbsolutePosition();
            int x0 = position.getColumn();
            int y0 = position.getLine();
            xCaretPosition = e.getX() - x0;
            yCaretPosition = e.getY() - y0;
            getModel().repaint();
        }
    }

    @Override
    public void processKeyEvent(KeyEvent e) {
        //boolean isPrintable = Character.isLetterOrDigit(e.getKeyChar()) ||
        //        Character.isSpaceChar(e.getKeyChar());
        boolean isPrintable = Character.isDefined(e.getKeyChar()) && !Character.isISOControl(e.getKeyChar());
        boolean shift = (e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != 0;
        boolean ctrl = (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0;

        if (ctrl)
            return;

        if (isPrintable /*&& !ctrl*/) {
            buttonChar(e.getKeyChar());
            e.consume();
            return;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_DELETE:
                if (!isEmptyText()) {
                    buttonDelete();
                    e.consume();
                }
                break;
            case KeyEvent.VK_BACK_SPACE:
                if (!isEmptyText()) {
                    buttonBackSpace();
                    e.consume();
                }
                break;
            case KeyEvent.VK_LEFT:
                if (xMoveCaret(xCaretPosition - 1, shift))
                    e.consume();
                return;
            case KeyEvent.VK_RIGHT:
                if (xMoveCaret(xCaretPosition + 1, shift))
                    e.consume();
                return;
            case KeyEvent.VK_DOWN:
                if (yMoveCaret(yCaretPosition + 1, shift))
                    e.consume();
                return;
            case KeyEvent.VK_UP:
                if (yMoveCaret(yCaretPosition - 1, shift))
                    e.consume();
                return;

            case KeyEvent.VK_HOME:
                if (xMoveCaret(0, shift))
                    e.consume();
                return;
            case KeyEvent.VK_END:
                final String txt = getText();
                if (txt == null) return;
                if (xMoveCaret(((String) getLines().get(yCaretPosition)).length(), shift))
                    e.consume();
                return;
            case KeyEvent.VK_ENTER:
                if (!isEmpty()) {
                    buttonEnter();
                    e.consume();
                }
                //return;
        }
    }

    /*
    @Override
    public void gainFocus(TextPosition pos, boolean shift, boolean ctrl) {
        String text = getText();
        if (text == null)
            text = "";
        if (pos.getColumn() >= 0 && pos.getColumn() <= text.length())
            moveCaret(pos.getColumn(), shift);
        repaint();

        getModel().setFocusedElement(this);
    }
    */

    private void buttonChar(char c) {
        if (xMarkPosition != -1)
            removeSelection();

        String text = getText();
        if (text == null)
            text = "";
        Vector v = getLines();
        int pos = xCaretPosition;
        for (int i = 0; i < yCaretPosition; i++) {
            pos += ((String) v.get(i)).length() + 1;
        }

        StringBuilder sb = new StringBuilder();
        sb
                .append(text.substring(0, pos))
                .append(c)
                .append(text.substring(pos));

        int newXCaretPosition;
        int newYCaretPosition;
        setText(sb.toString());
        v = getLines();
        int newPos = pos + 1;
        int i = 0;
        int iPos = 0;
        while (iPos <= newPos) {
            iPos += ((String) v.get(i)).length() + 1;
            i++;
        }
        newYCaretPosition = i - 1;
        newXCaretPosition = ((String) v.get(i - 1)).length() - iPos + newPos + 1;
        setCaretPosition(newXCaretPosition, newYCaretPosition);

        repaint();
    }

    private void buttonEnter() {
        if (xMarkPosition != -1)
            removeSelection();

        String text = getText();
        if (text == null)
            text = "";
        Vector v = getLines();
        int pos = xCaretPosition;
        for (int i = 0; i < yCaretPosition; i++) {
            pos += ((String) v.get(i)).length() + 1;
        }

        StringBuilder sb = new StringBuilder();
        sb
                .append(text.substring(0, pos))
                .append('\n')
                .append(text.substring(pos));

        setText(sb.toString());
        setStringCaretPosition(pos + 1);

        repaint();
    }

    private void setStringCaretPosition(int newPos) {
        Vector v = getLines();
        int newYCaretPosition, newXCaretPosition;
        int i = 0;
        int iPos = 0;
        while (iPos <= newPos) {
            iPos += ((String) v.get(i)).length() + 1;
            i++;
        }
        newYCaretPosition = i - 1;
        newXCaretPosition = ((String) v.get(i - 1)).length() - iPos + newPos + 1;
        setCaretPosition(newXCaretPosition, newYCaretPosition);
    }

    private void buttonBackSpace() {
        if (xMarkPosition != -1) {
            removeSelection();
            return;
        }

        String text = getText();
        Vector v = getLines();
        int pos = xCaretPosition;
        for (int i = 0; i < yCaretPosition; i++) {
            pos += ((String) v.get(i)).length() + 1;
        }
        if (text == null)
            return;
        if (xCaretPosition == 0 && yCaretPosition == 0)
            return;
        StringBuilder sb = new StringBuilder();
        sb
                .append(text.substring(0, pos - 1))
                .append(text.substring(pos));

        setText(sb.toString());
        setStringCaretPosition(pos - 1);
        repaint();
    }

    private void buttonDelete() {
        if (xMarkPosition != -1) {
            removeSelection();
            return;
        }
        String text = getText();
        if (text == null)
            return;
        Vector v = getLines();
        int pos = xCaretPosition;
        for (int i = 0; i < yCaretPosition; i++) {
            pos += ((String) v.get(i)).length() + 1;
        }
        if (pos == text.length())
            return;

        StringBuilder sb = new StringBuilder();
        sb
                .append(text.substring(0, pos))
                .append(text.substring(pos + 1));
        setText(sb.toString());
        setStringCaretPosition(pos);

        repaint();
    }

    private void removeSelection() {
        String text = getText();
        if (text == null)
            return;
        Vector v = getLines();
        int pos1 = xCaretPosition;
        for (int i = 0; i < yCaretPosition; i++) {
            pos1 += ((String) v.get(i)).length() + 1;
        }
        int pos2 = xMarkPosition;
        for (int i = 0; i < yMarkPosition; i++) {
            pos2 += ((String) v.get(i)).length() + 1;
        }
        int begin = Math.min(pos1, pos2);
        int end = Math.max(pos1, pos2);

        StringBuilder sb = new StringBuilder();
        sb
                .append(text.substring(0, begin))
                .append(text.substring(end));

        setText(sb.toString());
        setStringCaretPosition(begin);
        xMarkPosition = -1;
        yMarkPosition = -1;
        repaint();
    }

    private boolean xMoveCaret(int newXPosition, boolean shift) {
        int newYPosition = yCaretPosition;
        int len = getText() == null ? 0 : ((String) getLines().get(yCaretPosition)).length();
        if (newXPosition < 0) {
            if (yCaretPosition > 0) {
                newXPosition = ((String) getLines().get(yCaretPosition - 1)).length();
                newYPosition--;
            } else
                return shift;
        } else if (newXPosition > len) {
            if (yCaretPosition < getLines().size() - 1) {
                newXPosition = 0;
                newYPosition++;
            } else
                return shift;
        }

        if (!shift)
            xMarkPosition = -1;
        else if (xMarkPosition == -1) {
            yMarkPosition = yCaretPosition;
            xMarkPosition = xCaretPosition;
        }


        setCaretPosition(newXPosition, newYPosition);

        repaint();

        return true;
    }

    private boolean yMoveCaret(int newYPosition, boolean shift) {
        int newXPosition = xCaretPosition;
        int len = getText() == null ? 0 : getLines().size();
        if (newYPosition < 0 || newYPosition >= len) {
            return shift;
        }
        int newStrLen = ((String) getLines().get(newYPosition)).length();
        if (xCaretPosition > newStrLen) {
            newXPosition = newStrLen;
        }

        if (!shift)
            xMarkPosition = -1;
        else if (xMarkPosition == -1) {
            yMarkPosition = yCaretPosition;
            xMarkPosition = xCaretPosition;
        }
        setCaretPosition(newXPosition, newYPosition);

        repaint();

        return true;
    }

    public void setCaretPosition(int xCaretPosition, int yCaretPosition) {
        pcs.firePropertyChange("xCaretPosition", this.xCaretPosition, xCaretPosition);
        pcs.firePropertyChange("yCaretPosition", this.yCaretPosition, yCaretPosition);

        this.xCaretPosition = xCaretPosition;
        this.yCaretPosition = yCaretPosition;

        repaint();
    }

    public int getXCaretPosition() {
        return xCaretPosition;
    }

    public int getYCaretPosition() {
        return yCaretPosition;
    }

    public int getXMarkPosition() {
        return xMarkPosition;
    }

    public void setMarkPosition(int xMarkPosition, int yMarkPosition) {
        this.xMarkPosition = xMarkPosition;
        this.yMarkPosition = yMarkPosition;
        repaint();
    }
}