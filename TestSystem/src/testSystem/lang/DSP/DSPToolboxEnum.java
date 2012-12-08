package testSystem.lang.DSP;

import ru.ipo.structurededitor.model.EnumFieldParams;

/**
 * Created by IntelliJ IDEA.
 * User: Олег
 * Date: 03.08.2012
 * Time: 11:40:25
 * To change this template use File | Settings | File Templates.
 */
public enum DSPToolboxEnum {
    @EnumFieldParams(displayText = "filter")
    FILTER,
    @EnumFieldParams(displayText = "fft")
    FFT,
    @EnumFieldParams(displayText = "ifft")
    IFFT
}