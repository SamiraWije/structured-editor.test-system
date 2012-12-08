package testSystem.lang.DSP;

import ru.ipo.structurededitor.model.EnumFieldParams;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 22.03.11
 * Time: 14:39
 */
public enum Toolbox {
    @EnumFieldParams(displayText = "Control System Toolbox")
    CS_TOOLBOX,
    @EnumFieldParams(displayText = "Singnal Processing Toolbox")
    SP_TOOLBOX,
    @EnumFieldParams(displayText = "Optimization Toolbox")
    OPTIM_TOOLBOX
}
