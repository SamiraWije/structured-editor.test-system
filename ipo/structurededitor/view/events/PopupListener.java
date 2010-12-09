package ru.ipo.structurededitor.view.events;

import ru.ipo.structurededitor.view.ListDialog;

import java.util.EventListener;

/**
 * Слушатель события: изменен элемент с фокусом
 */
public interface PopupListener extends EventListener {

    public ListDialog showPopup(PopupEvent e);
    /*public void updatePopup(PopupEvent e);
    public boolean isPopupVisible();*/


}
