package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.elements.TextEditorElement;
import ru.ipo.structurededitor.view.elements.VisibleElement;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 14.01.2010
 * Time: 15:42:40
 */
public class IntegerEditor extends FieldEditor {
    //TODO remove the class

    public IntegerEditor(Object o, String fieldName) {
        super(o, fieldName);
    }

    @Override
    public VisibleElement createElement(StructuredEditorModel model) {
        final TextEditorElement editorElement = new TextEditorElement(model, (String) getValue());
        editorElement.addPropertyChangeListener("text", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                try {
                    int val = Integer.parseInt(editorElement.getText());
                    setValue(val);
                } catch (NumberFormatException e) {
                    //do nothing
                }
            }
        });
        return editorElement;
    }

    @Override
    protected void updateElement() {
        //TODO implement
    }

}