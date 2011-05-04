package ru.ipo.structurededitor.view;

import ru.ipo.structurededitor.controller.EditorsRegistry;
import ru.ipo.structurededitor.model.*;
import ru.ipo.structurededitor.view.editors.ArrayEditor;
import ru.ipo.structurededitor.view.editors.FieldEditor;
import ru.ipo.structurededitor.view.elements.CompositeElement;
import ru.ipo.structurededitor.view.elements.TextElement;
import ru.ipo.structurededitor.view.elements.VisibleElement;

/**
 * Класс, генерирующий графические элементы
 */
public class EditorRenderer {

    private final EditorsRegistry reg;
    private final VisibleElement renderResult;
    private final StructuredEditorModel model;

    /**
     * Конструктор панели для редактирования всего JavaBean в целом Не отдельной
     * ячейки, а всего целиком!
     *
     * @param model
     * @param editableBean
     */
    @SuppressWarnings({"JavaDoc"})
    public EditorRenderer(StructuredEditorModel model, DSLBean editableBean) {
        this.model = model;
        reg = model.getEditorsRegistry();


        Cell layout = editableBean.getLayout();

        renderResult = render(layout, editableBean);
        //model.setFocusedElement(renderResult);
    }

    public VisibleElement getRenderResult() {
        return renderResult;
    }

    private VisibleElement render(Cell layout, DSLBean editableBean) {

        if (layout instanceof ConstantCell) {
            ConstantCell cell = (ConstantCell) layout;
            return new TextElement(model, cell.getText());
        } else if (layout instanceof FieldCell) {
            FieldCell fieldCell = (FieldCell) layout;
            FieldEditor ed = reg.getEditor(editableBean.getClass(), fieldCell
                    .getFieldName(), editableBean, null, fieldCell.getSingleLined(), model);
            return ed.getElement();
        } else if (layout instanceof Vert || layout instanceof Horiz) {
            final Cell[] cells;
            if (layout instanceof Vert)
                cells = ((Vert) layout).getCells();
            else
                cells = ((Horiz) layout).getCells();

            CompositeElement res = new CompositeElement(model,
                    layout instanceof Vert ? CompositeElement.Orientation.Vertical
                            : CompositeElement.Orientation.Horizontal);

            for (Cell cell : cells)
                res.add(render(cell, editableBean));

            return res;
        } else if (layout instanceof VertArray || layout instanceof HorizArray) {
            //ArrayEditor ed;
            if (layout instanceof VertArray) {
                VertArray vertArray = (VertArray) layout;
                ArrayEditor ed = new ArrayEditor(editableBean, vertArray.getFieldName(),
                        CompositeElement.Orientation.Vertical, vertArray.getSpaceChar(), vertArray.getSingleLined(),
                        model);
                return ed.getElement();
            } else {
                HorizArray horizArray = (HorizArray) layout;
                ArrayEditor ed = new ArrayEditor(editableBean, horizArray.getFieldName(),
                        CompositeElement.Orientation.Horizontal, horizArray.getSpaceChar(), horizArray.getSingleLined(),
                        model);
                return ed.getElement();
            }


            // ArrayEditor res = new ArrayEditor(model,
            //    layout instanceof VertArray ? CompositeElement.Orientation.Vertical
            //               : CompositeElement.Orientation.Horizontal);
            // int i=0;
            // for (Cell cell : cells){
            //    res.add(render((ArrayItem)cell, editableBean,i));
            //   i++;
            // }
            // return res;
        } /*else if (layout instanceof ArrayItem) {
        ArrayItem ai = new ArrayItem()
      ArrayItem arrayItem = (ArrayItem) layout;
      FieldEditor ed = reg.getEditor(editableBean.getClass(), arrayItem
          .getFieldName(), editableBean);
      return ed.createElement(model);
    }   */

        throw new Error("Surprise: unknown layout in EditorRenderer.render()");
    }
}
