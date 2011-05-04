package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.controller.ArrayFieldMask;
import ru.ipo.structurededitor.controller.EditorsRegistry;
import ru.ipo.structurededitor.controller.Modification;
import ru.ipo.structurededitor.controller.ModificationVector;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.elements.ArrayElement;
import ru.ipo.structurededitor.view.elements.CompositeElement;
import ru.ipo.structurededitor.view.elements.VisibleElement;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 29.07.2010
 * Time: 13:17:50
 * To change this template use File | Settings | File Templates.
 */
public class ArrayEditor extends FieldEditor {
    private boolean singleLined = false;

    public ArrayEditor(Object o, String fieldName, CompositeElement.Orientation orientation, char spaceChar,
                       boolean singleLined, final StructuredEditorModel model) {
        super(o, fieldName, null, model);
        this.orientation = orientation;
        this.spaceChar = spaceChar;
        this.singleLined = singleLined;
        final ArrayElement arrayElement = new ArrayElement(model, orientation, spaceChar);
        setModificationVector(model.getModificationVector());

        arrayElement.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {

            }

            public void buttonDelete() {
                try {
                    ModificationVector modificationVector = getModificationVector();
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
                            model.setFocusedElementAndCaret(arrayElement.getChild(newIndex));
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
                            editors.get(i).setMask(new ArrayFieldMask(i + 1));
                            wm.invoke(getObject(), arr);
                        }
                        arr = resizeArray(arr, countItems - 1);
                        wm.invoke(getObject(), arr);
                        if (oldIndex < countItems - 1) {
                            model.setFocusedElementAndCaret(arrayElement.get(oldIndex));

                        }
                        if (modificationVector != null)
                            modificationVector.add(new Modification((DSLBean) getObject(), getFieldName(), oldArr, arr, null));

                    }
                } catch (Exception e) {
                    throw new Error("Failed to delete an array item: ", e);
                }
            }


            public void buttonInsert() {
                try {
                    ModificationVector modificationVector = getModificationVector();
                    int countItems = arrayElement.getChildrenCount();
                    //Adding a new element
                    VisibleElement currentItem = model.getFocusedElement();
                    while (!currentItem.getParent().equals(arrayElement))
                        currentItem = currentItem.getParent();
                    int oldIndex = arrayElement.getChildIndex(currentItem);
                    FieldEditor ed = createEditorInstance(oldIndex, model);
                    VisibleElement newItem = ed.getElement();
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
                        editors.get(i).setMask(new ArrayFieldMask(i - 1));
                    }
                    arr = delItem(arr, oldIndex);
                    wm.invoke(getObject(), arr);
                    editors.get(oldIndex).updateElement();
                    model.setFocusedElementAndCaret(newItem);
                    if (modificationVector != null)
                        modificationVector.add(new Modification((DSLBean) getObject(), getFieldName(), oldArr, arr,
                                null));

                } catch (Exception e) {
                    throw new Error("Failed to insert an array item: ", e);
                }
            }

            public void buttonEnter() {
                try {
                    ModificationVector modificationVector = getModificationVector();
                    int countItems = arrayElement.getChildrenCount();
                    //Adding a new element
                    VisibleElement currentItem;
                    currentItem = model.getFocusedElement();
                    while (!currentItem.getParent().equals(arrayElement))
                        currentItem = currentItem.getParent();
                    int oldIndex = arrayElement.getChildIndex(currentItem);
                    FieldEditor ed = createEditorInstance(oldIndex + 1, model);
                    editors.add(oldIndex + 1, ed);
                    VisibleElement newItem = ed.getElement();
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
                        editors.get(i).setMask(new ArrayFieldMask(i - 1));
                    }
                    arr = delItem(arr, oldIndex + 1);
                    wm.invoke(getObject(), arr);
                    editors.get(oldIndex + 1).updateElement();
                    if (modificationVector != null)
                        modificationVector.add(new Modification((DSLBean) getObject(), getFieldName(), oldArr, arr,
                                null));
                    model.setFocusedElementAndCaret(nextItem);
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
        setElement(arrayElement);
        updateElement();
    }

    //private ArrayElement arrayElement;
    private Vector<FieldEditor> editors = new Vector<FieldEditor>();

    private CompositeElement.Orientation orientation;
    //private Class<? extends FieldEditor> EditorClass;
    private char spaceChar;

    private FieldEditor createEditorInstance(int index, StructuredEditorModel model)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        final EditorsRegistry reg;
        reg = model.getEditorsRegistry();
        return reg.getEditor((Class<? extends DSLBean>) getObject().getClass(), getFieldName(), getObject(),
                new ArrayFieldMask(index), singleLined, model);

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
    protected void updateElement() {
        ArrayElement arrayElement = (ArrayElement) getElement();
        try {
            Object val = getValue();

            if (val == null || Array.getLength(val) == 0) {
                FieldEditor ed = createEditorInstance(0, arrayElement.getModel());
                editors.add(ed);
                arrayElement.add(ed.getElement());
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
                    FieldEditor ed = createEditorInstance(i, arrayElement.getModel());
                    editors.add(ed);
                    arrayElement.add(ed.getElement());
                }
            }

        } catch (Exception e) {
            System.out.println("Error in array updating! " + e);
        }
    }
}