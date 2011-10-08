package testSystem.view.editors;

import geogebra.kernel.GeoConic;
import ru.ipo.structurededitor.controller.FieldMask;
import ru.ipo.structurededitor.model.EditorSettings;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.editors.FieldEditor;
import testSystem.view.elements.GeoCircleLinkElement;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 20.12.2009
 * Time: 22:36:47
 */
public class GeoCircleEditor extends FieldEditor {
    public GeoCircleEditor(Object o, String fieldName, FieldMask mask, StructuredEditorModel model, EditorSettings settings) {
        super(o, fieldName, mask, model, settings);
        GeoConic val = (GeoConic) getValue();
        final GeoCircleLinkElement editorElement;
        editorElement = new GeoCircleLinkElement(model, val);
        editorElement.addPropertyChangeListener("text", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                setValue(editorElement.getValue());
            }
        });
        setElement(editorElement);
    }

    @Override
    protected void updateElement() {
        GeoCircleLinkElement editorElement = (GeoCircleLinkElement) getElement();
        GeoConic val = (GeoConic) getValue();
        editorElement.setValue(val);
    }
}