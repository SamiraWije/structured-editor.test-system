package testSystem.view.editors;

import geogebra.kernel.GeoSegment;
import ru.ipo.structurededitor.controller.FieldMask;
import ru.ipo.structurededitor.model.EditorSettings;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.editors.FieldEditor;
import testSystem.view.elements.GeoSegmentLinkElement;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 20.12.2009
 * Time: 22:36:47
 */
public class GeoSegmentEditor extends FieldEditor {
    public GeoSegmentEditor(Object o, String fieldName, FieldMask mask, StructuredEditorModel model, EditorSettings settings) {
        super(o, fieldName, mask, model, settings);
        GeoSegment val = (GeoSegment) getValue();
        final GeoSegmentLinkElement editorElement;
        editorElement = new GeoSegmentLinkElement(model, val);
        editorElement.addPropertyChangeListener("text", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                setValue(editorElement.getValue());
            }
        });
        setElement(editorElement);
    }

    @Override
    protected void updateElement() {
        GeoSegmentLinkElement editorElement = (GeoSegmentLinkElement) getElement();
        GeoSegment val = (GeoSegment) getValue();
        editorElement.setValue(val);
    }
}