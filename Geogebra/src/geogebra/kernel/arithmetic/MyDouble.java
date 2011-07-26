/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * MyDouble.java
 *
 * Created on 07. Oktober 2001, 12:23
 */

package geogebra.kernel.arithmetic;

import geogebra.Plain;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.Kernel;
import geogebra.util.MyMath;

import java.util.HashSet;

/**
 * 
 * @author Markus Hohenwarter
 * @version
 */
public class MyDouble extends ValidExpression implements NumberValue {

  /** c = a + b */
  final protected static void add(MyDouble a, MyDouble b, MyDouble c) {
    c.val = a.val + b.val;
    c.isAngle = a.isAngle && b.isAngle;
  }
  /** c = a / b */
  final protected static void div(MyDouble a, MyDouble b, MyDouble c) {
    if (b.kernel.isZero(b.val))
      c.val = Double.NaN;
    else
      c.val = a.val / b.val;
    c.isAngle = a.isAngle && !b.isAngle;
  }

  /** c = a * b */
  final protected static void mult(MyDouble a, MyDouble b, MyDouble c) {
    c.val = a.val * b.val;
    c.isAngle = a.isAngle || b.isAngle;
  }

  /** c = pow(a,b) */
  final protected static void pow(MyDouble a, MyDouble b, MyDouble c) {
    // if (b.val == 0d) {
    // if (a.val < 0d)
    // c.val = -1;
    // else
    // c.val = 1;
    // } else {
    // // check for integer value in exponent
    // //double bint = Math.round(b.val);
    // //if (b.kernel.isEqual(b.val, bint))
    // // c.val = Math.pow(a.val, bint);
    // //else
    // c.val = Math.pow(a.val, b.val);
    // }
    //    	
    c.val = Math.pow(a.val, b.val);
    c.isAngle = a.isAngle && !b.isAngle;
  }

  /*
   * Java quirk/bug Round(NaN) = 0
   */
  final private static double round(double x) {
    if (!(Double.isInfinite(x) || Double.isNaN(x)))
      return Math.round(x);
    else
      return x;

  }

  /** c = a - b */
  final protected static void sub(MyDouble a, MyDouble b, MyDouble c) {
    c.val = a.val - b.val;
    c.isAngle = a.isAngle && b.isAngle;
  }

  private double val;

  private boolean isAngle = false;

  private Kernel kernel;

  public static double LARGEST_INTEGER = 9007199254740992.0; // 0x020000000000000

  public MyDouble(Kernel kernel) {
    this(kernel, 0.0);
  }

  /** Creates new MyDouble */
  public MyDouble(Kernel kernel, double x) {
    this.kernel = kernel;
    val = x;
  }

  private MyDouble(MyDouble d) {
    kernel = d.kernel;
    val = d.val;
    isAngle = d.isAngle;
  }

  final protected MyDouble abs() {
    val = Math.abs(val);
    return this;
  }
  final protected MyDouble acos() {
    val = Math.acos(val);
    isAngle = kernel.arcusFunctionCreatesAngle;
    return this;
  }

  final protected MyDouble acosh() {
    val = MyMath.acosh(val);
    isAngle = false;
    return this;
  }

  final protected MyDouble apply(Functional f) {
    val = f.evaluate(val);
    isAngle = false; // want function to return numbers eg f(x) = sin(x), f(45°)
    return this;
  }

  final protected MyDouble asin() {
    val = Math.asin(val);
    isAngle = kernel.arcusFunctionCreatesAngle;
    return this;
  }

  final protected MyDouble asinh() {
    val = MyMath.asinh(val);
    isAngle = false;
    return this;
  }

  final protected MyDouble atan() {
    val = Math.atan(val);
    isAngle = kernel.arcusFunctionCreatesAngle;
    return this;
  }

  final protected MyDouble atanh() {
    val = MyMath.atanh(val);
    isAngle = false;
    return this;
  }

  final protected MyDouble cbrt() {
    val = MyMath.cbrt(val);
    isAngle = false;
    return this;
  }
  final protected MyDouble ceil() {
    // angle in degrees
    if (isAngle && kernel.getAngleUnit() == Kernel.ANGLE_DEGREE)
      val = Kernel.PI_180 * Math.ceil(val * Kernel.CONST_180_PI);
    else
      // number or angle in radians
      val = Math.ceil(val);
    return this;
  }

  final public boolean contains(ExpressionValue ev) {
    return ev == this;
  }

  final protected MyDouble cos() {
    val = Math.cos(val);
    isAngle = false;
    return this;
  }
  final protected MyDouble cosh() {
    val = MyMath.cosh(val);
    isAngle = false;
    return this;
  }
  public ExpressionValue deepCopy(Kernel kernel) {
    MyDouble ret = new MyDouble(this);
    ret.kernel = kernel;
    return ret;
  }

  final public ExpressionValue evaluate() {
    return this;
  }
  final protected MyDouble exp() {
    val = Math.exp(val);
    isAngle = false;
    return this;
  }
  final protected MyDouble factorial() {
    val = MyMath.factorial(val);
    isAngle = false;
    return this;
  }

  final protected MyDouble floor() {
    // angle in degrees
    if (isAngle && kernel.getAngleUnit() == Kernel.ANGLE_DEGREE)
      val = Kernel.PI_180 * Math.floor(val * Kernel.CONST_180_PI);
    else
      // number or angle in radians
      val = Math.floor(val);
    return this;
  }
  final protected MyDouble gamma() {
    val = MyMath.gamma(val, kernel);
    isAngle = false;
    return this;
  }
  final public double getDouble() {
    return val;
  }
  /*
   * interface NumberValue
   */
  final public MyDouble getNumber() {
    return new MyDouble(this);

    /*
     * Michael Borcherds 2008-05-20 removed unstable optimisation fails for eg
     * -2 sin(x) - 5 cos(x) if (isInTree()) { // used in expression node tree:
     * be careful return new MyDouble(this); } else { // not used anywhere:
     * reuse this object return this; }
     */
  }

  final public HashSet getVariables() {
    return null;
  }

  public boolean isAngle() {
    return isAngle;
  }

  public boolean isBooleanValue() {
    return false;
  }

  public boolean isConstant() {
    return true;
  }

  final public boolean isExpressionNode() {
    return false;
  }

  final public boolean isLeaf() {
    return true;
  }

  public boolean isListValue() {
    return false;
  }

  public boolean isNumberValue() {
    return true;
  }

  public boolean isPolynomialInstance() {
    return false;
  }

  public boolean isTextValue() {
    return false;
  }

  public boolean isVector3DValue() {
    return false;
  }

  public boolean isVectorValue() {
    return false;
  }

  final protected MyDouble log() {
    val = Math.log(val);
    isAngle = false;
    return this;
  }

  final protected MyDouble log10() {
    val = Math.log(val) / MyMath.LOG10;
    isAngle = false;
    return this;
  }

  final protected MyDouble log2() {
    val = Math.log(val) / MyMath.LOG2;
    isAngle = false;
    return this;
  }

  final public MyDouble random() {
    val = Math.random();
    isAngle = false;
    return this;
  }

  public void resolveVariables() {
  }

  final protected MyDouble round() {
    // angle in degrees
    if (isAngle && kernel.getAngleUnit() == Kernel.ANGLE_DEGREE)
      val = Kernel.PI_180 * MyDouble.round(val * Kernel.CONST_180_PI);
    else
      // number or angle in radians
      val = MyDouble.round(val);
    return this;
  }

  final public void set(double x) {
    val = x;
  }

  public void setAngle() {
    isAngle = true;
  }

  final protected MyDouble sgn() {
    val = MyMath.sgn(kernel, val);
    isAngle = false;
    return this;
  }

  final protected MyDouble sin() {
    val = Math.sin(val);
    isAngle = false;
    return this;
  }

  final protected MyDouble sinh() {
    val = MyMath.sinh(val);
    isAngle = false;
    return this;
  }

  final protected MyDouble sqrt() {
    val = Math.sqrt(val);
    isAngle = false;
    return this;
  }

  final protected MyDouble tan() {
    if (kernel.isZero(Math.cos(val)))
      val = Double.NaN;
    else
      val = Math.tan(val);
    isAngle = false;
    return this;
  }

  final protected MyDouble tanh() {
    val = MyMath.tanh(val);
    isAngle = false;
    return this;
  }

  final public GeoElement toGeoElement() {
    GeoNumeric num = new GeoNumeric(kernel.getConstruction());
    num.setValue(val);
    return num;
  }

  final public String toLaTeXString(boolean symbolic) {
    return toString();
  }

  @Override
  public String toString() {
    switch (kernel.getCASPrintForm()) {
      default :
        if (Double.isInfinite(val) || Double.isNaN(val))
          return Plain.undefined;

      case ExpressionNode.STRING_TYPE_GEOGEBRA_XML :
      case ExpressionNode.STRING_TYPE_MATH_PIPER :
        if (isAngle)
          return kernel.formatAngle(val).toString();
        else
          return kernel.format(val);
    }
  }

  final public String toValueString() {
    return toString();
  }
}
