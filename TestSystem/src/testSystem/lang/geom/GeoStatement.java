package testSystem.lang.geom;

import ru.ipo.structurededitor.model.*;
import ru.ipo.structurededitor.view.editors.settings.AbstractDSLBeanSettings;
import ru.ipo.structurededitor.view.editors.settings.ArraySettings;
import ru.ipo.structurededitor.view.editors.settings.EnumSettings;
import ru.ipo.structurededitor.view.editors.settings.StringSettings;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 15:47:18
 */
public class GeoStatement implements DSLBean, DSLBeanView {

    private String title;
    private String statement;
    private Pred[] preds;
    private Instrum[] instrums;

    /*
    public Element[] getElements() {
        return elements;
    }

    public void setElements(Element[] elements) {
        this.elements = elements;
    }

    private Element[] elements;*/

    public Pred[] getPreds() {
        return preds;
    }

    public void setPreds(Pred[] preds) {
        this.preds = preds;
    }

    public Instrum[] getInstrums() {
        return instrums;
    }

    public void setInstrums(Instrum[] instrums) {
        this.instrums = instrums;
    }


    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public Cell getLayout() {
        String headerHint = "<html><b>Заголовок</b><br>Это название задачи, отображается в списке задач<br>Надо лучше текст придумать</html>";
        String statementHint = "<html><b>Условие</b><br>Это условие задачи, отображается для участника</html>";
        String instrumentsHint = "<html><b>Инструменты</b><br>Набор инструментов, доступных участнику<br>во время решения задачи</html>";
        String predicatesHint = "<html><b>Предикаты</b><br>Список условий, которые<br>должен удовлетворить участник</html>";

        return new Vert(
                new Horiz(
                        new ConstantCell("Заголовок:", new ConstCellSettings().withToolTipText(headerHint)),
                        new FieldCell(
                                "title",
                                new StringSettings()
                                        .withToolTipText(headerHint)
                                        .withEmptyText("Введите заголовок")
                        )
                ),
                new Horiz(
                        new ConstantCell("Условие:", new ConstCellSettings().withToolTipText(statementHint)),
                        new FieldCell(
                                "statement",
                                new StringSettings().withSingleLine(false)
                                        .withToolTipText(statementHint)
                                        .withEmptyText("Введите условие")
                        )
                ),
                new Horiz(
                        new ConstantCell("Инструменты:", new ConstCellSettings().withToolTipText(instrumentsHint)),
                        createInstrumentsArrayFieldCell()
                ),
                new Horiz(
                        new ConstantCell("Предикаты:", new ConstCellSettings().withToolTipText(predicatesHint)),
                        createPredicatesArrayFieldCell()
                )
        );
    }

    private ArrayFieldCell createPredicatesArrayFieldCell() {
        return new ArrayFieldCell("preds", ArrayFieldCell.Orientation.Vertical)
                .withArraySettings(new ArraySettings()
                        .withInsertActionText("Добавить предикат")
                        .withRemoveActionText("Удалить предикат")
                        .withClearArrayActionText("Удалить все предикаты")
                        .withZeroElementsText("Предикаты не указаны")
                        .withMinElements(1)
                )
                .withItemsSettings(new AbstractDSLBeanSettings()
                        .withNullValueText("Выберите предикат")
                        .withSelectVariantActionText("Выбрать предикат")
                        .withSetNullActionText("Выбрать другой предикат")
                );
    }

    private ArrayFieldCell createInstrumentsArrayFieldCell() {
        ArraySettings arraySettings = new ArraySettings()
                .withInsertActionText("Вставить инструмент")
                .withZeroElementsText("Не ограничивать участника")
                .withRemoveActionText("Удалить инструмент")
                .withClearArrayActionText("Удалить все инструменты")
                /*.withMinElements(1)*/;

        EnumSettings instrSettings = new EnumSettings()
                .withNullText("Выберите инструмент")
                .withSelectVariantActionText("Выбрать инструмент")
                .withSelectOtherVariantActionText("Выбрать другой инструмент");

        return new ArrayFieldCell("instrums", ArrayFieldCell.Orientation.Vertical)
                .withArraySettings(arraySettings)
                .withItemsSettings(instrSettings);
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
