package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.controller.FieldMask;
import ru.ipo.structurededitor.view.editors.FieldEditor;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.elements.BooleanEditorElement;
import ru.ipo.structurededitor.view.elements.VisibleElement;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 20.12.2009
 * Time: 22:36:47
 */
public class BooleanEditor extends FieldEditor {

    //public

    public BooleanEditor(Object o, String fieldName, FieldMask mask, StructuredEditorModel model) {
        super(o, fieldName, mask, model);
        setModificationVector(model.getModificationVector());
        String bool_str = BooleanEditorElement.RUS_FALSE;
        Object val = getValue();
        if (val == null)
            setValue(false);
        else if ((Boolean) val) {
            bool_str = BooleanEditorElement.RUS_TRUE;
        }
        final BooleanEditorElement editorElement = new BooleanEditorElement(model, bool_str);
        editorElement.addPropertyChangeListener("text", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                String val = editorElement.getText();
                if (val.equals(BooleanEditorElement.RUS_TRUE))
                    setValue(true);
                else
                    setValue(false);
            }
        });
        setElement(editorElement);
    }

    @Override
    protected void updateElement() {
        BooleanEditorElement editorElement = (BooleanEditorElement)getElement();
        editorElement.setText((Boolean) getValue() ? BooleanEditorElement.RUS_TRUE : BooleanEditorElement.RUS_FALSE);
    }

}