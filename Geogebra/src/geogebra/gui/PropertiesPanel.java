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
import geogebra.gui.util.SpringUtilities;
import geogebra.gui.view.algebra.InputPanel;
import geogebra.kernel.AbsoluteScreenLocateable;
import geogebra.kernel.AlgoSlope;
import geogebra.kernel.CircularDefinitionException;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoImage;
import geogebra.kernel.GeoJavaScriptButton;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoSegment;
import geogebra.kernel.GeoText;
import geogebra.kernel.GeoVec3D;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.LimitedPath;
import geogebra.kernel.Locateable;
import geogebra.kernel.PointProperties;
import geogebra.kernel.TextProperties;
import geogebra.kernel.Traceable;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * panel for animation speed
 * 
 * @author adapted from AnimationStepPanel
 */
class AnimationSpeedPanel extends JPanel
    implements
      ActionListener,
      FocusListener,
      UpdateablePanel {

  private static final long serialVersionUID = 1L;

  private Object[] geos; // currently selected geos
  private final JTextField tfAnimSpeed;
  private boolean partOfSliderPanel = false;
  private final JComboBox animationModeCB;
  private final JLabel modeLabel, speedLabel;
  private final Kernel kernel;

  public AnimationSpeedPanel(Application app) {
    kernel = app.getKernel();

    // combo box for
    animationModeCB = new JComboBox();
    modeLabel = new JLabel();
    animationModeCB.addItem("\u21d4 " + Plain.Oscillating); // index 0
    animationModeCB.addItem("\u21d2 " + Plain.Increasing); // index 1
    animationModeCB.addItem("\u21d0 " + Plain.Decreasing); // index 2
    animationModeCB.addActionListener(this);
    animationModeCB.setSelectedIndex(GeoElement.ANIMATION_OSCILLATING);

    // text field for animation step
    speedLabel = new JLabel();
    tfAnimSpeed = new JTextField(5);
    speedLabel.setLabelFor(tfAnimSpeed);
    tfAnimSpeed.addActionListener(this);
    tfAnimSpeed.addFocusListener(this);

    // put it all together
    JPanel animPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    animPanel.add(speedLabel);
    animPanel.add(tfAnimSpeed);
    animPanel.add(modeLabel);
    animPanel.add(animationModeCB);

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    animPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    add(animPanel);

    setLabels();
  }

  /**
   * handle textfield changes
   */
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == tfAnimSpeed)
      doActionPerformed();
    else if (e.getSource() == animationModeCB)
      setType(animationModeCB.getSelectedIndex());
  }

  private boolean checkGeos(Object[] geos) {
    boolean geosOK = true;
    for (Object geo2 : geos) {
      GeoElement geo = (GeoElement) geo2;
      if (!geo.isChangeable() || geo.isGeoText() || geo.isGeoImage()
          || geo.isGeoList() || geo.isGeoBoolean() || !partOfSliderPanel
          && geo.isGeoNumeric() && geo.isIndependent() // slider
      ) {
        geosOK = false;
        break;
      }
    }

    return geosOK;
  }

  private void doActionPerformed() {
    NumberValue animSpeed = kernel.getAlgebraProcessor().evaluateToNumeric(
        tfAnimSpeed.getText());
    if (animSpeed != null) {
      for (Object geo2 : geos) {
        GeoElement geo = (GeoElement) geo2;
        geo.setAnimationSpeedObject(animSpeed);
        geo.updateCascade();
      }
      kernel.udpateNeedToShowAnimationButton();
      kernel.notifyRepaint();

    }
    update(geos);
  }

  public void focusGained(FocusEvent arg0) {
  }

  public void focusLost(FocusEvent e) {
    doActionPerformed();
  }

  public void setLabels() {
    modeLabel.setText(Plain.Repeat + ": ");
    speedLabel.setText(Plain.AnimationSpeed + ": ");

    int selectedIndex = animationModeCB.getSelectedIndex();
    animationModeCB.removeActionListener(this);

    animationModeCB.removeAllItems();
    animationModeCB.addItem("\u21d4 " + Plain.Oscillating); // index 0
    animationModeCB.addItem("\u21d2 " + Plain.Increasing); // index 1
    animationModeCB.addItem("\u21d0 " + Plain.Decreasing); // index 2

    animationModeCB.setSelectedIndex(selectedIndex);
    animationModeCB.addActionListener(this);
  }

  public void setPartOfSliderPanel() {
    partOfSliderPanel = true;
  }

  private void setType(int type) {

    if (geos == null)
      return;

    for (Object geo2 : geos) {
      GeoElement geo = (GeoElement) geo2;
      geo.setAnimationType(type);
      geo.updateRepaint();
    }

    update(geos);
  }

  public JPanel update(Object[] geos) {
    this.geos = geos;
    if (!checkGeos(geos))
      return null;

    tfAnimSpeed.removeActionListener(this);
    animationModeCB.removeActionListener(this);

    // check if properties have same values
    GeoElement temp, geo0 = (GeoElement) geos[0];
    boolean equalStep = true;
    boolean equalAnimationType = true;

    for (Object geo : geos) {
      temp = (GeoElement) geo;
      // same object visible value
      if (geo0.getAnimationSpeedObject() != temp.getAnimationSpeedObject())
        equalStep = false;
      if (geo0.getAnimationType() != temp.getAnimationType())
        equalAnimationType = false;
    }

    if (equalAnimationType)
      animationModeCB.setSelectedIndex(geo0.getAnimationType());
    else
      animationModeCB.setSelectedItem(null);

    kernel
        .setTemporaryPrintDecimals(PropertiesDialog.TEXT_FIELD_FRACTION_DIGITS);

    if (equalStep) {
      GeoElement speedObj = geo0.getAnimationSpeedObject();
      tfAnimSpeed.setText(speedObj == null ? "1" : speedObj.getLabel());
    } else
      tfAnimSpeed.setText("");

    // kernel.setMaximumFractionDigits(oldDigits);
    kernel.restorePrintAccuracy();

    tfAnimSpeed.addActionListener(this);
    animationModeCB.addActionListener(this);
    return this;
  }
}

/**
 * panel for animation step
 * 
 * @author Markus Hohenwarter
 */
class AnimationStepPanel extends JPanel
    implements
      ActionListener,
      FocusListener,
      UpdateablePanel {

  private static final long serialVersionUID = 1L;

  private Object[] geos; // currently selected geos
  private final JLabel label;
  private final AngleTextField tfAnimStep;
  private boolean partOfSliderPanel = false;

  private final Kernel kernel;

  public AnimationStepPanel(Application app) {
    kernel = app.getKernel();

    // text field for animation step
    label = new JLabel();
    tfAnimStep = new AngleTextField(5);
    label.setLabelFor(tfAnimStep);
    tfAnimStep.addActionListener(this);
    tfAnimStep.addFocusListener(this);

    // put it all together
    JPanel animPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    animPanel.add(label);
    animPanel.add(tfAnimStep);

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    animPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    add(animPanel);

    setLabels();
  }

  /**
   * handle textfield changes
   */
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == tfAnimStep)
      doActionPerformed();
  }

  private boolean checkGeos(Object[] geos) {
    boolean geosOK = true;
    for (Object geo2 : geos) {
      GeoElement geo = (GeoElement) geo2;
      if (!geo.isChangeable() || geo.isGeoText() || geo.isGeoImage()
          || geo.isGeoList() || geo.isGeoBoolean()
          || geo.isGeoJavaScriptButton() || !partOfSliderPanel
          && geo.isGeoNumeric() && geo.isIndependent() // slider
      ) {
        geosOK = false;
        break;
      }
    }

    return geosOK;
  }

  private void doActionPerformed() {
    double newVal = kernel.getAlgebraProcessor().evaluateToDouble(
        tfAnimStep.getText());
    if (!Double.isNaN(newVal))
      for (Object geo2 : geos) {
        GeoElement geo = (GeoElement) geo2;
        geo.setAnimationStep(newVal);
        geo.updateRepaint();
      }
    update(geos);
  }

  public void focusGained(FocusEvent arg0) {
  }

  public void focusLost(FocusEvent e) {
    doActionPerformed();
  }

  public void setLabels() {
    label.setText(Plain.AnimationStep + ": ");
  }

  public void setPartOfSliderPanel() {
    partOfSliderPanel = true;
  }

  public JPanel update(Object[] geos) {
    this.geos = geos;
    if (!checkGeos(geos))
      return null;

    tfAnimStep.removeActionListener(this);

    // check if properties have same values
    GeoElement temp, geo0 = (GeoElement) geos[0];
    boolean equalStep = true;
    boolean onlyAngles = true;

    for (Object geo : geos) {
      temp = (GeoElement) geo;
      // same object visible value
      if (geo0.getAnimationStep() != temp.getAnimationStep())
        equalStep = false;
      if (!temp.isGeoAngle())
        onlyAngles = false;
    }

    // set trace visible checkbox
    // int oldDigits = kernel.getMaximumFractionDigits();
    // kernel.setMaximumFractionDigits(PropertiesDialog.TEXT_FIELD_FRACTION_DIGITS);
    kernel
        .setTemporaryPrintDecimals(PropertiesDialog.TEXT_FIELD_FRACTION_DIGITS);

    if (equalStep)
      if (onlyAngles)
        tfAnimStep.setText(kernel.formatAngle(geo0.getAnimationStep())
            .toString());
      else
        tfAnimStep.setText(kernel.format(geo0.getAnimationStep()));
    else
      tfAnimStep.setText("");

    // kernel.setMaximumFractionDigits(oldDigits);
    kernel.restorePrintAccuracy();

    tfAnimStep.addActionListener(this);
    return this;
  }
}

/**
 * panel for condition to show object
 * 
 * @author Michael Borcherds 2008-04-01
 */
class ColorFunctionPanel extends JPanel
    implements
      ActionListener,
      FocusListener,
      UpdateablePanel {

  private static final long serialVersionUID = 1L;

  private Object[] geos; // currently selected geos
  private final JTextField tfRed, tfGreen, tfBlue;
  private final JButton btRemove;
  private final JLabel nameLabelR, nameLabelG, nameLabelB;

  private final Kernel kernel;
  private final PropertiesPanel propPanel;

  boolean processed = false;

  public ColorFunctionPanel(Application app, PropertiesPanel propPanel) {
    kernel = app.getKernel();
    this.propPanel = propPanel;

    // non auto complete input panel
    InputPanel inputPanelR = new InputPanel(null, app, 1, 7, false, false,
        false);
    InputPanel inputPanelG = new InputPanel(null, app, 1, 7, false, false,
        false);
    InputPanel inputPanelB = new InputPanel(null, app, 1, 7, false, false,
        false);
    tfRed = (AutoCompleteTextField) inputPanelR.getTextComponent();
    tfGreen = (AutoCompleteTextField) inputPanelG.getTextComponent();
    tfBlue = (AutoCompleteTextField) inputPanelB.getTextComponent();

    tfRed.addActionListener(this);
    tfRed.addFocusListener(this);
    tfGreen.addActionListener(this);
    tfGreen.addFocusListener(this);
    tfBlue.addActionListener(this);
    tfBlue.addFocusListener(this);

    nameLabelR = new JLabel();
    nameLabelR.setLabelFor(inputPanelR);
    nameLabelG = new JLabel();
    nameLabelG.setLabelFor(inputPanelR);
    nameLabelB = new JLabel();
    nameLabelB.setLabelFor(inputPanelR);

    btRemove = new JButton("\u2718");
    btRemove.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        for (Object geo2 : geos) {
          GeoElement geo = (GeoElement) geo2;
          geo.removeColorFunction();
          geo.updateRepaint();
        }
        tfRed.setText("");
        tfGreen.setText("");
        tfBlue.setText("");
      }
    });

    // put it all together
    setLayout(new FlowLayout(FlowLayout.LEFT));
    add(nameLabelR);
    add(inputPanelR);
    add(nameLabelG);
    add(inputPanelG);
    add(nameLabelB);
    add(inputPanelB);
    add(btRemove);

    setLabels();
  }

  /**
   * handle textfield changes
   */
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == tfRed || e.getSource() == tfGreen
        || e.getSource() == tfBlue)
      doActionPerformed();
  }

  // return true: want to be able to color all spreadsheet objects
  private boolean checkGeos(Object[] geos) {
    return true;
  }

  private void doActionPerformed() {
    processed = true;

    GeoList list = null;
    String strRed = tfRed.getText();
    String strGreen = tfGreen.getText();
    String strBlue = tfBlue.getText();
    if ((strRed == null || strRed.trim().length() == 0)
        && (strGreen == null || strGreen.trim().length() == 0)
        && (strBlue == null || strBlue.trim().length() == 0))
      // num = null;
      list = null;
    else {
      if (strRed == null || strRed.trim().length() == 0)
        strRed = "0";
      if (strGreen == null || strGreen.trim().length() == 0)
        strGreen = "0";
      if (strBlue == null || strBlue.trim().length() == 0)
        strBlue = "0";

      list = kernel.getAlgebraProcessor().evaluateToList(
          "{" + strRed + "," + strGreen + "," + strBlue + "}");
    }

    // set condition
    // try {
    if (list != null) { //
      if (list.get(0) instanceof NumberValue && // bugfix, enter "x" for a color
          list.get(1) instanceof NumberValue && //
          list.get(2) instanceof NumberValue)
        for (Object geo2 : geos) {
          GeoElement geo = (GeoElement) geo2;
          geo.setColorFunction(list);
        }

      list.updateRepaint();

      // to update "showObject" as well
      propPanel.updateSelection(geos);
    } else {
      // put back faulty text (for editing)
      tfRed.setText(strRed);
      tfGreen.setText(strGreen);
      tfBlue.setText(strBlue);
    }

  }

  public void focusGained(FocusEvent arg0) {
    processed = false;
  }

  public void focusLost(FocusEvent e) {
    if (!processed)
      doActionPerformed();
  }

  public void setLabels() {
    kernel.getApplication();

    setBorder(BorderFactory.createTitledBorder(geogebra.Menu.DynamicColors));

    nameLabelR.setText(geogebra.Menu.Red + ":");
    nameLabelG.setText(geogebra.Menu.Green + ":");
    nameLabelB.setText(geogebra.Menu.Blue + ":");

    btRemove.setToolTipText(Plain.Remove);
  }

  public JPanel update(Object[] geos) {
    this.geos = geos;
    if (!checkGeos(geos))
      return null;

    tfRed.removeActionListener(this);
    tfGreen.removeActionListener(this);
    tfBlue.removeActionListener(this);
    btRemove.removeActionListener(this);

    // take condition of first geo
    String strRed = "";
    String strGreen = "";
    String strBlue = "";
    GeoElement geo0 = (GeoElement) geos[0];
    GeoList colorList = geo0.getColorFunction();
    if (colorList != null) {
      strRed = colorList.get(0).getLabel();
      strGreen = colorList.get(1).getLabel();
      strBlue = colorList.get(2).getLabel();
    }

    for (Object geo2 : geos) {
      GeoElement geo = (GeoElement) geo2;
      GeoList colorListTemp = geo.getColorFunction();
      if (colorListTemp != null) {
        String strRedTemp = colorListTemp.get(0).getLabel();
        String strGreenTemp = colorListTemp.get(1).getLabel();
        String strBlueTemp = colorListTemp.get(2).getLabel();
        if (!strRed.equals(strRedTemp))
          strRed = "";
        if (!strGreen.equals(strGreenTemp))
          strGreen = "";
        if (!strBlue.equals(strBlueTemp))
          strBlue = "";
      }
    }

    tfRed.setText(strRed);
    tfRed.addActionListener(this);
    tfGreen.setText(strGreen);
    tfGreen.addActionListener(this);
    tfBlue.setText(strBlue);
    tfBlue.addActionListener(this);
    return this;
  }
}

/**
 * panel for name of object
 * 
 * @author Markus Hohenwarter
 */
class NamePanel extends JPanel
    implements
      ActionListener,
      FocusListener,
      UpdateablePanel {

  private static final long serialVersionUID = 1L;

  private final AutoCompleteTextField tfName, tfDefinition, tfCaption;
  private final JLabel nameLabel, defLabel, captionLabel;
  private final InputPanel inputPanelName, inputPanelDef, inputPanelCap;
  private final RenameInputHandler nameInputHandler;
  private final RedefineInputHandler defInputHandler;
  private GeoElement currentGeo;
  private boolean actionPerforming = false;

  public NamePanel(Application app) {
    // NAME PANEL
    nameInputHandler = new RenameInputHandler(app, null, false);

    // non auto complete input panel
    inputPanelName = new InputPanel(null, app, 1, 10, false, true, false);
    tfName = (AutoCompleteTextField) inputPanelName.getTextComponent();
    tfName.setAutoComplete(false);
    tfName.addActionListener(this);
    tfName.addFocusListener(this);

    // DEFINITON PANEL
    // Michael Borcherds 2007-12-31 BEGIN added third argument
    defInputHandler = new RedefineInputHandler(app, null, null);
    // Michael Borcherds 2007-12-31 END

    // definition field: non auto complete input panel
    inputPanelDef = new InputPanel(null, app, 1, 20, true, true, false);
    tfDefinition = (AutoCompleteTextField) inputPanelDef.getTextComponent();
    tfDefinition.setAutoComplete(false);
    tfDefinition.addActionListener(this);
    tfDefinition.addFocusListener(this);

    // caption field: non auto complete input panel
    inputPanelCap = new InputPanel(null, app, 1, 20, true, true, false);
    tfCaption = (AutoCompleteTextField) inputPanelCap.getTextComponent();
    tfCaption.setAutoComplete(false);
    tfCaption.addActionListener(this);
    tfCaption.addFocusListener(this);

    // name panel
    nameLabel = new JLabel();
    nameLabel.setLabelFor(inputPanelName);

    // definition panel
    defLabel = new JLabel();
    defLabel.setLabelFor(inputPanelDef);

    // caption panel
    captionLabel = new JLabel();
    captionLabel.setLabelFor(inputPanelCap);

    setLabels();
    updateGUI(true, true);
  }

  /**
   * handle textfield changes
   */
  public void actionPerformed(ActionEvent e) {
    doActionPerformed(e.getSource());
  }

  private boolean checkGeos(Object[] geos) {
    return geos.length == 1;
  }

  private synchronized void doActionPerformed(Object source) {
    actionPerforming = true;

    if (source == tfName) {
      // rename
      String strName = tfName.getText();
      nameInputHandler.processInput(strName);

      // reset label if not successful
      strName = currentGeo.getLabel();
      if (!strName.equals(tfName.getText())) {
        tfName.setText(strName);
        tfName.requestFocus();
      }
    } else if (source == tfDefinition) {
      String strDefinition = tfDefinition.getText();
      if (!strDefinition.equals(getDefText(currentGeo))) {
        defInputHandler.processInput(strDefinition);

        // reset definition string if not successful
        strDefinition = getDefText(currentGeo);
        if (!strDefinition.equals(tfDefinition.getText())) {
          tfDefinition.setText(strDefinition);
          tfDefinition.requestFocus();
        }
      }
    } else if (source == tfCaption) {
      String strCaption = tfCaption.getText();
      currentGeo.setCaption(strCaption);

      strCaption = getCaptionText(currentGeo);
      if (!strCaption.equals(tfCaption.getText().trim())) {
        tfCaption.setText(strCaption);
        tfCaption.requestFocus();
      }
    }
    currentGeo.updateRepaint();

    actionPerforming = false;
  }

  public void focusGained(FocusEvent arg0) {
  }

  public void focusLost(FocusEvent e) {
    if (!actionPerforming)
      doActionPerformed(e.getSource());
  }
  private String getCaptionText(GeoElement geo) {
    String strCap = currentGeo.getRawCaption();
    if (strCap.equals(currentGeo.getLabel()))
      return "";
    else
      return strCap;
  }

  private String getDefText(GeoElement geo) {
    /*
     * return geo.isIndependent() ? geo.toOutputValueString() :
     * geo.getCommandDescription();
     */
    return geo.getRedefineString(false, true);
  }

  public void setLabels() {
    nameLabel.setText(Plain.Name + ":");
    defLabel.setText(Plain.Definition + ":");
    captionLabel.setText(geogebra.Menu.Button_Caption + ":");
  }

  public JPanel update(Object[] geos) {
    if (!checkGeos(geos))
      return null;

    // NAME
    tfName.removeActionListener(this);

    // take name of first geo
    GeoElement geo0 = (GeoElement) geos[0];
    tfName.setText(geo0.getLabel());

    currentGeo = geo0;
    nameInputHandler.setGeoElement(geo0);

    tfName.addActionListener(this);

    // DEFINITION
    // boolean showDefinition = !(currentGeo.isGeoText() ||
    // currentGeo.isGeoImage());
    boolean showDefinition = currentGeo.isGeoText() ? ((GeoText) currentGeo)
        .isTextCommand() : !(currentGeo.isGeoImage()
        && currentGeo.isIndependent() || currentGeo.isGeoJavaScriptButton());
    if (showDefinition) {
      tfDefinition.removeActionListener(this);
      defInputHandler.setGeoElement(currentGeo);
      tfDefinition.setText(getDefText(currentGeo));
      tfDefinition.addActionListener(this);

      if (currentGeo.isIndependent())
        defLabel.setText(Plain.Value + ":");
      else
        defLabel.setText(Plain.Definition + ":");
    }

    // CAPTION
    boolean showCaption = !currentGeo.isTextValue(); // borcherds was
    // currentGeo.isGeoBoolean();
    if (showCaption) {
      tfCaption.removeActionListener(this);
      tfCaption.setText(getCaptionText(currentGeo));
      tfCaption.addActionListener(this);
    }
    // captionLabel.setVisible(showCaption);
    // inputPanelCap.setVisible(showCaption);

    updateGUI(showDefinition, showCaption);

    return this;
  }

  private void updateGUI(boolean showDefinition, boolean showCaption) {
    int rows = 1;
    removeAll();

    add(nameLabel);
    add(inputPanelName);

    if (showDefinition) {
      rows++;
      add(defLabel);
      add(inputPanelDef);
    }

    if (showCaption) {
      rows++;
      add(captionLabel);
      add(inputPanelCap);
    }

    // Lay out the panel
    setLayout(new SpringLayout());
    SpringUtilities.makeCompactGrid(this, rows, 2, // rows, cols
        5, 5, // initX, initY
        5, 5); // xPad, yPad
  }

}

/**
 * PropertiesPanel for displaying all gui elements for changing properties of
 * currently selected GeoElements.
 * 
 * @see update() in PropertiesPanel
 * @author Markus Hohenwarter
 */
public class PropertiesPanel extends JPanel {
  /**
   * panel to set object's absoluteScreenLocation flag
   * 
   * @author Markus Hohenwarter
   */
  private class AbsoluteScreenLocationPanel extends JPanel
      implements
        ItemListener,
        UpdateablePanel {
    /**
  	 * 
  	 */
    private static final long serialVersionUID = 1L;
    private Object[] geos; // currently selected geos
    private final JCheckBox cbAbsScreenLoc;

    public AbsoluteScreenLocationPanel() {
      // check boxes for show trace
      setLayout(new FlowLayout(FlowLayout.LEFT));
      cbAbsScreenLoc = new JCheckBox();
      cbAbsScreenLoc.addItemListener(this);

      // put it all together
      add(cbAbsScreenLoc);
    }

    private boolean checkGeos(Object[] geos) {
      for (Object geo2 : geos) {
        GeoElement geo = (GeoElement) geo2;
        if (geo instanceof AbsoluteScreenLocateable) {
          AbsoluteScreenLocateable absLoc = (AbsoluteScreenLocateable) geo;
          if (!absLoc.isAbsoluteScreenLocateable() || geo.isGeoBoolean()
              || geo.isGeoJavaScriptButton())
            return false;
        } else
          return false;
      }
      return true;
    }

    /**
     * listens to checkboxes and sets trace state
     */
    public void itemStateChanged(ItemEvent e) {
      AbsoluteScreenLocateable geo;
      Object source = e.getItemSelectable();

      // absolute screen location flag changed
      if (source == cbAbsScreenLoc) {
        boolean flag = cbAbsScreenLoc.isSelected();
        EuclidianView ev = app.getEuclidianView();
        for (Object geo2 : geos) {
          geo = (AbsoluteScreenLocateable) geo2;
          if (flag) {
            // convert real world to screen coords
            int x = ev.toScreenCoordX(geo.getRealWorldLocX());
            int y = ev.toScreenCoordY(geo.getRealWorldLocY());
            if (!geo.isAbsoluteScreenLocActive())
              geo.setAbsoluteScreenLoc(x, y);
          } else {
            // convert screen coords to real world
            double x = ev.toRealWorldCoordX(geo.getAbsoluteScreenLocX());
            double y = ev.toRealWorldCoordY(geo.getAbsoluteScreenLocY());
            if (geo.isAbsoluteScreenLocActive())
              geo.setRealWorldLoc(x, y);
          }
          geo.setAbsoluteScreenLocActive(flag);
          geo.toGeoElement().updateRepaint();
        }

        updateSelection(geos);
      }
    }

    public void setLabels() {
      cbAbsScreenLoc.setText(Plain.AbsoluteScreenLocation);
    }

    public JPanel update(Object[] geos) {
      this.geos = geos;
      if (!checkGeos(geos))
        return null;

      cbAbsScreenLoc.removeItemListener(this);

      // check if properties have same values
      AbsoluteScreenLocateable temp, geo0 = (AbsoluteScreenLocateable) geos[0];
      boolean equalVal = true;

      for (Object geo : geos) {
        temp = (AbsoluteScreenLocateable) geo;
        // same object visible value
        if (geo0.isAbsoluteScreenLocActive() != temp
            .isAbsoluteScreenLocActive())
          equalVal = false;
      }

      // set checkbox
      if (equalVal)
        cbAbsScreenLoc.setSelected(geo0.isAbsoluteScreenLocActive());
      else
        cbAbsScreenLoc.setSelected(false);

      cbAbsScreenLoc.addItemListener(this);
      return this;
    }
  }

  /**
   * panel for limted paths to set whether outlying intersection points are
   * allowed
   * 
   * @author Markus Hohenwarter
   */
  private class AllowOutlyingIntersectionsPanel extends JPanel
      implements
        ItemListener,
        UpdateablePanel {
    /**
  	 * 
  	 */
    private static final long serialVersionUID = 1L;
    private Object[] geos; // currently selected geos
    private final JCheckBox outlyingIntersectionsCB;

    public AllowOutlyingIntersectionsPanel() {
      // check boxes for show trace
      outlyingIntersectionsCB = new JCheckBox();
      outlyingIntersectionsCB.addItemListener(this);

      setLayout(new FlowLayout(FlowLayout.LEFT));
      add(outlyingIntersectionsCB);
    }

    private boolean checkGeos(Object[] geos) {
      for (Object geo2 : geos) {
        GeoElement geo = (GeoElement) geo2;
        if (!(geo instanceof LimitedPath))
          return false;
      }
      return true;
    }

    /**
     * listens to checkboxes and sets trace state
     */
    public void itemStateChanged(ItemEvent e) {
      LimitedPath geo;
      Object source = e.getItemSelectable();

      // show trace value changed
      if (source == outlyingIntersectionsCB)
        for (Object geo2 : geos) {
          geo = (LimitedPath) geo2;
          geo.setAllowOutlyingIntersections(outlyingIntersectionsCB
              .isSelected());
          geo.toGeoElement().updateRepaint();
        }
    }

    public void setLabels() {
      outlyingIntersectionsCB.setText(Plain.allowOutlyingIntersections);
    }

    public JPanel update(Object[] geos) {
      this.geos = geos;
      if (!checkGeos(geos))
        return null;

      outlyingIntersectionsCB.removeItemListener(this);

      // check if properties have same values
      LimitedPath temp, geo0 = (LimitedPath) geos[0];
      boolean equalVal = true;

      for (Object geo : geos) {
        temp = (LimitedPath) geo;
        // same value?
        if (geo0.allowOutlyingIntersections() != temp
            .allowOutlyingIntersections())
          equalVal = false;
      }

      // set trace visible checkbox
      if (equalVal)
        outlyingIntersectionsCB.setSelected(geo0.allowOutlyingIntersections());
      else
        outlyingIntersectionsCB.setSelected(false);

      outlyingIntersectionsCB.addItemListener(this);
      return this;
    }
  }
  /**
   * panel for angles to set whether reflex angles are allowed
   * 
   * @author Markus Hohenwarter
   */
  private class AllowReflexAnglePanel extends JPanel
      implements
        ItemListener,
        UpdateablePanel {
    /**
  	 * 
  	 */
    private static final long serialVersionUID = 1L;
    private Object[] geos; // currently selected geos
    private final JCheckBox reflexAngleCB;
    private final JCheckBox forceReflexAngleCB;

    public AllowReflexAnglePanel() {
      // Michael Borcherds 2007-11-19
      reflexAngleCB = new JCheckBox();
      reflexAngleCB.addItemListener(this);
      forceReflexAngleCB = new JCheckBox();
      forceReflexAngleCB.addItemListener(this);
      add(reflexAngleCB);

      // TODO make sure this line is commented out for 3.0 release, then
      // reinstated
      add(forceReflexAngleCB);

      setLayout(new FlowLayout(FlowLayout.LEFT));
    }

    private boolean checkGeos(Object[] geos) {
      for (Object geo2 : geos) {
        GeoElement geo = (GeoElement) geo2;
        if (geo.isIndependent() || !(geo instanceof GeoAngle))
          return false;
      }
      return true;
    }

    /**
     * listens to checkboxes and sets trace state
     */
    public void itemStateChanged(ItemEvent e) {
      GeoAngle geo;
      Object source = e.getItemSelectable();

      // Michael Borcherds 2007-11-19
      if (source == reflexAngleCB || source == forceReflexAngleCB)
        for (Object geo2 : geos) {
          geo = (GeoAngle) geo2;
          if (forceReflexAngleCB.isSelected()) {
            geo.setAngleStyle(3);
            reflexAngleCB.setEnabled(false);
          } else {
            reflexAngleCB.setEnabled(true);
            if (reflexAngleCB.isSelected())
              geo.setAngleStyle(0);
            else
              geo.setAngleStyle(2);
          }
          // Michael Borcherds 2007-11-19
          geo.updateRepaint();
        }
    }

    public void setLabels() {
      reflexAngleCB.setText(Plain.allowReflexAngle);
      forceReflexAngleCB.setText(Plain.forceReflexAngle);
    }

    public JPanel update(Object[] geos) {
      this.geos = geos;
      if (!checkGeos(geos))
        return null;

      reflexAngleCB.removeItemListener(this);
      forceReflexAngleCB.removeItemListener(this);

      // check if properties have same values
      GeoAngle temp, geo0 = (GeoAngle) geos[0];
      boolean equalangleStyle = true;
      boolean allreflex = true;

      for (Object geo : geos) {
        temp = (GeoAngle) geo;
        // same object visible value
        if (temp.getAngleStyle() != 3)
          allreflex = false;
        if (geo0.getAngleStyle() != temp.getAngleStyle())
          equalangleStyle = false;
      }

      if (allreflex == true)
        reflexAngleCB.setEnabled(false);
      else
        reflexAngleCB.setEnabled(true);

      if (equalangleStyle)
        switch (geo0.getAngleStyle()) {
          case 2 : // acute/obtuse
            reflexAngleCB.setSelected(false);
            forceReflexAngleCB.setSelected(false);
            break;
          case 3 : // force reflex
            reflexAngleCB.setSelected(true);
            forceReflexAngleCB.setSelected(true);
            break;
          default : // should be 0: anticlockwise
            reflexAngleCB.setSelected(true);
            forceReflexAngleCB.setSelected(false);
            break;

        }
      else {
        reflexAngleCB.setSelected(false);
        forceReflexAngleCB.setSelected(false);
      }

      reflexAngleCB.addItemListener(this);
      forceReflexAngleCB.addItemListener(this);
      return this;
    }
  }
  /**
   * panel for trace
   * 
   * @author adapted from TracePanel
   */
  private class AnimatingPanel extends JPanel
      implements
        ItemListener,
        UpdateablePanel {
    /**
  	 * 
  	 */
    private static final long serialVersionUID = 1L;
    private Object[] geos; // currently selected geos
    private final JCheckBox showAnimatingCB;

    public AnimatingPanel() {
      // check boxes for animating
      showAnimatingCB = new JCheckBox();
      showAnimatingCB.addItemListener(this);
      add(showAnimatingCB);
    }

    private boolean checkGeos(Object[] geos) {
      boolean geosOK = true;
      for (int i = 0; i < geos.length; i++)
        if (!((GeoElement) geos[i]).isAnimatable()) {
          geosOK = false;
          break;
        }
      return geosOK;
    }

    /**
     * listens to checkboxes and sets trace state
     */
    public void itemStateChanged(ItemEvent e) {
      GeoElement geo;
      Object source = e.getItemSelectable();

      // animating value changed
      if (source == showAnimatingCB) {
        boolean animate = showAnimatingCB.isSelected();
        for (Object geo2 : geos) {
          geo = (GeoElement) geo2;
          geo.setAnimating(animate);
          geo.updateRepaint();
        }

        // make sure that we are animating
        if (animate)
          kernel.getAnimatonManager().startAnimation();
      }
    }

    public void setLabels() {
      showAnimatingCB.setText(Plain.Animating);
    }

    public JPanel update(Object[] geos) {
      this.geos = geos;
      if (!checkGeos(geos))
        return null;

      showAnimatingCB.removeItemListener(this);

      // check if properties have same values
      GeoElement temp, geo0 = (GeoElement) geos[0];
      boolean equalAnimating = true;

      for (int i = 1; i < geos.length; i++) {
        temp = (GeoElement) geos[i];
        // same object visible value
        if (geo0.isAnimating() != temp.isAnimating())
          equalAnimating = false;
      }

      // set animating checkbox
      if (equalAnimating)
        showAnimatingCB.setSelected(geo0.isAnimating());
      else
        showAnimatingCB.setSelected(false);

      showAnimatingCB.addItemListener(this);
      return this;
    }
  }

  /**
   * panel to select the size of a GeoAngle's arc
   * 
   * @author Markus Hohenwarter
   */
  private class ArcSizePanel extends JPanel
      implements
        ChangeListener,
        UpdateablePanel {

    /**
  	 * 
  	 */
    private static final long serialVersionUID = 1L;
    private Object[] geos;
    private final JSlider slider;

    public ArcSizePanel() {
      // JLabel sizeLabel = new JLabel(app.getPlain("Size") + ":");
      slider = new JSlider(10, 100);
      slider.setMajorTickSpacing(10);
      slider.setMinorTickSpacing(5);
      slider.setPaintTicks(true);
      slider.setPaintLabels(true);
      slider.setSnapToTicks(true);

      /*
       * Dimension dim = slider.getPreferredSize(); dim.width =
       * SLIDER_MAX_WIDTH; slider.setMaximumSize(dim);
       * slider.setPreferredSize(dim);
       */

      // set label font
      Dictionary<?, ?> labelTable = slider.getLabelTable();
      Enumeration<?> en = labelTable.elements();
      JLabel label;
      while (en.hasMoreElements()) {
        label = (JLabel) en.nextElement();
        label.setFont(app.getSmallFont());
      }

      /*
       * //slider.setFont(app.getSmallFont()); slider.addChangeListener(this);
       * setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
       * sizeLabel.setAlignmentY(Component.TOP_ALIGNMENT);
       * slider.setAlignmentY(Component.TOP_ALIGNMENT);
       * //setBorder(BorderFactory
       * .createCompoundBorder(BorderFactory.createEtchedBorder(), //
       * BorderFactory.createEmptyBorder(3,5,0,5))); add(Box.createRigidArea(new
       * Dimension(5,0))); add(sizeLabel);
       */
      add(slider);
    }

    private boolean checkGeos(Object[] geos) {
      boolean geosOK = true;
      for (Object geo : geos)
        if (geo instanceof GeoAngle) {
          GeoAngle angle = (GeoAngle) geo;
          if (angle.isIndependent() || !angle.isDrawable()) {
            geosOK = false;
            break;
          }
        } else {
          geosOK = false;
          break;
        }
      return geosOK;
    }

    public void setLabels() {
      setBorder(BorderFactory.createTitledBorder(Plain.Size));
    }

    // added by Loïc BEGIN
    public void setMinValue() {
      slider.setValue(20);
    }
    // END

    /**
     * change listener implementation for slider
     */
    public void stateChanged(ChangeEvent e) {
      if (!slider.getValueIsAdjusting()) {
        int size = slider.getValue();
        GeoAngle angle;
        for (Object geo : geos) {
          angle = (GeoAngle) geo;
          // addded by Loïc BEGIN
          // check if decoration could be drawn
          if (size < 20
              && (angle.decorationType == GeoElement.DECORATION_ANGLE_THREE_ARCS || angle.decorationType == GeoElement.DECORATION_ANGLE_TWO_ARCS)) {
            angle.setArcSize(20);
            int selected = ((GeoAngle) geos[0]).decorationType;
            if (selected == GeoElement.DECORATION_ANGLE_THREE_ARCS
                || selected == GeoElement.DECORATION_ANGLE_TWO_ARCS)
              slider.setValue(20);
          }
          // END
          else
            angle.setArcSize(size);
          angle.updateRepaint();
        }
      }
    }

    public JPanel update(Object[] geos) {
      // check geos
      if (!checkGeos(geos))
        return null;

      this.geos = geos;
      slider.removeChangeListener(this);

      // set value to first point's size
      GeoAngle geo0 = (GeoAngle) geos[0];
      slider.setValue(geo0.getArcSize());

      slider.addChangeListener(this);
      return this;
    }
  }
  /**
   * panel for making an object auxiliary
   * 
   * @author Markus Hohenwarter
   */
  private class AuxiliaryObjectPanel extends JPanel
      implements
        ItemListener,
        UpdateablePanel {
    /**
  	 * 
  	 */
    private static final long serialVersionUID = 1L;
    private Object[] geos; // currently selected geos
    private final JCheckBox auxCB;

    public AuxiliaryObjectPanel() {
      // check boxes for show trace
      setLayout(new FlowLayout(FlowLayout.LEFT));
      auxCB = new JCheckBox();
      auxCB.addItemListener(this);
      add(auxCB);
    }

    private boolean checkGeos(Object[] geos) {
      // geo should be visible in algebra view
      for (Object geo2 : geos) {
        GeoElement geo = (GeoElement) geo2;
        if (!geo.isAlgebraVisible())
          return false;
      }
      return true;
    }

    /**
     * listens to checkboxes and sets trace state
     */
    public void itemStateChanged(ItemEvent e) {
      GeoElement geo;
      Object source = e.getItemSelectable();

      // show trace value changed
      if (source == auxCB)
        for (Object geo2 : geos) {
          geo = (GeoElement) geo2;
          geo.setAuxiliaryObject(auxCB.isSelected());
        }
    }

    public void setLabels() {
      auxCB.setText(Plain.AuxiliaryObject);
    }

    public JPanel update(Object[] geos) {
      this.geos = geos;
      if (!checkGeos(geos))
        return null;

      auxCB.removeItemListener(this);

      // check if properties have same values
      GeoElement temp, geo0 = (GeoElement) geos[0];
      boolean equalAux = true;

      for (Object geo : geos) {
        temp = (GeoElement) geo;
        // same object visible value
        if (geo0.isAuxiliaryObject() != temp.isAuxiliaryObject())
          equalAux = false;
      }

      // set trace visible checkbox
      if (equalAux)
        auxCB.setSelected(geo0.isAuxiliaryObject());
      else
        auxCB.setSelected(false);

      auxCB.addItemListener(this);
      return this;
    }
  }
  /**
   * panel to set a background image (only one checkbox)
   * 
   * @author Markus Hohenwarter
   */
  private class BackgroundImagePanel extends JPanel
      implements
        ItemListener,
        UpdateablePanel {
    /**
  	 * 
  	 */
    private static final long serialVersionUID = 1L;
    private Object[] geos; // currently selected geos
    private final JCheckBox isBGimage;

    public BackgroundImagePanel() {
      // check boxes for show trace
      isBGimage = new JCheckBox();
      isBGimage.addItemListener(this);
      setLayout(new FlowLayout(FlowLayout.LEFT));
      add(isBGimage);
    }

    private boolean checkGeos(Object[] geos) {
      for (int i = 0; i < geos.length; i++)
        if (!(geos[i] instanceof GeoImage))
          return false;
      return true;
    }

    /**
     * listens to checkboxes and sets trace state
     */
    public void itemStateChanged(ItemEvent e) {
      GeoImage geo;
      Object source = e.getItemSelectable();

      // show trace value changed
      if (source == isBGimage)
        for (Object geo2 : geos) {
          geo = (GeoImage) geo2;
          geo.setInBackground(isBGimage.isSelected());
          geo.updateRepaint();
        }
    }

    public void setLabels() {
      isBGimage.setText(Plain.BackgroundImage);
    }

    public JPanel update(Object[] geos) {
      this.geos = geos;
      if (!checkGeos(geos))
        return null;

      isBGimage.removeItemListener(this);

      // check if properties have same values
      GeoImage temp, geo0 = (GeoImage) geos[0];
      boolean equalIsBGimage = true;

      for (Object geo : geos) {
        temp = (GeoImage) geo;
        // same object visible value
        if (geo0.isInBackground() != temp.isInBackground())
          equalIsBGimage = false;
      }

      // set trace visible checkbox
      if (equalIsBGimage)
        isBGimage.setSelected(geo0.isInBackground());
      else
        isBGimage.setSelected(false);

      isBGimage.addItemListener(this);
      return this;
    }
  }
  /**
   * panel to fix checkbox (boolean object)
   */
  private class CheckBoxFixPanel extends JPanel
      implements
        ItemListener,
        UpdateablePanel {

    private static final long serialVersionUID = 1L;
    private Object[] geos; // currently selected geos
    private final JCheckBox checkboxFixCB;

    public CheckBoxFixPanel() {
      checkboxFixCB = new JCheckBox();
      checkboxFixCB.addItemListener(this);
      add(checkboxFixCB);
    }

    // show everything but numbers (note: drawable angles are shown)
    private boolean checkGeos(Object[] geos) {
      for (Object geo : geos)
        if (geo instanceof GeoBoolean) {
          GeoBoolean bool = (GeoBoolean) geo;
          if (!bool.isIndependent())
            return false;
        } else
          return false;
      return true;
    }

    /**
     * listens to checkboxes and sets object and label visible state
     */
    public void itemStateChanged(ItemEvent e) {
      Object source = e.getItemSelectable();

      // show object value changed
      if (source == checkboxFixCB)
        for (Object geo : geos) {
          GeoBoolean bool = (GeoBoolean) geo;
          bool.setCheckboxFixed(checkboxFixCB.isSelected());
          bool.updateRepaint();
        }
      updateSelection(geos);
    }

    public void setLabels() {
      checkboxFixCB.setText(Plain.FixCheckbox);
    }

    public JPanel update(Object[] geos) {
      this.geos = geos;
      if (!checkGeos(geos))
        return null;

      checkboxFixCB.removeItemListener(this);

      // check if properties have same values
      GeoBoolean temp, geo0 = (GeoBoolean) geos[0];
      boolean equalObjectVal = true;

      for (int i = 1; i < geos.length; i++) {
        temp = (GeoBoolean) geos[i];
        // same object visible value
        if (geo0.isCheckboxFixed() != temp.isCheckboxFixed()) {
          equalObjectVal = false;
          break;
        }
      }

      // set object visible checkbox
      if (equalObjectVal)
        checkboxFixCB.setSelected(geo0.isCheckboxFixed());
      else
        checkboxFixCB.setSelected(false);

      checkboxFixCB.addItemListener(this);
      return this;
    }

  } // CheckBoxFixPanel
  /**
   * panel color chooser and preview panel
   */
  private class ColorPanel extends JPanel
      implements
        UpdateablePanel,
        ChangeListener {

    private class PreviewPanel extends JPanel {
      /**
       * 
       */
      private static final long serialVersionUID = -6443064945050760083L;

      public PreviewPanel() {
        setPreferredSize(new Dimension(100, app.getFontSize() + 8));
        setBorder(BorderFactory.createRaisedBevelBorder());
      }
      @Override
      public void paintComponent(Graphics g) {
        Dimension size = getSize();

        g.setColor(getForeground());
        g.fillRect(0, 0, size.width, size.height);
      }
    }
    private static final long serialVersionUID = 1L;
    private Object[] geos; // currently selected geos
    private final JLabel previewLabel, currentColorLabel;

    private final JPanel previewPanel;

    public ColorPanel(JColorChooser colChooser) {
      colChooser.setLocale(app.getLocale());
      previewPanel = new PreviewPanel();
      previewLabel = new JLabel();
      currentColorLabel = new JLabel();
      AbstractColorChooserPanel[] tabs = colChooser.getChooserPanels();

      setLayout(new BorderLayout());

      /*
       * // Michael Borcherds 2008-03-14 // added RGB in a new tab // and moved
       * preview underneath JTabbedPane colorTabbedPane = new JTabbedPane();
       * colorTabbedPane.addTab( geogebra.Menu.Swatches, tabs[0] );
       * //colorTabbedPane.addTab( geogebra.Menu.HSB, tabs[1] );
       * colorTabbedPane.addTab( geogebra.Menu.RGB, tabs[2] );
       * colorTabbedPane.setSelectedIndex(0); JPanel p = new JPanel();
       * 
       * // create grid with one column p.setLayout(new GridBagLayout());
       * GridBagConstraints c = new GridBagConstraints(); c.fill =
       * GridBagConstraints.NONE; c.anchor = GridBagConstraints.NORTHWEST;
       * c.weightx = 0.0; c.weighty = 0.0;
       * 
       * c.gridx = 0; c.gridy = 0; c.gridwidth = 4; p.add(colorTabbedPane, c);
       * 
       * c.gridx = 0; c.gridy = 1; c.gridwidth = 1; c.insets = new
       * Insets(10,0,0,0); //top padding p.add(new JLabel(geogebra.Menu.Preview
       * + ": "), c);
       * 
       * c.gridx = 1; c.gridy = 1; c.gridwidth = 1; p.add(previewPanel, c);
       * 
       * c.weighty = 1.0; p.add(Box.createVerticalGlue(), c);
       */

      setLayout(new BorderLayout());
      add(tabs[0], BorderLayout.NORTH);

      JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
      p.add(previewLabel);
      p.add(previewPanel);
      p.add(currentColorLabel);
      add(p, BorderLayout.CENTER);

      // in order to get state changes we need to set color chooser to
      // a color that is different to the

      /*
       * // remove possible old change listeners from color chooser
       * ChangeListener [] listeners = (ChangeListener[])
       * colChooser.getListeners(ChangeListener.class); if (listeners != null) {
       * for (int i = 0; i< listeners.length; i++) {
       * colChooser.getSelectionModel().removeChangeListener( listeners[i]); } }
       */

      // colChooser.setColor(new Color(1, 1,1, 100));
      colChooser.getSelectionModel().addChangeListener(this);
    }

    // show everything but images
    private boolean checkGeos(Object[] geos) {
      for (Object geo : geos)
        /*
         * removed - we want to be able to change the color of everything in the
         * spreadsheet if (geos[i] instanceof GeoNumeric) { GeoNumeric num =
         * (GeoNumeric) geos[i]; if (!num.isDrawable()) return false; } else
         */
        if (geo instanceof GeoImage)
          return false;
      return true;
    }

    public void setLabels() {
      previewLabel.setText(geogebra.Menu.Preview + ": ");
    }

    /**
     * Listens for color chooser state changes
     */
    public void stateChanged(ChangeEvent arg0) {
      updateColor(colChooser.getColor());
    }

    public JPanel update(Object[] geos) {
      this.geos = geos;
      if (!checkGeos(geos))
        return null;

      // check if properties have same values
      GeoElement temp, geo0 = (GeoElement) geos[0];
      boolean equalObjColor = true;

      for (int i = 1; i < geos.length; i++) {
        temp = (GeoElement) geos[i];
        // same object color
        if (!geo0.getObjectColor().equals(temp.getObjectColor())) {
          equalObjColor = false;
          break;
        }
      }

      // set colorButton's color to object color
      Color col;
      if (equalObjColor) {
        col = geo0.getObjectColor();
        previewPanel.setToolTipText(col.getRed() + ", " + col.getGreen() + ", "
            + col.getBlue());
        currentColorLabel.setText("(" + previewPanel.getToolTipText() + ")");
      } else {
        col = null;
        previewPanel.setToolTipText("");
        currentColorLabel.setText("");
      }

      previewPanel.setForeground(col);
      return this;
    }

    /**
     * sets color of selected GeoElements
     */
    private void updateColor(Color col) {
      if (col == null || geos == null)
        return;

      // update preview panel
      previewPanel.setForeground(col);
      previewPanel.setToolTipText(col.getRed() + ", " + col.getGreen() + ", "
          + col.getBlue());
      currentColorLabel.setText("(" + previewPanel.getToolTipText() + ")");

      GeoElement geo;
      for (Object geo2 : geos) {
        geo = (GeoElement) geo2;
        geo.setObjColor(col);
        geo.updateRepaint();
      }

      Application.debug("Setting color RGB = " + col.getRed() + " "
          + col.getGreen() + " " + col.getBlue());

      // in order to get state changes we need to set color chooser to
      // a color that is not an available color
      colChooser.getSelectionModel().removeChangeListener(this);
      colChooser.setColor(new Color(0, 0, 1));

      colChooser.getSelectionModel().addChangeListener(this);
    }

  } // ColorPanel
  /**
   * panel to select the kind of conic equation for GeoConic
   * 
   * @author Markus Hohenwarter
   */
  private class ConicEqnPanel extends JPanel
      implements
        ActionListener,
        UpdateablePanel {
    /**
  	 * 
  	 */
    private static final long serialVersionUID = 1L;
    private Object[] geos;
    private final DefaultComboBoxModel eqnCBmodel;
    private final JComboBox eqnCB;
    private final JLabel eqnLabel;
    int implicitIndex, explicitIndex, specificIndex;

    public ConicEqnPanel() {
      eqnLabel = new JLabel();
      eqnCB = new JComboBox();
      eqnCBmodel = new DefaultComboBoxModel();
      eqnCB.setModel(eqnCBmodel);
      eqnCB.addActionListener(this);

      setLayout(new FlowLayout(FlowLayout.LEFT));
      add(eqnLabel);
      add(eqnCB);
    }

    /**
     * action listener implementation for coord combobox
     */
    public void actionPerformed(ActionEvent e) {
      Object source = e.getSource();
      if (source == eqnCB) {
        GeoConic geo;
        int selIndex = eqnCB.getSelectedIndex();
        if (selIndex == specificIndex)
          for (Object geo2 : geos) {
            geo = (GeoConic) geo2;
            geo.setToSpecific();
            geo.updateRepaint();
          }
        else if (selIndex == explicitIndex)
          for (Object geo2 : geos) {
            geo = (GeoConic) geo2;
            geo.setToExplicit();
            geo.updateRepaint();
          }
        else if (selIndex == implicitIndex)
          for (Object geo2 : geos) {
            geo = (GeoConic) geo2;
            geo.setToImplicit();
            geo.updateRepaint();
          }
      }
    }

    private boolean checkGeos(Object[] geos) {
      boolean geosOK = true;
      for (Object geo : geos)
        if (geo.getClass() != GeoConic.class) {
          geosOK = false;
          break;
        }
      return geosOK;
    }

    public void setLabels() {
      eqnLabel.setText(Plain.Equation + ":");

      if (geos != null)
        update(geos);

      // TODO: Anything else required? (F.S.)
    }

    public JPanel update(Object[] geos) {
      // check geos
      if (!checkGeos(geos))
        return null;

      this.geos = geos;
      eqnCB.removeActionListener(this);

      // check if all conics have same type and mode
      // and if specific, explicit is possible
      GeoConic temp, geo0 = (GeoConic) geos[0];
      boolean equalType = true;
      boolean equalMode = true;
      boolean specificPossible = geo0.isSpecificPossible();
      boolean explicitPossible = geo0.isExplicitPossible();
      for (int i = 1; i < geos.length; i++) {
        temp = (GeoConic) geos[i];
        // same type?
        if (geo0.getType() != temp.getType())
          equalType = false;
        // same mode?
        if (geo0.getToStringMode() != temp.getToStringMode())
          equalMode = false;
        // specific equation possible?
        if (!temp.isSpecificPossible())
          specificPossible = false;
        // explicit equation possible?
        if (!temp.isExplicitPossible())
          explicitPossible = false;
      }

      // specific can't be shown because there are different types
      if (!equalType)
        specificPossible = false;

      specificIndex = -1;
      explicitIndex = -1;
      implicitIndex = -1;
      int counter = -1;
      eqnCBmodel.removeAllElements();
      if (specificPossible) {
        eqnCBmodel.addElement(geo0.getSpecificEquation());
        specificIndex = ++counter;
      }
      if (explicitPossible) {
        eqnCBmodel.addElement(Plain.ExplicitConicEquation);
        explicitIndex = ++counter;
      }
      implicitIndex = ++counter;
      eqnCBmodel.addElement(Plain.ImplicitConicEquation);

      int mode;
      if (equalMode)
        mode = geo0.getToStringMode();
      else
        mode = -1;
      switch (mode) {
        case GeoConic.EQUATION_SPECIFIC :
          if (specificIndex > -1)
            eqnCB.setSelectedIndex(specificIndex);
          break;

        case GeoConic.EQUATION_EXPLICIT :
          if (explicitIndex > -1)
            eqnCB.setSelectedIndex(explicitIndex);
          break;

        case GeoConic.EQUATION_IMPLICIT :
          eqnCB.setSelectedIndex(implicitIndex);
          break;

        default :
          eqnCB.setSelectedItem(null);
      }

      eqnCB.addActionListener(this);
      return this;
    }
  }
  /**
   * panel to select the kind of coordinates (cartesian or polar) for GeoPoint
   * and GeoVector
   * 
   * @author Markus Hohenwarter
   */
  private class CoordPanel extends JPanel
      implements
        ActionListener,
        UpdateablePanel {
    /**
  	 * 
  	 */
    private static final long serialVersionUID = 1L;
    private Object[] geos;
    private final JLabel coordLabel;
    private final JComboBox coordCB;

    public CoordPanel() {
      coordLabel = new JLabel();
      coordCB = new JComboBox();

      setLayout(new FlowLayout(FlowLayout.LEFT));
      add(coordLabel);
      add(coordCB);
    }

    /**
     * action listener implementation for coord combobox
     */
    public void actionPerformed(ActionEvent e) {
      Object source = e.getSource();
      if (source == coordCB) {
        GeoVec3D geo;
        switch (coordCB.getSelectedIndex()) {
          case 0 : // Kernel.CARTESIAN
            for (Object geo2 : geos) {
              geo = (GeoVec3D) geo2;
              geo.setMode(Kernel.COORD_CARTESIAN);
              geo.updateRepaint();
            }
            break;

          case 1 : // Kernel.POLAR
            for (Object geo2 : geos) {
              geo = (GeoVec3D) geo2;
              geo.setMode(Kernel.COORD_POLAR);
              geo.updateRepaint();
            }
            break;
          case 2 : // Kernel.COMPLEX
            for (Object geo2 : geos) {
              geo = (GeoVec3D) geo2;
              geo.setMode(Kernel.COORD_COMPLEX);
              geo.updateRepaint();
            }
            break;
        }
      }
    }

    private boolean checkGeos(Object[] geos) {
      boolean geosOK = true;
      for (int i = 0; i < geos.length; i++) {
        if (!(geos[i] instanceof GeoPoint || geos[i] instanceof GeoVector))
          geosOK = false;

        System.out.println("check if fixed");
        if (((GeoElement) geos[i]).isFixed())
          geosOK = false;
      }
      return geosOK;
    }

    public void setLabels() {
      coordLabel.setText(Plain.Coordinates + ":");

      int selectedIndex = coordCB.getSelectedIndex();
      coordCB.removeActionListener(this);

      coordCB.removeAllItems();
      coordCB.addItem(Plain.CartesianCoords); // index 0
      coordCB.addItem(Plain.PolarCoords); // index 1
      coordCB.addItem(Plain.ComplexNumber); // index 2

      coordCB.setSelectedIndex(selectedIndex);
      coordCB.addActionListener(this);
    }

    public JPanel update(Object[] geos) {
      // check geos
      if (!checkGeos(geos))
        return null;

      this.geos = geos;
      coordCB.removeActionListener(this);

      // check if properties have same values
      GeoVec3D geo0 = (GeoVec3D) geos[0];
      boolean equalMode = true;

      int mode;
      if (equalMode)
        mode = geo0.getMode();
      else
        mode = -1;
      switch (mode) {
        case Kernel.COORD_CARTESIAN :
          coordCB.setSelectedIndex(0);
          break;
        case Kernel.COORD_POLAR :
          coordCB.setSelectedIndex(1);
          break;
        case Kernel.COORD_COMPLEX :
          coordCB.setSelectedIndex(2);
          break;
        default :
          coordCB.setSelectedItem(null);
      }

      coordCB.addActionListener(this);
      return this;
    }
  }
  /**
   * panel for three corner points of an image (A, B and D)
   */
  private class CornerPointsPanel extends JPanel
      implements
        ActionListener,
        FocusListener,
        UpdateablePanel {
    /**
  	 * 
  	 */
    private static final long serialVersionUID = 1L;
    private Object[] geos; // currently selected geos
    private final JLabel[] labelLocation;
    private final JComboBox[] cbLocation;
    private final DefaultComboBoxModel[] cbModel;

    public CornerPointsPanel() {
      labelLocation = new JLabel[3];
      cbLocation = new JComboBox[3];
      cbModel = new DefaultComboBoxModel[3];

      // put it all together
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

      // textfield for animation step
      for (int i = 0; i < 3; i++) {
        labelLocation[i] = new JLabel();
        cbLocation[i] = new JComboBox();
        cbLocation[i].setEditable(true);
        cbModel[i] = new DefaultComboBoxModel();
        cbLocation[i].setModel(cbModel[i]);
        labelLocation[i].setLabelFor(cbLocation[i]);
        cbLocation[i].addActionListener(this);
        cbLocation[i].addFocusListener(this);

        JPanel locPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        locPanel.add(labelLocation[i]);
        locPanel.add(cbLocation[i]);
        add(locPanel);
      }
    }

    /**
     * handle textfield changes
     */
    public void actionPerformed(ActionEvent e) {
      doActionPerformed(e.getSource());
    }

    private boolean checkGeos(Object[] geos) {
      for (Object geo2 : geos) {
        GeoElement geo = (GeoElement) geo2;
        if (geo instanceof GeoImage) {
          GeoImage img = (GeoImage) geo;
          if (img.isAbsoluteScreenLocActive() || !img.isIndependent())
            return false;
        } else
          return false;
      }
      return true;
    }

    private void doActionPerformed(Object source) {
      int number = 0;
      if (source == cbLocation[1])
        number = 1;
      else if (source == cbLocation[2])
        number = 2;

      String strLoc = (String) cbLocation[number].getSelectedItem();
      GeoPoint newLoc = null;

      if (strLoc == null || strLoc.trim().length() == 0)
        newLoc = null;
      else
        newLoc = kernel.getAlgebraProcessor().evaluateToPoint(strLoc);

      for (Object geo : geos) {
        GeoImage im = (GeoImage) geo;
        im.setCorner(newLoc, number);
        im.updateRepaint();
      }

      updateSelection(geos);
    }

    public void focusGained(FocusEvent arg0) {
    }

    public void focusLost(FocusEvent e) {
      doActionPerformed(e.getSource());
    }

    public void setLabels() {
      String strLabelStart = Plain.CornerPoint;

      for (int i = 0; i < 3; i++) {
        int pointNumber = i < 2 ? i + 1 : i + 2;
        labelLocation[i].setText(strLabelStart + " " + pointNumber + ":");
      }
    }

    public JPanel update(Object[] geos) {
      this.geos = geos;
      if (!checkGeos(geos))
        return null;

      for (int k = 0; k < 3; k++)
        cbLocation[k].removeActionListener(this);

      // repopulate model with names of points from the geoList's model
      // take all points from construction
      TreeSet<GeoElement> points = kernel.getConstruction()
          .getGeoSetLabelOrder(GeoElement.GEO_CLASS_POINT);
      if (points.size() != cbModel[0].getSize() - 1) {
        // clear models
        for (int k = 0; k < 3; k++) {
          cbModel[k].removeAllElements();
          cbModel[k].addElement(null);
        }

        // insert points
        Iterator<GeoElement> it = points.iterator();
        int count = 0;
        while (it.hasNext() || ++count > MAX_COMBOBOX_ENTRIES) {
          GeoPoint p = (GeoPoint) it.next();
          for (int k = 0; k < 3; k++)
            cbModel[k].addElement(p.getLabel());
        }
      }

      for (int k = 0; k < 3; k++) {
        // check if properties have same values
        GeoImage temp, geo0 = (GeoImage) geos[0];
        boolean equalLocation = true;

        for (Object geo : geos) {
          temp = (GeoImage) geo;
          // same object visible value
          if (geo0.getCorner(k) != temp.getCorner(k)) {
            equalLocation = false;
            break;
          }
        }

        // set location textfield
        GeoPoint p = geo0.getCorner(k);
        if (equalLocation && p != null)
          cbLocation[k].setSelectedItem(p.getLabel());
        else
          cbLocation[k].setSelectedItem(null);

        cbLocation[k].addActionListener(this);
      }
      return this;
    }
  }
  private class DecoAnglePanel extends JPanel
      implements
        ActionListener,
        UpdateablePanel {
    /**
     * 
     */
    private static final long serialVersionUID = 7328281965624057851L;
    private final JComboBox decoCombo;
    private final JLabel decoLabel;
    private Object[] geos;

    DecoAnglePanel() {
      super(new FlowLayout(FlowLayout.LEFT));
      // deco combobox
      DecorationAngleListRenderer renderer = new DecorationAngleListRenderer();
      renderer.setPreferredSize(new Dimension(80, 30));
      decoCombo = new JComboBox(GeoAngle.getDecoTypes());
      decoCombo.setRenderer(renderer);
      decoCombo.addActionListener(this);
      decoLabel = new JLabel();
      add(decoLabel);
      add(decoCombo);
    }

    public void actionPerformed(ActionEvent e) {
      Object source = e.getSource();
      if (source == decoCombo) {
        GeoAngle geo;
        int type = ((Integer) decoCombo.getSelectedItem()).intValue();
        for (Object geo2 : geos) {
          geo = (GeoAngle) geo2;
          geo.setDecorationType(type);
          // addded by Loïc BEGIN
          // check if decoration could be drawn
          if (geo.getArcSize() < 20
              && (geo.decorationType == GeoElement.DECORATION_ANGLE_THREE_ARCS || geo.decorationType == GeoElement.DECORATION_ANGLE_TWO_ARCS)) {
            geo.setArcSize(20);
            setSliderMinValue();
          }
          // END
          geo.updateRepaint();
        }
      }
    }

    private boolean checkGeos(Object[] geos) {
      boolean geosOK = true;
      for (int i = 0; i < geos.length; i++)
        if (!(geos[i] instanceof GeoAngle)) {
          geosOK = false;
          break;
        }
      return geosOK;
    }

    public void setLabels() {
      decoLabel.setText(Plain.Decoration + ":");
    }

    public JPanel update(Object[] geos) {
      // check geos
      if (!checkGeos(geos))
        return null;
      this.geos = geos;
      decoCombo.removeActionListener(this);

      // set slider value to first geo's decoration
      GeoAngle geo0 = (GeoAngle) geos[0];
      decoCombo.setSelectedIndex(geo0.decorationType);
      decoCombo.addActionListener(this);
      return this;
    }
  }
  // added by Loïc
  private class DecoSegmentPanel extends JPanel
      implements
        ActionListener,
        UpdateablePanel {
    /**
     * 
     */
    private static final long serialVersionUID = -7647210545771426134L;

    private final JComboBox decoCombo;
    private final JLabel decoLabel;
    private Object[] geos;

    DecoSegmentPanel() {
      super(new FlowLayout(FlowLayout.LEFT));
      // deco combobox
      DecorationListRenderer renderer = new DecorationListRenderer();
      renderer.setPreferredSize(new Dimension(130, app.getFontSize() + 6));
      decoCombo = new JComboBox(GeoSegment.getDecoTypes());
      decoCombo.setRenderer(renderer);
      decoCombo.addActionListener(this);

      decoLabel = new JLabel();
      add(decoLabel);
      add(decoCombo);
    }

    public void actionPerformed(ActionEvent e) {
      Object source = e.getSource();
      if (source == decoCombo) {
        GeoSegment geo;
        int type = ((Integer) decoCombo.getSelectedItem()).intValue();
        for (Object geo2 : geos) {
          geo = (GeoSegment) geo2;
          // Michael Borcherds 2007-11-20 BEGIN
          // geo.decorationType = type;
          geo.setDecorationType(type);
          // Michael Borcherds 2007-11-20 END
          geo.updateRepaint();
        }
      }
    }

    private boolean checkGeos(Object[] geos) {
      boolean geosOK = true;
      for (int i = 0; i < geos.length; i++)
        if (!(geos[i] instanceof GeoSegment)) {
          geosOK = false;
          break;
        }
      return geosOK;
    }

    public void setLabels() {
      decoLabel.setText(Plain.Decoration + ":");
    }

    public JPanel update(Object[] geos) {
      // check geos
      if (!checkGeos(geos))
        return null;
      this.geos = geos;
      decoCombo.removeActionListener(this);

      // set slider value to first geo's thickness
      GeoSegment geo0 = (GeoSegment) geos[0];
      decoCombo.setSelectedIndex(geo0.decorationType);

      decoCombo.addActionListener(this);
      return this;
    }
  }
  /**
   * panel to select the filling of a polygon or conic section
   * 
   * @author Markus Hohenwarter
   */
  private class FillingPanel extends JPanel
      implements
        ChangeListener,
        UpdateablePanel {

    /**
  	 * 
  	 */
    private static final long serialVersionUID = 1L;
    private Object[] geos;
    private final JSlider slider;

    public FillingPanel() {
      // JLabel sizeLabel = new JLabel(app.getPlain("Filling") + ":");
      slider = new JSlider(0, 100);
      slider.setMajorTickSpacing(25);
      slider.setMinorTickSpacing(5);
      slider.setPaintTicks(true);
      slider.setPaintLabels(true);
      slider.setSnapToTicks(true);

      /*
       * Dimension dim = slider.getPreferredSize(); dim.width =
       * SLIDER_MAX_WIDTH; slider.setMaximumSize(dim);
       * slider.setPreferredSize(dim);
       */

      // set label font
      Dictionary<?, ?> labelTable = slider.getLabelTable();
      Enumeration<?> en = labelTable.elements();
      JLabel label;
      while (en.hasMoreElements()) {
        label = (JLabel) en.nextElement();
        label.setFont(app.getSmallFont());
      }

      /*
       * //slider.setFont(app.getSmallFont()); slider.addChangeListener(this);
       * setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
       * sizeLabel.setAlignmentY(TOP_ALIGNMENT);
       * slider.setAlignmentY(TOP_ALIGNMENT);
       * //setBorder(BorderFactory.createCompoundBorder
       * (BorderFactory.createEtchedBorder(), //
       * BorderFactory.createEmptyBorder(3,5,0,5))); add(Box.createRigidArea(new
       * Dimension(5,0))); add(sizeLabel);
       */
      add(slider);
    }

    private boolean checkGeos(Object[] geos) {
      boolean geosOK = true;
      for (int i = 0; i < geos.length; i++)
        if (!((GeoElement) geos[i]).isFillable()) {
          geosOK = false;
          break;
        }
      return geosOK;
    }

    public void setLabels() {
      setBorder(BorderFactory.createTitledBorder(Plain.Filling));
    }

    /**
     * change listener implementation for slider
     */
    public void stateChanged(ChangeEvent e) {
      if (!slider.getValueIsAdjusting()) {
        float alpha = slider.getValue() / 100.0f;
        GeoElement geo;
        for (Object geo2 : geos) {
          geo = (GeoElement) geo2;
          geo.setAlphaValue(alpha);
          geo.updateRepaint();
        }
      }
    }

    public JPanel update(Object[] geos) {
      // check geos
      if (!checkGeos(geos))
        return null;

      this.geos = geos;
      slider.removeChangeListener(this);

      // set value to first geo's alpha value
      double alpha = ((GeoElement) geos[0]).getAlphaValue();
      slider.setValue((int) Math.round(alpha * 100));

      slider.addChangeListener(this);
      return this;
    }
  }
  /**
   * panel for fixing an object
   * 
   * @author Markus Hohenwarter
   */
  private class FixPanel extends JPanel
      implements
        ItemListener,
        UpdateablePanel {
    /**
  	 * 
  	 */
    private static final long serialVersionUID = 1L;
    private Object[] geos; // currently selected geos
    private final JCheckBox showFixCB;

    public FixPanel() {
      // check boxes for show trace
      showFixCB = new JCheckBox();
      showFixCB.addItemListener(this);
      add(showFixCB);
    }

    private boolean checkGeos(Object[] geos) {
      for (Object geo2 : geos) {
        GeoElement geo = (GeoElement) geo2;
        if (!geo.isFixable())
          return false;
      }
      return true;
    }

    /**
     * listens to checkboxes and sets trace state
     */
    public void itemStateChanged(ItemEvent e) {
      GeoElement geo;
      Object source = e.getItemSelectable();

      // show trace value changed
      if (source == showFixCB)
        for (Object geo2 : geos) {
          geo = (GeoElement) geo2;
          geo.setFixed(showFixCB.isSelected());
          geo.updateRepaint();
        }

      updateSelection(geos);
    }

    public void setLabels() {
      showFixCB.setText(Plain.FixObject);
    }

    public JPanel update(Object[] geos) {
      this.geos = geos;
      if (!checkGeos(geos))
        return null;

      showFixCB.removeItemListener(this);

      // check if properties have same values
      GeoElement temp, geo0 = (GeoElement) geos[0];
      boolean equalFix = true;

      for (Object geo : geos) {
        temp = (GeoElement) geo;
        // same object visible value
        if (geo0.isFixed() != temp.isFixed())
          equalFix = false;
      }

      // set trace visible checkbox
      if (equalFix)
        showFixCB.setSelected(geo0.isFixed());
      else
        showFixCB.setSelected(false);

      showFixCB.addItemListener(this);
      return this;
    }
  }
  /**
   * panel with label properties
   */
  private class LabelPanel extends JPanel
      implements
        ItemListener,
        ActionListener,
        UpdateablePanel {
    /**
  	 * 
  	 */
    private static final long serialVersionUID = 1L;
    private Object[] geos; // currently selected geos
    private final JCheckBox showLabelCB;
    private final JComboBox labelModeCB;
    private boolean showNameValueComboBox;

    public LabelPanel() {
      // check boxes for show object, show label
      showLabelCB = new JCheckBox();
      showLabelCB.addItemListener(this);

      // combo box for label mode: name or algebra
      labelModeCB = new JComboBox();
      labelModeCB.addActionListener(this);

      // labelPanel with show checkbox
      setLayout(new FlowLayout(FlowLayout.LEFT));
      add(showLabelCB);
      add(labelModeCB);
    }

    /**
     * action listener implementation for label mode combobox
     */
    public void actionPerformed(ActionEvent e) {
      Object source = e.getSource();
      if (source == labelModeCB) {
        GeoElement geo;
        int mode = labelModeCB.getSelectedIndex();
        for (Object geo2 : geos) {
          geo = (GeoElement) geo2;
          geo.setLabelMode(mode);
          geo.updateRepaint();
        }
      }
    }

    // show everything but numbers (note: drawable angles are shown)
    private boolean checkGeos(Object[] geos) {
      boolean geosOK = true;
      for (Object geo2 : geos) {
        GeoElement geo = (GeoElement) geo2;
        if (!geo.isLabelShowable()) {
          geosOK = false;
          break;
        }
      }
      return geosOK;
    }

    /**
     * listens to checkboxes and sets object and label visible state
     */
    public void itemStateChanged(ItemEvent e) {
      GeoElement geo;
      Object source = e.getItemSelectable();

      // show label value changed
      if (source == showLabelCB) {
        boolean flag = showLabelCB.isSelected();
        for (Object geo2 : geos) {
          geo = (GeoElement) geo2;
          geo.setLabelVisible(flag);
          geo.updateRepaint();
        }
        update(geos);
      }
    }

    public void setLabels() {
      showLabelCB.setText(Plain.ShowLabel + ":");

      int selectedIndex = labelModeCB.getSelectedIndex();
      labelModeCB.removeActionListener(this);

      labelModeCB.removeAllItems();
      labelModeCB.addItem(Plain.Name); // index 0
      labelModeCB.addItem(Plain.NameAndValue); // index 1
      labelModeCB.addItem(Plain.Value); // index 2
      labelModeCB.addItem(Plain.Caption); // index 3 Michael Borcherds

      labelModeCB.setSelectedIndex(selectedIndex);
      labelModeCB.removeActionListener(this);

      // change "Show Label:" to "Show Label" if there's no menu
      if (!showNameValueComboBox)
        showLabelCB.setText(Plain.ShowLabel);
      else
        showLabelCB.setText(Plain.ShowLabel + ":");
    }

    public JPanel update(Object[] geos) {
      this.geos = geos;
      if (!checkGeos(geos))
        return null;

      showLabelCB.removeItemListener(this);
      labelModeCB.removeActionListener(this);

      // check if properties have same values
      GeoElement temp, geo0 = (GeoElement) geos[0];
      boolean equalLabelVal = true;
      boolean equalLabelMode = true;
      showNameValueComboBox = geo0.isLabelValueShowable();

      for (int i = 1; i < geos.length; i++) {
        temp = (GeoElement) geos[i];
        // same label visible value
        if (geo0.isLabelVisible() != temp.isLabelVisible())
          equalLabelVal = false;
        // same label mode
        if (geo0.getLabelMode() != temp.getLabelMode())
          equalLabelMode = false;

        showNameValueComboBox = showNameValueComboBox
            && temp.isLabelValueShowable();
      }

      // change "Show Label:" to "Show Label" if there's no menu
      if (!showNameValueComboBox)
        showLabelCB.setText(Plain.ShowLabel);
      else
        showLabelCB.setText(Plain.ShowLabel + ":");

      // set label visible checkbox
      if (equalLabelVal) {
        showLabelCB.setSelected(geo0.isLabelVisible());
        labelModeCB.setEnabled(geo0.isLabelVisible());
      } else {
        showLabelCB.setSelected(false);
        labelModeCB.setEnabled(false);
      }

      // set label visible checkbox
      if (equalLabelMode)
        labelModeCB.setSelectedIndex(geo0.getLabelMode());
      else
        labelModeCB.setSelectedItem(null);

      // locus in selection
      labelModeCB.setVisible(showNameValueComboBox);
      showLabelCB.addItemListener(this);
      labelModeCB.addActionListener(this);
      return this;
    }

  } // LabelPanel
  /*
   * panel with layers properties Michael Borcherds
   */
  private class LayerPanel extends JPanel
      implements
        ItemListener,
        ActionListener,
        UpdateablePanel {
    /**
  	 * 
  	 */
    private static final long serialVersionUID = 1L;
    private Object[] geos; // currently selected geos
    JComboBox layerModeCB;
    private final JLabel layerLabel;

    public LayerPanel() {
      layerLabel = new JLabel();
      layerLabel.setLabelFor(layerModeCB);

      // combo box for label mode: name or algebra
      layerModeCB = new JComboBox();

      for (int layer = 0; layer <= EuclidianView.MAX_LAYERS; ++layer)
        layerModeCB.addItem(" " + layer);

      layerModeCB.addActionListener(this);

      // labelPanel with show checkbox
      setLayout(new FlowLayout(FlowLayout.LEFT));
      add(layerLabel);
      add(layerModeCB);
    }

    /**
     * action listener implementation for label mode combobox
     */
    public void actionPerformed(ActionEvent e) {
      Object source = e.getSource();
      if (source == layerModeCB) {
        GeoElement geo;
        int layer = layerModeCB.getSelectedIndex();
        for (Object geo2 : geos) {
          geo = (GeoElement) geo2;
          geo.setLayer(layer);
          geo.updateRepaint();
        }
      }
    }

    // show everything that's drawable
    // don't want layers for dependent numbers as we want to
    // minimise the XML for such objects to keep the spreadsheet fast
    private boolean checkGeos(Object[] geos) {
      boolean geosOK = true;
      for (int i = 0; i < geos.length; i++)
        if (!((GeoElement) geos[i]).isDrawable()) {
          geosOK = false;
          break;
        }
      return geosOK;
    }

    /**
     * listens to checkboxes and sets object and label visible state
     */
    public void itemStateChanged(ItemEvent e) {
    }

    public void setLabels() {
      layerLabel.setText(Plain.Layer + ":");
    }

    public JPanel update(Object[] geos) {
      this.geos = geos;
      if (!checkGeos(geos))
        return null;

      layerModeCB.removeActionListener(this);

      // check if properties have same values
      GeoElement temp, geo0 = (GeoElement) geos[0];
      boolean equalLayer = true;

      for (int i = 1; i < geos.length; i++) {
        temp = (GeoElement) geos[i];
        // same label visible value
        if (geo0.getLayer() != temp.getLayer())
          equalLayer = false;
      }

      if (equalLayer)
        layerModeCB.setSelectedIndex(geo0.getLayer());
      else
        layerModeCB.setSelectedItem(null);

      // locus in selection
      layerModeCB.addActionListener(this);
      return this;
    }

  } // LayersPanel
  /**
   * panel to select the kind of line equation for GeoLine
   * 
   * @author Markus Hohenwarter
   */
  private class LineEqnPanel extends JPanel
      implements
        ActionListener,
        UpdateablePanel {
    /**
  	 * 
  	 */
    private static final long serialVersionUID = 1L;
    private Object[] geos;
    private final JComboBox eqnCB;
    private final JLabel eqnLabel;

    public LineEqnPanel() {
      eqnLabel = new JLabel();
      eqnCB = new JComboBox();
      eqnCB.addActionListener(this);

      setLayout(new FlowLayout(FlowLayout.LEFT));
      add(eqnLabel);
      add(eqnCB);
    }

    /**
     * action listener implementation for coord combobox
     */
    public void actionPerformed(ActionEvent e) {
      Object source = e.getSource();
      if (source == eqnCB) {
        GeoLine geo;
        switch (eqnCB.getSelectedIndex()) {
          case 0 : // GeoLine.EQUATION_IMPLICIT
            for (Object geo2 : geos) {
              geo = (GeoLine) geo2;
              geo.setMode(GeoLine.EQUATION_IMPLICIT);
              geo.updateRepaint();
            }
            break;

          case 1 : // GeoLine.EQUATION_EXPLICIT
            for (Object geo2 : geos) {
              geo = (GeoLine) geo2;
              geo.setMode(GeoLine.EQUATION_EXPLICIT);
              geo.updateRepaint();
            }
            break;

          case 2 : // GeoLine.PARAMETRIC
            for (Object geo2 : geos) {
              geo = (GeoLine) geo2;
              geo.setMode(GeoLine.PARAMETRIC);
              geo.updateRepaint();
            }
            break;
        }
      }
    }

    private boolean checkGeos(Object[] geos) {
      boolean geosOK = true;
      for (int i = 0; i < geos.length; i++)
        if (!(geos[i] instanceof GeoLine) || geos[i] instanceof GeoSegment) {
          geosOK = false;
          break;
        }
      return geosOK;
    }

    public void setLabels() {
      eqnLabel.setText(Plain.Equation + ":");

      int selectedIndex = eqnCB.getSelectedIndex();
      eqnCB.removeActionListener(this);

      eqnCB.removeAllItems();
      eqnCB.addItem(Plain.ImplicitLineEquation);
      // index 0
      eqnCB.addItem(Plain.ExplicitLineEquation);
      // index 1
      eqnCB.addItem(Plain.ParametricForm); // index 2

      eqnCB.setSelectedIndex(selectedIndex);
      eqnCB.addActionListener(this);
    }

    public JPanel update(Object[] geos) {
      // check geos
      if (!checkGeos(geos))
        return null;

      this.geos = geos;
      eqnCB.removeActionListener(this);

      // check if properties have same values
      GeoLine temp, geo0 = (GeoLine) geos[0];
      boolean equalMode = true;
      for (int i = 1; i < geos.length; i++) {
        temp = (GeoLine) geos[i];
        // same mode?
        if (geo0.getMode() != temp.getMode())
          equalMode = false;
      }

      int mode;
      if (equalMode)
        mode = geo0.getMode();
      else
        mode = -1;
      switch (mode) {
        case GeoLine.EQUATION_IMPLICIT :
          eqnCB.setSelectedIndex(0);
          break;
        case GeoLine.EQUATION_EXPLICIT :
          eqnCB.setSelectedIndex(1);
          break;
        case GeoLine.PARAMETRIC :
          eqnCB.setSelectedIndex(2);
          break;
        default :
          eqnCB.setSelectedItem(null);
      }

      eqnCB.addActionListener(this);
      return this;
    }
  }
  /**
   * panel to select thickness and style (dashing) of a GeoLine
   * 
   * @author Markus Hohenwarter
   */
  private class LineStylePanel extends JPanel
      implements
        ChangeListener,
        ActionListener,
        UpdateablePanel {

    /**
  	 * 
  	 */
    private static final long serialVersionUID = 1L;
    private Object[] geos;
    private final JSlider slider;
    private final JPanel thicknessPanel;
    private final JLabel dashLabel;
    private final JComboBox dashCB;

    public LineStylePanel() {
      // thickness slider
      slider = new JSlider(1, 13);
      slider.setMajorTickSpacing(2);
      slider.setMinorTickSpacing(1);
      slider.setPaintTicks(true);
      slider.setPaintLabels(true);
      slider.setSnapToTicks(true);

      /*
       * Dimension dim = slider.getPreferredSize(); dim.width =
       * SLIDER_MAX_WIDTH; slider.setMaximumSize(dim);
       * slider.setPreferredSize(dim);
       */

      // set label font
      Dictionary<?, ?> labelTable = slider.getLabelTable();
      Enumeration<?> en = labelTable.elements();
      JLabel label;
      while (en.hasMoreElements()) {
        label = (JLabel) en.nextElement();
        label.setFont(app.getSmallFont());
      }
      // slider.setFont(app.getSmallFont());
      slider.addChangeListener(this);

      // line style combobox (dashing)
      DashListRenderer renderer = new DashListRenderer();
      renderer.setPreferredSize(new Dimension(130, app.getFontSize() + 6));
      dashCB = new JComboBox(EuclidianView.getLineTypes());
      dashCB.setRenderer(renderer);
      dashCB.addActionListener(this);

      // line style panel
      JPanel dashPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      dashLabel = new JLabel();
      dashPanel.add(dashLabel);
      dashPanel.add(dashCB);

      // thickness panel
      thicknessPanel = new JPanel();
      /*
       * JLabel thicknessLabel = new JLabel(app.getPlain("Thickness") + ":");
       * thicknessPanel.setLayout(new BoxLayout(thicknessPanel,
       * BoxLayout.X_AXIS));
       * thicknessLabel.setAlignmentY(Component.TOP_ALIGNMENT);
       * slider.setAlignmentY(Component.TOP_ALIGNMENT);
       * //thicknessPanel.setBorder
       * (BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
       * // BorderFactory.createEmptyBorder(3,5,0,5)));
       * thicknessPanel.add(Box.createRigidArea(new Dimension(5,0)));
       * thicknessPanel.add(thicknessLabel);
       */
      thicknessPanel.add(slider);

      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      thicknessPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
      dashPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
      add(thicknessPanel);
      add(dashPanel);
    }

    /**
     * action listener implementation for coord combobox
     */
    public void actionPerformed(ActionEvent e) {
      Object source = e.getSource();
      if (source == dashCB) {
        GeoElement geo;
        int type = ((Integer) dashCB.getSelectedItem()).intValue();
        for (Object geo2 : geos) {
          geo = (GeoElement) geo2;
          geo.setLineType(type);
          geo.updateRepaint();
        }
      }
    }

    private boolean allPolygons(Object[] geos) {

      if (geos == null || geos.length == 0)
        return false;

      for (int i = 0; i < geos.length; i++)
        if (!((GeoElement) geos[i]).getGeoElementForPropertiesDialog()
            .isGeoPolygon())
          return false;

      return true;

    }

    private boolean checkGeos(Object[] geos) {
      boolean geosOK = true;
      for (Object geo2 : geos) {
        GeoElement geo = ((GeoElement) geo2).getGeoElementForPropertiesDialog();
        if (!(geo.isPath() || geo.isGeoPolygon() || geo.isGeoLocus()
            && ((GeoList) geo).showLineProperties() || geo.isGeoList() || geo
            .isGeoNumeric()
            && ((GeoNumeric) geo).isDrawable())) {
          geosOK = false;
          break;
        }
      }
      return geosOK;
    }

    public void setLabels() {
      thicknessPanel.setBorder(BorderFactory
          .createTitledBorder(Plain.Thickness));

      dashLabel.setText(Plain.LineStyle + ":");
    }

    /**
     * change listener implementation for slider
     */
    public void stateChanged(ChangeEvent e) {
      if (!slider.getValueIsAdjusting()) {
        int size = slider.getValue();
        GeoElement geo;
        for (Object geo2 : geos) {
          geo = (GeoElement) geo2;
          geo.setLineThickness(size);
          geo.updateRepaint();
        }
      }
    }

    public JPanel update(Object[] geos) {
      // check geos
      if (!checkGeos(geos))
        return null;

      this.geos = geos;
      slider.removeChangeListener(this);
      dashCB.removeActionListener(this);

      // set slider value to first geo's thickness
      GeoElement temp, geo0 = (GeoElement) geos[0];
      slider.setValue(geo0.getLineThickness());

      // allow polygons to have thickness 0
      slider.setMinimum(allPolygons(geos) ? 0 : 1);

      // check if geos have same line style
      boolean equalStyle = true;
      for (int i = 1; i < geos.length; i++) {
        temp = (GeoElement) geos[i];
        // same style?
        if (geo0.getLineType() != temp.getLineType())
          equalStyle = false;
      }

      // select common line style
      if (equalStyle) {
        int type = geo0.getLineType();
        for (int i = 0; i < dashCB.getItemCount(); i++)
          if (type == ((Integer) dashCB.getItemAt(i)).intValue()) {
            dashCB.setSelectedIndex(i);
            break;
          }
      } else
        dashCB.setSelectedItem(null);

      slider.addChangeListener(this);
      dashCB.addActionListener(this);
      return this;
    }
  }
  /**
   * panel to select the size of a GeoPoint
   * 
   * @author Markus Hohenwarter
   */
  private class PointSizePanel extends JPanel
      implements
        ChangeListener,
        UpdateablePanel {

    /**
  	 * 
  	 */
    private static final long serialVersionUID = 1L;
    private Object[] geos;
    private final JSlider slider;

    public PointSizePanel() {
      // setBorder(BorderFactory.createTitledBorder(app.getPlain("Size")));
      // JLabel sizeLabel = new JLabel(app.getPlain("Size") + ":");

      slider = new JSlider(1, 9);
      slider.setMajorTickSpacing(2);
      slider.setMinorTickSpacing(1);
      slider.setPaintTicks(true);
      slider.setPaintLabels(true);
      slider.setSnapToTicks(true);

      /*
       * Dimension dim = slider.getPreferredSize(); dim.width =
       * SLIDER_MAX_WIDTH; slider.setMaximumSize(dim);
       * slider.setPreferredSize(dim);
       */

      // set label font
      Dictionary<?, ?> labelTable = slider.getLabelTable();
      Enumeration<?> en = labelTable.elements();
      JLabel label;
      while (en.hasMoreElements()) {
        label = (JLabel) en.nextElement();
        label.setFont(app.getSmallFont());
      }

      slider.setFont(app.getSmallFont());
      slider.addChangeListener(this);

      add(slider);
    }

    private boolean checkGeos(Object[] geos) {
      boolean geosOK = true;
      for (Object geo2 : geos) {
        GeoElement geo = (GeoElement) geo2;
        if (!geo.getGeoElementForPropertiesDialog().isGeoPoint()
            && !(geo.isGeoList() && ((GeoList) geo).showPointProperties())) {
          geosOK = false;
          break;
        }
      }
      return geosOK;
    }

    public void setLabels() {
      setBorder(BorderFactory.createTitledBorder(Plain.PointSize));
    }

    /**
     * change listener implementation for slider
     */
    public void stateChanged(ChangeEvent e) {
      if (!slider.getValueIsAdjusting()) {
        int size = slider.getValue();
        PointProperties point;
        for (Object geo : geos) {
          point = (PointProperties) geo;
          point.setPointSize(size);
          point.updateRepaint();
        }
      }
    }

    public JPanel update(Object[] geos) {
      // check geos
      if (!checkGeos(geos))
        return null;

      this.geos = geos;
      slider.removeChangeListener(this);

      // set value to first point's size
      PointProperties geo0 = (PointProperties) geos[0];
      slider.setValue(geo0.getPointSize());

      slider.addChangeListener(this);
      return this;
    }
  }

  /**
   * panel to change the point style
   * 
   * @author Florian Sonner
   * @version 2008-07-17
   */
  private class PointStylePanel extends JPanel
      implements
        UpdateablePanel,
        ActionListener {
    private static final long serialVersionUID = 1L;
    private Object[] geos;
    private final JRadioButton[] buttons;

    public PointStylePanel() {
      ButtonGroup buttonGroup = new ButtonGroup();

      String[] strPointStyle = {"\u25cf", "\u25cb", "\u2716"};
      String[] strPointStyleAC = {"0", "2", "1"};
      buttons = new JRadioButton[strPointStyle.length];

      for (int i = 0; i < strPointStyle.length; ++i) {
        buttons[i] = new JRadioButton(strPointStyle[i]);
        buttons[i].setActionCommand(strPointStyleAC[i]);
        buttons[i].addActionListener(this);
        buttons[i].setFont(app.getSmallFont());

        if (!strPointStyleAC[i].equals("-1"))
          buttons[i].setFont(app.getSmallFont());

        buttonGroup.add(buttons[i]);
        add(buttons[i]);
      }
    }

    public void actionPerformed(ActionEvent e) {
      int style = Integer.parseInt(e.getActionCommand());

      PointProperties point;
      for (Object geo : geos) {
        point = (PointProperties) geo;
        point.setPointStyle(style);
        point.updateRepaint();
      }
    }

    private boolean checkGeos(Object[] geos) {
      boolean geosOK = true;
      for (Object geo2 : geos) {
        GeoElement geo = (GeoElement) geo2;
        if (!geo.getGeoElementForPropertiesDialog().isGeoPoint()
            && !(geo.isGeoList() && ((GeoList) geo).showPointProperties())) {
          geosOK = false;
          break;
        }
      }
      return geosOK;
    }

    public void setLabels() {
      setBorder(BorderFactory.createTitledBorder(geogebra.Menu.PointStyle));
    }

    public JPanel update(Object[] geos) {
      // check geos
      if (!checkGeos(geos))
        return null;

      this.geos = geos;

      // set value to first point's style
      PointProperties geo0 = (PointProperties) geos[0];

      for (int i = 0; i < buttons.length; ++i)
        if (Integer.parseInt(buttons[i].getActionCommand()) == geo0
            .getPointStyle())
          buttons[i].setSelected(true);
        else
          buttons[i].setSelected(false);

      return this;
    }
  }
  // added 3/11/06
  private class RightAnglePanel extends JPanel
      implements
        ActionListener,
        UpdateablePanel {
    /**
     * 
     */
    private static final long serialVersionUID = -7297769432972306592L;
    private final JCheckBox emphasizeRightAngle;
    private Object[] geos;
    RightAnglePanel() {
      super(new FlowLayout(FlowLayout.LEFT));
      emphasizeRightAngle = new JCheckBox();
      emphasizeRightAngle.addActionListener(this);
      add(emphasizeRightAngle);
    }

    public void actionPerformed(ActionEvent e) {
      Object source = e.getSource();
      if (source == emphasizeRightAngle) {
        GeoAngle geo;
        boolean b = emphasizeRightAngle.isSelected();
        for (Object geo2 : geos) {
          geo = (GeoAngle) geo2;
          geo.setEmphasizeRightAngle(b);
          geo.updateRepaint();
        }
      }
    }

    private boolean checkGeos(Object[] geos) {
      boolean geosOK = true;
      for (int i = 0; i < geos.length; i++)
        if (!(geos[i] instanceof GeoAngle)) {
          geosOK = false;
          break;
        }
      /*
       * // If it isn't a right angle else if
       * (!kernel.isEqual(((GeoAngle)geos[i]).getValue(), Kernel.PI_HALF)){
       * geosOK=false; break; }
       */
      return geosOK;
    }

    public void setLabels() {
      emphasizeRightAngle.setText(Plain.EmphasizeRightAngle);
    }

    public JPanel update(Object[] geos) {
      // check geos
      if (!checkGeos(geos))
        return null;
      this.geos = geos;
      emphasizeRightAngle.removeActionListener(this);

      // set JcheckBox value to first geo's decoration
      GeoAngle geo0 = (GeoAngle) geos[0];
      emphasizeRightAngle.setSelected(geo0.isEmphasizeRightAngle());
      emphasizeRightAngle.addActionListener(this);
      return this;
    }

  }
  /**
   * panel for text editing
   */
  private class ScriptEditPanel extends JPanel
      implements
        ActionListener,
        UpdateablePanel {
    /**
  	 * 
  	 */
    private static final long serialVersionUID = 1L;
    private final ScriptInputDialog jsDialog, jsGlobalDialog;

    public ScriptEditPanel() {
      jsDialog = new ScriptInputDialog(app, Plain.JavaScript, null, 40, 10);
      jsGlobalDialog = new ScriptInputDialog(app, Plain.GlobalJavaScript, null,
          40, 10);
      setLayout(new BorderLayout());
      // add(td.getInputPanel(), BorderLayout.NORTH);
      // add(td2.getInputPanel(), BorderLayout.CENTER);
      JPanel btPanel = new JPanel(new BorderLayout(0, 0));
      btPanel.add(jsDialog.getInputPanel(), BorderLayout.NORTH);
      btPanel.add(jsDialog.getButtonPanel(), BorderLayout.EAST);
      add(btPanel, BorderLayout.NORTH);
      JPanel btPanel2 = new JPanel(new BorderLayout(0, 0));
      btPanel2.add(jsGlobalDialog.getInputPanel(), BorderLayout.NORTH);
      btPanel2.add(jsGlobalDialog.getButtonPanel(), BorderLayout.EAST);
      add(btPanel2, BorderLayout.SOUTH);
    }

    /**
     * handle textfield changes
     */
    public void actionPerformed(ActionEvent e) {
      // if (e.getSource() == btEdit)
      // app.showTextDialog((GeoText) geos[0]);
    }

    private boolean checkGeos(Object[] geos) {
      return geos.length == 1 && geos[0] instanceof GeoJavaScriptButton;
    }

    public void setLabels() {
      setBorder(BorderFactory.createTitledBorder(Plain.JavaScript));
      jsDialog.setLabels(Plain.JavaScript);
      jsGlobalDialog.setLabels(Plain.GlobalJavaScript);
    }

    public JPanel update(Object[] geos) {
      if (geos.length != 1 || !checkGeos(geos))
        return null;

      GeoJavaScriptButton button = (GeoJavaScriptButton) geos[0];
      jsDialog.setGeo(button);
      jsGlobalDialog.setGlobal();
      return this;
    }
  }
  /**
   * panel with show/hide object checkbox
   */
  private class ShowObjectPanel extends JPanel
      implements
        ItemListener,
        UpdateablePanel {

    private static final long serialVersionUID = 1L;
    private Object[] geos; // currently selected geos
    private final JCheckBox showObjectCB;

    public ShowObjectPanel() {
      // check box for show object
      showObjectCB = new JCheckBox();
      showObjectCB.addItemListener(this);
      add(showObjectCB);
    }

    // show everything but numbers (note: drawable angles are shown)
    private boolean checkGeos(Object[] geos) {
      boolean geosOK = true;
      for (Object geo2 : geos) {
        GeoElement geo = (GeoElement) geo2;
        if (!geo.isDrawable()
        // can't allow a free fixed number to become visible (as a slider)
            || geo.isGeoNumeric() && geo.isFixed()) {
          geosOK = false;
          break;
        }
      }
      return geosOK;
    }

    /**
     * listens to checkboxes and sets object and label visible state
     */
    public void itemStateChanged(ItemEvent e) {
      GeoElement geo;
      Object source = e.getItemSelectable();

      // show object value changed
      if (source == showObjectCB)
        for (Object geo2 : geos) {
          geo = (GeoElement) geo2;
          geo.setEuclidianVisible(showObjectCB.isSelected());
          geo.updateRepaint();
        }
      updateSelection(geos);
    }

    public void setLabels() {
      showObjectCB.setText(Plain.ShowObject);
    }

    public JPanel update(Object[] geos) {
      this.geos = geos;
      if (!checkGeos(geos))
        return null;

      showObjectCB.removeItemListener(this);

      // check if properties have same values
      GeoElement temp, geo0 = (GeoElement) geos[0];
      boolean equalObjectVal = true;
      boolean showObjectCondition = geo0.getShowObjectCondition() != null;

      for (int i = 1; i < geos.length; i++) {
        temp = (GeoElement) geos[i];
        // same object visible value
        if (geo0.isSetEuclidianVisible() != temp.isSetEuclidianVisible()) {
          equalObjectVal = false;
          break;
        }

        if (temp.getShowObjectCondition() != null)
          showObjectCondition = true;
      }

      // set object visible checkbox
      if (equalObjectVal)
        showObjectCB.setSelected(geo0.isSetEuclidianVisible());
      else
        showObjectCB.setSelected(false);

      showObjectCB.setEnabled(!showObjectCondition);

      showObjectCB.addItemListener(this);
      return this;
    }

  } // ShowObjectPanel
  /**
   * panel to select the size of a GeoPoint
   * 
   * @author Markus Hohenwarter
   */
  private class SlopeTriangleSizePanel extends JPanel
      implements
        ChangeListener,
        UpdateablePanel {

    /**
  	 * 
  	 */
    private static final long serialVersionUID = 1L;
    private Object[] geos;
    private final JSlider slider;

    public SlopeTriangleSizePanel() {
      slider = new JSlider(1, 10);
      slider.setMajorTickSpacing(2);
      slider.setMinorTickSpacing(1);
      slider.setPaintTicks(true);
      slider.setPaintLabels(true);
      slider.setSnapToTicks(true);

      setLabelFont();

      slider.addChangeListener(this);

      add(slider);
    }

    private boolean checkGeos(Object[] geos) {
      boolean geosOK = true;
      for (Object geo2 : geos) {
        GeoElement geo = (GeoElement) geo2;
        if (!(geo instanceof GeoNumeric && geo.getParentAlgorithm() instanceof AlgoSlope)) {
          geosOK = false;
          break;
        }
      }
      return geosOK;
    }

    private void setLabelFont() {
      Dictionary<?, ?> labelTable = slider.getLabelTable();
      Enumeration<?> en = labelTable.elements();
      JLabel label;
      while (en.hasMoreElements()) {
        label = (JLabel) en.nextElement();
        label.setFont(app.getSmallFont());
      }
    }

    public void setLabels() {
      setBorder(BorderFactory.createTitledBorder(Plain.Size));
    }

    /**
     * change listener implementation for slider
     */
    public void stateChanged(ChangeEvent e) {
      if (!slider.getValueIsAdjusting()) {
        int size = slider.getValue();
        GeoNumeric num;
        for (Object geo : geos) {
          num = (GeoNumeric) geo;
          num.setSlopeTriangleSize(size);
          num.updateRepaint();
        }
      }
    }

    public JPanel update(Object[] geos) {
      // check geos
      if (!checkGeos(geos))
        return null;

      this.geos = geos;
      slider.removeChangeListener(this);

      // set value to first point's size
      GeoNumeric geo0 = (GeoNumeric) geos[0];
      slider.setValue(geo0.getSlopeTriangleSize());

      slider.addChangeListener(this);
      return this;
    }
  }
  /**
   * panel for location of vectors and text
   */
  private class StartPointPanel extends JPanel
      implements
        ActionListener,
        FocusListener,
        UpdateablePanel {
    /**
  	 * 
  	 */
    private static final long serialVersionUID = 1L;
    /**
     * currently selected geos
     */
    private Object[] geos;
    private final JLabel label;
    private final JComboBox cbLocation;
    private final DefaultComboBoxModel cbModel;

    public StartPointPanel() {
      // textfield for animation step
      label = new JLabel();
      cbLocation = new JComboBox();
      cbLocation.setEditable(true);
      cbModel = new DefaultComboBoxModel();
      cbLocation.setModel(cbModel);
      label.setLabelFor(cbLocation);
      cbLocation.addActionListener(this);
      cbLocation.addFocusListener(this);

      // put it all together
      setLayout(new FlowLayout(FlowLayout.LEFT));
      add(label);
      add(cbLocation);
    }

    /**
     * handle textfield changes
     */
    public void actionPerformed(ActionEvent e) {
      if (e.getSource() == cbLocation)
        doActionPerformed();
    }

    private boolean checkGeos(Object[] geos) {
      boolean geosOK = true;
      for (Object geo2 : geos) {
        GeoElement geo = (GeoElement) geo2;
        if (!(geo instanceof Locateable && !((Locateable) geo).isAlwaysFixed())
            || geo.isGeoImage()) {
          geosOK = false;
          break;
        }
      }
      return geosOK;
    }

    private void doActionPerformed() {
      String strLoc = (String) cbLocation.getSelectedItem();
      GeoPoint newLoc = null;

      if (strLoc == null || strLoc.trim().length() == 0)
        newLoc = null;
      else
        newLoc = kernel.getAlgebraProcessor().evaluateToPoint(strLoc);

      for (Object geo : geos) {
        Locateable l = (Locateable) geo;
        try {
          l.setStartPoint(newLoc);
          l.toGeoElement().updateRepaint();
        } catch (CircularDefinitionException e) {
          app.showError("CircularDefinition");
        }
      }

      updateSelection(geos);
    }

    public void focusGained(FocusEvent arg0) {
    }

    public void focusLost(FocusEvent e) {
      doActionPerformed();
    }

    public void setLabels() {
      label.setText(Plain.StartingPoint + ": ");
    }

    public JPanel update(Object[] geos) {
      this.geos = geos;
      if (!checkGeos(geos))
        return null;

      cbLocation.removeActionListener(this);

      // repopulate model with names of points from the geoList's model
      // take all points from construction
      TreeSet<GeoElement> points = kernel.getConstruction()
          .getGeoSetLabelOrder(GeoElement.GEO_CLASS_POINT);
      if (points.size() != cbModel.getSize() - 1) {
        cbModel.removeAllElements();
        cbModel.addElement(null);
        Iterator<GeoElement> it = points.iterator();
        int count = 0;
        while (it.hasNext() || ++count > MAX_COMBOBOX_ENTRIES) {
          GeoPoint p = (GeoPoint) it.next();
          cbModel.addElement(p.getLabel());
        }
      }

      // check if properties have same values
      Locateable temp, geo0 = (Locateable) geos[0];
      boolean equalLocation = true;

      for (Object geo : geos) {
        temp = (Locateable) geo;
        // same object visible value
        if (geo0.getStartPoint() != temp.getStartPoint()) {
          equalLocation = false;
          break;
        }

      }

      // set location textfield
      GeoPoint p = geo0.getStartPoint();
      if (equalLocation && p != null)
        cbLocation.setSelectedItem(p.getLabel());
      else
        cbLocation.setSelectedItem(null);

      cbLocation.addActionListener(this);
      return this;
    }
  }
  private class TabPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -3737632152226510208L;
    private String title;
    private final ArrayList<JPanel> panelList;
    private boolean makeVisible = true;

    protected TabPanel(ArrayList<JPanel> pVec) {
      panelList = pVec;

      setLayout(new BorderLayout());
      JPanel panel = new JPanel();
      panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      JScrollPane scrollPane = new JScrollPane(panel);
      scrollPane.setBorder(BorderFactory.createEmptyBorder());
      // setPreferredSize(new Dimension(450, 110));
      add(scrollPane, BorderLayout.CENTER);

      // create grid with one column
      panel.setLayout(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      c.fill = GridBagConstraints.NONE;
      c.anchor = GridBagConstraints.NORTHWEST;
      c.weightx = 1.0;
      c.weighty = 1E-12;

      for (int i = 0; i < pVec.size(); i++) {
        JPanel p = pVec.get(i);
        c.gridx = 0;
        c.gridy = i;

        panel.add(p, c);
      }
      c.weighty = 1.0;
      panel.add(Box.createVerticalGlue(), c);
    }

    public void addToTabbedPane(JTabbedPane tabs) {
      if (makeVisible)
        tabs.addTab(title, this);
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public void update(Object[] geos) {
      makeVisible = updateTabPanel(this, panelList, geos);
    }
  }
  /**
   * panel for text editing
   */
  private class TextEditPanel extends JPanel
      implements
        ActionListener,
        UpdateablePanel {
    /**
  	 * 
  	 */
    private static final long serialVersionUID = 1L;
    private final TextInputDialog td;

    public TextEditPanel() {
      td = new TextInputDialog(app, Plain.Text, null, null, 30, 5);
      setLayout(new BorderLayout());
      add(td.getInputPanel(), BorderLayout.CENTER);
      JPanel btPanel = new JPanel(new BorderLayout(0, 0));
      btPanel.add(td.getLaTeXPanel(), BorderLayout.WEST);
      btPanel.add(td.getButtonPanel(), BorderLayout.EAST);
      add(btPanel, BorderLayout.SOUTH);
    }

    /**
     * handle textfield changes
     */
    public void actionPerformed(ActionEvent e) {
      // if (e.getSource() == btEdit)
      // app.showTextDialog((GeoText) geos[0]);
    }

    private boolean checkGeos(Object[] geos) {
      return geos.length == 1 && geos[0] instanceof GeoText
          && !((GeoText) geos[0]).isTextCommand();
    }

    public void setLabels() {
      setBorder(BorderFactory.createTitledBorder(Plain.Edit));
      td.setLabels(Plain.Text);
    }

    public JPanel update(Object[] geos) {
      if (geos.length != 1 || !checkGeos(geos))
        return null;

      GeoText text = (GeoText) geos[0];
      td.setGeoText(text);
      return this;
    }
  }
  /**
   * panel to select the size of a GeoText
   * 
   * @author Markus Hohenwarter
   */
  private class TextOptionsPanel extends JPanel
      implements
        ActionListener,
        UpdateablePanel {
    class ComboBoxRenderer extends JLabel implements ListCellRenderer {
      /**
       * 
       */
      private static final long serialVersionUID = 8691157313969367215L;
      JSeparator separator;

      public ComboBoxRenderer() {
        setOpaque(true);
        setBorder(new EmptyBorder(1, 1, 1, 1));
        separator = new JSeparator(JSeparator.HORIZONTAL);
      }

      public Component getListCellRendererComponent(JList list, Object value,
          int index, boolean isSelected, boolean cellHasFocus) {
        String str = value == null ? "" : value.toString();
        if ("---".equals(str))
          return separator;
        if (isSelected) {
          setBackground(list.getSelectionBackground());
          setForeground(list.getSelectionForeground());
        } else {
          setBackground(list.getBackground());
          setForeground(list.getForeground());
        }
        setFont(list.getFont());
        setText(str);
        return this;
      }
    }
    private static final long serialVersionUID = 1L;

    private Object[] geos;
    private final JLabel decimalLabel;
    private final JComboBox cbFont, cbSize;

    private JComboBox cbDecimalPlaces;

    private final JToggleButton btBold, btItalic;
    private final JPanel secondLine;

    private boolean secondLineVisible = false;

    public TextOptionsPanel() {
      String[] fonts = {"Verdana", "Serif"};
      cbFont = new JComboBox(fonts);
      cbFont.addActionListener(this);

      /**
       * Ðàçìåðû øðèôòîâ
       */
      cbSize = new JComboBox(new String[]{Plain.ExtraSmall, Plain.Small,
          Plain.Medium, Plain.Large, Plain.ExtraLarge});
      cbSize.addActionListener(this);

      // toggle buttons for bold and italic
      btBold = new JToggleButton();
      btBold.setFont(app.getBoldFont());
      btBold.addActionListener(this);
      btItalic = new JToggleButton();
      btItalic.setFont(app.getPlainFont().deriveFont(Font.ITALIC));
      btItalic.addActionListener(this);

      decimalPlaces();

      JPanel firstLine = fontSizeBoldItalic();

      secondLine = new JPanel();
      secondLine.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
      decimalLabel = new JLabel();
      secondLine.add(decimalLabel);
      secondLine.add(cbDecimalPlaces);

      setLayout(new BorderLayout(5, 5));
      add(firstLine, BorderLayout.NORTH);
      add(secondLine, BorderLayout.SOUTH);
      secondLineVisible = true;
    }

    /**
     * change listener implementation for slider
     */
    public void actionPerformed(ActionEvent e) {
      Object source = e.getSource();

      if (source == cbSize) {
        TextProperties text;
        for (Object geo : geos) {
          text = (TextProperties) geo;
          text.setFontSize(cbSize.getSelectedIndex() * 2 - 4); // transform
          // indices to the
          // range -4, .. ,
          // 4
          ((GeoElement) text).updateRepaint();
        }
      } else if (source == cbFont) {
        boolean serif = cbFont.getSelectedIndex() == 1;
        TextProperties text;
        for (Object geo : geos) {
          text = (TextProperties) geo;
          text.setSerifFont(serif);
          ((GeoElement) text).updateRepaint();
        }
      } else if (source == cbDecimalPlaces) {
        int decimals = cbDecimalPlaces.getSelectedIndex();
        // Application.debug(decimals+"");
        // Application.debug(roundingMenuLookup[decimals]+"");
        TextProperties text;
        for (Object geo : geos) {
          text = (TextProperties) geo;
          if (decimals < 8)
            // Application.debug("decimals"+roundingMenuLookup[decimals]+"");
            text.setPrintDecimals(Application.roundingMenuLookup[decimals],
                true);
          else
            // Application.debug("figures"+roundingMenuLookup[decimals]+"");
            text
                .setPrintFigures(Application.roundingMenuLookup[decimals], true);
          ((GeoElement) text).updateRepaint();
        }
      } else if (source == btBold || source == btItalic) {
        int style = 0;
        if (btBold.isSelected())
          style += 1;
        if (btItalic.isSelected())
          style += 2;

        TextProperties text;
        for (Object geo : geos) {
          text = (TextProperties) geo;
          text.setFontStyle(style);
          ((GeoElement) text).updateRepaint();
        }
      }
    }

    private boolean checkGeos(Object[] geos) {
      boolean geosOK = true;
      for (int i = 0; i < geos.length; i++)
        if (!((GeoElement) geos[i]).getGeoElementForPropertiesDialog()
            .isGeoText()) {
          geosOK = false;
          break;
        }
      return geosOK;
    }

    private void decimalPlaces() {
      ComboBoxRenderer renderer = new ComboBoxRenderer();
      cbDecimalPlaces = new JComboBox(app.getRoundingMenu());
      cbDecimalPlaces.setRenderer(renderer);
      cbDecimalPlaces.addActionListener(this);
    }

    private JPanel fontSizeBoldItalic() {
      JPanel firstLine = new JPanel();
      firstLine.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
      firstLine.add(cbFont);
      firstLine.add(cbSize);
      firstLine.add(btBold);
      firstLine.add(btItalic);
      return firstLine;
    }

    public void setLabels() {
      String[] fontSizes = new String[]{Plain.ExtraSmall, Plain.Small,
          Plain.Medium, Plain.Large, Plain.ExtraLarge};

      int selectedIndex = cbSize.getSelectedIndex();
      cbSize.removeActionListener(this);
      cbSize.removeAllItems();

      for (int i = 0; i < fontSizes.length; ++i)
        cbSize.addItem(fontSizes[i]);

      cbSize.setSelectedIndex(selectedIndex);
      cbSize.addActionListener(this);

      btItalic.setText(Plain.Italic.substring(0, 1));
      btBold.setText(Plain.Bold.substring(0, 1));

      decimalLabel.setText(geogebra.Menu.Rounding + ":");
    }

    public JPanel update(Object[] geos) {
      // check geos
      if (!checkGeos(geos))
        return null;

      this.geos = geos;

      cbSize.removeActionListener(this);
      cbFont.removeActionListener(this);
      cbDecimalPlaces.removeActionListener(this);

      // set value to first text's size and style
      TextProperties geo0 = (TextProperties) geos[0];

      cbSize.setSelectedIndex(geo0.getFontSize() / 2 + 2); // font size ranges
      // from -4 to 4,
      // transform this to
      // 0,1,..,4
      cbFont.setSelectedIndex(geo0.isSerifFont() ? 1 : 0);
      int selItem = -1;

      int decimals = geo0.getPrintDecimals();
      if (decimals > 0 && decimals < Application.decimalsLookup.length
          && !geo0.useSignificantFigures())
        selItem = Application.decimalsLookup[decimals];

      int figures = geo0.getPrintFigures();
      if (figures > 0 && figures < Application.figuresLookup.length
          && geo0.useSignificantFigures())
        selItem = Application.figuresLookup[figures];

      cbDecimalPlaces.setSelectedIndex(selItem);

      if (((GeoElement) geo0).isIndependent() || geo0 instanceof GeoList) { // don't
        // want
        // rounding
        // option
        // for
        // lists
        // of
        // texts?
        if (secondLineVisible) {
          remove(secondLine);
          secondLineVisible = false;
        }
      } else if (!secondLineVisible) {
        add(secondLine, BorderLayout.SOUTH);
        secondLineVisible = true;
      }

      int style = geo0.getFontStyle();
      btBold
          .setSelected(style == Font.BOLD || style == Font.BOLD + Font.ITALIC);
      btItalic.setSelected(style == Font.ITALIC
          || style == Font.BOLD + Font.ITALIC);

      cbSize.addActionListener(this);
      cbFont.addActionListener(this);
      cbDecimalPlaces.addActionListener(this);
      return this;
    }
  }
  /**
   * panel for trace
   * 
   * @author Markus Hohenwarter
   */
  private class TracePanel extends JPanel
      implements
        ItemListener,
        UpdateablePanel {
    /**
  	 * 
  	 */
    private static final long serialVersionUID = 1L;
    private Object[] geos; // currently selected geos
    private final JCheckBox showTraceCB;

    public TracePanel() {
      // check boxes for show trace
      showTraceCB = new JCheckBox();
      showTraceCB.addItemListener(this);
      add(showTraceCB);
    }

    private boolean checkGeos(Object[] geos) {
      boolean geosOK = true;
      for (int i = 0; i < geos.length; i++)
        if (!(geos[i] instanceof Traceable)) {
          geosOK = false;
          break;
        }
      return geosOK;
    }

    /**
     * listens to checkboxes and sets trace state
     */
    public void itemStateChanged(ItemEvent e) {
      Traceable geo;
      Object source = e.getItemSelectable();

      // show trace value changed
      if (source == showTraceCB)
        for (Object geo2 : geos) {
          geo = (Traceable) geo2;
          geo.setTrace(showTraceCB.isSelected());
          geo.updateRepaint();
        }
    }

    public void setLabels() {
      showTraceCB.setText(Plain.ShowTrace);
    }

    public JPanel update(Object[] geos) {
      this.geos = geos;
      if (!checkGeos(geos))
        return null;

      showTraceCB.removeItemListener(this);

      // check if properties have same values
      Traceable temp, geo0 = (Traceable) geos[0];
      boolean equalTrace = true;

      for (int i = 1; i < geos.length; i++) {
        temp = (Traceable) geos[i];
        // same object visible value
        if (geo0.getTrace() != temp.getTrace())
          equalTrace = false;
      }

      // set trace visible checkbox
      if (equalTrace)
        showTraceCB.setSelected(geo0.getTrace());
      else
        showTraceCB.setSelected(false);

      showTraceCB.addItemListener(this);
      return this;
    }
  }
  private static final int MAX_COMBOBOX_ENTRIES = 200;
  private final Application app;
  private final Kernel kernel;
  private final JColorChooser colChooser;
  private static final long serialVersionUID = 1L;
  private NamePanel namePanel;
  private final ShowObjectPanel showObjectPanel;
  private final ColorPanel colorPanel;
  private LabelPanel labelPanel;
  private LayerPanel layerPanel;

  private final CoordPanel coordPanel;
  private final LineEqnPanel lineEqnPanel;
  private final ConicEqnPanel conicEqnPanel;
  private final PointSizePanel pointSizePanel;
  private final PointStylePanel pointStylePanel;
  private final TextOptionsPanel textOptionsPanel;
  private final ArcSizePanel arcSizePanel;
  private final LineStylePanel lineStylePanel;
  private final DecoSegmentPanel decoSegmentPanel;
  private final DecoAnglePanel decoAnglePanel;

  private final RightAnglePanel rightAnglePanel;

  private final FillingPanel fillingPanel;

  private final TracePanel tracePanel;

  private AnimatingPanel animatingPanel;

  private final FixPanel fixPanel;

  private final CheckBoxFixPanel checkBoxFixPanel;

  private final AllowReflexAnglePanel allowReflexAnglePanel;

  private final AllowOutlyingIntersectionsPanel allowOutlyingIntersectionsPanel;

  private final AuxiliaryObjectPanel auxPanel;

  private final AnimationStepPanel animStepPanel;

  private final AnimationSpeedPanel animSpeedPanel;

  private SliderPanel sliderPanel;

  private final SlopeTriangleSizePanel slopeTriangleSizePanel;

  private StartPointPanel startPointPanel;

  private CornerPointsPanel cornerPointsPanel;

  private TextEditPanel textEditPanel;

  private ScriptEditPanel scriptEditPanel;
  private BackgroundImagePanel bgImagePanel;

  private final AbsoluteScreenLocationPanel absScreenLocPanel;

  private ShowConditionPanel showConditionPanel;

  private ColorFunctionPanel colorFunctionPanel;

  private TabPanel basicTab;

  private TabPanel colorTab;

  private TabPanel styleTab;

  private TabPanel lineStyleTab;

  private TabPanel sliderTab;

  private TabPanel textTab;

  private TabPanel positionTab;

  private TabPanel algebraTab;

  private TabPanel scriptTab;

  private TabPanel advancedTab;

  /**
   * If just panels should be displayed which are used if the user modifies the
   * default properties of an object type.
   */
  private final boolean isDefaults;

  private final JTabbedPane tabs;

  /**
   * A list of the tab panels
   */
  private ArrayList<TabPanel> tabPanelList;

  public PropertiesPanel(Application app, JColorChooser colChooser,
      boolean isDefaults) {
    this.isDefaults = isDefaults;

    this.app = app;
    kernel = app.getKernel();
    this.colChooser = colChooser;

    // load panels which are hidden for the defaults dialog
    if (!isDefaults) {
      namePanel = new NamePanel(app);
      labelPanel = new LabelPanel();
      layerPanel = new LayerPanel(); // Michael Borcherds 2008-02-26
      animatingPanel = new AnimatingPanel();
      scriptEditPanel = new ScriptEditPanel();
      textEditPanel = new TextEditPanel();
      startPointPanel = new StartPointPanel();
      cornerPointsPanel = new CornerPointsPanel();
      bgImagePanel = new BackgroundImagePanel();
      showConditionPanel = new ShowConditionPanel(app, this);
      colorFunctionPanel = new ColorFunctionPanel(app, this);
      // coordinateFunctionPanel = new CoordinateFunctionPanel(app, this);
      sliderPanel = new SliderPanel(app, this, false);
    }

    showObjectPanel = new ShowObjectPanel();
    colorPanel = new ColorPanel(colChooser);
    coordPanel = new CoordPanel();
    lineEqnPanel = new LineEqnPanel();
    conicEqnPanel = new ConicEqnPanel();
    pointSizePanel = new PointSizePanel();
    pointStylePanel = new PointStylePanel();
    textOptionsPanel = new TextOptionsPanel();
    arcSizePanel = new ArcSizePanel();
    slopeTriangleSizePanel = new SlopeTriangleSizePanel();
    lineStylePanel = new LineStylePanel();
    decoSegmentPanel = new DecoSegmentPanel();
    decoAnglePanel = new DecoAnglePanel();
    rightAnglePanel = new RightAnglePanel();
    fillingPanel = new FillingPanel();
    tracePanel = new TracePanel();
    animatingPanel = new AnimatingPanel();
    fixPanel = new FixPanel();
    checkBoxFixPanel = new CheckBoxFixPanel();
    absScreenLocPanel = new AbsoluteScreenLocationPanel();
    auxPanel = new AuxiliaryObjectPanel();
    animStepPanel = new AnimationStepPanel(app);
    animSpeedPanel = new AnimationSpeedPanel(app);
    sliderPanel = new SliderPanel(app, this, false);
    startPointPanel = new StartPointPanel();
    cornerPointsPanel = new CornerPointsPanel();
    textEditPanel = new TextEditPanel();
    bgImagePanel = new BackgroundImagePanel();
    allowReflexAnglePanel = new AllowReflexAnglePanel();
    allowOutlyingIntersectionsPanel = new AllowOutlyingIntersectionsPanel();
    showConditionPanel = new ShowConditionPanel(app, this);
    colorFunctionPanel = new ColorFunctionPanel(app, this);

    // tabbed pane for properties
    tabs = new JTabbedPane();
    initTabs();

    setLayout(new BorderLayout());
    add(tabs, BorderLayout.CENTER);
  }

  /**
   * Initialize the tabs
   */
  private void initTabs() {
    tabPanelList = new ArrayList<TabPanel>();

    // basic tab
    ArrayList<JPanel> basicTabList = new ArrayList<JPanel>();

    if (!isDefaults)
      basicTabList.add(namePanel);

    basicTabList.add(showObjectPanel);

    if (!isDefaults)
      basicTabList.add(labelPanel);

    basicTabList.add(tracePanel);

    if (!isDefaults)
      basicTabList.add(animatingPanel);

    basicTabList.add(fixPanel);
    basicTabList.add(auxPanel);
    basicTabList.add(checkBoxFixPanel);

    if (!isDefaults)
      basicTabList.add(bgImagePanel);

    basicTabList.add(absScreenLocPanel);
    basicTabList.add(allowReflexAnglePanel);
    basicTabList.add(rightAnglePanel);
    basicTabList.add(allowOutlyingIntersectionsPanel);
    basicTab = new TabPanel(basicTabList);
    tabPanelList.add(basicTab);

    // text tab
    ArrayList<JPanel> textTabList = new ArrayList<JPanel>();
    textTabList.add(textOptionsPanel);

    if (!isDefaults)
      textTabList.add(textEditPanel);

    textTab = new TabPanel(textTabList);
    tabPanelList.add(textTab);

    // script tab
    if (!isDefaults) {
      ArrayList<JPanel> scriptTabList = new ArrayList<JPanel>();
      // scriptTabList.add(scriptOptionsPanel);

      scriptTabList.add(scriptEditPanel);

      scriptTab = new TabPanel(scriptTabList);
      tabPanelList.add(scriptTab);
    }

    // slider tab
    if (!isDefaults) {
      ArrayList<JPanel> sliderTabList = new ArrayList<JPanel>();
      sliderTabList.add(sliderPanel);
      sliderTab = new TabPanel(sliderTabList);
      tabPanelList.add(sliderTab);
    }

    // color tab
    ArrayList<JPanel> colorTabList = new ArrayList<JPanel>();
    colorTabList.add(colorPanel);
    colorTab = new TabPanel(colorTabList);
    tabPanelList.add(colorTab);

    // style tab
    ArrayList<JPanel> styleTabList = new ArrayList<JPanel>();
    styleTabList.add(slopeTriangleSizePanel);
    styleTabList.add(pointSizePanel);
    styleTabList.add(pointStylePanel); // Florian Sonner 2008-07-17
    styleTabList.add(lineStylePanel);
    styleTabList.add(arcSizePanel);
    styleTabList.add(fillingPanel);
    styleTab = new TabPanel(styleTabList);
    tabPanelList.add(styleTab);

    // decoration
    ArrayList<JPanel> decorationTabList = new ArrayList<JPanel>();
    decorationTabList.add(decoAnglePanel);
    decorationTabList.add(decoSegmentPanel);
    lineStyleTab = new TabPanel(decorationTabList);
    tabPanelList.add(lineStyleTab);

    // filling style
    // ArrayList fillingTabList = new ArrayList();
    // fillingTabList.add(fillingPanel);
    // TabPanel fillingTab = new TabPanel(app.getPlain("Filling"),
    // fillingTabList);
    // fillingTab.addToTabbedPane(tabs);

    // position
    if (!isDefaults) {
      ArrayList<JPanel> positionTabList = new ArrayList<JPanel>();

      positionTabList.add(startPointPanel);
      positionTabList.add(cornerPointsPanel);

      positionTab = new TabPanel(positionTabList);
      tabPanelList.add(positionTab);
    }

    // algebra tab
    ArrayList<JPanel> algebraTabList = new ArrayList<JPanel>();
    algebraTabList.add(coordPanel);
    algebraTabList.add(lineEqnPanel);
    algebraTabList.add(conicEqnPanel);
    algebraTabList.add(animStepPanel);
    algebraTab = new TabPanel(algebraTabList);
    tabPanelList.add(algebraTab);

    // advanced tab
    if (!isDefaults) {
      ArrayList<JPanel> advancedTabList = new ArrayList<JPanel>();

      advancedTabList.add(showConditionPanel);
      advancedTabList.add(colorFunctionPanel);
      // advancedTabList.add(coordinateFunctionPanel);
      advancedTabList.add(layerPanel); // Michael Borcherds 2008-02-26

      advancedTab = new TabPanel(advancedTabList);
      tabPanelList.add(advancedTab);
    }

    setLabels();
  }

  /**
   * Update the labels of this panel in case the user language was changed.
   */
  public void setLabels() {
    // update labels of tabs
    // TODO change label for script tab
    basicTab.setTitle(geogebra.Menu.Properties_Basic);
    colorTab.setTitle(Plain.Color);
    styleTab.setTitle(geogebra.Menu.Properties_Style);
    lineStyleTab.setTitle(Plain.Decoration);
    textTab.setTitle(Plain.Text);
    algebraTab.setTitle(geogebra.Menu.Properties_Algebra);

    if (!isDefaults) {
      positionTab.setTitle(geogebra.Menu.Properties_Position);
      sliderTab.setTitle(Plain.Slider);
      scriptTab.setTitle(Plain.JavaScript);
      advancedTab.setTitle(geogebra.Menu.Advanced);
    }

    // update the labels of the panels
    showObjectPanel.setLabels();
    colorPanel.setLabels();
    coordPanel.setLabels();
    lineEqnPanel.setLabels();
    conicEqnPanel.setLabels();
    pointSizePanel.setLabels();
    pointStylePanel.setLabels();
    textOptionsPanel.setLabels();
    arcSizePanel.setLabels();
    lineStylePanel.setLabels();
    decoSegmentPanel.setLabels();
    decoAnglePanel.setLabels();
    rightAnglePanel.setLabels();
    fillingPanel.setLabels();
    tracePanel.setLabels();
    fixPanel.setLabels();
    checkBoxFixPanel.setLabels();
    allowReflexAnglePanel.setLabels();
    allowOutlyingIntersectionsPanel.setLabels();
    auxPanel.setLabels();
    animStepPanel.setLabels();
    animSpeedPanel.setLabels();
    slopeTriangleSizePanel.setLabels();
    absScreenLocPanel.setLabels();

    if (!isDefaults) {
      namePanel.setLabels();
      labelPanel.setLabels();
      layerPanel.setLabels();
      animatingPanel.setLabels();
      scriptEditPanel.setLabels();
      textEditPanel.setLabels();
      startPointPanel.setLabels();
      cornerPointsPanel.setLabels();
      bgImagePanel.setLabels();
      showConditionPanel.setLabels();
      colorFunctionPanel.setLabels();
      sliderPanel.setLabels();
    }

    // remember selected tab
    Component selectedTab = tabs.getSelectedComponent();

    // update tab labels
    tabs.removeAll();
    for (int i = 0; i < tabPanelList.size(); i++) {
      TabPanel tp = tabPanelList.get(i);
      tp.addToTabbedPane(tabs);
    }

    // switch back to previously selected tab
    if (tabs.getTabCount() > 0) {
      int index = tabs.indexOfComponent(selectedTab);
      tabs.setSelectedIndex(Math.max(0, index));
      tabs.setVisible(true);
    } else
      tabs.setVisible(false);
  }

  // added by Loïc BEGIN
  public void setSliderMinValue() {
    arcSizePanel.setMinValue();
  }
  // END

  public void updateSelection(Object[] geos) {
    // if (geos == oldSelGeos) return;
    // oldSelGeos = geos;

    updateTabs(geos);
  }

  private boolean updateTabPanel(TabPanel tabPanel, ArrayList<JPanel> tabList,
      Object[] geos) {
    // update all panels and their visibility
    boolean oneVisible = false;
    int size = tabList.size();
    for (int i = 0; i < size; i++) {
      UpdateablePanel up = (UpdateablePanel) tabList.get(i);
      boolean show = up.update(geos) != null;
      up.setVisible(show);
      if (show)
        oneVisible = true;
    }

    return oneVisible;
  }

  /**
   * Update all tabs after new GeoElements were selected.
   * 
   * @param geos
   */
  private void updateTabs(Object[] geos) {
    if (geos.length == 0) {
      tabs.setVisible(false);
      return;
    }

    // remember selected tab
    Component selectedTab = tabs.getSelectedComponent();

    tabs.removeAll();
    for (int i = 0; i < tabPanelList.size(); i++) {
      TabPanel tp = tabPanelList.get(i);
      tp.update(geos);
      tp.addToTabbedPane(tabs);
    }

    // switch back to previously selected tab
    if (tabs.getTabCount() > 0) {
      int index = tabs.indexOfComponent(selectedTab);
      tabs.setSelectedIndex(Math.max(0, index));
      tabs.setVisible(true);
    } else
      tabs.setVisible(false);
  }

} // PropertiesPanel

/**
 * panel for condition to show object
 * 
 * @author Markus Hohenwarter
 */
class ShowConditionPanel extends JPanel
    implements
      ActionListener,
      FocusListener,
      UpdateablePanel {

  private static final long serialVersionUID = 1L;

  private Object[] geos; // currently selected geos
  private final JTextField tfCondition;

  private final Kernel kernel;
  private final PropertiesPanel propPanel;

  boolean processed = false;

  public ShowConditionPanel(Application app, PropertiesPanel propPanel) {
    kernel = app.getKernel();
    this.propPanel = propPanel;

    // non auto complete input panel
    InputPanel inputPanel = new InputPanel(null, app, 20, false);
    tfCondition = (AutoCompleteTextField) inputPanel.getTextComponent();

    tfCondition.addActionListener(this);
    tfCondition.addFocusListener(this);

    // put it all together
    setLayout(new FlowLayout(FlowLayout.LEFT));
    add(inputPanel);

    setLabels();
  }

  /**
   * handle textfield changes
   */
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == tfCondition)
      doActionPerformed();
  }

  private boolean checkGeos(Object[] geos) {
    for (Object geo2 : geos) {
      GeoElement geo = (GeoElement) geo2;
      if (!geo.isEuclidianShowable())
        return false;
    }

    return true;
  }

  private void doActionPerformed() {
    processed = true;
    GeoBoolean cond;
    String strCond = tfCondition.getText();
    if (strCond == null || strCond.trim().length() == 0)
      cond = null;
    else
      cond = kernel.getAlgebraProcessor().evaluateToBoolean(strCond);

    if (cond != null || strCond.trim().length() == 0) {
      // set condition
      try {
        for (Object geo2 : geos) {
          GeoElement geo = (GeoElement) geo2;
          geo.setShowObjectCondition(cond);
        }

      } catch (CircularDefinitionException e) {
        tfCondition.setText("");
        kernel.getApplication().showError("CircularDefinition");
      }

      if (cond != null)
        cond.updateRepaint();

      // to update "showObject" as well
      propPanel.updateSelection(geos);
    } else
      // put back faulty condition (for editing)
      tfCondition.setText(strCond);

  }

  public void focusGained(FocusEvent arg0) {
    processed = false;
  }

  public void focusLost(FocusEvent e) {
    if (!processed)
      doActionPerformed();
  }

  public void setLabels() {
    setBorder(BorderFactory
        .createTitledBorder(geogebra.Menu.Condition_ShowObject));
  }

  public JPanel update(Object[] geos) {
    this.geos = geos;
    if (!checkGeos(geos))
      return null;

    tfCondition.removeActionListener(this);

    // take condition of first geo
    String strCond = "";
    GeoElement geo0 = (GeoElement) geos[0];
    GeoBoolean cond = geo0.getShowObjectCondition();
    if (cond != null)
      strCond = cond.getLabel();

    for (Object geo2 : geos) {
      GeoElement geo = (GeoElement) geo2;
      cond = geo.getShowObjectCondition();
      if (cond != null) {
        String strCondGeo = cond.getLabel();
        if (!strCond.equals(strCondGeo))
          strCond = "";
      }
    }

    tfCondition.setText(strCond);
    tfCondition.addActionListener(this);
    return this;
  }
}

/**
 * panel for numeric slider
 * 
 * @author Markus Hohenwarter
 */
class SliderPanel extends JPanel
    implements
      ActionListener,
      FocusListener,
      UpdateablePanel {
  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;
  private Object[] geos; // currently selected geos
  private final AngleTextField tfMin, tfMax;
  private final JTextField tfWidth;
  private final JTextField[] tfields;
  private final JLabel[] tLabels;
  private final JCheckBox cbSliderFixed;
  private final JComboBox coSliderHorizontal;

  private final AnimationStepPanel stepPanel;
  private final AnimationSpeedPanel speedPanel;
  private final Kernel kernel;
  private final PropertiesPanel propPanel;
  private final JPanel intervalPanel, sliderPanel, animationPanel;
  private final boolean useTabbedPane;

  public SliderPanel(Application app, PropertiesPanel propPanel,
      boolean useTabbedPane) {
    kernel = app.getKernel();
    this.propPanel = propPanel;
    this.useTabbedPane = useTabbedPane;

    intervalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
    sliderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
    animationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

    cbSliderFixed = new JCheckBox();
    cbSliderFixed.addActionListener(this);
    sliderPanel.add(cbSliderFixed);

    coSliderHorizontal = new JComboBox();
    coSliderHorizontal.addActionListener(this);
    sliderPanel.add(coSliderHorizontal);

    tfMin = new AngleTextField(5);
    tfMax = new AngleTextField(5);
    tfWidth = new JTextField(4);
    tfields = new JTextField[3];
    tLabels = new JLabel[3];
    tfields[0] = tfMin;
    tfields[1] = tfMax;
    tfields[2] = tfWidth;
    int numPairs = tLabels.length;

    // add textfields
    for (int i = 0; i < numPairs; i++) {
      JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
      tLabels[i] = new JLabel("", SwingConstants.LEADING);
      p.add(tLabels[i]);
      JTextField textField = tfields[i];
      tLabels[i].setLabelFor(textField);
      textField.addActionListener(this);
      textField.addFocusListener(this);
      p.add(textField);
      p.setAlignmentX(Component.LEFT_ALIGNMENT);

      if (i < 2)
        intervalPanel.add(p);
      else
        sliderPanel.add(p);
    }

    // add increment to intervalPanel
    stepPanel = new AnimationStepPanel(app);
    stepPanel.setPartOfSliderPanel();
    intervalPanel.add(stepPanel);

    speedPanel = new AnimationSpeedPanel(app);
    speedPanel.setPartOfSliderPanel();
    animationPanel.add(speedPanel);

    setLabels();
  }

  /**
   * handle textfield changes
   */
  public void actionPerformed(ActionEvent e) {
    Object source = e.getSource();
    if (source == cbSliderFixed)
      doCheckBoxActionPerformed((JCheckBox) source);
    else if (source == coSliderHorizontal)
      doComboBoxActionPerformed((JComboBox) source);
    else
      doTextFieldActionPerformed((JTextField) e.getSource());
  }

  private boolean checkGeos(Object[] geos) {
    boolean geosOK = true;
    for (Object geo2 : geos) {
      GeoElement geo = (GeoElement) geo2;
      if (!(geo.isIndependent() && geo.isGeoNumeric())) {
        geosOK = false;
        break;
      }
    }
    return geosOK;
  }

  private void doCheckBoxActionPerformed(JCheckBox source) {
    boolean fixed = source.isSelected();
    for (Object geo : geos) {
      GeoNumeric num = (GeoNumeric) geo;
      num.setSliderFixed(fixed);
      num.updateRepaint();
    }
    update(geos);
  }

  private void doComboBoxActionPerformed(JComboBox source) {
    boolean horizontal = source.getSelectedIndex() == 0;
    for (Object geo : geos) {
      GeoNumeric num = (GeoNumeric) geo;
      num.setSliderHorizontal(horizontal);
      num.updateRepaint();
    }
    update(geos);
  }

  private void doTextFieldActionPerformed(JTextField source) {
    String inputText = source.getText().trim();
    boolean emptyString = inputText.equals("");
    double value = Double.NaN;
    if (!emptyString)
      value = kernel.getAlgebraProcessor().evaluateToDouble(inputText);

    if (source == tfMin)
      for (Object geo : geos) {
        GeoNumeric num = (GeoNumeric) geo;
        if (emptyString)
          num.setIntervalMinInactive();
        else
          num.setIntervalMin(value);
        num.updateRepaint();
      }
    else if (source == tfMax)
      for (Object geo : geos) {
        GeoNumeric num = (GeoNumeric) geo;
        if (emptyString)
          num.setIntervalMaxInactive();
        else
          num.setIntervalMax(value);
        num.updateRepaint();
      }
    else if (source == tfWidth)
      for (Object geo : geos) {
        GeoNumeric num = (GeoNumeric) geo;
        num.setSliderWidth(value);
        num.updateRepaint();
      }

    if (propPanel != null)
      propPanel.updateSelection(geos);
    else
      update(geos);
  }

  public void focusGained(FocusEvent arg0) {
  }

  public void focusLost(FocusEvent e) {
    doTextFieldActionPerformed((JTextField) e.getSource());
  }

  private void initPanels() {
    removeAll();

    System.out
        .println("put together interval, slider options, animation panels");
    if (useTabbedPane) {
      setLayout(new FlowLayout());
      JTabbedPane tabbedPane = new JTabbedPane();
      tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      tabbedPane.addTab(Plain.Interval, intervalPanel);
      tabbedPane.addTab(geogebra.Menu.Slider, sliderPanel);
      tabbedPane.addTab(Plain.Animation, animationPanel);
      add(tabbedPane);
    } else {
      System.out.println("no tabs");
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      intervalPanel.setBorder(BorderFactory.createTitledBorder(Plain.Interval));
      sliderPanel.setBorder(BorderFactory.createTitledBorder(Plain.Slider));
      animationPanel.setBorder(BorderFactory
          .createTitledBorder(Plain.Animation));
      add(intervalPanel);
      add(Box.createVerticalStrut(5));
      add(sliderPanel);
      add(Box.createVerticalStrut(5));
      add(animationPanel);
    }
  }

  public void setLabels() {
    initPanels();

    cbSliderFixed.setText(Plain.fixed);

    String[] comboStr = {Plain.horizontal, Plain.vertical};

    int selectedIndex = coSliderHorizontal.getSelectedIndex();
    coSliderHorizontal.removeActionListener(this);
    coSliderHorizontal.removeAllItems();

    for (int i = 0; i < comboStr.length; ++i)
      coSliderHorizontal.addItem(comboStr[i]);

    coSliderHorizontal.setSelectedIndex(selectedIndex);
    coSliderHorizontal.addActionListener(this);

    String[] labels = {Plain.min + ":", Plain.max + ":", Plain.Width + ":"};

    for (int i = 0; i < tLabels.length; ++i)
      tLabels[i].setText(labels[i]);
  }

  public JPanel update(Object[] geos) {
    stepPanel.update(geos);
    speedPanel.update(geos);

    this.geos = geos;
    if (!checkGeos(geos))
      return null;

    for (JTextField tfield : tfields)
      tfield.removeActionListener(this);
    coSliderHorizontal.removeActionListener(this);
    cbSliderFixed.removeActionListener(this);

    // check if properties have same values
    GeoNumeric temp, num0 = (GeoNumeric) geos[0];
    boolean equalMax = true;
    boolean equalMin = true;
    boolean equalWidth = true;
    boolean equalSliderFixed = true;
    boolean equalSliderHorizontal = true;
    boolean onlyAngles = true;

    for (Object geo : geos) {
      temp = (GeoNumeric) geo;

      if (!num0.isIntervalMinActive() || !temp.isIntervalMinActive()
          || !kernel.isEqual(num0.getIntervalMin(), temp.getIntervalMin()))
        equalMin = false;
      if (!num0.isIntervalMaxActive() || !temp.isIntervalMaxActive()
          || !kernel.isEqual(num0.getIntervalMax(), temp.getIntervalMax()))
        equalMax = false;
      if (!kernel.isEqual(num0.getSliderWidth(), temp.getSliderWidth()))
        equalWidth = false;
      if (num0.isSliderFixed() != temp.isSliderFixed())
        equalSliderFixed = false;
      if (num0.isSliderHorizontal() != temp.isSliderHorizontal())
        equalSliderHorizontal = false;

      if (!(temp instanceof GeoAngle))
        onlyAngles = false;
    }

    // set values
    // int oldDigits = kernel.getMaximumFractionDigits();
    // kernel.setMaximumFractionDigits(PropertiesDialogGeoElement.TEXT_FIELD_FRACTION_DIGITS);
    kernel
        .setTemporaryPrintDecimals(PropertiesDialog.TEXT_FIELD_FRACTION_DIGITS);
    if (equalMin) {
      if (onlyAngles)
        tfMin.setText(kernel.formatAngle(num0.getIntervalMin()).toString());
      else
        tfMin.setText(kernel.format(num0.getIntervalMin()));
    } else
      tfMin.setText("");

    if (equalMax) {
      if (onlyAngles)
        tfMax.setText(kernel.formatAngle(num0.getIntervalMax()).toString());
      else
        tfMax.setText(kernel.format(num0.getIntervalMax()));
    } else
      tfMax.setText("");

    if (equalWidth)
      tfWidth.setText(kernel.format(num0.getSliderWidth()));
    else
      tfMax.setText("");

    // kernel.setMaximumFractionDigits(oldDigits);
    kernel.restorePrintAccuracy();

    if (equalSliderFixed)
      cbSliderFixed.setSelected(num0.isSliderFixed());

    if (equalSliderHorizontal)
      // TODO why doesn't this work when you create a slider
      coSliderHorizontal.setSelectedIndex(num0.isSliderHorizontal() ? 0 : 1);

    for (JTextField tfield : tfields)
      tfield.addActionListener(this);
    coSliderHorizontal.addActionListener(this);
    cbSliderFixed.addActionListener(this);

    return this;
  }
}

interface UpdateablePanel {
  public void setVisible(boolean flag);
  public JPanel update(Object[] geos);
}