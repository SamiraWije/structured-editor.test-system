/*package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.StructuredEditor;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.StructuredEditorUI;
import ru.ipo.structurededitor.view.TextPosition;
import ru.ipo.structurededitor.view.elements.TextEditorElement;
import ru.ipo.structurededitor.view.elements.VisibleElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 10.11.2010
 * Time: 16:22:29
 *\/
public class DateEditor extends FieldEditor {

    private TextEditorElement editorElement;
    private DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    private String error = null;
    private Popup popup = null;
    private StructuredEditorModel model;

    public DateEditor(Object o, String fieldName) {
        super(o, fieldName);
    }

    public DateEditor(Object o, String fieldName, int index) {
        super(o, fieldName, index);
    }

    private String dateToString(Date date) {
        return format.format(date);
    }

    private Date parseString(String date) throws Exception {
        if (date == null)
            throw new Exception("Date may not be null");
        Date res;
        try {
            res = format.parse(date);

            //test if parsed date != entered
            String resAsString = format.format(res);
            if (!resAsString.equals(date)) {
                //find difference
                int pos = 0;
                while (pos < resAsString.length() &&
                        pos < date.length() &&
                        resAsString.charAt(pos) == date.charAt(pos)) {
                    pos++;
                }
                throw new ParseException("Возможно, не хватает нулей", pos);
            }
        } catch (ParseException e) {
            throw new Exception(
                    "<html>Формат: ДД:ММ:ГГ ЧЧ:ММ<br>" +
                            "Ошибка в позиции " + (e.getErrorOffset() + 1) + ":<br>" +
                            e.getMessage() + "</html>"
            );
        }
        return res;
    }

    private String valueToString() {
        return dateToString((Date) getValue());
    }

    @Override
    public VisibleElement createElement(StructuredEditorModel model) {
        this.model = model;
        editorElement = new TextEditorElement(model, valueToString());

        format.setLenient(false);

        editorElement.addPropertyChangeListener("text", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                try {
                    setValue(parseString(editorElement.getText()));
                    hideErrorPopup();
                    error = null;
                } catch (Exception e) {
                    setValue(null);
                    error = e.getMessage();
                    showErrorPopup();
                }
            }
        });

        editorElement.addPropertyChangeListener("focused", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (editorElement.isFocused())
                    showErrorPopup();
                else
                    hideErrorPopup();
            }
        });

        final KeyStroke ctrlT = KeyStroke.getKeyStroke("control T");
        editorElement.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (ctrlT.equals(KeyStroke.getKeyStrokeForEvent(e))) {
                    e.consume();
                    setValue(new Date());
                }
            }
        });

        return editorElement;
    }

    private void showErrorPopup() {
        if (error == null)
            return;

        hideErrorPopup();
        TextPosition elementPosition = editorElement.getAbsolutePosition();
        StructuredEditor editor = model.getEditor();
        StructuredEditorUI ui = editor.getUI();
        //Rectangle rect = editor.getVisibleRect();
        Point onScreen = editor.getLocationOnScreen();
        popup = PopupFactory.getSharedInstance().getPopup(
                editor,
                new JLabel(error),
                ui.xToPixels(elementPosition.getColumn()) + onScreen.x,
                ui.yToPixels(elementPosition.getLine() + 1) + onScreen.y
        );
        popup.show();
    }

    private void hideErrorPopup() {
        if (popup != null)
            popup.hide();
    }

    @Override
    protected void updateElement() {
        editorElement.setText(valueToString());
    }
} */
