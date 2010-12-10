package ru.ipo.structurededitor.controller;

import ru.ipo.structurededitor.Defaults;
import ru.ipo.structurededitor.model.DSLBean;

import java.beans.*;
import java.util.HashMap;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Сопоставление Beans и редакторов
 */
public class EditorsRegistry<T> {

    static private HashMap<Class, EditorsRegistry> instances = new HashMap<Class, EditorsRegistry>();
    /**
     * Редактор по умолчанию для всех тех полей, для которых не нашлось ничего получше
     */
    private Class<? extends T> defaultEditor;

    // Редактор для создания следующего элемента массива                                                      
    private Class<? extends T> nextArrayEditor;
    private Class<? extends T> enumEditor;

    static public <T> EditorsRegistry<T> getInstance(Class<T> c) {
        EditorsRegistry<T> editorsRegistry = (EditorsRegistry<T>) instances.get(c);

        if (editorsRegistry == null) {
            editorsRegistry = new EditorsRegistry<T>();
            instances.put(c, editorsRegistry);
            Defaults.registerDefaultEditors();
        }

        return editorsRegistry;
    }

    /**
     * Сопоставление типов свойств и редакторов
     */
    private HashMap<Class<?>, Class<? extends T>> propTypeToEditor = new HashMap<Class<?>, Class<? extends T>>();

    /**
     * Соответствие конкретных свойств и редакторов
     */
    private HashMap<String, Class<? extends T>> propToEditor = new HashMap<String, Class<? extends T>>();

    public Class<? extends T> getDefaultEditor() {
        return defaultEditor;
    }

    public void setDefaultEditor(Class<? extends T> defaultEditor) {
        this.defaultEditor = defaultEditor;
    }

    /**
     * Задаем конкретный редактор для поля DSLBean
     *
     * @param beanClass    класс
     * @param propertyName имя свойства
     * @param editor       класс редактора
     */
    public void registerEditor(Class<? extends DSLBean> beanClass, String propertyName, Class<? extends T> editor) {
        String key = getKey(beanClass, propertyName);
        propToEditor.put(key, editor);
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
    public void registerEditor(Class<?> propertyType, Class<? extends T> editor) {
        propTypeToEditor.put(propertyType, editor);
    }

    /**
     * Получение редактора для поля DSLBean
     * Сначала поиск в таблице для конкретных полей, потом поиск по типу
     *
     * @param beanClass    класс бина
     * @param propertyName имя свойства
     * @param obj          объект, с которым связан редактор
     * @return редактор для свойства
     */
    public T getEditor(Class<? extends DSLBean> beanClass, String propertyName, Object obj) {
        try {
            
            Class<? extends T> pec = propToEditor.get(getKey(beanClass, propertyName));
            if (pec != null)
                return createEditorInstance(pec, obj, propertyName);

            BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
            PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor d : descriptors) {
                if (d.getName().equals(propertyName)) {
                    Class<?> propClass = d.getPropertyType();
                    if (propClass.isArray())
                        propClass = propClass.getComponentType();
                    pec = propTypeToEditor.get(propClass);
                    if (pec == null && propClass.isEnum())
                        pec = enumEditor;
                    if (pec != null)
                        return createEditorInstance(pec, obj, propertyName);
                    break;
                }
            }
            return createEditorInstance(defaultEditor, obj, propertyName);
        } catch (Exception e) {
            throw new Error("Failed to create editor: ", e);
        }
    }

    private T createEditorInstance(Class<? extends T> pec, Object obj, String propertyName) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        final Constructor<? extends T> c = pec.getConstructor(Object.class, String.class);
        return c.newInstance(obj, propertyName);
    }

    private EditorsRegistry() {
    }

    public Class<? extends T> getNextArrayEditor() {
        return nextArrayEditor;
    }

    public void setNextArrayEditor(Class<? extends T> nextArrayEditor) {
        this.nextArrayEditor = nextArrayEditor;
    }

    public Class<? extends T> getEnumEditor() {
        return enumEditor;
    }

    public void setEnumEditor(Class<? extends T> enumEditor) {
        this.enumEditor = enumEditor;
    }
}
