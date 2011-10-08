package testSystem.view.elements;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoSegment;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.events.GeoSelectionChangedEvent;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 29.03.11
 * Time: 15:21
 */
public class GeoSegmentLinkElement extends GeoLinkElement {
    public GeoSegmentLinkElement(StructuredEditorModel model, GeoSegment value) {
        super(model, value);
        //typeStr = "Точка";
        typeStr = "[";
        typeStr1= "]";
        setNullText("[Выберите отрезок на чертеже]");
        setValue(value);
    }

    public GeoSegmentLinkElement(StructuredEditorModel model) {
        super(model);
    }

    @Override
    public void processGeoSelectionChangedEvent(GeoSelectionChangedEvent e) {
        GeoElement elem = (GeoElement) e.getSelectedGeo();
        if (isFocused() && elem instanceof GeoSegment) {
            setValue(elem);
        }
    }
}
