package testSystem.lang.DSP;

import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: olegperch
 * Date: 22.04.12
 * Time: 9:38
 * To change this template use File | Settings | File Templates.
 */
@DSLBeanParams(shortcut = "block", description = "Элемент Simulink")
public class BlockTool extends AbstractTool {
    Block tool;

    public Block getTool() {
        return tool;
    }

    public void setTool(Block tool) {
        this.tool = tool;
    }

    public BlockTool() {
        toolType="Элемент";
    }
}
