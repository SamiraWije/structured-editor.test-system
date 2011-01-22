package ru.ipo.structurededitor.view.events;

import ru.ipo.structurededitor.view.elements.ComboBoxTextEditorElement;

import java.util.EventObject;
import java.util.Vector;

/**
 * Событие: поменялся выделенный элемент
 */
public class PopupEvent extends EventObject {
    private Vector<String> filteredPopupList;
    private String longStr;
    private int x;
    private int y;
    //private ComboBoxTextEditorElement cmb;
    public PopupEvent(Object source, Vector<String> filteredPopupList,
                      String longStr, int x, int y/*, ComboBoxTextEditorElement cmb*/) {
        super(source);
        this.filteredPopupList=filteredPopupList;
        this.longStr=longStr;
        this.x=x;
        this.y=y;
        //this.cmb=cmb;
    }
    public Vector<String> getFilteredPopupList(){
        return filteredPopupList;
    }
    public String getLongStr(){
        return longStr;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }

    /*public ComboBoxTextEditorElement getCmb() {
        return cmb;
    } */
}
