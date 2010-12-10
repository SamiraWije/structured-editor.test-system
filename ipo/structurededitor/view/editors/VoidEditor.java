package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.elements.VisibleElement;
import ru.ipo.structurededitor.view.elements.TextElement;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 03.01.2010
 * Time: 23:02:07
 */
public class VoidEditor extends FieldEditor {

    public VoidEditor(Object o, String fieldName) {
        super(o, fieldName);
    }

    public VoidEditor(Object o, String fieldName, int index) {
        this(o, fieldName);
    }

    @Override
    public VisibleElement createElement(StructuredEditorModel model) {
        return new TextElement(model, "VOID");
    }

    @Override
    protected void updateElement() {
        //do nothing
    }
}
