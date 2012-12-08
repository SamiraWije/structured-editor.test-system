package testSystem.view.editors;

import ru.ipo.structurededitor.actions.VisibleElementAction;
import ru.ipo.structurededitor.controller.FieldMask;
import ru.ipo.structurededitor.model.EditorSettings;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.editors.FieldEditor;
import ru.ipo.structurededitor.view.editors.settings.StringSettings;
import ru.ipo.structurededitor.view.elements.MATLABTextEditorElement;
import ru.ipo.structurededitor.view.elements.TextEditorElement;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 20.12.2009
 * Time: 22:36:47
 */
public class MATLABEditor extends FieldEditor {

    private VisibleElementAction setNullAction;

    public MATLABEditor(Object o, String fieldName, FieldMask mask, StructuredEditorModel model, EditorSettings settings) {
        super(o, fieldName, mask, model, settings);

        final MATLABTextEditorElement editorElement;
        editorElement = new MATLABTextEditorElement(model, null, getSettings().isSingleLine());

        editorElement.setEmptyText(getSettings().getEmptyText());
        editorElement.setNullText(getSettings().getNullText());
        editorElement.setToolTipText(getSettings().getToolTipText());

        createActions();

        addTextChangedListener(editorElement);

        setElement(editorElement);

        if (getValue() == null && !getSettings().isNullAllowed())
            setValue("", false);

        updateElement();
    }

    private void addTextChangedListener(final MATLABTextEditorElement editorElement) {
        editorElement.addPropertyChangeListener("text", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String newValue = (String) evt.getNewValue();
                setValue(newValue);
                updateSetNullActionVisibility(editorElement, newValue);
            }
        });
    }

    private void createActions() {
        String actionText = getSettings().isNullAllowed() ? "Удалить текст" : "Очистить текст";

        setNullAction = new VisibleElementAction(actionText, "delete.png", "control DELETE") {
            @Override
            public void run(StructuredEditorModel model) {
                setValue(getSettings().isNullAllowed() ? null : "");
                updateElement();
                getModel().moveCaretToElement(getElement());
            }
        };
    }

    private StringSettings getSettings() {
        return getSettings(StringSettings.class);
    }

    @Override
    protected void updateElement() {
        MATLABTextEditorElement textElement = (MATLABTextEditorElement) getElement();
        String value = (String) getValue();
        textElement.setText(value);

        updateSetNullActionVisibility(textElement, value);
    }

    private void updateSetNullActionVisibility(MATLABTextEditorElement textElement, String value) {
        if (value == null || value.equals(""))
            textElement.removeAction(setNullAction);
        else
            textElement.addAction(setNullAction);
    }

}