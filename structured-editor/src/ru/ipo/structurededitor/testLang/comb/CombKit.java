package ru.ipo.structurededitor.testLang.comb;

import ru.ipo.structurededitor.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:07:49
 */
@DSLBeanParams(shortcut = "C", description = "Множество сочетаний")
public class CombKit extends Kit {

    private Kit kit;
    private int k;

    public Kit getKit() {
        return kit;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public Cell getLayout() {
        return new Vert(new Horiz(new ConstantCell("Множество сочетаний элементов"), new FieldCell("kit")),
                new Horiz(new ConstantCell("по k ="), new FieldCell("k")));
    }
}
