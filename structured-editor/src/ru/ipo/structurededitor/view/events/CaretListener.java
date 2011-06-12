package ru.ipo.structurededitor.view.events;

import java.util.EventListener;

/**
 * Event listener: caret is need to be output
 */
public interface CaretListener extends EventListener {

    public void showCaret(CaretEvent e);
}
