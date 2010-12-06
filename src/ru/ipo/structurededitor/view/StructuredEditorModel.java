package ru.ipo.structurededitor.view;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import ru.ipo.structurededitor.StructuredEditor;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.view.elements.VisibleElement;

/**
 * Корень дерева ячеек
 */
public class StructuredEditorModel {

    private VisibleElement rootElement;
    private StructuredEditor editor;
    private VisibleElement focusedElement;

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

    public StructuredEditor getEditor() {
        return editor;
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

    public StructuredEditorUI getUI() {
        if (editor != null)
            return editor.getUI();
        else
            return null;
    }

    // PropertyChangeSupport

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName,
                                             PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    public void setEditor(StructuredEditor editor) {
        this.editor = editor;
    }

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