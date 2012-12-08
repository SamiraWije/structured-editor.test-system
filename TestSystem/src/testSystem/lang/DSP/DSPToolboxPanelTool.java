package testSystem.lang.DSP;

import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: Олег
 * Date: 03.08.2012
 * Time: 11:36:14
 * To change this template use File | Settings | File Templates.
 */
@DSLBeanParams(shortcut = "DSP", description = "DSP System Toolbox")
public class DSPToolboxPanelTool extends ToolboxPanelTool {
    DSPToolboxEnum toolboxEnum;

    public DSPToolboxEnum getToolboxEnum() {
        return toolboxEnum;
    }

    public void setToolboxEnum(DSPToolboxEnum toolboxEnum) {
        this.toolboxEnum = toolboxEnum;
    }

    public DSPToolboxPanelTool() {
        toolboxName="DSP Toolbox";
    }
}