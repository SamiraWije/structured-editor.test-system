package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.controller.FieldMask;
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

    public VoidEditor(Object o, String fieldName, FieldMask mask, StructuredEditorModel model) {
        super(o, fieldName,mask,model);
        setElement(new TextElement(model, "VOID"));
    }


    @Override
    protected void updateElement() {
        //do nothing
    }
}
