package ru.ipo.structurededitor.view.editors;

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

    private TextEditorElement editorElement;

    public StringEditor(Object o, String fieldName) {
        super(o, fieldName);
    }

    public StringEditor(Object o, String fieldName, int index) {
        super(o, fieldName, index);
    }

    @Override
    public VisibleElement createElement(StructuredEditorModel model) {
        setModificationVector(model.getModificationVector());
        String str;
        /*if (EmptyFieldsRegistry.getInstance().isEmpty((DSLBean)getObject(), getFieldName()))
            str=null;
        else*/
            str=(String) getValue();
        editorElement = new TextEditorElement(model,str);
        editorElement.addPropertyChangeListener("text", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                setValue(editorElement.getText());
            }
        });
        /*
        editorElement.addPropertyChangeListener("refresh", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                String text;
                VisibleElement.RefreshProperties rp = (VisibleElement.RefreshProperties) evt.getNewValue();
                setEmpty(rp.getEmpty());
                setObject(rp.getObject());
                Object val = getValue();
                if (val == null)
                    text = "";
                else
                    text = (String) val;
                editorElement.setText(text);
                editorElement.resetPosition();
                editorElement.resumeRefresh();
            }
        }); */

        return editorElement;
    }

    @Override
    protected void updateElement() {
        editorElement.setText((String) getValue());
    }
}