package testSystem.lang.DSP;

import ru.ipo.structurededitor.model.EnumFieldParams;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 22.03.11
 * Time: 14:39
 */
public enum Funct {
    @EnumFieldParams(displayText = "plot")
    PLOT,
    @EnumFieldParams(displayText = "tf")
    TF,
    @EnumFieldParams(displayText = "fmin")
    FMIN,
    @EnumFieldParams(displayText = "fft")
    FFT,
    @EnumFieldParams(displayText = "ifft")
    IFFT,
    @EnumFieldParams(displayText = "floor")
    FLOOR,
    @EnumFieldParams(displayText = "max")
    MAX,
}
