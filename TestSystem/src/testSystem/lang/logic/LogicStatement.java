package testSystem.lang.logic;

import ru.ipo.structurededitor.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 15:47:18
 */
public class LogicStatement implements DSLBean, DSLBeanView {

    private String title;
    private String statement;
    private LogicCondition condition;

    public LogicCondition getCondition() {
        return condition;
    }

    public void setCondition(LogicCondition condition) {
        this.condition = condition;
    }


    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }
     public Cell getViewLayout() {
        return new Vert(
                new Horiz(new ConstantCell("Заголовок:"), new FieldCell("title")),
                new Horiz(new ConstantCell("Условие:"), new FieldCell("statement"))

        );
    }
    public Cell getLayout() {
        return new Vert(
                new Horiz(new ConstantCell("Заголовок:"), new FieldCell("title")),
                new Horiz(new ConstantCell("Условие:"), new FieldCell("statement")),
                new Horiz(new ConstantCell("Верификатор:"), new FieldCell("condition"))
        );
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
