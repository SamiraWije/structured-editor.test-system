package ru.ipo.structurededitor.view;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Vector;

import ru.ipo.structurededitor.controller.EditorsRegistry;
import ru.ipo.structurededitor.controller.ModificationVector;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.model.DSLBeansRegistry;
import ru.ipo.structurededitor.view.elements.VisibleElement;
import ru.ipo.structurededitor.view.events.*;

import javax.swing.event.EventListenerList;

/**
 * Корень дерева ячеек
 */
public class StructuredEditorModel {

    public int getAbsoluteCaretX() {
        return absoluteCaretX;
    }

    public void setAbsoluteCaretX(int absoluteCaretX) {
        this.absoluteCaretX = absoluteCaretX;
    }

    public int getAbsoluteCaretY() {
        return absoluteCaretY;
    }

    public void setAbsoluteCaretY(int absoluteCaretY) {
        this.absoluteCaretY = absoluteCaretY;
    }

    private int absoluteCaretX=0;
    private int absoluteCaretY=0;

    private VisibleElement rootElement;
    //private StructuredEditor editor;
    private VisibleElement focusedElement;

    public EditorsRegistry getEditorsRegistry() {
        return editorsRegistry;
    }

    public void setEditorsRegistry(EditorsRegistry editorsRegistry) {
        this.editorsRegistry = editorsRegistry;
        setRootElement(new EditorRenderer(this, o).getRenderResult());
    }

    private EditorsRegistry editorsRegistry;

    private DSLBeansRegistry beansRegistry;

    public DSLBeansRegistry getBeansRegistry() {
        return beansRegistry;
    }

    public void setBeansRegistry(DSLBeansRegistry beansRegistry) {
        this.beansRegistry = beansRegistry;
        setRootElement(new EditorRenderer(this, o).getRenderResult());
    }

    private DSLBean o;

    public StructuredEditorModel(DSLBean o) {
        this(o, new ModificationVector());
    }

    public StructuredEditorModel(DSLBean o, ModificationVector modificationVector) {
       this.o = o;
       setModificationVector(modificationVector);
       editorsRegistry = new EditorsRegistry();
       beansRegistry = new DSLBeansRegistry();

       setRootElement(new EditorRenderer(this, o).getRenderResult());

    }
    public void setObject(DSLBean o) {
        this.o = o;
    }
    public DSLBean getObject() {
        return o;
    }

    public ModificationVector getModificationVector() {
        return modificationVector;
    }

    public void setModificationVector(ModificationVector modificationVector) {

        this.modificationVector = modificationVector;
    }

    private ModificationVector modificationVector;


    private EventListenerList listenerList = new EventListenerList();
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * Добавление слушателя для события "изменение любого свойства класса"
     *
     * @param listener Слушатель
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    /**
     * Добавление слушателя для события "изменение конкретного свойства класса"
     *
     * @param propertyName Имя свойства
     * @param listener     Слушатель
     */
    public void addPropertyChangeListener(String propertyName,
                                          PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    /*public StructuredEditor getEditor() {
        return editor;
    } */

    public void addCaretListener(CaretListener l) {
        listenerList.add(CaretListener.class, l);
    }

    public void removeCaretListener(CaretListener l) {
        listenerList.remove(CaretListener.class, l);
    }

    public void addPopupListener(PopupListener l) {
        listenerList.add(PopupListener.class, l);
    }

    public void removePopupListener(PopupListener l) {
        listenerList.remove(PopupListener.class, l);
    }
    public void addRepaintListener(RepaintListener l) {
        listenerList.add(RepaintListener.class, l);
    }

    public void removeRepaintListener(RepaintListener l) {
        listenerList.remove(RepaintListener.class, l);
    }
    public ListDialog showPopup(Vector<String> filteredPopupList, String longStr, int x, int y) {
        return firePopupShow(new PopupEvent(this, filteredPopupList, longStr, x, y));

    }
    public void showCaret(Display d){

        fireCaretShow(new CaretEvent(this,d));
    }
    public void repaint() {
        fireRepaint();

    }

    protected void fireRepaint() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
                 if (listeners[i]==RepaintListener.class) {
                     // Lazily create the event:
                     /*if (Event == null)
                         fooEvent = new FooEvent(this);*/
                     ((RepaintListener)listeners[i+1]).repaint();
                 }
        }


    }
    protected ListDialog firePopupShow(PopupEvent pe) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
                 if (listeners[i]==PopupListener.class) {
                     // Lazily create the event:
                     /*if (Event == null)
                         fooEvent = new FooEvent(this);*/
                     return ((PopupListener)listeners[i+1]).showPopup(pe);
                 }
        }
        return null;

    }
    protected void fireCaretShow(CaretEvent ce) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
                 if (listeners[i]==CaretListener.class) {
                     // Lazily create the event:
                     /*if (Event == null)
                         fooEvent = new FooEvent(this);*/
                     ((CaretListener)listeners[i+1]).showCaret(ce);
                     //return;
                 }
        }


    }

    public VisibleElement getFocusedElement() {
        return focusedElement;
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return pcs.getPropertyChangeListeners();
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return pcs.getPropertyChangeListeners(propertyName);
    }

    /**
     * Корень отображаемого элемента соответствующего корню дерева ячеек
     *
     * @return
     */
    public VisibleElement getRootElement() {
        return rootElement;
    }

    /*public StructuredEditorUI getUI() {
        if (editor != null)
            return editor.getUI();
        else
            return null;
    } */
    // PropertyChangeSupport

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName,
                                             PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    /* public void setEditor(StructuredEditor editor) {
        this.editor = editor;
    }*/

    /**
     * Установить активный элемент и вызвать всех подписанных слушателей
     *
     * @param focusedElement element to set focus to
     */

    public void setFocusedElement(VisibleElement focusedElement) {
        if (focusedElement == this.focusedElement)
            return;

        // Фокус может иметь только элемент без дочерних элементов
        // Спускаемся к самому вложенному элементу
        if (focusedElement != null) {
            while (focusedElement.getChildrenCount() != 0)
                focusedElement = focusedElement.getChild(0);
        }

        VisibleElement oldValue = this.focusedElement;
        this.focusedElement = focusedElement;
        pcs.firePropertyChange("focusedElement", oldValue, focusedElement);

        if (oldValue != null)
            oldValue.fireFocusChanged(true);
        if (focusedElement != null)
            focusedElement.fireFocusChanged(false);

    }

    public void setRootElement(VisibleElement rootElement) {
        this.rootElement = rootElement;
        setFocusedElement(rootElement);
    }
}