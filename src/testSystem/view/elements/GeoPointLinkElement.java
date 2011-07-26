package testSystem.view.elements;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.events.GeoSelectionChangedEvent;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 29.03.11
 * Time: 15:21
 * To change this template use File | Settings | File Templates.
 */
public class GeoPointLinkElement extends GeoLinkElement {
    public GeoPointLinkElement(StructuredEditorModel model, GeoPoint value) {
        super(model, value);
        //typeStr = "Точка";
        typeStr = "";
        emptyString = "[Выберите точку на чертеже]";
        setValue(value);
    }

    public GeoPointLinkElement(StructuredEditorModel model) {
        super(model);
    }

    @Override
    public void processGeoSelectionChangedEvent(GeoSelectionChangedEvent e) {
        GeoElement elem = (GeoElement) e.getSelectedGeo();
        if (isFocused() && elem instanceof GeoPoint) {
            setValue(elem);
        }
    }
}
