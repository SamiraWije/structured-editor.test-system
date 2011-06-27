package testSystem.view.elements;

import geogebra.kernel.GeoElement;
import geogebra.main.Application;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.elements.TextElement;
import ru.ipo.structurededitor.view.events.GeoSelectionChangedEvent;
import ru.ipo.structurededitor.view.events.GeoSelectionChangedListener;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 29.03.11
 * Time: 14:50
 * To change this template use File | Settings | File Templates.
 */
public class GeoLinkElement extends TextElement {
    protected String typeStr,typeStr1="";

    public GeoElement getValue() {
        return value;
    }

    public void setValue(GeoElement value) {
        this.value = value;
        if (value != null)
            setText(typeStr + value.getCaption()+typeStr1);
        else
            setText(emptyString);
    }

    private GeoElement value = null;

    public GeoLinkElement(StructuredEditorModel model, GeoElement value) {
        super(model);
        emptyString = "[Выберите элемент на чертеже]";
        Application app = (Application)getModel().getApp();
        if (app!=null){
            app.getEuclidianView().getEuclidianController().addGeoSelectionChangedListener(new GeoSelectionChangedListener() {
                public void geoSelectionChanged(GeoSelectionChangedEvent e) {
                //Object selection = e.getSelectedGeo();
                    processGeoSelectionChangedEvent(e);
                }
            });
        }
        setValue(value);
    }


    public void processGeoSelectionChangedEvent(GeoSelectionChangedEvent e) {

    }
    public GeoLinkElement(StructuredEditorModel model) {

        super(model);
    }


}
