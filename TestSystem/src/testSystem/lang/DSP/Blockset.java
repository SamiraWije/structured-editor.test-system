package testSystem.lang.DSP;

import ru.ipo.structurededitor.model.EnumFieldParams;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 22.03.11
 * Time: 14:39
 */
public enum Blockset {
    @EnumFieldParams(displayText = "Control System Toolbox")
    CS_TOOLBOX,
    @EnumFieldParams(displayText = "DSP System Toolbox")
    DSP_TOOLBOX,
    @EnumFieldParams(displayText = "Simulink Extras")
    SIM_EXTRAS
}
