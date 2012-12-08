package testSystem.lang.DSP;

import ru.ipo.structurededitor.model.Cell;
import ru.ipo.structurededitor.model.ConstantCell;
import ru.ipo.structurededitor.model.FieldCell;
import ru.ipo.structurededitor.model.Vert;

/**
 * Created by IntelliJ IDEA.
 * User: Олег
 * Date: 02.08.2012
 * Time: 18:46:30
 * To change this template use File | Settings | File Templates.
 */
public abstract class ToolboxPanelTool extends PanelTool {
    protected String toolboxName;
    @Override
    public Cell getLayout() {
        return new Vert(new ConstantCell(toolboxName),new FieldCell("toolboxEnum"));
    }
}
