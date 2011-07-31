package testSystem.lang.geom;

import ru.ipo.structurededitor.model.*;
import ru.ipo.structurededitor.view.editors.settings.AbstractDSLBeanSettings;

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
        return new Vert(
                new Horiz(new ConstantCell("Заголовок:"), new FieldCell("title")),
                new Horiz(new ConstantCell("Условие:"), new FieldCell("statement")),
                new Horiz(new ConstantCell("Инструменты:"), new ArrayFieldCell("instrums",
                        ArrayFieldCell.Orientation.Vertical).withItemsSettings(
                        new AbstractDSLBeanSettings().withSelectVariantActionText("Выбрать инструмент").
                                withSetNullActionText("Удалить инструмент")).withArraySettings(
                        new AbstractDSLBeanSettings().withSelectVariantActionText("Добавить инструмент").
                                withSetNullActionText("Удалить инструмент"))),
                new Horiz(new ConstantCell("Формальные условия:"), new ArrayFieldCell("preds",
                        ArrayFieldCell.Orientation.Vertical).withItemsSettings(
                        new AbstractDSLBeanSettings().withSelectVariantActionText("Выбрать условие").
                                withSetNullActionText("Удалить условие")).withArraySettings(
                        new AbstractDSLBeanSettings().withSelectVariantActionText("Добавить условие").
                                withSetNullActionText("Удалить условие")))
        );
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Cell getViewLayout() {
        return  new Vert(
                new Horiz(new ConstantCell("Заголовок:"), new FieldCell("title")),
                new Horiz(new ConstantCell("Условие:"), new FieldCell("statement"))
        );
    }
}
