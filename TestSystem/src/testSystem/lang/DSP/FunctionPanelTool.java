package testSystem.lang.DSP;

import ru.ipo.structurededitor.model.Cell;
import ru.ipo.structurededitor.model.ConstantCell;
import ru.ipo.structurededitor.model.DSLBeanParams;
import ru.ipo.structurededitor.model.FieldCell;

/**
 * Created by IntelliJ IDEA.
 * User: Олег
 * Date: 02.08.2012
 * Time: 18:33:56
 * To change this template use File | Settings | File Templates.
 */
@DSLBeanParams(shortcut = "Function", description = "Single MATLAB function")
public class FunctionPanelTool extends PanelTool {
    private String funName;

    public String getFunName() {
        return funName;
    }

    public void setFunName(String funName) {
        this.funName = funName;
    }

    @Override
    public Cell getLayout() {
        return new FieldCell("funName");  //To change body of implemented methods use File | Settings | File Templates.
    }
}
