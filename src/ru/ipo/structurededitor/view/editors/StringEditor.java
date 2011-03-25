package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.controller.FieldMask;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.elements.TextEditorElement;
import ru.ipo.structurededitor.view.elements.VisibleElement;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 20.12.2009
 * Time: 22:36:47
 */
public class StringEditor extends FieldEditor {
    public StringEditor(Object o, String fieldName, FieldMask mask, boolean singleLined, StructuredEditorModel model) {
        super(o, fieldName, mask, singleLined, model);
        setModificationVector(model.getModificationVector());
        String str;
        str=(String) getValue();
        final TextEditorElement editorElement;
        editorElement = new TextEditorElement(model,str,singleLined);
        editorElement.addPropertyChangeListener("text", new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    setValue(editorElement.getText());
                }
            });
        setElement(editorElement);
    }

    @Override
    protected void updateElement() {
        TextEditorElement editorElement = (TextEditorElement) getElement();
        editorElement.setText((String) getValue());
    }
}