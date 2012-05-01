package testSystem.lang.DSP;

import ru.ipo.structurededitor.model.*;
import ru.ipo.structurededitor.view.editors.settings.ArraySettings;
import ru.ipo.structurededitor.view.editors.settings.EnumSettings;
import ru.ipo.structurededitor.view.editors.settings.StringSettings;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 15:47:18
 */
public class DSPStatement implements DSLBean, DSLBeanView {

    private String title;
    private String statement;
    private AbstractTool[] tools;
    private String verifier;

    public AbstractTool[] getTools() {
        return tools;
    }

    public void setTools(AbstractTool[] tools) {
        this.tools = tools;
    }

    public String getVerifier() {
        return verifier;
    }

    public void setVerifier(String verifier) {
        this.verifier = verifier;
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
                new Horiz(new ConstantCell("Условие:"), new FieldCell("statement",
                new StringSettings().withSingleLine(false)
                ))

        );
    }
    public Cell getLayout() {
        return new Vert(
                new Horiz(new ConstantCell("Заголовок:"), new FieldCell("title")),
                new Horiz(new ConstantCell("Условие:"), new FieldCell("statement",
                                 new StringSettings().withSingleLine(false))),
                new Horiz(new ConstantCell("Инструменты:"), createInstrumentsArrayFieldCell()),
                new Horiz(new ConstantCell("Верификатор:"), new FieldCell("verifier",
                                 new StringSettings().withSingleLine(false)))

        );
    }

     private ArrayFieldCell createInstrumentsArrayFieldCell() {
        ArraySettings arraySettings = new ArraySettings()
                .withInsertActionText("Вставить инструмент")
                .withZeroElementsText("Не ограничивать участника")
                .withRemoveActionText("Удалить инструмент")
                .withClearArrayActionText("Удалить все инструменты")
                /*.withMinElements(1)*/;

        /*EnumSettings instrSettings = new EnumSettings()
                .withNullText("Выберите инструмент")
                .withSelectVariantActionText("Выбрать инструмент")
                .withSelectOtherVariantActionText("Выбрать другой инструмент");*/

        return new ArrayFieldCell("tools", ArrayFieldCell.Orientation.Vertical)
                .withArraySettings(arraySettings);
                //.withItemsSettings(instrSettings);
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
