package testSystem.lang.DSP;

import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: Олег
 * Date: 03.08.2012
 * Time: 11:36:14
 * To change this template use File | Settings | File Templates.
 */
@DSLBeanParams(shortcut = "CS", description = "Control System Toolbox")
public class CSToolboxPanelTool extends ToolboxPanelTool {
    CSToolboxEnum toolboxEnum;

    public CSToolboxEnum getToolboxEnum() {
        return toolboxEnum;
    }

    public void setToolboxEnum(CSToolboxEnum toolboxEnum) {
        this.toolboxEnum = toolboxEnum;
    }

    public CSToolboxPanelTool() {
        toolboxName="CS Toolbox";
    }
}
