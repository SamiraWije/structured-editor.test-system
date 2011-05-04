package ru.ipo.structurededitor.view.events;

import java.util.EventObject;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 27.03.11
 * Time: 11:52
 * To change this template use File | Settings | File Templates.
 */
public class GeoSelectionChangedEvent extends EventObject {
    public Object getSelectedGeo() {
        return selectedGeo;
    }

    Object selectedGeo;

    public GeoSelectionChangedEvent(Object source, Object selectedGeo) {
        super(source);
        this.selectedGeo = selectedGeo;
    }
}
