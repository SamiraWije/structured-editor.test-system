package ru.ipo.structurededitor.view.events;

import java.util.EventListener;

/**
 * Слушатель события: изменен элемент с фокусом
 */
public interface FocusChangedEventListener extends EventListener {

    void focusChanged(FocusChangedEvent e);

}
