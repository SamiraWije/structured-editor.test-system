/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * GeoVec2D.java
 *
 * Created on 31. August 2001, 11:34
 */

package geogebra.kernel.arithmetic;

import geogebra.kernel.GeoVec2D;
import geogebra.kernel.Kernel;
import geogebra.main.MyParseError;

import java.util.HashSet;

/**
 * 
 * @author Markus
 * @version
 */
public class MyVecNode extends ValidExpression implements VectorValue {

  private ExpressionValue x, y;
  private int mode = Kernel.COORD_CARTESIAN;
  private final Kernel kernel;

  /** Creates new MyVec2D */
  public MyVecNode(Kernel kernel) {
    this.kernel = kernel;
  }

  /**
   * Creates new MyVec2D with coordinates (x,y) as ExpresssionNodes. Both nodes
   * must evaluate to NumberValues.
   */
  public MyVecNode(Kernel kernel, ExpressionValue x, ExpressionValue y) {
    this(kernel);
    setCoords(x, y);
  }

  final public boolean contains(ExpressionValue ev) {
    return ev == this;
  }

  public ExpressionValue deepCopy(Kernel kernel) {
    return new MyVecNode(kernel, x.deepCopy(kernel), y.deepCopy(kernel));
  }

  public ExpressionValue evaluate() {
    return this;
  }

  final public double[] getCoords() {
    // check if both ExpressionNodes represent NumberValues
    ExpressionValue evx = x.evaluate();
    if (!evx.isNumberValue()) {
      String[] str = {"NumberExpected", evx.toString()};
      throw new MyParseError(kernel.getApplication(), str);
    }
    ExpressionValue evy = y.evaluate();
    if (!evy.isNumberValue()) {
      String[] str = {"NumberExpected", evy.toString()};
      throw new MyParseError(kernel.getApplication(), str);
    }

    if (mode == Kernel.COORD_POLAR) {
      double r = ((NumberValue) evx).getDouble();
      // negative radius yields undefined result
      if (r < 0)
        r = Double.NaN;
      double phi = ((NumberValue) evy).getDouble();
      double[] ret = {r * Math.cos(phi), r * Math.sin(phi)};
      return ret;
    } else { // CARTESIAN
      double[] ret = {((NumberValue) evx).getDouble(),
          ((NumberValue) evy).getDouble()};
      return ret;
    }
  }

  /** POLAR or CARTESIAN */
  public int getMode() {
    return mode;
  }

  /** returns all GeoElement objects in the both coordinate subtrees */
  public HashSet<?> getVariables() {
    HashSet temp, varset = x.getVariables();
    if (varset == null)
      varset = new HashSet<Object>();
    temp = y.getVariables();
    if (temp != null)
      varset.addAll(temp);

    return varset;
  }

  /**
   * interface VectorValue implementation
   */
  public GeoVec2D getVector() {
    GeoVec2D ret = new GeoVec2D(kernel, getCoords());
    ret.setMode(mode);
    return ret;
  }

  public ExpressionValue getX() {
    return x;
  }

  public ExpressionValue getY() {
    return y;
  }

  public boolean hasPolarCoords() {
    return mode == Kernel.COORD_POLAR;
  }

  public boolean isBooleanValue() {
    return false;
  }

  public boolean isConstant() {
    return x.isConstant() && y.isConstant();
  }

  final public boolean isExpressionNode() {
    return false;
  }

  public boolean isLeaf() {
    return true;
  }

  public boolean isListValue() {
    return false;
  }

  public boolean isNumberValue() {
    return false;
  }

  public boolean isPoint() {
    return true;
  }

  public boolean isPolynomialInstance() {
    return false;
  }

  public boolean isTextValue() {
    return false;
  }
  public boolean isVector3DValue() {
    // TODO Auto-generated method stub
    return false;
  }

  // could be vector or point
  public boolean isVectorValue() {
    return true;
  }

  public void resolveVariables() {
    x.resolveVariables();
    y.resolveVariables();
  }

  private void setCoords(ExpressionValue x, ExpressionValue y) {
    this.x = x;
    this.y = y;
  }

  public void setMode(int mode) {
    this.mode = mode;
  }

  public void setPolarCoords(ExpressionValue r, ExpressionValue phi) {
    setCoords(r, phi);
    mode = Kernel.COORD_POLAR;
  }

  final public String toLaTeXString(boolean symbolic) {
    return toString();
  }

  @Override
  final public String toString() {
    StringBuffer sb = new StringBuffer();

    sb.append('(');
    sb.append(x.toString());
    if (mode == Kernel.COORD_CARTESIAN)
      sb.append(", ");
    else
      sb.append("; ");
    sb.append(y.toString());
    sb.append(')');
    return sb.toString();
  }

  public String toValueString() {
    return toString();
  }
}
