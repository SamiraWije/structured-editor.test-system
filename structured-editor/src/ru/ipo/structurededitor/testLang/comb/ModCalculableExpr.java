package ru.ipo.structurededitor.testLang.comb;

import ru.ipo.structurededitor.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:29:20
 */
@DSLBeanParams(shortcut = "ВычМод", description = "Вычислить по модулю")
public class ModCalculableExpr extends CalculableExpr {
    int mod;

    public Cell getLayout() {
        return new Horiz(new ConstantCell("Вычислить (("), new FieldCell("ce"), new ConstantCell(")"),
                new ConstantCell("mod"), new FieldCell("mod"), new ConstantCell(")"));
    }


    public int getMod() {
        return mod;
    }

    public void setMod(int mod) {
        this.mod = mod;
    }
}
