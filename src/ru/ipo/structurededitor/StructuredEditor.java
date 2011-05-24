package ru.ipo.structurededitor;

import geogebra.main.DefaultApplication;
import ru.ipo.structurededitor.model.DefaultDSLBean;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.StructuredEditorUI;
import ru.ipo.structurededitor.view.TextPosition;
import ru.ipo.structurededitor.view.VisibleElementsGraph;
import ru.ipo.structurededitor.view.elements.VisibleElement;
import ru.ipo.structurededitor.view.events.GeoSelectionChangedEvent;
import ru.ipo.structurededitor.view.events.GeoSelectionChangedListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Created by IntelliJ IDEA. User: Ilya Date: 02.01.2010 Time: 14:52:43
 */
public class StructuredEditor extends JComponent implements Scrollable {

    public boolean isView() {
        return view;
    }

    private boolean view=false;
    public StructuredEditor() {

        this(new StructuredEditorModel(new DefaultDSLBean()));
    }

    private StructuredEditorModel model;

    public DefaultApplication getApp() {
        return app;
    }

    public void setApp(DefaultApplication app) {
        this.app = app;
        app.getEuclidianView().getEuclidianController().addGeoSelectionChangedListener(new GeoSelectionChangedListener() {
            public void geoSelectionChanged(GeoSelectionChangedEvent e) {
                //Object selection = e.getSelectedGeo();
                model.getRootElement().fireGeoSelectionChangedEvent(e);


            }
        });
    }

    private DefaultApplication app;

    public StructuredEditor(StructuredEditorModel model) {
        setModel(model);

        // TODO set UI by: getUiClassID, UIManager.install UI and so on
        setUI(new StructuredEditorUI());

        setFocusable(true);

        registerCaretMovementKeyStrokes();
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
    }

     public StructuredEditor(StructuredEditorModel model, boolean view) {
       this(model);
       this.view=view;
       model.setView(view);
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
        if (!view){
            VisibleElement el = model.getFocusedElement();
            while (el != null) {
                el.fireKeyEvent(e);
                if (e.isConsumed())
                    return;
                el = el.getParent();
            }
        }
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
       if (!view){
        VisibleElementsGraph graph = new VisibleElementsGraph(model
                .getRootElement());
        int x = getUI().pixelsToX((e.getX()));
        int y = getUI().pixelsToY((e.getY()));

        TextPosition tp = model.getRootElement().getAbsolutePosition();
        int w = model.getRootElement().getWidth();
        int h = model.getRootElement().getHeight();
        if (e.getID() == MouseEvent.MOUSE_CLICKED && x <= tp.getColumn() + w && y <= tp.getLine() + h) {
            this.requestFocusInWindow();
            if (app!=null)
                app.clearSelectedGeos();
            TextPosition p = graph.normalize(new TextPosition(y, x), VisibleElementsGraph.Direction.Down);
            x = p.getColumn();
            y = p.getLine();
            model.setAbsoluteCaretY(y);
            model.setAbsoluteCaretX(x);
            VisibleElement newFocused = graph.findElementByPos(x, y);
            if (newFocused == model.getFocusedElement())
                model.repaint();
            else
                model.setFocusedElement(newFocused);
            //VisibleElement el = model.getRootElement();
            e = new MouseEvent((Component) e.getSource(), e.getID(), e.getWhen(), e.getModifiers(), x,
                    y, e.getClickCount(), e.isPopupTrigger(), e.getButton());
            newFocused.fireMouseEvent(e);

        }
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


}
