package ru.ipo.structurededitor.view.events;

import ru.ipo.structurededitor.view.ListDialog;

import java.util.EventListener;

/**
 * Слушатель события: изменен элемент с фокусом
 */
public interface GeoSelectionChangedListener extends EventListener {

    public void geoSelectionChanged(GeoSelectionChangedEvent e);

}
