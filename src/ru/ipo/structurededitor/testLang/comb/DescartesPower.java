package ru.ipo.structurededitor.testLang.comb;

import ru.ipo.structurededitor.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:07:49
 */
@DSLBeanParams(shortcut = "^", description = "Декартова степень")
public class  DescartesPower extends Kit {

    private Kit kit;
    private int pow;

    public Kit getKit() {
        return kit;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }

    public int getPow() {
        return pow;
    }

    public void setPow(int pow) {
        this.pow = pow;
    }

    public Cell getLayout() {
        return new Horiz(new FieldCell("kit"), new ConstantCell("в степени"), new FieldCell("pow"));
    }
}
