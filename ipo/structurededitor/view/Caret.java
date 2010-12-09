package ru.ipo.structurededitor.view;

import java.awt.Graphics;
import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Created by IntelliJ IDEA. User: Ilya Date: 13.01.2010 Time: 18:26:54
 */
public class Caret {

    PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private int line;
    private int column;

    private final StructuredEditorModel model;

    public Caret(StructuredEditorModel model) {
        this.model = model;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName,
                                          PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public int getColumn() {
        return column;
    }

    public int getLine() {
        return line;
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return pcs.getPropertyChangeListeners();
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return pcs.getPropertyChangeListeners(propertyName);
    }

    public void moveCaretToPoint(Point p) {
        StructuredEditorUI ui = model.getUI();

        int line_ = (int) Math.round(p.x / (double) ui.getCharHeight());
        int col_ = (int) Math.floor(p.y / (double) ui.getCharWidth());

        tryMoveCaret(line_, col_);
    }

    // -------------------  property change listeners ------------------

    /**
     * Отрисовка каретки
     *
     * @param g полотно для рисования
     */
    public void paint(Graphics g) {
        StructuredEditorUI ui = model.getUI();
        int x0 = ui.getCharWidth() * column;
        int y0 = ui.getCharHeight() * line;

        g.drawLine(x0, y0, x0, y0 + ui.getCharHeight());
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName,
                                             PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    public void setColumn(int column) {
        int oldColumn = this.column;
        this.column = column;
        pcs.firePropertyChange("column", oldColumn, column);
    }

    public void setLine(int line) {
        int oldLine = this.line;
        this.line = line;
        pcs.firePropertyChange("line", oldLine, line);
    }

    private void tryMoveCaret(int line_, int col_) {
        //TODO make use of model and move caret in the possible place only
        line = line_;
        column = col_;
    }
}
