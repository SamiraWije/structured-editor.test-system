package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.controller.FieldMask;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.elements.DoubleEditorElement;
import ru.ipo.structurededitor.view.elements.VisibleElement;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Creat  ed by IntelliJ IDEA.
 * User: Ilya
 * Date: 20.12.2009
 * Time: 22:36:47
 */
public class DoubleEditor extends FieldEditor {

    private DoubleEditorElement editorElement;



    public DoubleEditor(Object o, String fieldName, FieldMask mask) {
        super(o, fieldName, mask);
    }
    @Override
    public VisibleElement createElement(StructuredEditorModel model) {
        setModificationVector(model.getModificationVector());
        String text;
        /*if (EmptyFieldsRegistry.getInstance().isEmpty((DSLBean) getObject(), getFieldName()))
            text = null;
        else {*/
            Object val = getValue();
            if (val == null)
                text = null;
            else
                text = Double.toString((Double) val);
        //}
        editorElement = new DoubleEditorElement(model, text);
        editorElement.addPropertyChangeListener("text", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                try {
                    double val = Double.parseDouble(editorElement.getText());
                    setValue(val);

                } catch (NumberFormatException e) {
                    setValue(null);
                }

            }
        });

        return editorElement;
    }

    @Override
    protected void updateElement() {
        Object val = getValue();
        editorElement.setText(val == null ? "" : val.toString());
    }

}