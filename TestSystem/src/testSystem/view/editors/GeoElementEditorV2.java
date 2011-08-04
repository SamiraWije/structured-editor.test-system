package testSystem.view.editors;

import geogebra.kernel.*;
import geogebra.main.Application;
import ru.ipo.structurededitor.actions.VisibleElementAction;
import ru.ipo.structurededitor.controller.FieldMask;
import ru.ipo.structurededitor.model.EditorSettings;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.editors.FieldEditor;
import ru.ipo.structurededitor.view.elements.TextElement;
import ru.ipo.structurededitor.view.events.GeoSelectionChangedEvent;
import ru.ipo.structurededitor.view.events.GeoSelectionChangedListener;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 03.08.11
 * Time: 17:08
 */
public class GeoElementEditorV2 extends FieldEditor {

    private final VisibleElementAction setNullAction;

    public GeoElementEditorV2(Object o, String fieldName, FieldMask mask, StructuredEditorModel model, EditorSettings settings) {
        super(o, fieldName, mask, model, settings);

        final TextElement editorElement = new TextElement(model);
        setElement(editorElement);

        editorElement.setNullText(getNullTextByType(getFieldType()));

        setNullAction = new VisibleElementAction(getNullActionText(getFieldType()), "properties.png", "control SPACE") {
            @Override
            public void run(StructuredEditorModel model) {
                setValue(null);
                updateElement();
            }
        };

        //add listener for geogebra selection changed
        Application app = (Application) getModel().getApp();
        if (app != null) {
            app.getEuclidianView().getEuclidianController().addGeoSelectionChangedListener(new GeoSelectionChangedListener() {
                public void geoSelectionChanged(GeoSelectionChangedEvent e) {
                    GeoElement elem = (GeoElement) e.getSelectedGeo();
                    if (elem != null && getElement().isFocused() && elem.getClass().equals(getFieldType())) {
                        setValue(elem);
                        updateElement();
                    }

                    //TODO here is a memory leak because listeners of this type are never removed
                }
            });
        }

        updateElement();
    }

    @Override
    protected void updateElement() {
        GeoElement value = (GeoElement) getValue();

        TextElement editorElement = (TextElement) getElement();

        if (value == null) {
            editorElement.setText(null);
            editorElement.removeAction(setNullAction);
        } else {
            editorElement.setText(wrap(value.getCaption(), getFieldType()));
            editorElement.addAction(setNullAction);
        }
    }

    private String getNullActionText(Class<?> fieldType) {
        if (GeoAngle.class.equals(fieldType))
            return "Выбрать другой угол";
        else if (GeoLine.class.equals(fieldType))
            return "Выбрать другую прямую";
        else if (GeoSegment.class.equals(fieldType))
            return "Выбрать другой отрезок";
        else if (GeoPoint.class.equals(fieldType))
            return "Выбрать другую точку";
        else if (GeoConic.class.equals(fieldType))
            return "Выбрать другую окружность";
        else /*if (GeoElement.class.equals(fieldType))*/
            return "Выбрать другой элемент";
    }

    private String getNullTextByType(Class<?> fieldType) {
        String text;
        if (GeoAngle.class.equals(fieldType))
            text = "угол";
        else if (GeoLine.class.equals(fieldType))
            text = "прямую";
        else if (GeoSegment.class.equals(fieldType))
            text = "отрезок";
        else if (GeoPoint.class.equals(fieldType))
            text = "точку";
        else if (GeoConic.class.equals(fieldType))
            text = "окружность";
        else /*if (GeoElement.class.equals(fieldType))*/
            text = "элемент";

        return "[Выберите " + text + " на чертеже]";
    }

    private String wrap(String caption, Class<?> fieldType) {
        if (GeoAngle.class.equals(fieldType))
            return "∠" + caption;
        else if (GeoLine.class.equals(fieldType))
            return "/" + caption;
        else if (GeoSegment.class.equals(fieldType))
            return "|" + caption;
        else if (GeoPoint.class.equals(fieldType))
            return "." + caption;
        else if (GeoConic.class.equals(fieldType))
            return "∘" + caption;
        else /*if (GeoElement.class.equals(fieldType))*/
            return caption;
    }

}