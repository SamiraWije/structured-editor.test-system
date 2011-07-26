package testSystem.view.editors;

import geogebra.kernel.GeoElement;
import testSystem.view.elements.GeoLineLinkElement;
import testSystem.view.elements.GeoLinkElement;
import ru.ipo.structurededitor.controller.FieldMask;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.editors.FieldEditor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 20.12.2009
 * Time: 22:36:47
 */
public class GeoElementEditor extends FieldEditor {
    public GeoElementEditor(Object o, String fieldName, FieldMask mask, StructuredEditorModel model) {
        super(o, fieldName, mask, model);
        setModificationVector(model.getModificationVector());
        GeoElement val = (GeoElement) getValue();
        final GeoLinkElement editorElement;
        editorElement = new GeoLinkElement(model, val);
        editorElement.addPropertyChangeListener("text", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                setValue(editorElement.getValue());
            }
        });
        setElement(editorElement);
    }

    @Override
    protected void updateElement() {
        GeoLinkElement editorElement = (GeoLineLinkElement) getElement();
        GeoElement val = (GeoElement) getValue();
        editorElement.setValue(val);
    }
}