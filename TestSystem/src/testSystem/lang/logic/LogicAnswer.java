package testSystem.lang.logic;

import ru.ipo.structurededitor.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 15:47:18
 */
public class LogicAnswer implements DSLBean{



    public LogicAtomValue[] getAnswer() {
        return answer;
    }

    public void setAnswer(LogicAtomValue[] answer) {
        this.answer = answer;
    }

    private LogicAtomValue[] answer;


     public Cell getLayout() {
        return new Vert(
              new Horiz(new ConstantCell("Ответ:"), new ArrayFieldCell("answer", ArrayFieldCell.Orientation.Vertical))
        );
    }

}
