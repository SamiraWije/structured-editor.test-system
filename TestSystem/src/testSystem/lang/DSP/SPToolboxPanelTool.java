package testSystem.lang.DSP;

import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: Олег
 * Date: 03.08.2012
 * Time: 11:36:14
 * To change this template use File | Settings | File Templates.
 */
@DSLBeanParams(shortcut = "SP", description = "Signal Processing Toolbox")
public class SPToolboxPanelTool extends ToolboxPanelTool {
    SPToolboxEnum toolboxEnum;

    public SPToolboxEnum getToolboxEnum() {
        return toolboxEnum;
    }

    public void setToolboxEnum(SPToolboxEnum toolboxEnum) {
        this.toolboxEnum = toolboxEnum;
    }

    public SPToolboxPanelTool() {
        toolboxName="SP Toolbox";
    }
}