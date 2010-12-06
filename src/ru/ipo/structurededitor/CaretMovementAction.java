package ru.ipo.structurededitor;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ru.ipo.structurededitor.view.StructuredEditorModel;
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

        VisibleElement neighbour = graph.getNeighbour(editorModel
                .getFocusedElement(), dir);
        editorModel.setFocusedElement(neighbour);
    }
}
