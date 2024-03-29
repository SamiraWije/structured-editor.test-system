package testSystem.view.elements;

import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoElement;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.events.GeoSelectionChangedEvent;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 29.03.11
 * Time: 15:21
 */
public class GeoAngleLinkElement extends GeoLinkElement {
    public GeoAngleLinkElement(StructuredEditorModel model, GeoAngle value) {
        super(model, value);
        //typeStr = "Точка";
        typeStr = "∠";
        setNullText("[Выберите угол на чертеже]");
        setValue(value);
    }

    public GeoAngleLinkElement(StructuredEditorModel model) {
        super(model);
    }

    @Override
    public void processGeoSelectionChangedEvent(GeoSelectionChangedEvent e) {
        GeoElement elem = (GeoElement) e.getSelectedGeo();
        if (isFocused() && elem instanceof GeoAngle) {
            setValue(elem);
        }
    }
}
