package testSystem.lang.DSP;

import ru.ipo.structurededitor.model.EnumFieldParams;

/**
 * Created by IntelliJ IDEA.
 * User: Олег
 * Date: 03.08.2012
 * Time: 11:40:25
 * To change this template use File | Settings | File Templates.
 */
public enum SPToolboxEnum {
    @EnumFieldParams(displayText = "abs")
    ABS,
    @EnumFieldParams(displayText = "angle")
    ANGLE,
    @EnumFieldParams(displayText = "kaiser")
    KAISER,
    @EnumFieldParams(displayText = "hamming")
    HAMMING,
    @EnumFieldParams(displayText = "chebwin")
    CHEBWIN,
}