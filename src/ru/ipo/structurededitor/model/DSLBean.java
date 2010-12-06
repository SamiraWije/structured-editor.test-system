package ru.ipo.structurededitor.model;

/**
 * Интерфейс для редактируемых наборов данных. Например, текст задачи - это
 * DSLBean
 */
public interface DSLBean {
    /**
     * Получаем графическое представление Bean'а
     *
     * @return ячейка для отображение Bean'а
     */
    Cell getLayout();
}
