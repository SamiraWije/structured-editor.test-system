package ru.ipo.structurededitor.controller;

import javax.swing.event.EventListenerList;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 23.12.10
 * Time: 15:17
 * To change this template use File | Settings | File Templates.
 */
public class ModificationEventSupport {

    private EventListenerList listenerList = new EventListenerList();

    public void addModificationListener(ModificationListener l) {
        listenerList.add(ModificationListener.class, l);
    }

    public void removeModificationListener(ModificationListener l) {
        listenerList.remove(ModificationListener.class, l);
    }

    protected void fireModification() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ModificationListener.class) {
                // Lazily create the event:
                /*if (Event == null)
             fooEvent = new FooEvent(this);*/
                ((ModificationListener) listeners[i + 1]).modificationPerformed();
            }
        }
    }
}
