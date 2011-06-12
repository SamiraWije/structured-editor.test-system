package ru.ipo.structurededitor.controller;

import java.util.EventListener;

/**
 * Editor repaint event listener
 */
public interface ModificationListener extends EventListener {

    public void modificationPerformed();

}
