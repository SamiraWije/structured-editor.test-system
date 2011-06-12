package ru.ipo.structurededitor.controller;

import ru.ipo.structurededitor.Defaults;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.editors.FieldEditor;
import ru.ipo.structurededitor.view.editors.StringEditor;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Сопоставление Beans и редакторов
 */
public class EditorsRegistry {

    //static private EditorsRegistry instance;
    /**
     * Редактор по умолчанию для всех тех полей, для которых не нашлось ничего получше
     */
    private Class<? extends FieldEditor> defaultEditor;

    // Редактор для создания следующего элемента массива                                                      
    //private Class<? extends T> nextArrayEditor;
    //private Class<? extends FieldEditor> enumEditor;

    /*static public EditorsRegistry getInstance() {
        if (instance == null) {
            instance = new EditorsRegistry();
            Defaults.registerDefaultEditors();
        }

        return instance;
    } */

    /**
     * Сопоставление типов свойств и редакторов
     */
    private HashMap<Class<?>, Class<? extends FieldEditor>> propTypeToEditor = new HashMap<Class<?>, Class<? extends FieldEditor>>();
    private ArrayList<EditorsRegistryHook> hooks = new ArrayList<EditorsRegistryHook>();


    public Class<? extends FieldEditor> getDefaultEditor() {
        return defaultEditor;
    }

    public void setDefaultEditor(Class<? extends FieldEditor> defaultEditor) {
        this.defaultEditor = defaultEditor;
    }


    private String getKey(Class<? extends DSLBean> beanClass, String propertyName) {
        return beanClass.getName() + "." + propertyName;
    }

    /**
     * Задаем редактор для всех полей определенного типа
     *
     * @param propertyType тип поля
     * @param editor       класс редактора
     */
    public void registerEditor(Class<?> propertyType, Class<? extends FieldEditor> editor) {
        propTypeToEditor.put(propertyType, editor);
    }

    public void registerHook(EditorsRegistryHook hook) {
        hooks.add(hook);
    }

    /**
     * Получение редактора для поля DSLBean
     * Сначала поиск в таблице для конкретных полей, потом поиск по типу
     *
     * @param beanClass    класс бина
     * @param propertyName имя свойства
     * @param obj          объект, с которым связан редактор
     * @param mask         маска поля
     * @param model        модель редактора
     * @return редактор для свойства
     */
    public FieldEditor getEditor(Class<? extends DSLBean> beanClass, String propertyName, Object obj, FieldMask mask,
                                 boolean singleLined, StructuredEditorModel model) {
        try {

            Class<? extends FieldEditor> pec;

            BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
            PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor d : descriptors) {
                if (d.getName().equals(propertyName)) {
                    Class<?> propClass = d.getPropertyType();
                    if (mask != null)
                        propClass = mask.getValueClass(propClass);
                    pec = propTypeToEditor.get(propClass);
                    /*if (pec == null && propClass.isEnum())
                        pec = enumEditor;*/
                    Class<? extends FieldEditor> hooked = null;
                    for (EditorsRegistryHook hook : hooks) {
                        hooked = hook.substituteEditor((Class<? extends DSLBean>) beanClass, propertyName, mask, propClass);
                        if (hooked != null) {
                            break;
                        }
                    }
                    if (hooked != null) {
                        pec = hooked;
                    }
                    if (pec != null)
                        return createEditorInstance(pec, obj, propertyName, mask, singleLined, model);
                    break;
                }
            }
            return createEditorInstance(defaultEditor, obj, propertyName, mask, singleLined, model);
        } catch (Exception e) {
            throw new Error("Failed to create editor: ", e);
        }
    }

    private FieldEditor createEditorInstance(Class<? extends FieldEditor> pec, Object obj, String propertyName,
                                             FieldMask mask, boolean singleLined, StructuredEditorModel model)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        final Constructor<? extends FieldEditor> c;
        if (pec == StringEditor.class) {
            c = pec.getConstructor(Object.class, String.class, FieldMask.class,
                    boolean.class, StructuredEditorModel.class);
            return c.newInstance(obj, propertyName, mask, singleLined, model);
        } else {
            c = pec.getConstructor(Object.class, String.class, FieldMask.class,
                    StructuredEditorModel.class);
            return c.newInstance(obj, propertyName, mask, model);
        }
    }

    public EditorsRegistry() {
        Defaults.registerDefaultEditors(this);
    }
}
