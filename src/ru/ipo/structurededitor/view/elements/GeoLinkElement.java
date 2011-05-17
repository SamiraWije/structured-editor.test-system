package ru.ipo.structurededitor.view.elements;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPoint;
import ru.ipo.structurededitor.view.StructuredEditorModel;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 29.03.11
 * Time: 14:50
 * To change this template use File | Settings | File Templates.
 */
public class GeoLinkElement extends TextElement {
    protected String typeStr;

    public GeoElement getValue() {
        return value;
    }

    public void setValue(GeoElement value) {
        this.value = value;
        if (value != null)
            setText(typeStr + " " + value.getCaption());
        else
            setText(emptyString);
    }

    private GeoElement value = null;

    public GeoLinkElement(StructuredEditorModel model, GeoElement value) {
        super(model);
        emptyString = "[Выберите элемент на чертеже]";
        setValue(value);
    }

    public GeoLinkElement(StructuredEditorModel model) {

        super(model);
    }


}
