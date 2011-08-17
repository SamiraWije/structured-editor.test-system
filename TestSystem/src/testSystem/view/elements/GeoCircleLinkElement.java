package testSystem.view.elements;

import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.events.GeoSelectionChangedEvent;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 29.03.11
 * Time: 15:21
 */
public class GeoCircleLinkElement extends GeoLinkElement {
    public GeoCircleLinkElement(StructuredEditorModel model, GeoConic value) {
        super(model, value);
        //typeStr = "Точка";
        typeStr = "окр. ";
        setNullText("[Выберите окружность на чертеже]");
        setValue(value);
    }

    public GeoCircleLinkElement(StructuredEditorModel model) {
        super(model);
    }

    @Override
    public void processGeoSelectionChangedEvent(GeoSelectionChangedEvent e) {
        GeoElement elem = (GeoElement) e.getSelectedGeo();
        if (isFocused() && elem instanceof GeoConic) {
            setValue(elem);
        }
    }
}
