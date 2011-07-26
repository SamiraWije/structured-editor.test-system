package testSystem.view.editors;

import geogebra.kernel.GeoLine;
import ru.ipo.structurededitor.model.EditorSettings;
import testSystem.view.elements.GeoLineLinkElement;
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
public class GeoLineEditor extends FieldEditor {
    public GeoLineEditor(Object o, String fieldName, FieldMask mask, StructuredEditorModel model, EditorSettings settings) {
        super(o, fieldName, mask, model, settings);
        setModificationVector(model.getModificationVector());
        GeoLine val = (GeoLine) getValue();
        final GeoLineLinkElement editorElement;
        editorElement = new GeoLineLinkElement(model, val);
        editorElement.addPropertyChangeListener("text", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                setValue(editorElement.getValue());
            }
        });
        setElement(editorElement);
    }

    @Override
    protected void updateElement() {
        GeoLineLinkElement editorElement = (GeoLineLinkElement) getElement();
        GeoLine val = (GeoLine) getValue();
        editorElement.setValue(val);
    }
}