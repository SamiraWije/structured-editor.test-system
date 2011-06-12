package ru.ipo.structurededitor.model;

/**
 * Интерфейс для просмотра наборов данных. Например, текст задачи - это
 * DSLBean
 */
public interface DSLBeanView {
    /**
     * Получаем графическое представление Bean'а
     *
     * @return ячейка для отображение Bean'а
     */
    Cell getViewLayout();
}
