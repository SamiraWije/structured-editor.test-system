package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.controller.EmptyFieldsRegistry;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.view.editors.FieldEditor;
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

    private IntEditorElement editorElement;

    public IntEditor(Object o, String fieldName) {
        super(o, fieldName);
    }

    public IntEditor(Object o, String fieldName, int index) {
        super(o, fieldName, index);
    }

    @Override
    public VisibleElement createElement(StructuredEditorModel model) {
        String text;
        Object val = getValue();
        if (EmptyFieldsRegistry.getInstance().isEmpty((DSLBean) getObject(), getFieldName()))
            text = null;
        else {
            if (val == null)
                text = "";
            else
                text = Integer.toString((Integer) val);
        }
        editorElement = new IntEditorElement(model, text);
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
        /*editorElement.addPropertyChangeListener("refresh", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                String text;
                VisibleElement.RefreshProperties rp = (VisibleElement.RefreshProperties) evt.getNewValue();
                setEmpty(rp.getEmpty());
                setObject(rp.getObject());
                Object val = getValue();
                if (val == null)
                    text = "";
                else
                    text = Integer.toString((Integer) val);
                editorElement.setText(text);
                editorElement.resetPosition();
                editorElement.resumeRefresh();

            }
        }); */

        return editorElement;
    }

    @Override
    protected void updateElement() {
        Object val = getValue();
        editorElement.setText(val == null ? "" : val.toString());
    }
}