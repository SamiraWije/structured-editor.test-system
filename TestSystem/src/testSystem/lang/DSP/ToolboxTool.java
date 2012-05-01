package testSystem.lang.DSP;

import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: olegperch
 * Date: 22.04.12
 * Time: 9:38
 * To change this template use File | Settings | File Templates.
 */
@DSLBeanParams(shortcut = "toolbox", description = "Набор функций MATLAB")
public class ToolboxTool extends AbstractTool {
    Toolbox tool;

    public Toolbox getTool() {
        return tool;
    }

    public void setTool(Toolbox tool) {
        this.tool = tool;
    }

    public ToolboxTool() {
        toolType="Набор функций";
    }
}
