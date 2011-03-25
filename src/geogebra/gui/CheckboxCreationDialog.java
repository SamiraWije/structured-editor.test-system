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
import geogebra.euclidian.EuclidianView;
import geogebra.gui.inputbar.AutoCompleteTextField;
import geogebra.gui.view.algebra.InputPanel;
import geogebra.kernel.CircularDefinitionException;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoElement;
import geogebra.main.Application;
import geogebra.main.GeoElementSelectionListener;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.*;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.*;
import javax.swing.text.JTextComponent;

/**
 * Dialog to create a GeoBoolean object (checkbox) that determines the
 * visibility of a list of objects.
 */
public class CheckboxCreationDialog extends JDialog
    implements
      WindowFocusListener,
      ActionListener,
      GeoElementSelectionListener {

  private static final long serialVersionUID = 1L;

  private JTextComponent tfCaption;
  private JButton btApply, btCancel;
  private JPanel optionPane, btPanel;
  private DefaultListModel listModel;
  private DefaultComboBoxModel comboModel;

  private final Point location;
  private final Application app;
  private GeoBoolean geoBoolean;

  /**
   * Input Dialog for a GeoText object
   */
  protected CheckboxCreationDialog(Application app, Point location,
      GeoBoolean geoBoolean) {
    super(app.getFrame(), false);
    this.app = app;
    this.location = location;
    this.geoBoolean = geoBoolean;

    initLists();
    createGUI(geogebra.Menu.ShowCheckBox);
    pack();
    setLocationRelativeTo(app.getMainComponent());
  }

  public void actionPerformed(ActionEvent ae) {
    Object src = ae.getSource();

    if (src == btCancel)
      setVisible(false);
    else if (src == btApply) {
      apply();
      setVisible(false);
    }
  }

  private void apply() {
    // create new GeoBoolean
    if (geoBoolean == null) {
      geoBoolean = new GeoBoolean(app.getKernel().getConstruction());
      geoBoolean.setAbsoluteScreenLoc(location.x, location.y);
      geoBoolean.setLabel(null);
    }

    // set visibility condition for all GeoElements in list
    try {
      for (int i = 0; i < listModel.size(); i++) {
        GeoElement geo = (GeoElement) listModel.get(i);
        geo.setShowObjectCondition(geoBoolean);
      }
    } catch (CircularDefinitionException e) {
      app.showError("CircularDefinition");
    }

    // set caption text
    String strCaption = tfCaption.getText().trim();
    if (strCaption.length() > 0)
      geoBoolean.setCaption(strCaption);

    // update boolean (updates visibility of geos from list too)
    geoBoolean.setValue(true);
    geoBoolean.setEuclidianVisible(true);
    geoBoolean.setLabelVisible(true);
    geoBoolean.updateRepaint();
  }

  private void createGUI(String title) {
    setTitle(title);
    setResizable(false);

    // create caption panel
    JLabel captionLabel = new JLabel(geogebra.Menu.Button_Caption + ":");
    String initString = geoBoolean == null ? "" : geoBoolean.getCaption();
    InputPanel ip = new InputPanel(initString, app, 1, 15, true, true, false);
    tfCaption = ip.getTextComponent();
    if (tfCaption instanceof AutoCompleteTextField) {
      AutoCompleteTextField atf = (AutoCompleteTextField) tfCaption;
      atf.setAutoComplete(false);
    }

    captionLabel.setLabelFor(tfCaption);
    JPanel captionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    captionPanel.add(captionLabel);
    captionPanel.add(ip);

    // list panel
    JPanel listPanel = ToolCreationDialog.createInputOutputPanel(app,
        listModel, comboModel, false);

    // buttons
    btApply = new JButton(Plain.Apply);
    btApply.setActionCommand("Apply");
    btApply.addActionListener(this);
    btCancel = new JButton(Plain.Cancel);
    btCancel.setActionCommand("Cancel");
    btCancel.addActionListener(this);
    btPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    btPanel.add(btApply);
    btPanel.add(btCancel);

    System.out.println("Create the JOptionPane.");
    optionPane = new JPanel(new BorderLayout(5, 5));

    // create object list
    optionPane.add(captionPanel, BorderLayout.NORTH);
    optionPane.add(listPanel, BorderLayout.CENTER);
    optionPane.add(btPanel, BorderLayout.SOUTH);
    optionPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    // Make this dialog display it.
    setContentPane(optionPane);
  }

  public void geoElementSelected(GeoElement geo, boolean addToSelection) {
    listModel.addElement(geo);
  }

  private void initLists() {
    // fill combo box with all geos
    comboModel = new DefaultComboBoxModel();
    TreeSet<GeoElement> sortedSet = app.getKernel().getConstruction()
        .getGeoSetNameDescriptionOrder();

    // lists for combo boxes to select input and output objects
    // fill combobox models
    Iterator<GeoElement> it = sortedSet.iterator();
    comboModel.addElement(null);
    while (it.hasNext()) {
      GeoElement geo = it.next();
      if (geo.isEuclidianShowable())
        comboModel.addElement(geo);
    }

    // fill list with all selected geos
    listModel = new DefaultListModel() {
      private static final long serialVersionUID = -4451263400959463149L;

      @Override
      public void addElement(Object ob) {
        if (contains(ob))
          return;

        if (ob instanceof GeoElement) {
          GeoElement geo = (GeoElement) ob;
          if (geo.isEuclidianShowable()) {
            super.addElement(geo);
            comboModel.removeElement(geo);
          }
        }
      }
    };

    // add all selected geos to list
    for (int i = 0; i < app.getSelectedGeos().size(); i++) {
      GeoElement geo = (GeoElement) app.getSelectedGeos().get(i);
      listModel.addElement(geo);
    }
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

  public void windowGainedFocus(WindowEvent arg0) {
    // make sure this dialog is the current selection listener
    if (app.getMode() != EuclidianView.MODE_SELECTION_LISTENER
        || app.getCurrentSelectionListener() != this)
      app.setSelectionListenerMode(this);
  }

  public void windowLostFocus(WindowEvent arg0) {
  }

}
