package testSystem.lang.DSP;

import ru.ipo.structurededitor.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: Олег
 * Date: 03.08.2012
 * Time: 12:07:44
 * To change this template use File | Settings | File Templates.
 */
public class DSPPanel implements DSLBean {
    PanelTool tools[];

    public PanelTool[] getTools() {
        return tools;
    }

    public void setTools(PanelTool[] tools) {
        this.tools = tools;
    }

    @Override
    public Cell getLayout() {
        return new Vert(new ConstantCell("Выберите функцию для вставки в скрипт-решение:"),
                new ArrayFieldCell("tools", ArrayFieldCell.Orientation.Horizontal));
    }
}
