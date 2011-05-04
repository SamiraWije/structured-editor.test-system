package ru.ipo.structurededitor.view.elements;

import ru.ipo.structurededitor.view.ListDialog;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.TextPosition;
import ru.ipo.structurededitor.view.events.ComboBoxSelectListener;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 04.01.2010
 * Time: 0:15:01
 */
public class ComboBoxTextEditorElement<T> extends TextEditorElement {

    private EventListenerList listenerList = new EventListenerList();
    private HashMap<String, T> values = new HashMap<String, T>();

    //    private DefaultListModel listModel = new DefaultListModel();
//    private JList list;
    private ListDialog dialog;
    private Vector<String> popupList = new Vector<String>();

    public Vector<String> getFilteredPopupList() {
        return filteredPopupList;
    }

    private Vector<String> shortcutList = new Vector<String>();

    public Vector<String> getFilteredShortcutList() {
        return filteredShortcutList;
    }

    private Vector<String> filteredShortcutList;
    private Vector<String> filteredPopupList;
    private String longStr;

    public void addComboBoxSelectListener(ComboBoxSelectListener l) {
        listenerList.add(ComboBoxSelectListener.class, l);
    }

    public void removeComboBoxSelectListener(ComboBoxSelectListener l) {
        listenerList.remove(ComboBoxSelectListener.class, l);
    }

    //private JList popupList = new JList(listModel);
    //private int maxListItemLength = 0;

    /*public void actionPerformed(ActionEvent e) {
        setMarkPosition(-1, -1);
        String text = e.getActionCommand();
        if (text.indexOf(' ') != -1)
            text = text.substring(0, text.indexOf(' '));
        setText(text);
        setCaretPosition(text.length(), 0);
    } */

    /*public void showPopup(int x, int y) {
        /*if (isPopupVisible())
            getModel().getEditor().remove(popup);* /
        list = ListDialog.showDialog(getModel().getEditor(),  filteredPopupList.toArray(),
                filteredPopupList.get(0),longStr, x, y, this);
        dialog=ListDialog.getDialog();
        //return (String) list.getSelectedValue();
        //popup.show(getModel().getEditor(), x, y);

        //JPopupMenu popUp;
        //popUp.getUI().get

        //popup.show();
    } */

    /*public boolean isPopupVisible() {
        return popup.isVisible();
    }

    public void hidePopup() {
        getModel().getEditor().remove(popup);
        //popup.hide();
        //popup = null;
    } */

    public void fireSelect() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ComboBoxSelectListener.class) {
                // Lazily create the event:
                /*if (Event == null)
             fooEvent = new FooEvent(this);*/
                ((ComboBoxSelectListener) listeners[i + 1]).itemSelected();
            }
        }


    }

    public ComboBoxTextEditorElement(StructuredEditorModel model) {
        super(model);

        /*addPropertyChangeListener("text",new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                  updateList((String)evt.getNewValue(),getXCaretPosition());
            }
        });*/
        addPropertyChangeListener("xCaretPosition", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                updateList(getText(), (Integer) evt.getNewValue());
            }
        });
        //popup.setBorder(new LineBorder(Color.BLACK, 1));
    }

    private void updateList(String text, int xCaretPos) {
        filteredPopupList = new Vector<String>();
        filteredShortcutList = new Vector<String>();
        text = text.substring(0, xCaretPos);
        for (int i = 0; i < popupList.size(); i++) {
            String item = popupList.get(i);
            if (item.indexOf(text) == 0) {
                filteredPopupList.add(item);
                filteredShortcutList.add(shortcutList.get(i));
            }
        }

        if (filteredPopupList.isEmpty()) {
            filteredPopupList.add("(Пусто)");
            filteredShortcutList.add("");
        }
        justifyList();
        if (dialog != null && dialog.isVisible()) {
            dialog.getList().setListData(filteredPopupList);
            dialog.getList().setSelectedValue(filteredPopupList.get(0), false);
            dialog.getList().setPrototypeCellValue(longStr);
        }

    }

    @Override
    public void processKeyEvent(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                return;
            case KeyEvent.VK_SPACE:
                if (e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK && (dialog == null || !dialog.isVisible())) {
                    updateList("", 0);
                    findLocAndShowPopup();
                    e.consume();
                    return;
                }
                break;
            /*case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_ESCAPE:
                if (isPopupVisible())
                    hidePopup();
                break;
            case KeyEvent.VK_UP:
                int n1 = popup.getComponentCount();
                if (isPopupVisible()) {
                    sm = popup.getSelectionModel();
                    sm.setSelectedIndex((n1 + popup.getSelectionModel().getSelectedIndex() - 1) % n1);
                    e.consume();
                    return;
                }
                break;
            case KeyEvent.VK_DOWN:
                int n2 = popup.getSubElements().length;
                sm = popup.getSelectionModel();
                if (isPopupVisible()) {
                    sm.setSelectedIndex((n2 + popup.getSelectionModel().getSelectedIndex() + 1) % n2);
                    e.consume();
                    return;
                }
                break;
            case KeyEvent.VK_ENTER:
                if (isPopupVisible()) {
                    setMarkPosition(-1);
                    sm = popup.getSelectionModel();
                    String text = popup.getComponent(sm.getSelectedIndex()).getName();
                    if (text.indexOf(' ') != -1)
                        text = text.substring(0, text.indexOf(' '));
                    setText(text);
                    setCaretPosition(text.length());
                    hidePopup();
                    e.consume();
                    return;
                }
                break; */
        }

        super.processKeyEvent(e);
        /*if (isPopupVisible())
            findLocAndShowPopup();*/
    }

    private void findLocAndShowPopup() {
        TextPosition position = getAbsolutePosition();
        int x = position.getColumn() + getXCaretPosition();
        int y = position.getLine() + 1;

        //StructuredEditor editor = getModel().getEditor();
        /*showPopup(
                getModel().getUI().xToPixels(x) + editor.getLocationOnScreen().x,
                getModel().getUI().yToPixels(y) + editor.getLocationOnScreen().y
        );*/
        dialog = getModel().showPopup(filteredPopupList, longStr, x, y);
        dialog.setCmb(this);
        dialog.getList().addKeyListener(new KeyListener() {

            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                processKeyEvent(e);
            }

            public void keyReleased(KeyEvent e) {
            }
        });

        /*
        if (sm.getSelectedIndex() == -1) {
            if (popup.getComponentCount() == 0)
                addValue("(пусто)", null);
            sm.setSelectedIndex(0);
        } */
    }

    /*public void clearValues() {
        popup.removeAll();
        values.clear();
    } */

    public void justifyList() {
        int maxListItemLength = 0;
        //popup.show(getModel().getEditor(),0,0);
        if (filteredPopupList == null)
            filteredPopupList = (Vector) popupList.clone();
        JList list = new JList();
        FontMetrics fm = list.getFontMetrics(list.getFont());
        for (Object sto : filteredPopupList) {
            String str = (String) sto;
            int listItemLength = fm.charsWidth(str.toCharArray(), 0, str.length());
            if (listItemLength > maxListItemLength) {
                maxListItemLength = listItemLength;
                char charr[] = new char[str.toCharArray().length];
                System.arraycopy(str.toCharArray(), 0, charr, 0, str.toCharArray().length);
                longStr = new String(charr);
            }
        }
        int i = 0;
        for (Object lstItem : filteredPopupList) {
            String listItem = (String) lstItem;
            int k = fm.charsWidth(listItem.toCharArray(), 0, listItem.length());
            if (listItem.indexOf(' ') > -1) {
                while (k < maxListItemLength) {
                    listItem = listItem.replaceFirst(" ", "  ");
                    k = fm.charsWidth(listItem.toCharArray(), 0, listItem.length());
                }
                //popupList.remove(i);
                filteredPopupList.set(i, listItem);


            }
            i++;
        }

    }


    public void addValue(final String shortcut, String description, final T value) {
        String text = shortcut + " " + description;
        popupList.add(text);
        shortcutList.add(shortcut);
        values.put(shortcut, value);
    }

    public T getValue() {
        return values.get(getText());
    }

    public void setValue(T value) {
        for (String key : values.keySet())
            if (values.get(key).equals(value))
                setText(key);

        if (value == null)
            setText(null);
    }

    public void forcedSetValue(T value) {
        for (String key : values.keySet())
            if (values.get(key).equals(value)) {
                forcedSetText(key);
                updateList(getText(), getXCaretPosition());
            }
    }
}