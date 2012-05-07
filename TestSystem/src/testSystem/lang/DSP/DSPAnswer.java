package testSystem.lang.DSP;

import ru.ipo.structurededitor.model.*;
import ru.ipo.structurededitor.view.editors.settings.StringSettings;
import testSystem.lang.logic.LogicAtomValue;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 15:47:18
 */
public class DSPAnswer implements DSLBean{
    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    private String answer;


     public Cell getLayout() {
        return new Vert(
              new Horiz(new ConstantCell("Решение:"), new FieldCell("answer",
                      new StringSettings().withSingleLine(false)))
        );
    }

}
