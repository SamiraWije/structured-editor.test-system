package ru.ipo.structurededitor.view.elements;

import ru.ipo.structurededitor.view.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 02.01.2010
 * Time: 15:28:00
 */
public class TextElement extends VisibleElement {

    private String text;
    private Vector lines;

    //Color constants
    public static final Color NORMAL_TEXT_COLOR = Color.BLACK;

    private TextProperties textProperties = new TextProperties(Font.PLAIN, NORMAL_TEXT_COLOR);
    private TextProperties nullTextProperties = new TextProperties(Font.BOLD, Color.LIGHT_GRAY);
    public final int LINE_LENGTH = 50;

    private final String NULL_STRING = "[Пусто]";
    public String emptyString = NULL_STRING;

    public TextElement(StructuredEditorModel model, String text) {
        super(model);

        setHeight(1);

        addPropertyChangeListener("text", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                updateLines();
                setHeight(countHeight());
                setWidth(countWidth());
            }
        });

        setText(text);
    }


    protected Vector getLines() {
        return lines;
    }

    protected void updateLines() {
        try {
            if (text != null) {
                Collection list = Arrays.asList(text.split("\n", -1));
                lines = new Vector(list);
                int i = 0;
                while (i < lines.size()) {
                    String str = (String) lines.get(i);
                    if (str.length() > LINE_LENGTH) {
                        String str1 = str.substring(0, LINE_LENGTH);
                        int posSpace = str1.lastIndexOf(' ');
                        if (posSpace > -1) {
                            str1 = str1.substring(0, posSpace);
                        } else
                            posSpace = LINE_LENGTH-1;
                        String str2 = str.substring(posSpace + 1);
                        lines.set(i, str1);
                        lines.insertElementAt(str2, i + 1);

                    }
                    i++;
                }
            } else {
                lines = new Vector();
                lines.add("");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void settextProperties(TextProperties tp) {
        textProperties = tp;
    }

    public int countWidth() {
        if (isEmptyText())
            return emptyString.length();
        else {
            int maxLen = 0;
            for (Object o : lines) {
                int len = ((String) o).length();
                if (len > maxLen)
                    maxLen = len;
            }
            return maxLen;
        }

    }

    public int countHeight() {
        if (isEmptyText())
            return 1;
        else
            return lines.size();
    }

    public TextElement(StructuredEditorModel model) {
        this(model, null);
    }



    public void setEmptyString(String emptyString) {
        this.emptyString = emptyString;
        setWidth(countWidth());
    }

    @Override
    public void processMouseEvent(MouseEvent evt){
        /*TextPosition position = getAbsolutePosition();
        int x = position.getColumn();
        int y = position.getLine() + 1;*/

        if (evt.getClickCount()>=1)
        {
            getModel().setFocusedElement(this);
        }

    }
    public void drawElement(int x0, int y0, Display d) {
        //drawText("\u0421\u0430\u043c\u044b\u0435.",nullTextProperties,x0,y0,d);
        if (isEmptyText())
            drawText(emptyString, nullTextProperties, x0, y0, d);
        else
            drawText(textProperties, x0, y0, d);
    }

    public boolean isEmpty() {
        return (text == null || text.equals(""));
    }

    private void drawText(TextProperties textProperties, int x0, int y0, Display d) {
        //int x =x0;
        int y, i = 0;

        for (Object str : lines) {
            y = y0 + i;
            d.drawString((String) str, x0, y, textProperties);
            i++;
        }

    }

    private void drawText(String text, TextProperties textProperties, int x0, int y0, Display d) {
        d.drawString(text, x0, y0, textProperties);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        String oldText = this.text;
        this.text = text;
        pcs.firePropertyChange("text", oldText, text);
        /*StructuredEditorUI ui = getModel().getUI();
        if (ui != null)
            ui.redrawEditor();
        else*/
            repaint();
    }

    public void forcedSetText(String text) {
        this.text = text;
        updateLines();
        setWidth(countWidth());
        setHeight(countHeight());

        /*StructuredEditorUI ui = getModel().getUI();
        if (ui != null)
            ui.redrawEditor();
        else*/
            repaint();
    }

    public boolean isEmptyText() {
        return text == null || text.equals("");
    }
}