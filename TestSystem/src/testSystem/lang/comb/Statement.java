package testSystem.lang.comb;

import ru.ipo.structurededitor.model.*;
import ru.ipo.structurededitor.view.editors.settings.StringSettings;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 15:47:18
 */
public class Statement implements DSLBean, DSLBeanView {

    private String title;
    private String statement;
    private Examiner examiner;
    private Kit kit;


    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public Kit getKit() {
        return kit;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }

    public Examiner getExaminer() {
        return examiner;
    }

    public void setExaminer(Examiner examiner) {
        this.examiner = examiner;
    }

    public Cell getLayout() {
        return new Vert(
                new Horiz(new ConstantCell("Заголовок:"), new FieldCell("title")),
                new Horiz(new ConstantCell("Условие:"), new FieldCell("statement",
                         new StringSettings().withSingleLine(false))),
                new Horiz(new ConstantCell("Множество:"), new FieldCell("kit")),
                new Horiz(new ConstantCell("Верификатор:"), new FieldCell("examiner"))
        );
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Cell getViewLayout() {
        return new Vert(
                       new Horiz(new ConstantCell("Заголовок:"), new FieldCell("title")),
                       new Horiz(new ConstantCell("Условие:"), new FieldCell("statement"))
               );
    }
}