 package ru.ipo.structurededitor.view.events;

import ru.ipo.structurededitor.view.Display;

import java.util.EventObject;
import java.util.Vector;

/**
 * Event: caret is need to be output
 */
public class CaretEvent extends EventObject {
    private Display d;

    public CaretEvent(Object source, Display d) {
        super(source);
        this.d=d;
    }

    public Display getD() {
        return d;
    }
}
