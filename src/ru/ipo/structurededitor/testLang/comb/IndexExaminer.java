package ru.ipo.structurededitor.testLang.comb;

import ru.ipo.structurededitor.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:15:32
 */
@DSLBeanParams(shortcut = "Индексирующий", description = "верификатор")
public class IndexExaminer extends Examiner {

    private Expr expr;
    private Kit indexingElem;

    public Expr getExpr() {
        return expr;
    }

    public void setExpr(Expr expr) {
        this.expr = expr;
    }

    public Cell getLayout() {
        return new Vert(
                new ConstantCell("Индексирующий верификатор"),
                new Horiz(new ConstantCell("Предикат:"), new FieldCell("expr")),
                new Horiz(new ConstantCell("Индексирующее множество:"), new FieldCell("indexingElem"))
        );
    }

    public void setIndexingElem(Kit indexingElem) {
        this.indexingElem = indexingElem;
    }

    public Kit getIndexingElem() {
        return indexingElem;
    }
}
