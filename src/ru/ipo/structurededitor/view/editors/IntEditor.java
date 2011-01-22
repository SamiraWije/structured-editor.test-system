package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.controller.FieldMask;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.elements.IntEditorElement;
import ru.ipo.structurededitor.view.elements.VisibleElement;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 20.12.2009
 * Time: 22:36:47
 */
public class IntEditor extends FieldEditor {


    public IntEditor(Object o, String fieldName, FieldMask mask, StructuredEditorModel model) {
        super(o, fieldName, mask, model);
        setModificationVector(model.getModificationVector());
        String text;
        Object val = getValue();
        if (val == null)
            text = "";
        else
            text = Integer.toString((Integer) val);
        final IntEditorElement editorElement = new IntEditorElement(model, text);
        editorElement.addPropertyChangeListener("text", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                try {
                    int val = Integer.parseInt(editorElement.getText());
                    setValue(val);
                } catch (NumberFormatException e) {
                    setValue(null);
                }

            }
        });
        setElement(editorElement);
    }

    @Override
    protected void updateElement() {
        IntEditorElement editorElement = (IntEditorElement)getElement();
        Object val = getValue();
        editorElement.setText(val == null ? "" : val.toString());
    }
}