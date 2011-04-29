package ru.ipo.structurededitor.view.editors;

import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPoint;
import ru.ipo.structurededitor.controller.FieldMask;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.elements.GeoLineLinkElement;
import ru.ipo.structurededitor.view.elements.GeoPointLinkElement;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 20.12.2009
 * Time: 22:36:47
 */
public class GeoPointEditor extends FieldEditor {
    public GeoPointEditor(Object o, String fieldName, FieldMask mask, StructuredEditorModel model) {
        super(o, fieldName, mask, model);
        setModificationVector(model.getModificationVector());
        GeoPoint val=(GeoPoint) getValue();
        final GeoPointLinkElement editorElement;
        editorElement = new GeoPointLinkElement(model,val);
        editorElement.addPropertyChangeListener("text", new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    setValue(editorElement.getValue());
                }
            });
        setElement(editorElement);
    }

    @Override
    protected void updateElement() {
        GeoPointLinkElement editorElement = (GeoPointLinkElement) getElement();
        GeoPoint val=(GeoPoint) getValue();
        editorElement.setValue(val);
    }
}