package ru.ipo.structurededitor.controller;

import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.view.editors.FieldEditor;

/**
 * Created by IntelliJ IDEA.
 * User: iposov
 * Date: 02.12.2010
 * Time: 14:49:03
 * <p/>
 * Позоволяет переопределять выбор редактора в EditorRegistry.
 * При выборе редактора EditorRegistry первым делом определяет класс редактируемого значения. Это
 * происходит на основе класса бина и имени поля, помимо этого анализируется маска поля, которая,
 * если она установлена, заменяет класс редактируемое значения. Например, поле типа int[] может
 * быть превращено в поле int.
 * Далее по классу значения должен быть определен класс редактора. Перед тем как выбрать зарегистрированный
 * для данного типа класс EditorsRegistry опрашивает все зарегистрированные хуки. Если один
 * из них предлагает заменить класс, то делает замену.
 */
public interface EditorsRegistryHook {

    /**
     * Отвечает на вопрос, на что надо заменить редактор.
     *
     * @param beanClass    класс бина
     * @param propertyName имя свойства бина
     * @param mask         маска
     * @param valueType    вычисленный тип значения. Зависит от предыдущих параметров, но и так вычисляется
     *                     внутри EditorRegistry, поэтому его не требуется вычислять повторно.
     * @return null, если подмены редактора нет. Класс редактора, если подмена необходима.
     */
    Class<? extends FieldEditor> substituteEditor(Class<? extends DSLBean> beanClass, String propertyName, FieldMask mask, Class valueType);
}
