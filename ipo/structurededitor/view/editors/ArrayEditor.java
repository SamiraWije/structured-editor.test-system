package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.controller.EditorsRegistry;
import ru.ipo.structurededitor.controller.Modification;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.view.EditorRenderer;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.elements.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 29.07.2010
 * Time: 13:17:50
 * To change this template use File | Settings | File Templates.
 */
public class ArrayEditor extends FieldEditor {
    public ArrayEditor(Object o, String fieldName, CompositeElement.Orientation orientation, char spaceChar) {
        super(o, fieldName);
        this.orientation = orientation;
        final EditorsRegistry<FieldEditor> reg;
        reg = EditorsRegistry.getInstance(FieldEditor.class);
        @SuppressWarnings("unchecked")
        FieldEditor ed = reg.getEditor((Class<? extends DSLBean>) getObject().getClass(), fieldName, getObject());
        EditorClass = ed.getClass();
        this.spaceChar = spaceChar;
    }

    private ArrayElement arrayElement;
    private Vector<FieldEditor> editors = new Vector<FieldEditor>();

    private CompositeElement.Orientation orientation;
    private Class<? extends FieldEditor> EditorClass;
    private char spaceChar;

    private FieldEditor createEditorInstance(int Index) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        final Constructor<? extends FieldEditor> c = EditorClass.getConstructor(Object.class, String.class,
                int.class);
        return c.newInstance(getObject(), getFieldName(), Index);
    }

    /**
     * Reallocates an array with a new size, and copies the contents
     * of the old array to the new array.
     *
     * @param oldArray the old array, to be reallocated.
     * @param newSize  the new array size.
     * @return A new array with the same contents.
     */
    public static Object resizeArray(Object oldArray, int newSize) {
        try {
            int oldSize = java.lang.reflect.Array.getLength(oldArray);
            Class elementType = oldArray.getClass().getComponentType();
            Object newArray = java.lang.reflect.Array.newInstance(
                    elementType, newSize);
            int preserveLength = Math.min(oldSize, newSize);
            if (preserveLength > 0) {
                System.arraycopy(oldArray, 0, newArray, 0, preserveLength);
            }
            return newArray;
        } catch (Exception e) {
            System.out.println("Error in array resizing! " + e);
        }
        return null;
    }

    public static Object delItem(Object oldArray, int index) {
        try {
            int size = Array.getLength(oldArray);
            Class elementType = oldArray.getClass().getComponentType();
            Object newArray = Array.newInstance(
                    elementType, size);
            for (int i = 0; i < size; i++) {
                Object o = Array.get(oldArray, i);
                if (i != index && o != null)
                    Array.set(newArray, i, o);
            }
            return newArray;
        } catch (Exception e) {
            System.out.println("Error in array resizing! " + e);
        }
        return null;
    }

    private Object reserveOldArr(Object arr) {
        int countItems = Array.getLength(arr);
        Class elementType = arr.getClass().getComponentType();
        Object oldArr = java.lang.reflect.Array.newInstance(
                elementType, countItems);
        System.arraycopy(arr, 0, oldArr, 0, countItems);
        return oldArr;
    }

    @Override
    public VisibleElement createElement(final StructuredEditorModel model) {
        arrayElement = new ArrayElement(model, orientation, spaceChar);
        setModificationVector(model.getModificationVector());
        updateElement();
        /*try {
            Object val = getValue();


            //TODO review the hack about zero length arrays
            if (val == null || Array.getLength(val) == 0) {
                FieldEditor ed = createEditorInstance(0);
                editors.add(ed);
                arrayElement.add(ed.createElement(model));
                //arrayElement.add(new TextElement(model,"Пустой массив"));
                PropertyDescriptor pd = new PropertyDescriptor(getFieldName(), getObject().getClass());
                Method wm = pd.getWriteMethod();
                Object arr = Array.newInstance(pd.getPropertyType().getComponentType(), 1);
                wm.invoke(getObject(), arr);
            } else {
                for (int i = 0; i < Array.getLength(val); i++) {
                    FieldEditor ed = createEditorInstance(i);
                    editors.add(ed);
                    arrayElement.add(ed.createElement(model));
                }
            }
        } catch (Exception e) {
            throw new Error("Failed to create editor for array item: ", e);
        }

        arrayElement.addPropertyChangeListener("refresh", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                VisibleElement.RefreshProperties rp = (VisibleElement.RefreshProperties) evt.getNewValue();
                setEmpty(rp.getEmpty());
                setObject(rp.getObject());
                Object val = getValue();
                int count = arrayElement.getChildrenCount();
                if (count > 0)
                    for (int i = 0; i < count; i++)
                        arrayElement.remove(0);

                try {
                    for (int i = 0; i < Array.getLength(val); i++) {
                        FieldEditor ed = createEditorInstance(i);
                        editors.add(ed);
                        arrayElement.add(ed.createElement(model));
                    }
                } catch (Exception e) {
                    System.out.println("Error in array updating! " + e);
                }


            }
        }); */
        arrayElement.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {

            }

            public void buttonDelete() {
                try {
                    int countItems = arrayElement.getChildrenCount();
                    if (countItems > 1) {

                        VisibleElement itemToDelete;

                        itemToDelete = model.getFocusedElement();
                        while (!itemToDelete.getParent().equals(arrayElement))
                            itemToDelete = itemToDelete.getParent();

                        int oldIndex = arrayElement.getChildIndex(itemToDelete);
                        editors.remove(oldIndex);
                        arrayElement.remove(oldIndex);

                        if (oldIndex == countItems - 1) {
                            int newIndex = oldIndex - 1;
                            model.setFocusedElement(arrayElement.getChild(newIndex));
                        }


                        //Deleting the array item
                        PropertyDescriptor pd = new PropertyDescriptor(getFieldName(), getObject().getClass());
                        Method wm = pd.getWriteMethod();
                        Method rm = pd.getReadMethod();
                        Object arr = reserveOldArr(rm.invoke(getObject()));
                        Object oldArr = reserveOldArr(arr);

                        for (int i = oldIndex; i <= countItems - 2; i++) {
                            Object o = Array.get(arr, i + 1);
                            if (o != null) {
                                Array.set(arr, i, o);
                            } else {
                                arr = delItem(arr, i);
                            }
                            editors.get(i).setIndex(i + 1);
                            wm.invoke(getObject(), arr);
                        }
                        arr = resizeArray(arr, countItems - 1);
                        wm.invoke(getObject(), arr);
                        if (oldIndex < countItems - 1)
                            model.setFocusedElement(arrayElement.get(oldIndex));
                       new Modification((DSLBean)getObject(),getFieldName(),oldArr,arr,-1);

                    }
                } catch (Exception e) {
                    throw new Error("Failed to delete an array item: ", e);
                }
            }


            public void buttonInsert() {
                try {
                    int countItems = arrayElement.getChildrenCount();
                    //Adding a new element
                    VisibleElement currentItem = model.getFocusedElement();
                    while (!currentItem.getParent().equals(arrayElement))
                        currentItem = currentItem.getParent();
                    int oldIndex = arrayElement.getChildIndex(currentItem);
                    FieldEditor ed = createEditorInstance(oldIndex);
                    VisibleElement newItem = ed.createElement(model);
                    arrayElement.add(newItem, oldIndex);
                    editors.insertElementAt(ed, oldIndex);

                    //Inserting the array item
                    PropertyDescriptor pd = new PropertyDescriptor(getFieldName(), getObject().getClass());
                    Method wm = pd.getWriteMethod();
                    Method rm = pd.getReadMethod();
                    Object arr = rm.invoke(getObject());
                    Object oldArr = reserveOldArr(arr);
                    arr = resizeArray(arr, countItems + 1);

                    for (int i = countItems; i >= oldIndex + 1; i--) {
                        Object o = Array.get(arr, i - 1);
                        if (o != null) {
                            Array.set(arr, i, o);
                        } else {
                            arr = delItem(arr, i);

                        }
                        wm.invoke(getObject(), arr);
                        editors.get(i).setIndex(i - 1);
                    }
                    arr = delItem(arr, oldIndex);
                    wm.invoke(getObject(), arr);
                    editors.get(oldIndex).updateElement();
                    model.setFocusedElement(newItem);
                    new Modification((DSLBean)getObject(),getFieldName(),oldArr,arr,-1);

                } catch (Exception e) {
                    throw new Error("Failed to insert an array item: ", e);
                }
            }

            public void buttonEnter() {
                try {
                    int countItems = arrayElement.getChildrenCount();
                    //Adding a new element
                    VisibleElement currentItem;
                    currentItem = model.getFocusedElement();
                    while (!currentItem.getParent().equals(arrayElement))
                        currentItem = currentItem.getParent();
                    int oldIndex = arrayElement.getChildIndex(currentItem);
                    FieldEditor ed = createEditorInstance(oldIndex + 1);
                    editors.add(oldIndex + 1, ed);
                    VisibleElement newItem = ed.createElement(model);
                    arrayElement.add(newItem, oldIndex + 1);
                    VisibleElement nextItem = arrayElement.get(oldIndex + 1);

                    //Inserting the array item
                    PropertyDescriptor pd = new PropertyDescriptor(getFieldName(), getObject().getClass());
                    Method wm = pd.getWriteMethod();
                    Method rm = pd.getReadMethod();
                    Object arr = rm.invoke(getObject());
                    Object oldArr = reserveOldArr(arr);
                    arr = resizeArray(arr, countItems + 1);

                    for (int i = countItems; i >= oldIndex + 2; i--) {
                        Object o = Array.get(arr, i - 1);
                        if (o != null) {
                            Array.set(arr, i, o);

                        } else {
                            arr = delItem(arr, i);
                        }
                        wm.invoke(getObject(), arr);
                        editors.get(i).setIndex(i - 1);
                    }
                    arr = delItem(arr, oldIndex + 1);
                    wm.invoke(getObject(), arr);
                    editors.get(oldIndex + 1).updateElement();
                    new Modification((DSLBean)getObject(),getFieldName(),oldArr,arr,-1);
                    model.setFocusedElement(nextItem);
                } catch (Exception e) {
                    throw new Error("Failed to insert an array item: ", e);
                }

            }

            public void keyPressed(KeyEvent e) {
                boolean ctrl = (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_BACK_SPACE:
                        /*if (ctrl)
                         {
                              buttonBackspace();
                              e.consume();
                         }
                        break;*/

                    case KeyEvent.VK_DELETE:
                        //if (ctrl)
                    {
                        buttonDelete();
                        e.consume();
                    }
                    break;
                    case KeyEvent.VK_INSERT:
                        buttonInsert();
                        e.consume();
                        break;
                    case KeyEvent.VK_ENTER:
                        buttonEnter();
                        e.consume();
                        break;
                }
            }

            public void keyReleased(KeyEvent e) {
            }
        });
        return arrayElement;
    }

    @Override
    protected void updateElement() {
        try {
            Object val = getValue();

            if (val == null || Array.getLength(val) == 0) {
                FieldEditor ed = createEditorInstance(0);
                editors.add(ed);
                arrayElement.add(ed.createElement(arrayElement.getModel()));
                //arrayElement.add(new TextElement(model,"Пустой массив"));
                PropertyDescriptor pd = new PropertyDescriptor(getFieldName(), getObject().getClass());
                Method wm = pd.getWriteMethod();
                Object arr = Array.newInstance(pd.getPropertyType().getComponentType(), 1);
                wm.invoke(getObject(), arr);
            } else {
                int count = arrayElement.getChildrenCount();
                if (count > 0)
                    for (int i = 0; i < count; i++)
                        arrayElement.remove(0);


                for (int i = 0; i < Array.getLength(val); i++) {
                    FieldEditor ed = createEditorInstance(i);
                    editors.add(ed);
                    arrayElement.add(ed.createElement(arrayElement.getModel()));
                }
            }

        } catch (Exception e) {
            System.out.println("Error in array updating! " + e);
        }
    }
}