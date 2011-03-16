package ru.ipo.structurededitor;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.TextPosition;
import ru.ipo.structurededitor.view.VisibleElementsGraph;
import ru.ipo.structurededitor.view.elements.VisibleElement;

/**
 * Created by IntelliJ IDEA. User: Ilya Date: 14.01.2010 Time: 22:20:40
 */
public class CaretMovementAction extends AbstractAction {
    /**
     *
     */
    private static final long serialVersionUID = -8218572692421598416L;

    private final VisibleElementsGraph.Direction dir;

    /**
     * @param dir
     */
    public CaretMovementAction(VisibleElementsGraph.Direction dir) {
        this.dir = dir;
    }

    public void actionPerformed(ActionEvent e) {
        final StructuredEditorModel editorModel = ((StructuredEditor) e.getSource())
                .getModel();
        VisibleElementsGraph graph = new VisibleElementsGraph(editorModel
                .getRootElement());

        //VisibleElement neighbour = graph.getNeighbour(editorModel
        //        .getFocusedElement(), dir);
        int x = editorModel.getAbsoluteCaretX(), y = editorModel.getAbsoluteCaretY();
        switch (dir) {
            case Down:
                y++;
                break;
            case Up:
                y--;
                break;
            case Left:
                x--;
                break;
            case Right:
                x++;
                break;
        }
        TextPosition p = graph.normalize(new TextPosition(y, x), dir);
        x = p.getColumn();
        y = p.getLine();
        editorModel.setAbsoluteCaretY(y);
        editorModel.setAbsoluteCaretX(x);
        editorModel.repaint();
        editorModel.setFocusedElement(graph.findElementByPos(x, y));
    }
}
