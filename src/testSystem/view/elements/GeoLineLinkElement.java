package testSystem.view.elements;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.events.GeoSelectionChangedEvent;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 29.03.11
 * Time: 15:21
 * To change this template use File | Settings | File Templates.
 */
public class GeoLineLinkElement extends GeoLinkElement {
    public GeoLineLinkElement(StructuredEditorModel model, GeoLine value) {
        super(model, value);
        typeStr = "Прямая";
        emptyString = "[Выберите прямую на чертеже]";
        setValue(value);
    }

    public GeoLineLinkElement(StructuredEditorModel model) {
        super(model);
    }

    @Override
    public void processGeoSelectionChangedEvent(GeoSelectionChangedEvent e) {
        GeoElement elem = (GeoElement) e.getSelectedGeo();
        if (isFocused() && elem instanceof GeoLine) {
            setValue(elem);
        }
    }
}
