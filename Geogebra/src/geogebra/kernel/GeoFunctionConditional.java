/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.kernel;

import geogebra.Plain;
import geogebra.kernel.arithmetic.Function;

/**
 * Explicit function in one variable ("x") in the form of an If-Then-Else
 * statement
 * 
 * example: If[ x < 2, x^2, x + 2 ] where "x < 2" is a boolean function
 * 
 * @author Markus Hohenwarter
 */
public class GeoFunctionConditional extends GeoFunction {

  private boolean isDefined = true;

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;
  private GeoFunction condFun, ifFun, elseFun;

  private GeoFunctionConditional derivGeoFun;

  private final StringBuffer sbToString = new StringBuffer(80);

  /**
   * Creates a new GeoFunctionConditional object.
   * 
   * @param c
   * @param condFun
   *          : a GeoFunction that evaluates to a boolean value (i.e.
   *          isBooleanFunction() returns true)
   * @param ifFun
   * @param elseFun
   *          : may be null
   */
  protected GeoFunctionConditional(Construction c, GeoFunction condFun,
      GeoFunction ifFun, GeoFunction elseFun) {
    super(c);
    this.condFun = condFun;
    this.ifFun = ifFun;
    this.elseFun = elseFun;
  }

  private GeoFunctionConditional(GeoFunctionConditional geo) {
    super(geo.cons);
    set(geo);
  }

  @Override
  public GeoElement copy() {
    return new GeoFunctionConditional(this);
  }

  /**
   * Returns this function's value at position x.
   * 
   * @param x
   * @return f(x) = condition(x) ? ifFun(x) : elseFun(x)
   */
  @Override
  final public double evaluate(double x) {
    if (condFun.evaluateBoolean(x))
      return ifFun.evaluate(x);
    else if (elseFun == null)
      return Double.NaN;
    else
      return elseFun.evaluate(x);
  }

  @Override
  protected String getClassName() {
    return "GeoFunctionConditional";
  }

  final public GeoFunction getElseFunction() {
    return elseFun;
  }

  /**
   * Returns the corresponding Function for the given x-value. This is important
   * for conditional functions where we have two different Function objects.
   */
  @Override
  public Function getFunction(double x) {
    if (elseFun == null)
      return ifFun.getFunction(x);
    else if (condFun.evaluateBoolean(x))
      return ifFun.getFunction(x);
    else
      return elseFun.getFunction(x);
  }

  @Override
  public int getGeoClassType() {
    return GEO_CLASS_FUNCTIONCONDITIONAL;
  }

  @Override
  public GeoFunction getGeoDerivative(int order) {
    if (derivGeoFun == null)
      derivGeoFun = new GeoFunctionConditional(this);

    derivGeoFun.setDerivative(this, order);
    return derivGeoFun;
  }

  final public GeoFunction getIfFunction() {
    return ifFun;
  }

  @Override
  protected String getTypeString() {
    return "Function";
  }

  @Override
  public boolean isBooleanFunction() {
    return false;
  }

  @Override
  public boolean isDefined() {
    return isDefined;
  }

  @Override
  final public boolean isEqual(GeoElement geo) {

    if (geo.getGeoClassType() != GeoElement.GEO_CLASS_FUNCTIONCONDITIONAL)
      return false;

    GeoFunctionConditional geoFun = (GeoFunctionConditional) geo;

    // TODO better CAS checking for condFun

    return condFun.toValueString().equals(geoFun.condFun.toValueString())
        && ifFun.isEqual(geoFun.ifFun) && elseFun.isEqual(geoFun.elseFun);

  }

  @Override
  public boolean isGeoFunction() {
    return true;
  }

  @Override
  public boolean isGeoFunctionConditional() {
    return true;
  }
  @Override
  public boolean isOnPath(GeoPointInterface PI, double eps) {

    GeoPoint P = (GeoPoint) PI;

    if (P.getPath() == this)
      return true;

    return isDefined() && Math.abs(evaluate(P.inhomX) - P.inhomY) <= eps;
  }

  @Override
  public boolean isPolynomialFunction(boolean forRootFinding, boolean symbolic) {
    return false;
  }

  /*
   * Path interface
   */
  @Override
  public void pointChanged(GeoPointInterface PI) {

    GeoPoint P = (GeoPoint) PI;

    if (P.z == 1.0)
      P.x = P.x;
    else
      P.x = P.x / P.z;

    P.y = evaluate(P.x);
    P.z = 1.0;

    // set path parameter for compatibility with
    // PathMoverGeneric
    PathParameter pp = P.getPathParameter();
    pp.t = P.x;
  }
  /**
   * Replaces geo and all its dependent geos in this function's expressions by
   * copies of their values.
   */
  @Override
  public void replaceChildrenByValues(GeoElement geo) {
    if (condFun != null)
      condFun.replaceChildrenByValues(geo);
    if (ifFun != null)
      ifFun.replaceChildrenByValues(geo);
    if (elseFun != null)
      elseFun.replaceChildrenByValues(geo);
  }

  @Override
  public void set(GeoElement geo) {
    GeoFunctionConditional geoFunCond = (GeoFunctionConditional) geo;
    isDefined = geoFunCond.isDefined;

    if (condFun == null)
      condFun = (GeoFunction) geoFunCond.condFun.copyInternal(cons);

    if (isAlgoMacroOutput()) {
      condFun.setAlgoMacroOutput(true);
      condFun.setParentAlgorithm(getParentAlgorithm());
      condFun.setConstruction(cons);
    }
    condFun.set(geoFunCond.condFun);

    if (ifFun == null)
      ifFun = (GeoFunction) geoFunCond.ifFun.copyInternal(cons);
    if (isAlgoMacroOutput()) {
      ifFun.setAlgoMacroOutput(true);
      ifFun.setParentAlgorithm(getParentAlgorithm());
      ifFun.setConstruction(cons);
    }
    ifFun.set(geoFunCond.ifFun);

    if (geoFunCond.elseFun == null)
      elseFun = null;
    else {
      if (elseFun == null)
        elseFun = (GeoFunction) geoFunCond.elseFun.copyInternal(cons);
      if (isAlgoMacroOutput()) {
        elseFun.setAlgoMacroOutput(true);
        elseFun.setParentAlgorithm(getParentAlgorithm());
        elseFun.setConstruction(cons);
      }
      elseFun.set(geoFunCond.elseFun);
    }
  }

  /**
   * Set this function to the n-th derivative of f
   * 
   * @param f
   * @param order
   */
  @Override
  public void setDerivative(GeoDeriveable f, int n) {
    GeoFunctionConditional fcond = (GeoFunctionConditional) f;
    ifFun.setDerivative(fcond.ifFun, n);
    if (elseFun != null)
      elseFun.setDerivative(fcond.elseFun, n);
  }

  /**
   * Set this function to the integral of f
   * 
   * @param f
   * @param order
   */
  @Override
  public void setIntegral(GeoFunction f) {
    GeoFunctionConditional fcond = (GeoFunctionConditional) f;
    ifFun.setIntegral(fcond.ifFun);
    if (elseFun != null)
      elseFun.setIntegral(fcond.elseFun);
  }

  @Override
  public boolean setInterval(double a, double b) {
    boolean success = ifFun.setInterval(a, b);
    if (elseFun != null)
      success = elseFun.setInterval(a, b) && success;

    return success;
  }

  @Override
  final public String toLaTeXString(boolean symbolic) {
    return toString(symbolic);
  }

  @Override
  public final String toString() {
    sbToString.setLength(0);
    if (isLabelSet()) {
      sbToString.append(label);
      sbToString.append("(x) = ");
    }
    sbToString.append(toValueString());
    return sbToString.toString();
  }

  private String toString(boolean symbolic) {
    if (isDefined()) {
      StringBuffer sb = new StringBuffer(80);
      sb.append(app.getCommand("If"));
      sb.append("[");

      if (symbolic)
        sb.append(condFun.toSymbolicString());
      else
        sb.append(condFun.toValueString());

      sb.append(", ");

      if (symbolic)
        sb.append(ifFun.toSymbolicString());
      else
        sb.append(ifFun.toValueString());

      if (elseFun != null) {
        sb.append(", ");
        if (symbolic)
          sb.append(elseFun.toSymbolicString());
        else
          sb.append(elseFun.toValueString());
      }
      sb.append("]");
      return sb.toString();
    } else
      return Plain.undefined;
  }

  @Override
  final public String toSymbolicString() {
    return toString(true);
  }

  @Override
  final public String toValueString() {
    return toString(false);
  }

  @Override
  public void translate(double vx, double vy) {
    // translate condition by vx, thus
    // changing every x into (x - vx)
    condFun.translate(vx, 0);

    // translate if and else parts too
    ifFun.translate(vx, vy);
    if (elseFun != null)
      elseFun.translate(vx, vy);
  }

}
