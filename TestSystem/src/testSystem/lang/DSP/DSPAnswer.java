package testSystem.lang.DSP;

import ru.ipo.structurededitor.model.*;
import ru.ipo.structurededitor.view.editors.settings.StringSettings;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 15:47:18
 */
public class DSPAnswer implements DSLBean{
    public String getAnswerMATLAB() {
        return answerMATLAB;
    }

    public void setAnswerMATLAB(String answerMATLAB) {
        this.answerMATLAB = answerMATLAB;
    }

    private String answerMATLAB;


     public Cell getLayout() {
        return new Vert(
              new Horiz(new ConstantCell("Решение:"), new FieldCell("answerMATLAB",
                      new StringSettings().withSingleLine(false)))
        );
    }

}
