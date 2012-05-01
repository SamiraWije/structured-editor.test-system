package testSystem.lang.DSP;

import ru.ipo.structurededitor.model.DSLBeanParams;

/**
 * Created by IntelliJ IDEA.
 * User: olegperch
 * Date: 22.04.12
 * Time: 9:38
 * To change this template use File | Settings | File Templates.
 */
@DSLBeanParams(shortcut = "blockset", description = "Набор элемент Simulink")
public class BlocksetTool extends AbstractTool {
    Blockset tool;

    public Blockset getTool() {
        return tool;
    }

    public void setTool(Blockset tool) {
        this.tool = tool;
    }

    public BlocksetTool() {
        toolType="Набор элементов";
    }
}
