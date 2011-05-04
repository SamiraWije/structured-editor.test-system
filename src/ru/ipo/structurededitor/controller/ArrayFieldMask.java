package ru.ipo.structurededitor.controller;

import ru.ipo.structurededitor.view.editors.ArrayEditor;

import java.lang.reflect.Array;


/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 29.12.10
 * Time: 16:15
 * To change this template use File | Settings | File Templates.
 */
public class ArrayFieldMask implements FieldMask {
    private int index;

    public ArrayFieldMask(int index) {
        this.index = index;
    }

    public Object get(Object field) {
        if (index < Array.getLength(field))
            return Array.get(field, index);
        else
            return null;
    }

    public Object set(Object field, Object value) {
        /*if (field == null) {
                    field = Array.newInstance(pd.getPropertyType().getComponentType(), 1);

                }*/
        if (Array.getLength(field) <= index)
            field = ArrayEditor.resizeArray(field, index + 1);

        if (value == null) {
            return ArrayEditor.delItem(field, index);
        } else {
            Array.set(field, index, value);
            return field;
        }
    }

    public Class getValueClass(Class fieldClass) {
        return fieldClass.getComponentType();
    }
}
