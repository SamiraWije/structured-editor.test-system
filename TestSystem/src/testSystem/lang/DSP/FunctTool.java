package testSystem.lang.DSP;

import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: olegperch
 * Date: 22.04.12
 * Time: 9:38
 * To change this template use File | Settings | File Templates.
 */
@DSLBeanParams(shortcut = "()", description = "Функция MATLAB")
public class FunctTool extends AbstractTool {
    Funct tool;

    public Funct getTool() {
        return tool;
    }

    public void setTool(Funct tool) {
        this.tool = tool;
    }

    public FunctTool() {
        toolType="Функция";
    }
}
