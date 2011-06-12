package ru.ipo.structurededitor.view.events;

import java.util.EventObject;

/**
 * Событие: поменялся выделенный элемент
 */
public class FocusChangedEvent extends EventObject {

    public FocusChangedEvent(Object source) {
        super(source);
    }
}
