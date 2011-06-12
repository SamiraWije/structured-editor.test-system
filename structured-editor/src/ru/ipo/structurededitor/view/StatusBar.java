package ru.ipo.structurededitor.view;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 22.09.2010
 * Time: 14:25:29
 * To change this template use File | Settings | File Templates.
 */

import javax.swing.*;
import java.awt.*;

public class StatusBar extends JLabel {

    /**
     * Creates a new instance of StatusBar
     *
     * @param str - value of status bar
     */
    public StatusBar(String str) {
        super();
        super.setPreferredSize(new Dimension(100, 16));
        setMessage(str);
    }

    public void setMessage(String message) {
        setText(" " + message);
    }
}