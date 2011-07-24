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
import geogebra.kernel.arithmetic.*;
import geogebra.kernel.roots.RealRootFunction;

/**
 * Explicit function in one variable ("x"). This is actually a wrapper class for
 * Function in geogebra.kernel.arithmetic. In arithmetic trees (ExpressionNode)
 * it evaluates to a Function.
 * 
 * @author Markus Hohenwarter
 */
public class GeoFunction extends GeoElement
    implements
      Path,
      Translateable,
      Traceable,
      Functional,
      GeoFunctionable,
      GeoDeriveable,
      ParametricCurve,
      LineProperties,
      RealRootFunction {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  protected static GeoFunction add(GeoFunction resultFun, GeoFunction fun1,
      GeoFunction fun2) {

    Kernel kernel = fun1.getKernel();

    FunctionVariable x1 = fun1.getFunction().getFunctionVariable();
    FunctionVariable x2 = fun2.getFunction().getFunctionVariable();
    FunctionVariable x = new FunctionVariable(kernel);

    ExpressionNode left = fun1.getFunctionExpression().getCopy(kernel);
    ExpressionNode right = fun2.getFunctionExpression().getCopy(kernel);

    ExpressionNode sum = new ExpressionNode(fun1.getKernel(), left.replace(x1,
        x), ExpressionNode.PLUS, right.replace(x2, x));

    Function f = new Function(sum, x);

    resultFun.setFunction(f);
    resultFun.setDefined(true);

    return resultFun;
  }
  public static GeoFunction subtract(GeoFunction resultFun, GeoFunction fun1,
      GeoFunction fun2) {

    Kernel kernel = fun1.getKernel();

    FunctionVariable x1 = fun1.getFunction().getFunctionVariable();
    FunctionVariable x2 = fun2.getFunction().getFunctionVariable();
    FunctionVariable x = new FunctionVariable(kernel);

    ExpressionNode left = fun1.getFunctionExpression().getCopy(kernel);
    ExpressionNode right = fun2.getFunctionExpression().getCopy(kernel);

    ExpressionNode sum = new ExpressionNode(fun1.getKernel(), left.replace(x1,
        x), ExpressionNode.MINUS, right.replace(x2, x));

    Function f = new Function(sum, x);

    resultFun.setFunction(f);
    resultFun.setDefined(true);

    return resultFun;
  }
  private Function fun;
  private boolean isDefined = true;

  private boolean trace, spreadsheetTrace;

  // parent conditional function
  // private GeoFunctionConditional parentCondFun = null;

  private final String varStr = "x";

  // Victor Franco Espino 25-04-2007

  // if the function includes a division by var, e.g. 1/x, 1/(2+x)
  private boolean includesDivisionByVar = false;

  // Victor Franco Espino 25-04-2007
  /*
   * Parameter in dialog box for adjust color of curvature
   */
  double CURVATURE_COLOR = 15;// optimal value

  private GeoFunction derivGeoFun;

  private Function includesDivisionByVarFun = null;

  private final StringBuffer sbToString = new StringBuffer(80);

  public GeoFunction(Construction c) {
    super(c);
  }

  protected GeoFunction(Construction c, String label, Function f) {
    super(c);
    fun = f;
    setLabel(label);
  }

  /** copy constructor */
  private GeoFunction(GeoFunction f) {
    super(f.cons);
    set(f);
  }

  @Override
  public GeoElement copy() {
    return new GeoFunction(this);
  }

  public PathMover createPathMover() {
    return new PathMoverGeneric(this);
  }

  @Override
  public ExpressionValue evaluate() {
    return this;
  }

  /**
   * Returns this function's value at position x.
   * 
   * @param x
   * @return f(x)
   */
  public double evaluate(double x) {
    if (fun == null)
      return Double.NaN;
    else
      return fun.evaluate(x);
  }

  /**
   * Returns this boolean function's value at position x.
   * 
   * @param x
   * @return f(x)
   */
  final protected boolean evaluateBoolean(double x) {
    return fun.evaluateBoolean(x);
  }

  /**
   * Evaluates curvature for function: k(x) = f''/T^3, T = sqrt(1+(f')^2)
   * 
   * @author Victor Franco Espino, Markus Hohenwarter
   */
  public double evaluateCurvature(double x) {
    Function f1 = fun.getDerivative(1);
    Function f2 = fun.getDerivative(2);
    if (f1 == null || f2 == null)
      return Double.NaN;

    double f1eval = f1.evaluate(x);
    double t = Math.sqrt(1 + f1eval * f1eval);
    double t3 = t * t * t;
    return f2.evaluate(x) / t3;
  }

  public GeoVec2D evaluateCurve(double t) {
    return new GeoVec2D(kernel, t, evaluate(t));
  }

  public void evaluateCurve(double t, double[] out) {
    out[0] = t;
    out[1] = evaluate(t);
  }

  @Override
  protected String getClassName() {
    return "GeoFunction";
  }

  final public Function getFunction() {
    return fun;
  }

  /**
   * Returns the corresponding Function for the given x-value. This is important
   * for conditional functions where we have two differen Function objects.
   * 
   * @param startValue
   * @return
   */
  public Function getFunction(double x) {
    return fun;
  }

  final public ExpressionNode getFunctionExpression() {
    if (fun == null)
      return null;
    else
      return fun.getExpression();
  }
  @Override
  public int getGeoClassType() {
    return GEO_CLASS_FUNCTION;
  }

  public GeoFunction getGeoDerivative(int order) {
    if (derivGeoFun == null)
      derivGeoFun = new GeoFunction(cons);

    derivGeoFun.setDerivative(this, order);
    return derivGeoFun;
  }

  public GeoFunction getGeoFunction() {
    return this;
  }

  /**
   * Returns the largest possible parameter value for this path (may be
   * Double.POSITIVE_INFINITY)
   * 
   * @return
   */
  public double getMaxParameter() {
    return kernel.getXmax();
  }

  /**
   * Returns the smallest possible parameter value for this path (may be
   * Double.NEGATIVE_INFINITY)
   * 
   * @return
   */
  public double getMinParameter() {
    return kernel.getXmin();
  }

  public int getMode() {
    // dummy
    return -1;
  }

  final public RealRootFunction getRealRootFunctionX() {
    return new RealRootFunction() {
      public double evaluate(double t) {
        return t;
      }
    };
  }

  final public RealRootFunction getRealRootFunctionY() {
    return new RealRootFunction() {
      public double evaluate(double t) {
        return GeoFunction.this.evaluate(t);
      }
    };
  }

  public boolean getSpreadsheetTrace() {
    return spreadsheetTrace;
  }

  public boolean getTrace() {
    return trace;
  }
  @Override
  protected String getTypeString() {
    return "Function";
  }

  public String getVarString() {
    return varStr;
  }

  /**
   * save object in xml format
   */
  @Override
  public final String getXML() {
    StringBuffer sb = new StringBuffer();

    // an indpendent function needs to add
    // its expression itself
    // e.g. f(x) = x� - 3x
    if (isIndependent()) {
      sb.append("<expression");
      sb.append(" label =\"");
      sb.append(label);
      sb.append("\" exp=\"");
      sb.append(toString());
      // expression
      sb.append("\"/>\n");
    }

    sb.append("<element");
    sb.append(" type=\"function\"");
    sb.append(" label=\"");
    sb.append(label);
    sb.append("\">\n");
    sb.append(getXMLtags());
    sb.append(getCaptionXML());
    sb.append("</element>\n");

    return sb.toString();
  }

  /**
   * returns all class-specific xml tags for getXML
   */
  @Override
  protected String getXMLtags() {
    StringBuffer sb = new StringBuffer();
    sb.append(super.getXMLtags());

    // line thickness and type
    sb.append(getLineStyleXML());

    return sb.toString();
  }

  /**
   * Returns whether this function includes a division by variable, e.g. f(x) =
   * 1/x, 1/(2+x), sin(3/x), ...
   */
  final protected boolean includesDivisionByVar() {
    if (includesDivisionByVarFun != fun) {
      includesDivisionByVarFun = fun;
      includesDivisionByVar = fun != null && fun.includesDivisionByVariable();
    }
    return includesDivisionByVar;
  }

  public boolean isBooleanFunction() {
    if (fun != null)
      return fun.isBooleanFunction();
    else
      return false;
  }

  public boolean isClosedPath() {
    return false;
  }
  @Override
  public boolean isDefined() {
    return isDefined && fun != null;
  }

  // Michael Borcherds 2009-02-15
  @Override
  public boolean isEqual(GeoElement geo) {

    // return return geo.isEqual(this); rather than false
    // in case we improve checking in GeoFunctionConditional in future
    if (geo.getGeoClassType() == GeoElement.GEO_CLASS_FUNCTIONCONDITIONAL)
      return geo.isEqual(this);

    String f = getFormulaString(ExpressionNode.STRING_TYPE_MATH_PIPER, true);
    String g = geo
        .getFormulaString(ExpressionNode.STRING_TYPE_MATH_PIPER, true);

    String diff = "";

    try {
      diff = kernel.evaluateMathPiper("TrigSimpCombine(ExpandBrackets(" + f
          + "-(" + g + ")))");
    } catch (Exception e) {
      return false;
    }

    if ("0".equals(diff))
      return true;
    else
      return false;

  }

  final public boolean isFunctionInX() {
    return true;
  }

  @Override
  public boolean isGeoDeriveable() {
    return true;
  }

  @Override
  public boolean isGeoFunction() {
    if (fun != null)
      return !fun.isBooleanFunction();
    else
      return true;
  }

  @Override
  public boolean isGeoFunctionable() {
    return isGeoFunction();
  }

  @Override
  public boolean isNumberValue() {
    return false;
  }

  public boolean isOnPath(GeoPointInterface PI, double eps) {

    GeoPoint P = (GeoPoint) PI;

    if (P.getPath() == this)
      return true;

    return isDefined && Math.abs(fun.evaluate(P.inhomX) - P.inhomY) <= eps;
  }

  @Override
  public boolean isPath() {
    return true;
  }

  public boolean isPolynomialFunction(boolean forRootFinding) {
    return isPolynomialFunction(forRootFinding, false);
  }

  /**
   * Returns true if this function is a polynomial.
   * 
   * @param forRootFinding
   *          : set to true if you want to allow functions that can be factored
   *          into polynomial factors for root finding (e.g. sqrt(x) could be
   *          replaced by x)
   * @param symbolic
   *          : function's symbolic expression must be a polynomial, e.g. x^2 is
   *          ok but not x^a
   */
  public boolean isPolynomialFunction(boolean forRootFinding, boolean symbolic) {
    // don't do root finding simplification here
    // i.e. don't replace a factor "sqrt(x)" by "x"
    if (!isDefined())
      return false;
    else
      return fun.isConstantFunction()
          || (symbolic ? fun.getSymbolicPolynomialFactors(forRootFinding) : fun
              .getPolynomialFactors(forRootFinding)) != null;
  }

  @Override
  public boolean isPolynomialInstance() {
    return false;
  }

  @Override
  public boolean isTextValue() {
    return false;
  }

  @Override
  public boolean isTraceable() {
    return true;
  }

  @Override
  final public boolean isTranslateable() {
    return fun != null && !isBooleanFunction();
  }

  public boolean isVector3DValue() {
    return false;
  }

  @Override
  public boolean isVectorValue() {
    return false;
  }

  public void pathChanged(GeoPointInterface PI) {

    GeoPoint P = (GeoPoint) PI;

    PathParameter pp = P.getPathParameter();
    P.x = pp.t;
    pointChanged(P);
  }

  /*
   * Path interface
   */
  public void pointChanged(GeoPointInterface PI) {

    GeoPoint P = (GeoPoint) PI;

    if (P.z == 1.0)
      P.x = P.x;
    else
      P.x = P.x / P.z;

    if (fun.hasInterval()) {
      // don't let P move out of interval
      double a = fun.getIntervalMin();
      double b = fun.getIntervalMax();
      if (P.x < a)
        P.x = a;
      else if (P.x > b)
        P.x = b;
    }

    P.y = fun.evaluate(P.x);
    P.z = 1.0;

    // set path parameter for compatibility with
    // PathMoverGeneric
    PathParameter pp = P.getPathParameter();
    pp.t = P.x;
  }

  /**
   * Replaces geo and all its dependent geos in this function's expression by
   * copies of their values.
   */
  public void replaceChildrenByValues(GeoElement geo) {
    if (fun != null)
      fun.replaceChildrenByValues(geo);
  }

  @Override
  public void set(GeoElement geo) {
    GeoFunction geoFun = (GeoFunction) geo;

    if (geo == null || geoFun.fun == null) {
      fun = null;
      isDefined = false;
      return;
    } else {
      isDefined = geoFun.isDefined;
      fun = new Function(geoFun.fun, kernel);
    }

    // macro OUTPUT
    if (geo.cons != cons && isAlgoMacroOutput())
      // this object is an output object of AlgoMacro
      // we need to check the references to all geos in its function's
      // expression
      if (!geoFun.isIndependent()) {
        AlgoMacro algoMacro = (AlgoMacro) getParentAlgorithm();
        algoMacro.initFunction(fun);
      }
  }

  public void setDefined(boolean defined) {
    isDefined = defined;
  }

  /**
   * Set this function to the n-th derivative of f
   * 
   * @param f
   * @param order
   */
  public void setDerivative(GeoDeriveable fd, int n) {
    GeoFunction f = (GeoFunction) fd;

    if (f.isDefined()) {
      fun = f.fun.getDerivative(n);
      isDefined = fun != null;
    } else
      isDefined = false;
  }

  /**
   * Set this function to the expanded version of f, e.g. 3*(x-2) is expanded to
   * 3*x - 6.
   */
  public void setExpanded(GeoFunction f) {
    if (f.isDefined())
      fun = f.fun.getExpanded();
    else
      isDefined = false;
  }

  public void setFunction(Function f) {
    fun = f;
  }

  /**
   * Set this function to the integral of f
   * 
   * @param f
   */
  public void setIntegral(GeoFunction f) {
    if (f.isDefined())
      fun = f.fun.getIntegral();
    else
      isDefined = false;
  }

  public boolean setInterval(double a, double b) {
    if (fun == null)
      return false;
    else
      return fun.setInterval(a, b);
  }

  public void setMode(int mode) {
    // dummy
  }

  public void setSpreadsheetTrace(boolean spreadsheetTrace) {
    this.spreadsheetTrace = spreadsheetTrace;
  }

  public void setTrace(boolean trace) {
    this.trace = trace;
  }

  @Override
  public void setUndefined() {
    isDefined = false;
  }

  @Override
  public boolean showInAlgebraView() {
    return true;
  }

  @Override
  protected boolean showInEuclidianView() {
    return isDefined();
  }

  @Override
  public String toLaTeXString(boolean symbolic) {
    if (isDefined())
      return fun.toLaTeXString(symbolic);
    else
      return Plain.undefined;
  }

  @Override
  public String toString() {
    sbToString.setLength(0);
    if (isLabelSet()) {
      sbToString.append(label);
      sbToString.append("(");
      sbToString.append(varStr);
      sbToString.append(") = ");
    }
    sbToString.append(toValueString());
    return sbToString.toString();
  }

  /*
   * public final GeoFunctionConditional getParentCondFun() { return
   * parentCondFun; }
   * 
   * public final void setParentCondFun(GeoFunctionConditional parentCondFun) {
   * this.parentCondFun = parentCondFun; }
   */

  public String toSymbolicString() {
    if (isDefined())
      return fun.toString();
    else
      return Plain.undefined;
  }

  @Override
  public String toValueString() {
    if (isDefined())
      return fun.toValueString();
    else
      return Plain.undefined;
  }

  public void translate(double vx, double vy) {
    fun.translate(vx, vy);
  }

  /**
   * translate function by vector v
   */
  final public void translate(GeoVector v) {
    translate(v.x, v.y);
  }

}
