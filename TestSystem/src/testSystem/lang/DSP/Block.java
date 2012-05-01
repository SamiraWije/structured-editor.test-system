package testSystem.lang.DSP;

import ru.ipo.structurededitor.model.EnumFieldParams;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 22.03.11
 * Time: 14:39
 */
public enum Block {
    @EnumFieldParams(displayText = "Сумматор")
    SUM,
    @EnumFieldParams(displayText = "Передаточная функция")
    TF,
    @EnumFieldParams(displayText = "Мультиплексор")
    MUX
}
