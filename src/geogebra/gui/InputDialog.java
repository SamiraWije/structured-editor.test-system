/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.gui;

import geogebra.Plain;
import geogebra.gui.inputbar.AutoCompleteTextField;
import geogebra.gui.view.algebra.InputPanel;
import geogebra.kernel.GeoElement;
import geogebra.main.Application;
import geogebra.main.GeoElementSelectionListener;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.text.JTextComponent;

public class InputDialog extends JDialog
    implements
      ActionListener,
      WindowFocusListener {

  private static final long serialVersionUID = 1L;

  protected static final int DEFAULT_COLUMNS = 30;
  public static final int DEFAULT_ROWS = 10;

  protected String inputText = null;
  protected InputPanel inputPanel;
  protected JButton btApply;

  protected JButton btCancel;

  private JButton btProperties;

  protected JButton btOK;
  private JPanel optionPane, buttonsPanel, btPanel, btPanel2;
  private GeoElementSelectionListener sl;
  private JLabel msgLabel;

  protected String initString;
  protected Application app;
  protected InputHandler inputHandler;

  private GeoElement geo;

  private final ArrayList tempArrayList = new ArrayList();

  public InputDialog(Application app, String message, String title,
      String initString, boolean autoComplete, InputHandler handler) {
    this(app, message, title, initString, autoComplete, handler, false, false,
        null);
  }

  protected InputDialog(Application app, String message, String title,
      String initString, boolean autoComplete, InputHandler handler,
      boolean modal, boolean selectInitText, GeoElement geo) {
    this(app.getFrame(), modal);
    this.app = app;
    this.geo = geo;
    inputHandler = handler;
    this.initString = initString;

    createGUI(title, message, autoComplete, DEFAULT_COLUMNS, 1, false, true,
        selectInitText, false, geo != null, geo != null);
    optionPane.add(inputPanel, BorderLayout.CENTER);
    centerOnScreen();

    if (initString != null && selectInitText)
      inputPanel.selectText();
    else
    // workaround for Mac OS X 10.5 problem (first character typed deleted)
    // TODO [UNTESTED]
    if (Application.MAC_OS)
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          inputPanel.getTextComponent().setSelectionStart(1);
          inputPanel.getTextComponent().setSelectionEnd(1);
        }
      });

  }

  /**
   * Creates a non-modal standard input dialog.
   */
  protected InputDialog(Application app, String message, String title,
      String initString, boolean autoComplete, InputHandler handler,
      GeoElement geo) {
    this(app, message, title, initString, autoComplete, handler, false, false,
        geo);
  }

  protected InputDialog(JFrame frame, boolean modal) {
    super(frame, modal);
  }

  /**
   * Handles button clicks for dialog.
   */
  public void actionPerformed(ActionEvent e) {
    Object source = e.getSource();

    // boolean finished = false;
    try {
      if (source == btOK || source == inputPanel.getTextComponent()) {
        inputText = inputPanel.getText();
        setVisible(!inputHandler.processInput(inputText));
      } else if (source == btApply) {
        inputText = inputPanel.getText();
        inputHandler.processInput(inputText);
      } else if (source == btCancel)
        setVisible(false);
      else if (source == btProperties && geo != null) {
        setVisible(false);
        tempArrayList.clear();
        tempArrayList.add(geo);
        app.getGuiManager().showPropertiesDialog(tempArrayList);

      }
    } catch (Exception ex) {
      // do nothing on uninitializedValue
      setVisible(false);
    }
    // setVisible(!finished);
  }

  protected void centerOnScreen() {
    pack();
    // center on screen
    setLocationRelativeTo(app.getMainComponent());
  }

  protected void createGUI(String title, String message, boolean autoComplete,
      int columns, int rows, boolean specialChars, boolean greekLetters,
      boolean selectInitText, boolean showDisplayChars, boolean showProperties,
      boolean showApply) {
    setResizable(false);

    // Create components to be displayed
    inputPanel = new InputPanel(initString, app, rows, columns, specialChars,
        greekLetters, showDisplayChars);

    sl = new GeoElementSelectionListener() {
      public void geoElementSelected(GeoElement geo, boolean addToSelection) {
        insertGeoElement(geo);
        inputPanel.getTextComponent().requestFocusInWindow();
      }
    };

    // add listeners to textfield
    JTextComponent textComp = inputPanel.getTextComponent();
    if (textComp instanceof AutoCompleteTextField) {
      AutoCompleteTextField tf = (AutoCompleteTextField) textComp;
      tf.setAutoComplete(autoComplete);
      tf.addActionListener(this);
    }

    // buttons
    btPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    btPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    btProperties = new JButton();
    btProperties.setActionCommand("OpenProperties");
    btProperties.addActionListener(this);
    btOK = new JButton();
    btOK.setActionCommand("OK");
    btOK.addActionListener(this);
    btCancel = new JButton();
    btCancel.setActionCommand("Cancel");
    btCancel.addActionListener(this);
    btApply = new JButton();
    btApply.setActionCommand("Apply");
    btApply.addActionListener(this);

    optionPane = new JPanel(new BorderLayout(5, 5));
    buttonsPanel = new JPanel(new BorderLayout(5, 5));
    msgLabel = new JLabel(message);

    if (showProperties) {
      btPanel2.add(btProperties);
      buttonsPanel.add(btPanel2, BorderLayout.EAST);
      buttonsPanel.add(btPanel, BorderLayout.WEST);
    } else
      buttonsPanel.add(btPanel, BorderLayout.EAST);

    btPanel.add(btOK);
    btPanel.add(btCancel);
    if (showApply)
      btPanel.add(btApply);

    optionPane.add(msgLabel, BorderLayout.NORTH);
    optionPane.add(buttonsPanel, BorderLayout.SOUTH);
    optionPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    // Make this dialog display it.
    setContentPane(optionPane);

    setLabels(title);
  }

  public JPanel getButtonPanel() {
    return btPanel;
  }

  public String getInputString() {
    return inputText;
  }

  public String getText() {
    return inputPanel.getText();
  }

  public void insertGeoElement(GeoElement geo) {
    if (geo != null)
      insertString(" " + geo.getLabel() + " ");
  }

  protected void insertString(String str) {
    if (str != null)
      inputPanel.insertString(str);
  }

  public void selectText() {
    inputPanel.selectText();
  }

  public void setCaretPosition(int pos) {
    JTextComponent tc = inputPanel.getTextComponent();
    tc.setCaretPosition(pos);
    tc.requestFocusInWindow();
  }

  /**
   * Update the labels of this component (applied if the language was changed).
   * 
   * @param The
   *          title of the dialog which is customized for every dialog
   */
  public void setLabels(String title) {
    setTitle(title);

    btOK.setText(Plain.OK);
    btCancel.setText(Plain.Cancel);
    btApply.setText(Plain.Apply);

    btProperties.setText(Plain.Properties + "...");
  }

  public void setRelativeCaretPosition(int pos) {
    JTextComponent tc = inputPanel.getTextComponent();
    try {
      tc.setCaretPosition(tc.getCaretPosition() + pos);
    } catch (Exception e) {
    }
    tc.requestFocusInWindow();
  }
  public void setText(String text) {
    inputPanel.setText(text);
  }

  @Override
  public void setVisible(boolean flag) {
    if (!isModal())
      if (flag)
        addWindowFocusListener(this);
      else {
        removeWindowFocusListener(this);
        app.setSelectionListenerMode(null);
      }
    super.setVisible(flag);
  }

  public void showGreekLetters(boolean flag) {
    inputPanel.showGreekLetters(flag);
  }

  public void showSpecialCharacters(boolean flag) {
    inputPanel.showSpecialChars(flag);
  }

  public void windowGainedFocus(WindowEvent arg0) {
    if (!isModal())
      app.setSelectionListenerMode(sl);
  }

  public void windowLostFocus(WindowEvent arg0) {
  }

}