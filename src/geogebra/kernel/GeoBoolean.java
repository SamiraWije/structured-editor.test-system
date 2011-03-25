/* 
 GeoGebra - Dynamic Mathematics for Schools
 Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.*;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * 
 * @author Markus
 * @version
 */
public class GeoBoolean extends GeoElement
    implements
      BooleanValue,
      NumberValue,
      AbsoluteScreenLocateable {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;
  private boolean value = false;
  private boolean isDefined = true;
  private boolean checkboxFixed = false;

  private ArrayList<GeoElement> condListenersShowObject;

  private StringBuffer sbToString;

  public GeoBoolean(Construction c) {
    super(c);
    setEuclidianVisible(false);
  }

  @Override
  public GeoElement copy() {
    GeoBoolean ret = new GeoBoolean(cons);
    ret.setValue(value);
    return ret;
  }

  /**
   * Tells conidition listeners that their condition is removed and calls
   * super.remove()
   */
  @Override
  protected void doRemove() {
    if (condListenersShowObject != null) {
      // copy conditionListeners into array
      Object[] geos = condListenersShowObject.toArray();
      condListenersShowObject.clear();

      // tell all condition listeners
      for (Object geo2 : geos) {
        GeoElement geo = (GeoElement) geo2;
        geo.removeCondition(this);
        kernel.notifyUpdate(geo);
      }
    }

    super.doRemove();
  }

  @Override
  final public ExpressionValue evaluate() {
    return this;
  }

  public int getAbsoluteScreenLocX() {
    return labelOffsetX;
  }

  public int getAbsoluteScreenLocY() {
    return labelOffsetY;
  }

  final public boolean getBoolean() {
    return value;
  }

  @Override
  protected String getClassName() {
    return "GeoBoolean";
  }

  /**
   * Returns 1 for true and 0 for false.
   */
  public double getDouble() {
    return value ? 1 : 0;
  }

  @Override
  public int getGeoClassType() {
    return GEO_CLASS_BOOLEAN;
  }

  final public int getMode() {
    return -1;
  }

  final public MyBoolean getMyBoolean() {
    return new MyBoolean(value);
  }

  public MyDouble getNumber() {
    return new MyDouble(kernel, getDouble());
  }

  public double getRealWorldLocX() {
    return 0;
  }

  public double getRealWorldLocY() {
    return 0;
  }

  private StringBuffer getSbToString() {
    if (sbToString == null)
      sbToString = new StringBuffer();
    return sbToString;
  }

  @Override
  protected String getTypeString() {
    return "Boolean";
  }

  @Override
  final public HashSet getVariables() {
    HashSet varset = new HashSet();
    varset.add(this);
    return varset;
  }

  /**
   * returns all class-specific xml tags for saveXML
   */
  @Override
  protected String getXMLtags() {
    StringBuffer sb = new StringBuffer();
    sb.append("\t<value val=\"");
    sb.append(value);
    sb.append("\"/>\n");

    sb.append(getXMLvisualTags(isIndependent()));
    sb.append(getXMLfixedTag());
    sb.append(getAuxiliaryXML());

    // checkbox fixed
    if (checkboxFixed) {
      sb.append("\t<checkbox fixed=\"");
      sb.append(checkboxFixed);
      sb.append("\"/>\n");
    }

    return sb.toString();
  }

  public boolean isAbsoluteScreenLocActive() {
    return true;
  }

  @Override
  public boolean isAbsoluteScreenLocateable() {
    return isIndependent();
  }

  @Override
  public boolean isBooleanValue() {
    return true;
  }

  public final boolean isCheckboxFixed() {
    return checkboxFixed;
  }

  /**
   * interface BooleanValue
   */
  @Override
  final public boolean isConstant() {
    return false;
  }
  @Override
  final public boolean isDefined() {
    return isDefined;
  }

  // Michael Borcherds 2008-04-30
  @Override
  final public boolean isEqual(GeoElement geo) {
    // return false if it's a different type, otherwise use equals() method
    if (geo.isGeoBoolean())
      return equals(geo);
    else
      return false;
  }

  @Override
  public boolean isFixable() {
    // visible checkbox should not be fixable
    return isIndependent() && !isSetEuclidianVisible();
  }

  @Override
  public boolean isGeoBoolean() {
    return true;
  }

  @Override
  final public boolean isLeaf() {
    return true;
  }

  @Override
  public boolean isNumberValue() {
    return true;
  }

  @Override
  public boolean isPolynomialInstance() {
    return false;
  }

  @Override
  public boolean isTextValue() {
    return false;
  }

  public boolean isVector3DValue() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isVectorValue() {
    return false;
  }

  /**
   * Registers geo as a listener for updates of this boolean object. If this
   * object is updated it calls geo.updateConditions()
   * 
   * @param geo
   */
  protected void registerConditionListener(GeoElement geo) {
    if (condListenersShowObject == null)
      condListenersShowObject = new ArrayList<GeoElement>();
    condListenersShowObject.add(geo);
  }

  @Override
  public void resolveVariables() {
  }

  @Override
  public void set(GeoElement geo) {
    GeoBoolean b = (GeoBoolean) geo;
    setValue(b.value);
    isDefined = b.isDefined;
  }

  public void setAbsoluteScreenLoc(int x, int y) {
    if (checkboxFixed)
      return;

    labelOffsetX = x;
    labelOffsetY = y;
  }

  public void setAbsoluteScreenLocActive(boolean flag) {
  }

  public final void setCheckboxFixed(boolean checkboxFixed) {
    this.checkboxFixed = checkboxFixed;
  }

  final public void setDefined() {
    isDefined = true;
  }

  // dummy implementation of mode
  final public void setMode(int mode) {
  }

  public void setRealWorldLoc(double x, double y) {
  }

  @Override
  final public void setUndefined() {
    isDefined = false;
  }

  public void setValue(boolean val) {
    value = val;
  }

  @Override
  public final boolean showInAlgebraView() {
    return true;
  }

  @Override
  public boolean showInEuclidianView() {
    return isIndependent();
  }

  @Override
  final public String toString() {
    StringBuffer sbToString = getSbToString();
    sbToString.setLength(0);
    sbToString.append(label);
    sbToString.append(" = ");
    sbToString.append(toValueString());
    return sbToString.toString();
  }

  @Override
  final public String toValueString() {
    switch (kernel.getCASPrintForm()) {
      case ExpressionNode.STRING_TYPE_MATH_PIPER :
        return value ? "True" : "False";

      default :
        return value ? "true" : "false";
    }
  }

  protected void unregisterConditionListener(GeoElement geo) {
    if (condListenersShowObject != null)
      condListenersShowObject.remove(geo);
  }

  /**
   * Calls super.update() and update() for all registered condition listener
   * geos.
   */
  @Override
  public void update() {
    super.update();

    // update all registered locatables (they have this point as start point)
    if (condListenersShowObject != null)
      for (int i = 0; i < condListenersShowObject.size(); i++) {
        GeoElement geo = condListenersShowObject.get(i);
        kernel.notifyUpdate(geo);
      }
  }

}