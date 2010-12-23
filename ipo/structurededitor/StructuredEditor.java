package ru.ipo.structurededitor;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.StructuredEditorUI;
import ru.ipo.structurededitor.view.VisibleElementsGraph;
import ru.ipo.structurededitor.view.elements.VisibleElement;

/**
 * Created by IntelliJ IDEA. User: Ilya Date: 02.01.2010 Time: 14:52:43
 */
public class StructuredEditor extends JComponent implements Scrollable {

    private StructuredEditorModel model;
    private DSLBean o;

    public StructuredEditor(StructuredEditorModel model, DSLBean o) {
        setModel(model);
        setObject(o);
        // TODO set UI by: getUiClassID, UIManager.install UI and so on
        setUI(new StructuredEditorUI());

        setFocusable(true);

        registerCaretMovementKeyStrokes();
    }

    public DSLBean getObject() {
        return o;
    }

    public StructuredEditorModel getModel() {
        return model;
    }

    public Dimension getPreferredScrollableViewportSize() {
        return ui.getPreferredSize(this);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension parentDimension = getParent().getSize();
        parentDimension = parentDimension == null
                ? new Dimension(1, 1)
                : parentDimension;

        Dimension uiDimension = ui.getPreferredSize(this);
        uiDimension = uiDimension == null ? new Dimension(1, 1) : uiDimension;

        if (uiDimension.width > parentDimension.width)
            parentDimension.width = uiDimension.width;
        if (uiDimension.height > parentDimension.height)
            parentDimension.height = uiDimension.height;

        return parentDimension;
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect,
                                           int orientation, int direction) {
        return getScrollableUnitIncrement(visibleRect, orientation, direction);
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    // implement Scrollable

    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation,
                                          int direction) {
        switch (orientation) {
            case SwingConstants.VERTICAL:
                return ((StructuredEditorUI) ui).getCharHeight();
            case SwingConstants.HORIZONTAL:
                return ((StructuredEditorUI) ui).getCharWidth();
        }
        return 0; // may not occur
    }

    public StructuredEditorUI getUI() {
        return (StructuredEditorUI) ui;
    }

    @Override
    protected void processComponentKeyEvent(KeyEvent e) {

        VisibleElement el = model.getFocusedElement();
        while (el != null) {
            el.fireKeyEvent(e);
            if (e.isConsumed())
                return;
            el = el.getParent();
        }
    }

    private void registerCaretMovementKeyStrokes() {
        getInputMap().put(KeyStroke.getKeyStroke("pressed UP"), "move caret up");
        getInputMap()
                .put(KeyStroke.getKeyStroke("pressed DOWN"), "move caret down");
        getInputMap()
                .put(KeyStroke.getKeyStroke("pressed LEFT"), "move caret left");
        getInputMap().put(KeyStroke.getKeyStroke("pressed RIGHT"),
                "move caret right");
        getActionMap().put("move caret up",
                new CaretMovementAction(VisibleElementsGraph.Direction.Up));
        getActionMap().put("move caret down",
                new CaretMovementAction(VisibleElementsGraph.Direction.Down));
        getActionMap().put("move caret left",
                new CaretMovementAction(VisibleElementsGraph.Direction.Left));
        getActionMap().put("move caret right",
                new CaretMovementAction(VisibleElementsGraph.Direction.Right));
    }

    public void setModel(StructuredEditorModel model) {
        //model.setEditor(this);
        this.model = model;
        setUI(new StructuredEditorUI());
    }

    public void setObject(DSLBean o) {
        this.o = o;
    }
}