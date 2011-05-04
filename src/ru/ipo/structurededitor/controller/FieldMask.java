package ru.ipo.structurededitor.controller;

/**
 * Created by IntelliJ IDEA.
 * User: iposov
 * Date: 02.12.2010
 * Time: 14:24:57
 */
public interface FieldMask {

    /**
     * Возвращает значение внутри заданного поля. Например, если задан массив, значением будет один
     * из индексов массива
     *
     * @param field значение поля
     * @return Значение внутри поля
     */
    Object get(Object field);

    /**
     * Устанавливает значение внуть заданного поля. Например, если поле является массивом, значение
     * может устанавливатся в один из элементов массива
     *
     * @param field поле
     * @param value устанавливаемое значение
     * @return Новое значение всего поля
     */
    Object set(Object field, Object value);

    /**
     * Кажая маска должна уметь сообщать тип значений, с которыми она работает. Например, если
     * маскируется массив целых чисел, то маска вернет тип int.class как тот тип, который можно
     * устанавливать в качестве значения поля.
     *
     * @param fieldClass класс поля, (например, int[].class)
     * @return класс маскируемых значений (int.class)
     */
    Class getValueClass(Class fieldClass);

}
