 package ru.ipo.structurededitor.view.events;

import ru.ipo.structurededitor.view.Display;

import java.util.EventObject;
import java.util.Vector;

/**
 * Event: caret is need to be output
 */
public class CaretEvent extends EventObject {
    private int x;
    private int y;
    private Display d;

    public CaretEvent(Object source, int x, int y, Display d) {
        super(source);
        this.x=x;
        this.y=y;
        this.d=d;
    }

    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }

    public Display getD() {
        return d;
    }
}
