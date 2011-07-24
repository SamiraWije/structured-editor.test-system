/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * Kernel.java
 *
 * Created on 30. August 2001, 20:12
 */

package geogebra.kernel;

import geogebra.Plain;
import geogebra.io.MyXMLHandler;
import geogebra.kernel.arithmetic.*;
import geogebra.kernel.commands.AlgebraProcessor;
import geogebra.kernel.optimization.ExtremumFinder;
import geogebra.kernel.parser.Parser;
import geogebra.kernel.statistics.*;
import geogebra.main.Application;
import geogebra.main.MyError;
import geogebra.main.View;
import geogebra.util.ScientificFormat;

import java.text.NumberFormat;
import java.util.*;

import org.apache.commons.math.complex.Complex;

public class Kernel {

  // standard precision
  public final static double STANDARD_PRECISION = 1E-8;

  // minimum precision
  public final static double MIN_PRECISION = 1E-5;
  private final static double INV_MIN_PRECISION = 1E5;

  // maximum reasonable precision
  public final static double MAX_PRECISION = 1E-12;

  // current working precision
  public static double EPSILON = STANDARD_PRECISION;

  // maximum precision of double numbers
  public final static double MAX_DOUBLE_PRECISION = 1E-15;
  public final static double INV_MAX_DOUBLE_PRECISION = 1E15;

  // style of point/vector coordinates
  public static final int COORD_STYLE_DEFAULT = 0; // A = (3, 2) and B = (3;
  // 90�)
  public static final int COORD_STYLE_AUSTRIAN = 1; // A(3|2) and B(3; 90�)
  public static final int COORD_STYLE_FRENCH = 2; // A: (3, 2) and B: (3; 90�)
  /**
   * Returns the ConstructionElement for the given GeoElement. If geo is
   * independent geo itself is returned. If geo is dependent it's parent
   * algorithm is returned.
   */
  public static ConstructionElement getConstructionElement(GeoElement geo) {
    AlgoElement algo = geo.getParentAlgorithm();
    if (algo == null)
      return geo;
    else
      return algo;
  }

  private int coordStyle = 0;
  // STATIC
  final public static int ANGLE_RADIANT = 1;
  final public static int ANGLE_DEGREE = 2;
  final public static int COORD_CARTESIAN = 3;
  final public static int COORD_POLAR = 4;
  final public static int COORD_COMPLEX = 5;
  final public static String EULER_STRING = "\u212f"; // "\u0435";
  final public static String PI_STRING = "\u03c0";
  final public static double PI_2 = 2.0 * Math.PI;
  final public static double PI_HALF = Math.PI / 2.0;
  final public static double SQRT_2_HALF = Math.sqrt(2.0) / 2.0;
  final public static double PI_180 = Math.PI / 180;

  final public static double CONST_180_PI = 180 / Math.PI;
  // private static boolean KEEP_LEADING_SIGN = true;
  // print precision
  public static final int STANDARD_PRINT_DECIMALS = 2;
  private double PRINT_PRECISION = 1E-2;
  private final NumberFormat nf;
  private final ScientificFormat sf;

  public boolean useSignificantFigures = false;
  // rounding hack, see format()
  private static final double ROUND_HALF_UP_FACTOR_DEFAULT = 1.0 + 1E-15;

  private double ROUND_HALF_UP_FACTOR = ROUND_HALF_UP_FACTOR_DEFAULT;
  // used to store info when rounding is temporarily changed
  private Stack<Boolean> useSignificantFiguresList;
  private Stack<Integer> noOfSignificantFiguresList;

  /*
   * Significant figures
   * 
   * How to do:
   * 
   * private ScientificFormat sf; sf = new ScientificFormat(5, 20, false);
   * String s = sf.format(double)
   * 
   * need to address:
   * 
   * PRINT_PRECISION setPrintDecimals() getPrintDecimals()
   * getMaximumFractionDigits() setMaximumFractionDigits()
   * 
   * how to determine whether to use nf or sf
   */

  private Stack<Integer> noOfDecimalPlacesList;
  private int casPrintForm;

  private String casPrintFormPI; // for pi

  // before May 23, 2005 the function acos(), asin() and atan()
  // had an angle as result. Now the result is a number.
  // this flag is used to distinguish the different behaviour
  // depending on the the age of saved construction files
  public boolean arcusFunctionCreatesAngle = false;
  private boolean translateCommandName = true;
  private boolean undoActive = true;
  private boolean notifyViewsActive = true;
  private boolean viewReiniting = false;

  private boolean allowVisibilitySideEffects = true;

  // silentMode is used to create helper objects without any side effects
  // i.e. in silentMode no labels are created and no objects are added to views
  private boolean silentMode = false;

  // setResolveUnkownVarsAsDummyGeos
  private boolean resolveVariablesForCASactive = false;

  private double xmin, xmax, ymin, ymax, xscale, yscale;
  // Views may register to be informed about
  // changes to the Kernel
  // (add, remove, update)
  private final View[] views = new View[20];

  private int viewCnt = 0;
  protected Construction cons;
  protected Application app;
  protected AlgebraProcessor algProcessor;
  private EquationSolver eqnSolver;
  private RegressionMath regMath;
  private ExtremumFinder extrFinder;
  protected Parser parser;

  private Object ggbCAS;
  // Continuity on or off, default: false since V3.0
  private boolean continuous = false;

  private MacroManager macroManager;

  /** Evaluator for ExpressionNode */
  protected ExpressionNodeEvaluator expressionNodeEvaluator;

  private ArrayList<AlgoElement> renameListenerAlgos;

  private int oldViewCnt;

  private boolean notifyRepaint = true;

  /*
   * to avoid multiple calculations of the intersection points of the same two
   * objects, we remember all the intersection algorithms created
   */
  private final ArrayList<AlgoIntersect> intersectionAlgos = new ArrayList<AlgoIntersect>();

  static final int TRANSFORM_TRANSLATE = 0;

  static final int TRANSFORM_MIRROR_AT_POINT = 1;

  static final int TRANSFORM_MIRROR_AT_LINE = 2;

  static final int TRANSFORM_ROTATE = 3;

  static final int TRANSFORM_ROTATE_AROUND_POINT = 4;

  static final int TRANSFORM_DILATE = 5;

  // copy array a to array b
  final static void copy(double[] a, double[] b) {
    for (int i = 0; i < a.length; i++)
      b[i] = a[i];
  }

  // c[] = a[] / b
  final static void divide(double[] a, double b, double[] c) {
    for (int i = 0; i < a.length; i++)
      c[i] = a[i] / b;
  }

  /**
   * Compute greatest common divisor of given doubles. Note: all double values
   * are cast to long.
   */
  final public static double gcd(double[] numbers) {
    long gcd = (long) numbers[0];
    for (double number : numbers)
      gcd = gcd((long) number, gcd);
    return gcd;
  }

  /**
   * greatest common divisor
   */
  final public static long gcd(long m, long n) {
    // Return the GCD of positive integers m and n.
    if (m == 0 || n == 0)
      return Math.max(Math.abs(m), Math.abs(n));

    long p = m, q = n;
    while (p % q != 0) {
      long r = p % q;
      p = q;
      q = r;
    }
    return q;
  }

  public static boolean isEqual(double x, double y, double eps) {
    return x - eps < y && y < x + eps;
  }

  public static boolean keepOrientationForTransformation(int transformationType) {
    switch (transformationType) {
      case TRANSFORM_MIRROR_AT_LINE :
        return false;

      default :
        return true;
    }
  }

  /** returns max of abs(a[i]) */
  final static double maxAbs(double[] a) {
    double temp, max = Math.abs(a[0]);
    for (int i = 1; i < a.length; i++) {
      temp = Math.abs(a[i]);
      if (temp > max)
        max = temp;
    }
    return max;
  }

  // change signs of double array values, write result to array b
  final static void negative(double[] a, double[] b) {
    for (int i = 0; i < a.length; i++)
      b[i] = -a[i];
  }

  /**
   * Round a double to the given scale e.g. roundToScale(5.32, 1) = 5.0,
   * roundToScale(5.32, 0.5) = 5.5, roundToScale(5.32, 0.25) = 5.25,
   * roundToScale(5.32, 0.1) = 5.3
   */
  final public static double roundToScale(double x, double scale) {
    if (scale == 1.0)
      return Math.round(x);
    else
      return Math.round(x / scale) * scale;
  }

  final private static char sign(double x) {
    if (x > 0)
      return '+';
    else
      return '-';
  }

  private static String transformedGeoLabel(GeoElement geo) {
    if (geo.isLabelSet() && !geo.hasIndexLabel() && !geo.label.endsWith("'''"))
      return geo.label + "'";
    else
      return null;
  }

  // temp for buildEquation
  private final double[] temp = new double[6];

  private final StringBuffer sbBuildImplicitVarPart = new StringBuffer(80);

  private final StringBuffer sbBuildImplicitEquation = new StringBuffer(80);

  private final StringBuffer sbBuildLHS = new StringBuffer(80);

  private final StringBuffer sbBuildExplicitConicEquation = new StringBuffer(80);

  private final StringBuffer sbBuildExplicitLineEquation = new StringBuffer(50);

  private StringBuffer sbFormatSF;

  private StringBuffer sbFormat;

  private final StringBuffer sbFormatSigned = new StringBuffer(40);

  private final StringBuffer sbFormatAngle = new StringBuffer(40);

  private AnimationManager animationManager;
  String libraryJavaScript = "function ggbOnInit() {}";
  public Kernel() {
    nf = NumberFormat.getInstance(Locale.ENGLISH);
    nf.setGroupingUsed(false);

    sf = new ScientificFormat(5, 16, false);

    setCASPrintForm(ExpressionNode.STRING_TYPE_GEOGEBRA);
  }
  public Kernel(Application app) {
    this();
    this.app = app;

    newConstruction();
    newExpressionNodeEvaluator();
  }
  /**
   * Creates a new macro within the kernel. A macro is a user defined command in
   * GeoGebra.
   */
  public void addMacro(Macro macro) {
    if (macroManager == null)
      macroManager = new MacroManager();
    macroManager.addMacro(macro);
  }
  /**
   * Victor Franco Espino 18-04-2007: New commands
   * 
   * Calculate affine ratio: (A,B,C) = (t(C)-t(A)) : (t(C)-t(B))
   */

  final public GeoNumeric AffineRatio(String label, GeoPoint A, GeoPoint B,
      GeoPoint C) {
    AlgoAffineRatio affine = new AlgoAffineRatio(cons, label, A, B, C);
    GeoNumeric M = affine.getResult();
    return M;

  }

  /**
   * angle of c (angle between first eigenvector and (1,0))
   */
  final public GeoAngle Angle(String label, GeoConic c) {
    AlgoAngleConic algo = new AlgoAngleConic(cons, label, c);
    GeoAngle angle = algo.getAngle();
    return angle;
  }

  /**
   * Angle named label between line g and line h
   */
  final public GeoAngle Angle(String label, GeoLine g, GeoLine h) {
    AlgoAngleLines algo = new AlgoAngleLines(cons, label, g, h);
    GeoAngle angle = algo.getAngle();
    return angle;
  }
  /** Converts number to angle */
  final public GeoAngle Angle(String label, GeoNumeric num) {
    AlgoAngleNumeric algo = new AlgoAngleNumeric(cons, label, num);
    GeoAngle angle = algo.getAngle();
    return angle;
  }

  /**
   * Angle named label between three points
   */
  final public GeoAngle Angle(String label, GeoPoint A, GeoPoint B, GeoPoint C) {
    AlgoAnglePoints algo = new AlgoAnglePoints(cons, label, A, B, C);
    GeoAngle angle = algo.getAngle();
    return angle;
  }

  /**
   * Angle named label for a point or a vector
   */
  final public GeoAngle Angle(String label, GeoVec3D v) {
    AlgoAngleVector algo = new AlgoAngleVector(cons, label, v);
    GeoAngle angle = algo.getAngle();
    return angle;
  }

  /**
   * Angle named label between vector v and vector w
   */
  final public GeoAngle Angle(String label, GeoVector v, GeoVector w) {
    AlgoAngleVectors algo = new AlgoAngleVectors(cons, label, v, w);
    GeoAngle angle = algo.getAngle();
    return angle;
  }

  /**
   * Creates a new point C by rotating B around A using angle alpha and a new
   * angle BAC. The labels[0] is for the angle, labels[1] for the new point
   */
  final public GeoElement[] Angle(String[] labels, GeoPoint B, GeoPoint A,
      NumberValue alpha) {
    return Angle(labels, B, A, alpha, true);
  }

  /**
   * Creates a new point C by rotating B around A using angle alpha and a new
   * angle BAC (for positive orientation) resp. angle CAB (for negative
   * orientation). The labels[0] is for the angle, labels[1] for the new point
   */
  final public GeoElement[] Angle(String[] labels, GeoPoint B, GeoPoint A,
      NumberValue alpha, boolean posOrientation) {
    // this is actually a macro
    String pointLabel = null, angleLabel = null;
    if (labels != null)
      switch (labels.length) {
        case 2 :
          pointLabel = labels[1];

        case 1 :
          angleLabel = labels[0];

        default :
      }

    // rotate B around A using angle alpha
    GeoPoint C = (GeoPoint) Rotate(pointLabel, B, alpha, A)[0];

    // create angle according to orientation
    GeoAngle angle;
    if (posOrientation)
      angle = Angle(angleLabel, B, A, C);
    else
      angle = Angle(angleLabel, C, A, B);

    // return angle and new point
    GeoElement[] ret = {angle, C};
    return ret;
  }

  /**
   * all angles of given polygon
   */
  final public GeoAngle[] Angles(String[] labels, GeoPolygon poly) {
    AlgoAnglePolygon algo = new AlgoAnglePolygon(cons, labels, poly);
    GeoAngle[] angles = algo.getAngles();
    // for (int i=0; i < angles.length; i++) {
    // angles[i].setAlphaValue(0.0f);
    // }
    return angles;
  }

  /**
   * Angular bisector of points A, B, C
   */
  final public GeoLine AngularBisector(String label, GeoPoint A, GeoPoint B,
      GeoPoint C) {
    AlgoAngularBisectorPoints algo = new AlgoAngularBisectorPoints(cons, label,
        A, B, C);
    GeoLine g = algo.getLine();
    return g;
  }

  /**
   * Angular bisectors of lines g, h
   */
  final public GeoLine[] AngularBisector(String[] labels, GeoLine g, GeoLine h) {
    AlgoAngularBisectorLines algo = new AlgoAngularBisectorLines(cons, labels,
        g, h);
    GeoLine[] lines = algo.getLines();
    return lines;
  }

  /**
   * Append[object,list] Michael Borcherds
   */
  final public GeoList Append(String label, GeoElement geo, GeoList list) {
    AlgoAppend algo = new AlgoAppend(cons, label, geo, list);
    GeoList list2 = algo.getResult();
    return list2;
  }

  /**
   * Append[list,object] Michael Borcherds
   */
  final public GeoList Append(String label, GeoList list, GeoElement geo) {
    AlgoAppend algo = new AlgoAppend(cons, label, list, geo);
    GeoList list2 = algo.getResult();
    return list2;
  }

  /**
   * Area named label of conic
   */
  final public GeoNumeric Area(String label, GeoConic c) {
    AlgoAreaConic algo = new AlgoAreaConic(cons, label, c);
    GeoNumeric num = algo.getArea();
    return num;
  }

  /**
   * Area named label of P[0], ..., P[n]
   */
  final public GeoNumeric Area(String label, GeoPoint[] P) {
    AlgoAreaPoints algo = new AlgoAreaPoints(cons, label, P);
    GeoNumeric num = algo.getArea();
    return num;
  }

  /**
   * asymptotes to c
   */
  final public GeoLine[] Asymptote(String[] labels, GeoConic c) {
    AlgoAsymptote algo = new AlgoAsymptote(cons, labels, c);
    GeoLine[] asymptotes = algo.getAsymptotes();
    return asymptotes;
  }

  public void attach(View view) {
    // Application.debug("ATTACH " + view + ", notifyActive: " +
    // notifyViewsActive);
    if (!notifyViewsActive)
      viewCnt = oldViewCnt;

    // view already attached?
    boolean viewFound = false;
    for (int i = 0; i < viewCnt; i++)
      if (views[i] == view) {
        viewFound = true;
        break;
      }

    if (!viewFound)
      // new view
      views[viewCnt++] = view;

    /*
     * System.out.print("  current views: "); for (int i = 0; i < viewCnt; i++)
     * { System.out.print(views[i] + ", "); } Application.debug();
     */

    if (!notifyViewsActive) {
      oldViewCnt = viewCnt;
      viewCnt = 0;
    }
  }

  /**
   * axes of c
   */
  final public GeoLine[] Axes(String[] labels, GeoConic c) {
    AlgoAxes algo = new AlgoAxes(cons, labels, c);
    GeoLine[] axes = algo.getAxes();
    return axes;
  }

  /**
   * returns the current x-axis step Michael Borcherds
   */
  final public GeoNumeric AxisStepX(String label) {
    AlgoAxisStepX algo = new AlgoAxisStepX(cons, label);
    GeoNumeric t = algo.getResult();
    return t;
  }

  /**
   * returns the current y-axis step Michael Borcherds
   */
  final public GeoNumeric AxisStepY(String label) {
    AlgoAxisStepY algo = new AlgoAxisStepY(cons, label);
    GeoNumeric t = algo.getResult();
    return t;
  }
  /**
   * BarChart
   */
  final public GeoNumeric BarChart(String label, GeoList list1, GeoList list2) {
    AlgoBarChart algo = new AlgoBarChart(cons, label, list1, list2);
    GeoNumeric sum = algo.getSum();
    return sum;
  }

  /*
   * GeoElement specific
   */

  /**
   * BarChart
   */
  final public GeoNumeric BarChart(String label, GeoList list1, GeoList list2,
      NumberValue width) {
    AlgoBarChart algo = new AlgoBarChart(cons, label, list1, list2, width);
    GeoNumeric sum = algo.getSum();
    return sum;
  }

  /**
   * BarChart
   */
  final public GeoNumeric BarChart(String label, GeoList list, GeoNumeric a) {
    AlgoBarChart algo = new AlgoBarChart(cons, label, list, a);
    GeoNumeric sum = algo.getSum();
    return sum;
  }

  /* *******************************************
   * Construction specific methods *******************************************
   */

  /**
   * BarChart
   */
  final public GeoNumeric BarChart(String label, NumberValue a, NumberValue b,
      GeoElement expression, GeoNumeric localVar, NumberValue from,
      NumberValue to, NumberValue step) {

    AlgoSequence seq = new AlgoSequence(cons, expression, localVar, from, to,
        step);
    cons.removeFromConstructionList(seq);

    AlgoBarChart algo = new AlgoBarChart(cons, label, a, b, (GeoList) seq
        .getOutput()[0]);
    GeoNumeric sum = algo.getSum();
    return sum;
  }

  /**
   * BarChart
   */
  final public GeoNumeric BarChart(String label, NumberValue a, NumberValue b,
      GeoList list) {
    AlgoBarChart algo = new AlgoBarChart(cons, label, a, b, list);
    GeoNumeric sum = algo.getSum();
    return sum;
  }

  /**
   * Binomial[n,r] Michael Borcherds
   */
  final public GeoNumeric Binomial(String label, NumberValue a, NumberValue b) {
    AlgoBinomial algo = new AlgoBinomial(cons, label, a, b);
    GeoNumeric num = algo.getResult();
    return num;
  }

  final public GeoBoolean Boolean(String label, boolean value) {
    GeoBoolean b = new GeoBoolean(cons);
    b.setValue(value);
    b.setLabel(label);
    return b;
  }

  /**
   * BoxPlot
   */
  final public GeoNumeric BoxPlot(String label, NumberValue a, NumberValue b,
      GeoList rawData) {

    /*
     * AlgoListMin min = new AlgoListMin(cons,rawData);
     * cons.removeFromConstructionList(min); AlgoQ1 Q1 = new
     * AlgoQ1(cons,rawData); cons.removeFromConstructionList(Q1); AlgoMedian
     * median = new AlgoMedian(cons,rawData);
     * cons.removeFromConstructionList(median); AlgoQ3 Q3 = new
     * AlgoQ3(cons,rawData); cons.removeFromConstructionList(Q3); AlgoListMax
     * max = new AlgoListMax(cons,rawData);
     * cons.removeFromConstructionList(max);
     * 
     * AlgoBoxPlot algo = new AlgoBoxPlot(cons, label, a, b,
     * (NumberValue)(min.getMin()), (NumberValue)(Q1.getQ1()),
     * (NumberValue)(median.getMedian()), (NumberValue)(Q3.getQ3()),
     * (NumberValue)(max.getMax()));
     */

    AlgoBoxPlot algo = new AlgoBoxPlot(cons, label, a, b, rawData);

    GeoNumeric sum = algo.getSum();
    return sum;
  }

  /**
   * BoxPlot
   */
  final public GeoNumeric BoxPlot(String label, NumberValue a, NumberValue b,
      NumberValue min, NumberValue Q1, NumberValue median, NumberValue Q3,
      NumberValue max) {
    AlgoBoxPlot algo = new AlgoBoxPlot(cons, label, a, b, min, Q1, median, Q3,
        max);
    GeoNumeric sum = algo.getSum();
    return sum;
  }

  // form: y� = f(x) (coeff of y = 0)
  final StringBuffer buildExplicitConicEquation(double[] numbers,
      String[] vars, int pos, boolean KEEP_LEADING_SIGN) {
    // y�-coeff is 0
    double d, dabs, q = numbers[pos];
    // coeff of y� is 0 or coeff of y is not 0
    if (isZero(q))
      return buildImplicitEquation(numbers, vars, KEEP_LEADING_SIGN);

    int i, leadingNonZero = numbers.length;
    for (i = 0; i < numbers.length; i++)
      if (i != pos && // except y� coefficient
          (Math.abs(numbers[i]) >= PRINT_PRECISION || useSignificantFigures)) {
        leadingNonZero = i;
        break;
      }

    // BUILD EQUATION STRING
    sbBuildExplicitConicEquation.setLength(0);
    sbBuildExplicitConicEquation.append(vars[pos]);
    sbBuildExplicitConicEquation.append(" = ");

    if (leadingNonZero == numbers.length) {
      sbBuildExplicitConicEquation.append("0");
      return sbBuildExplicitConicEquation;
    } else if (leadingNonZero == numbers.length - 1) {
      // only constant coeff
      d = -numbers[leadingNonZero] / q;
      sbBuildExplicitConicEquation.append(format(d));
      return sbBuildExplicitConicEquation;
    } else {
      // leading coeff
      d = -numbers[leadingNonZero] / q;
      sbBuildExplicitConicEquation.append(formatCoeff(d));
      sbBuildExplicitConicEquation.append(vars[leadingNonZero]);

      // other coeffs
      for (i = leadingNonZero + 1; i < vars.length; i++)
        if (i != pos) {
          d = -numbers[i] / q;
          dabs = Math.abs(d);
          if (dabs >= PRINT_PRECISION || useSignificantFigures) {
            sbBuildExplicitConicEquation.append(' ');
            sbBuildExplicitConicEquation.append(sign(d));
            sbBuildExplicitConicEquation.append(' ');
            sbBuildExplicitConicEquation.append(formatCoeff(dabs));
            sbBuildExplicitConicEquation.append(vars[i]);
          }
        }

      // constant coeff
      d = -numbers[i] / q;
      dabs = Math.abs(d);
      if (dabs >= PRINT_PRECISION || useSignificantFigures) {
        sbBuildExplicitConicEquation.append(' ');
        sbBuildExplicitConicEquation.append(sign(d));
        sbBuildExplicitConicEquation.append(' ');
        sbBuildExplicitConicEquation.append(format(dabs));
      }

      // Application.debug(sbBuildExplicitConicEquation.toString());

      return sbBuildExplicitConicEquation;
    }
  }

  // y = k x + d
  final StringBuffer buildExplicitLineEquation(double[] numbers, String[] vars) {

    double d, dabs, q = numbers[1];
    sbBuildExplicitLineEquation.setLength(0);

    // BUILD EQUATION STRING
    // special case
    // y-coeff is 0: form x = constant
    if (isZero(q)) {
      sbBuildExplicitLineEquation.append("x");

      if (casPrintForm == ExpressionNode.STRING_TYPE_MATH_PIPER)
        sbBuildExplicitLineEquation.append(" == ");
      else
        sbBuildExplicitLineEquation.append(" = ");

      sbBuildExplicitLineEquation.append(format(-numbers[2] / numbers[0]));
      return sbBuildExplicitLineEquation;
    }

    // standard case: y-coeff not 0
    sbBuildExplicitLineEquation.append("y");
    if (casPrintForm == ExpressionNode.STRING_TYPE_MATH_PIPER)
      sbBuildExplicitLineEquation.append(" == ");
    else
      sbBuildExplicitLineEquation.append(" = ");

    // x coeff
    d = -numbers[0] / q;
    dabs = Math.abs(d);
    if (dabs >= PRINT_PRECISION || useSignificantFigures) {
      sbBuildExplicitLineEquation.append(formatCoeff(d));

      if (casPrintForm == ExpressionNode.STRING_TYPE_MATH_PIPER)
        sbBuildExplicitLineEquation.append('*');

      sbBuildExplicitLineEquation.append('x');

      // constant
      d = -numbers[2] / q;
      dabs = Math.abs(d);
      if (dabs >= PRINT_PRECISION || useSignificantFigures) {
        sbBuildExplicitLineEquation.append(' ');
        sbBuildExplicitLineEquation.append(sign(d));
        sbBuildExplicitLineEquation.append(' ');
        sbBuildExplicitLineEquation.append(format(dabs));
      }
    } else
      // only constant
      sbBuildExplicitLineEquation.append(format(-numbers[2] / q));
    return sbBuildExplicitLineEquation;
  }

  final StringBuffer buildImplicitEquation(double[] numbers, String[] vars,
      boolean KEEP_LEADING_SIGN) {

    sbBuildImplicitEquation.setLength(0);
    sbBuildImplicitEquation.append(buildImplicitVarPart(numbers, vars,
        KEEP_LEADING_SIGN));
    if (casPrintForm == ExpressionNode.STRING_TYPE_MATH_PIPER)
      sbBuildImplicitEquation.append(" == ");
    else
      sbBuildImplicitEquation.append(" = ");

    // temp is set by buildImplicitVarPart
    sbBuildImplicitEquation.append(format(-temp[vars.length]));

    return sbBuildImplicitEquation;
  }

  // lhs of implicit equation without constant coeff
  final private StringBuffer buildImplicitVarPart(double[] numbers,
      String[] vars, boolean KEEP_LEADING_SIGN) {
    int leadingNonZero = -1;
    sbBuildImplicitVarPart.setLength(0);

    for (int i = 0; i < vars.length; i++)
      if (!isZero(numbers[i])) {
        leadingNonZero = i;
        break;
      }

    // check if integers and divide through gcd
    boolean allIntegers = true;
    for (double number : numbers)
      allIntegers = allIntegers && isInteger(number);
    if (allIntegers)
      // divide by greates common divisor
      divide(numbers, gcd(numbers), numbers);

    // no left hand side
    if (leadingNonZero == -1) {
      sbBuildImplicitVarPart.append("0");
      return sbBuildImplicitVarPart;
    }

    // don't change leading coefficient
    if (KEEP_LEADING_SIGN)
      copy(numbers, temp);
    else if (numbers[leadingNonZero] < 0)
      negative(numbers, temp);
    else
      copy(numbers, temp);

    // BUILD EQUATION STRING
    // valid left hand side
    // leading coefficient
    sbBuildImplicitVarPart.append(formatCoeff(temp[leadingNonZero]));
    if (casPrintForm == ExpressionNode.STRING_TYPE_MATH_PIPER)
      sbBuildImplicitVarPart.append("*");
    sbBuildImplicitVarPart.append(vars[leadingNonZero]);

    // other coefficients on lhs
    String sign;
    double abs;
    for (int i = leadingNonZero + 1; i < vars.length; i++) {
      if (temp[i] < 0.0) {
        sign = " - ";
        abs = -temp[i];
      } else {
        sign = " + ";
        abs = temp[i];
      }

      if (abs >= PRINT_PRECISION || useSignificantFigures) {
        sbBuildImplicitVarPart.append(sign);
        sbBuildImplicitVarPart.append(formatCoeff(abs));
        if (casPrintForm == ExpressionNode.STRING_TYPE_MATH_PIPER)
          sbBuildImplicitVarPart.append("*");
        sbBuildImplicitVarPart.append(vars[i]);
      }
    }
    return sbBuildImplicitVarPart;
  }

  // lhs of lhs = 0
  final public StringBuffer buildLHS(double[] numbers, String[] vars,
      boolean KEEP_LEADING_SIGN) {
    sbBuildLHS.setLength(0);
    sbBuildLHS.append(buildImplicitVarPart(numbers, vars, KEEP_LEADING_SIGN));

    // add constant coeff
    double coeff = temp[vars.length];
    if (Math.abs(coeff) >= PRINT_PRECISION || useSignificantFigures) {
      sbBuildLHS.append(' ');
      sbBuildLHS.append(sign(coeff));
      sbBuildLHS.append(' ');
      sbBuildLHS.append(format(Math.abs(coeff)));
    }
    return sbBuildLHS;
  }

  final public GeoNumeric Cauchy(String label, NumberValue a, NumberValue b,
      NumberValue c) {
    AlgoCauchy algo = new AlgoCauchy(cons, label, a, b, c);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * Creates a list object for a range of cells in the spreadsheet. e.g. A1:B2
   */
  final public GeoList CellRange(String label, GeoElement startCell,
      GeoElement endCell) {
    AlgoCellRange algo = new AlgoCellRange(cons, label, startCell, endCell);
    return algo.getList();
  }

  /**
   * Center of conic
   */
  final public GeoPoint Center(String label, GeoConic c) {
    AlgoCenterConic algo = new AlgoCenterConic(cons, label, c);
    GeoPoint midpoint = algo.getPoint();
    return midpoint;
  }

  /**
   * Centroid of a
   */
  final public GeoPoint Centroid(String label, GeoPolygon p) {
    AlgoCentroidPolygon algo = new AlgoCentroidPolygon(cons, label, p);
    GeoPoint centroid = algo.getPoint();
    return centroid;
  }

  /**
   * Checks if x is close (Kernel.MIN_PRECISION) to a decimal fraction, eg
   * 2.800000000000001. If it is, the decimal fraction eg 2.8 is returned,
   * otherwise x is returned.
   */
  final public double checkDecimalFraction(double x) {
    double fracVal = x * INV_MIN_PRECISION;
    double roundVal = Math.round(fracVal);
    if (isEqual(fracVal, roundVal))
      return roundVal / INV_MIN_PRECISION;
    else
      return x;
  }

  /* ******************************
   * redo / undo for current construction *****************************
   */

  /**
   * Checks if x is very close (1E-8) to an integer. If it is, the integer value
   * is returned, otherwise x is returnd.
   */
  final public double checkInteger(double x) {
    double roundVal = Math.round(x);
    if (Math.abs(x - roundVal) < EPSILON)
      return roundVal;
    else
      return x;
  }

  final public GeoNumeric ChiSquared(String label, NumberValue a, NumberValue b) {
    AlgoChiSquared algo = new AlgoChiSquared(cons, label, a, b);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * if x is nearly zero, 0.0 is returned, else x is returned
   */
  final public double chop(double x) {
    if (isZero(x))
      return 0.0d;
    else
      return x;
  }

  /**
   * circle with midpoint M through point P
   */
  final public GeoConic Circle(String label, GeoPoint M, GeoPoint P) {
    AlgoCircleTwoPoints algo = new AlgoCircleTwoPoints(cons, label, M, P);
    GeoConic circle = algo.getCircle();
    circle.setToSpecific();
    circle.update();
    notifyUpdate(circle);
    return circle;
  }

  /**
   * circle with through points A, B, C
   */
  final public GeoConic Circle(String label, GeoPoint A, GeoPoint B, GeoPoint C) {
    AlgoCircleThreePoints algo = new AlgoCircleThreePoints(cons, label, A, B, C);
    GeoConic circle = algo.getCircle();
    circle.setToSpecific();
    circle.update();
    notifyUpdate(circle);
    return circle;
  }

  /**
   * circle with midpoint M and radius BC Michael Borcherds 2008-03-14
   */
  final public GeoConic Circle(
  // this is actually a macro
      String label, GeoPoint A, GeoPoint B, GeoPoint C, boolean dummy) {

    AlgoJoinPointsSegment algoSegment = new AlgoJoinPointsSegment(cons, B, C,
        null);
    cons.removeFromConstructionList(algoSegment);

    AlgoCirclePointRadius algo = new AlgoCirclePointRadius(cons, label, A,
        algoSegment.getSegment(), true);
    GeoConic circle = algo.getCircle();
    circle.setToSpecific();
    circle.update();
    notifyUpdate(circle);
    return circle;
  }

  /**
   * circle with midpoint M and radius segment Michael Borcherds 2008-03-15
   */
  final public GeoConic Circle(String label, GeoPoint A, GeoSegment segment) {

    AlgoCirclePointRadius algo = new AlgoCirclePointRadius(cons, label, A,
        segment, true);
    GeoConic circle = algo.getCircle();
    circle.setToSpecific();
    circle.update();
    notifyUpdate(circle);
    return circle;
  }

  /**
   * circle with midpoint M and radius r
   */
  final public GeoConic Circle(String label, GeoPoint M, NumberValue r) {
    AlgoCirclePointRadius algo = new AlgoCirclePointRadius(cons, label, M, r);
    GeoConic circle = algo.getCircle();
    circle.setToSpecific();
    circle.update();
    notifyUpdate(circle);
    return circle;
  }

  /**
   * circle arc from center and twho points on arc
   */
  final public GeoConicPart CircleArc(String label, GeoPoint A, GeoPoint B,
      GeoPoint C) {
    AlgoConicPartCircle algo = new AlgoConicPartCircle(cons, label, A, B, C,
        GeoConicPart.CONIC_PART_ARC);
    return algo.getConicPart();
  }

  /**
   * circle sector from center and twho points on arc
   */
  final public GeoConicPart CircleSector(String label, GeoPoint A, GeoPoint B,
      GeoPoint C) {
    AlgoConicPartCircle algo = new AlgoConicPartCircle(cons, label, A, B, C,
        GeoConicPart.CONIC_PART_SECTOR);
    return algo.getConicPart();
  }

  /* *******************************************************
   * methods for view-Pattern (Model-View-Controller)
   * ******************************************************
   */

  /**
   * circle arc from three points
   */
  final public GeoConicPart CircumcircleArc(String label, GeoPoint A,
      GeoPoint B, GeoPoint C) {
    AlgoConicPartCircumcircle algo = new AlgoConicPartCircumcircle(cons, label,
        A, B, C, GeoConicPart.CONIC_PART_ARC);
    return algo.getConicPart();
  }

  /**
   * circle sector from three points
   */
  final public GeoConicPart CircumcircleSector(String label, GeoPoint A,
      GeoPoint B, GeoPoint C) {
    AlgoConicPartCircumcircle algo = new AlgoConicPartCircumcircle(cons, label,
        A, B, C, GeoConicPart.CONIC_PART_SECTOR);
    return algo.getConicPart();
  }

  /**
   * Circumference named label of GeoConic
   */
  final public GeoNumeric Circumference(String label, GeoConic conic) {
    AlgoCircumferenceConic algo = new AlgoCircumferenceConic(cons, label, conic);
    return algo.getCircumference();
  }

  public void clearConstruction() {
    if (macroManager != null)
      macroManager.setAllMacrosUnused();

    // clear animations
    if (animationManager != null) {
      animationManager.stopAnimation();
      animationManager.clearAnimatedGeos();
    }

    cons.clearConstruction();
    notifyClearView();
    notifyRepaint();
    System.gc();
  }

  /*
   * final public void notifyRemoveAll(View view) { Collection geos =
   * cons.getAllGeoElements(); Iterator it = geos.iterator(); while
   * (it.hasNext()) { view.remove((GeoElement) it.next()); } }
   */

  /**
   * Column of geo.
   */
  final public GeoNumeric Column(String label, GeoElement geo) {
    AlgoColumn algo = new AlgoColumn(cons, label, geo);
    GeoNumeric ret = algo.getResult();
    return ret;
  }

  /**
   * ColumnName[]
   */
  final public GeoText ColumnName(String label, GeoElement geo) {
    AlgoColumnName algo = new AlgoColumnName(cons, label, geo);
    GeoText t = algo.getGeoText();
    return t;
  }

  /** Conic label with equation ax� + bxy + cy� + dx + ey + f = 0 */
  final public GeoConic Conic(String label, double a, double b, double c,
      double d, double e, double f) {
    double[] coeffs = {a, b, c, d, e, f};
    GeoConic conic = new GeoConic(cons, label, coeffs);
    return conic;
  }

  /**
   * conic through five points
   */
  final public GeoConic Conic(String label, GeoPoint[] points) {
    AlgoConicFivePoints algo = new AlgoConicFivePoints(cons, label, points);
    GeoConic conic = algo.getConic();
    return conic;
  }

  /**
   * conic sector from conic and points
   */
  final public GeoConicPart ConicArc(String label, GeoConic conic, GeoPoint P,
      GeoPoint Q) {
    AlgoConicPartConicPoints algo = new AlgoConicPartConicPoints(cons, label,
        conic, P, Q, GeoConicPart.CONIC_PART_ARC);
    return algo.getConicPart();
  }

  /**
   * conic arc from conic and parameters
   */
  final public GeoConicPart ConicArc(String label, GeoConic conic,
      NumberValue a, NumberValue b) {
    AlgoConicPartConicParameters algo = new AlgoConicPartConicParameters(cons,
        label, conic, a, b, GeoConicPart.CONIC_PART_ARC);
    return algo.getConicPart();
  }
  /**
   * conic sector from conic and points
   */
  final public GeoConicPart ConicSector(String label, GeoConic conic,
      GeoPoint P, GeoPoint Q) {
    AlgoConicPartConicPoints algo = new AlgoConicPartConicPoints(cons, label,
        conic, P, Q, GeoConicPart.CONIC_PART_SECTOR);
    return algo.getConicPart();
  }

  /**
   * conic sector from conic and parameters
   */
  final public GeoConicPart ConicSector(String label, GeoConic conic,
      NumberValue a, NumberValue b) {
    AlgoConicPartConicParameters algo = new AlgoConicPartConicParameters(cons,
        label, conic, a, b, GeoConicPart.CONIC_PART_SECTOR);
    return algo.getConicPart();
  }

  /**
   * returns the current construction protocol step Michael Borcherds 2008-05-15
   */
  final public GeoNumeric ConstructionStep(String label) {
    AlgoConstructionStep algo = new AlgoConstructionStep(cons, label);
    GeoNumeric t = algo.getResult();
    return t;
  }

  /**
   * returns current construction protocol step for an object Michael Borcherds
   * 2008-05-15
   */
  final public GeoNumeric ConstructionStep(String label, GeoElement geo) {
    AlgoStepObject algo = new AlgoStepObject(cons, label, geo);
    GeoNumeric t = algo.getResult();
    return t;
  }

  /**
   * Converts a NumberValue object to an ExpressionNode object.
   */
  public ExpressionNode convertNumberValueToExpressionNode(NumberValue nv) {
    GeoElement geo = nv.toGeoElement();
    AlgoElement algo = geo.getParentAlgorithm();

    if (algo != null && algo instanceof AlgoDependentNumber) {
      AlgoDependentNumber algoDep = (AlgoDependentNumber) algo;
      return algoDep.getExpression().getCopy(this);
    } else
      return new ExpressionNode(this, geo);
  }

  final public double convertToAngleValue(double val) {
    double value = val % PI_2;
    if (isZero(value)) {
      if (val < 1.0)
        value = 0.0;
      else
        value = PI_2;
    } else if (value < 0.0)
      value += PI_2;
    return value;
  }

  /**
   * Corner of image
   */
  final public GeoPoint Corner(String label, GeoImage img, NumberValue number) {
    AlgoImageCorner algo = new AlgoImageCorner(cons, label, img, number);
    return algo.getCorner();
  }

  /**
   * Corner of text Michael Borcherds 2007-11-26
   */
  final public GeoPoint Corner(String label, GeoText txt, NumberValue number) {
    AlgoTextCorner algo = new AlgoTextCorner(cons, label, txt, number);
    return algo.getCorner();
  }

  /**
   * Corner of Drawing Pad Michael Borcherds 2008-05-10
   */
  final public GeoPoint CornerOfDrawingPad(String label, NumberValue number) {
    AlgoDrawingPadCorner algo = new AlgoDrawingPadCorner(cons, label, number);
    return algo.getCorner();
  }

  /* **********************************
   * MACRO handling *********************************
   */

  /**
   * If-then-else construct for functions. example: If[ x < 2, x^2, x + 2 ]
   */
  final public GeoNumeric CountIf(String label, GeoFunction boolFun,
      GeoList list) {

    AlgoCountIf algo = new AlgoCountIf(cons, label, boolFun, list);
    return algo.getResult();
  }

  /**
   * Covariance[list] Michael Borcherds
   */
  final public GeoNumeric Covariance(String label, GeoList list) {
    AlgoListCovariance algo = new AlgoListCovariance(cons, label, list);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * Covariance[list,list] Michael Borcherds
   */
  final public GeoNumeric Covariance(String label, GeoList listX, GeoList listY) {
    AlgoDoubleListCovariance algo = new AlgoDoubleListCovariance(cons, label,
        listX, listY);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * Creates a new GeoElement object for the given type string.
   * 
   * @param type
   *          : String as produced by GeoElement.getXMLtypeString()
   */
  public GeoElement createGeoElement(Construction cons, String type)
      throws MyError {
    // the type strings are the classnames in lowercase without the beginning
    // "geo"
    // due to a bug in GeoGebra 2.6c the type strings for conics
    // in XML may be "ellipse", "hyperbola", ...

    switch (type.charAt(0)) {
      case 'a' : // angle
        return new GeoAngle(cons);

      case 'b' : // angle
        return new GeoBoolean(cons);

      case 'c' : // conic
        if (type.equals("conic"))
          return new GeoConic(cons);
        else if (type.equals("conicpart"))
          return new GeoConicPart(cons, 0);
        else if (type.equals("circle"))
          return new GeoConic(cons);

      case 'd' : // doubleLine // bug in GeoGebra 2.6c
        return new GeoConic(cons);

      case 'e' : // ellipse, emptyset // bug in GeoGebra 2.6c
        return new GeoConic(cons);

      case 'f' : // function
        return new GeoFunction(cons);

      case 'h' : // hyperbola // bug in GeoGebra 2.6c
        return new GeoConic(cons);

      case 'i' : // image
        if (type.equals("image"))
          return new GeoImage(cons);
        else if (type.equals("intersectinglines")) // bug in GeoGebra 2.6c
          return new GeoConic(cons);

      case 'j' : // javascriptbutton
        return new GeoJavaScriptButton(cons);

      case 'l' : // line, list, locus
        if (type.equals("line"))
          return new GeoLine(cons);
        else if (type.equals("list"))
          return new GeoList(cons);
        else
          return new GeoLocus(cons);

      case 'n' : // numeric
        return new GeoNumeric(cons);

      case 'p' : // point, polygon
        if (type.equals("point"))
          return new GeoPoint(cons);
        else if (type.equals("polygon"))
          return new GeoPolygon(cons, null);
        else
          // parabola, parallelLines, point // bug in GeoGebra 2.6c
          return new GeoConic(cons);

      case 'r' : // ray
        return new GeoRay(cons, null);

      case 's' : // segment
        return new GeoSegment(cons, null, null);

      case 't' : // text
        return new GeoText(cons);

      case 'v' : // vector
        return new GeoVector(cons);

      default :
        throw new MyError(cons.getApplication(), "Kernel: GeoElement of type "
            + type + " could not be created.");
    }
  }

  /**
   * Calculate cross ratio: (A,B,C,D) = affineRatio(A, B, C) / affineRatio(A, B,
   * D)
   */

  final public GeoNumeric CrossRatio(String label, GeoPoint A, GeoPoint B,
      GeoPoint C, GeoPoint D) {

    AlgoCrossRatio cross = new AlgoCrossRatio(cons, label, A, B, C, D);
    GeoNumeric M = cross.getResult();
    return M;

  }

  /**
   * Calculate Curvature for function: k(x) = f''/T^3, T = sqrt(1+(f')^2)
   */

  final public GeoNumeric Curvature(String label, GeoPoint A, GeoFunction f) {

    AlgoCurvature algo = new AlgoCurvature(cons, label, A, f);
    GeoNumeric k = algo.getResult();
    return k;

  }

  /**
   * Calculate Curvature for Curve: k(t) = (a'(t)b''(t)-a''(t)b'(t))/T^3, T =
   * sqrt(a'(t)^2+b'(t)^2)
   */

  final public GeoNumeric CurvatureCurve(String label, GeoPoint A,
      GeoCurveCartesian f) {

    AlgoCurvatureCurve algo = new AlgoCurvatureCurve(cons, label, A, f);
    GeoNumeric k = algo.getResult();
    return k;

  }

  /**
   * Calculate Curvature Vector for function: c(x) = (1/T^4)*(-f'*f'',f''), T =
   * sqrt(1+(f')^2)
   */

  final public GeoVector CurvatureVector(String label, GeoPoint A, GeoFunction f) {

    AlgoCurvatureVector algo = new AlgoCurvatureVector(cons, label, A, f);
    GeoVector v = algo.getVector();
    return v;

  }

  /**
   * 
   * Calculate Curvature Vector for curve: c(t) =
   * ((a'(t)b''(t)-a''(t)b'(t))/T^4) * (-b'(t),a'(t)) T = sqrt(a'(t)^2+b'(t)^2)
   */

  final public GeoVector CurvatureVectorCurve(String label, GeoPoint A,
      GeoCurveCartesian f) {

    AlgoCurvatureVectorCurve algo = new AlgoCurvatureVectorCurve(cons, label,
        A, f);
    GeoVector v = algo.getVector();
    return v;

  }

  /**
   * Cartesian curve command: Curve[ <expression x-coord>, <expression x-coord>,
   * <number-var>, <from>, <to> ]
   */
  final public GeoCurveCartesian CurveCartesian(String label,
      NumberValue xcoord, NumberValue ycoord, GeoNumeric localVar,
      NumberValue from, NumberValue to) {
    AlgoCurveCartesian algo = new AlgoCurveCartesian(cons, label, xcoord,
        ycoord, localVar, from, to);
    return algo.getCurve();
  }

  /**
   * 
   * Calculate Curve Length between the parameters t0 and t1: integral from t0
   * to t1 on T = sqrt(a'(t)^2+b'(t)^2)
   */

  final public GeoNumeric CurveLength(String label, GeoCurveCartesian c,
      GeoNumeric t0, GeoNumeric t1) {

    AlgoLengthCurve algo = new AlgoLengthCurve(cons, label, c, t0, t1);
    GeoNumeric length = algo.getLength();
    return length;

  }

  /**
   * Calculate Curve Length between the points A and B: integral from t0 to t1
   * on T = sqrt(a'(t)^2+b'(t)^2)
   */
  final public GeoNumeric CurveLength2Points(String label, GeoCurveCartesian c,
      GeoPoint A, GeoPoint B) {
    AlgoLengthCurve2Points algo = new AlgoLengthCurve2Points(cons, label, c, A,
        B);
    GeoNumeric length = algo.getLength();
    return length;
  }

  /***********************************
   * FACTORY METHODS FOR GeoElements
   ***********************************/

  /**
   * Defined[object] Michael Borcherds
   */
  final public GeoBoolean Defined(String label, GeoElement geo) {
    AlgoDefined algo = new AlgoDefined(cons, label, geo);
    GeoBoolean result = algo.getResult();
    return result;
  }

  /**
   * Text dependent on coefficients of arithmetic expressions with variables,
   * represented by trees. e.g. c = a & b
   */
  final public GeoBoolean DependentBoolean(String label, ExpressionNode root) {
    AlgoDependentBoolean algo = new AlgoDependentBoolean(cons, label, root);
    return algo.getGeoBoolean();
  }

  /**
   * Conic dependent on coefficients of arithmetic expressions with variables,
   * represented by trees. e.g. y� = 2 p x
   */
  final public GeoConic DependentConic(String label, Equation equ) {
    AlgoDependentConic algo = new AlgoDependentConic(cons, label, equ);
    GeoConic conic = algo.getConic();
    return conic;
  }

  /**
   * Function dependent on coefficients of arithmetic expressions with
   * variables, represented by trees. e.g. f(x) = a x� + b x�
   */
  final public GeoFunction DependentFunction(String label, Function fun) {
    AlgoDependentFunction algo = new AlgoDependentFunction(cons, label, fun);
    GeoFunction f = algo.getFunction();
    return f;
  }

  /**
   * Creates a dependent copy of origGeo with label
   */
  final public GeoElement DependentGeoCopy(String label,
      ExpressionNode origGeoNode) {
    AlgoDependentGeoCopy algo = new AlgoDependentGeoCopy(cons, label,
        origGeoNode);
    return algo.getGeo();
  }

  /**
   * Line dependent on coefficients of arithmetic expressions with variables,
   * represented by trees. e.g. y = k x + d
   */
  final public GeoLine DependentLine(String label, Equation equ) {
    AlgoDependentLine algo = new AlgoDependentLine(cons, label, equ);
    GeoLine line = algo.getLine();
    return line;
  }

  /**
   * Number dependent on arithmetic expression with variables, represented by a
   * tree. e.g. t = 6z - 2
   */
  final public GeoNumeric DependentNumber(String label, ExpressionNode root,
      boolean isAngle) {
    AlgoDependentNumber algo = new AlgoDependentNumber(cons, label, root,
        isAngle);
    GeoNumeric number = algo.getNumber();
    return number;
  }

  /**
   * Point dependent on arithmetic expression with variables, represented by a
   * tree. e.g. P = (4t, 2s)
   */
  final public GeoPoint DependentPoint(String label, ExpressionNode root,
      boolean complex) {
    AlgoDependentPoint algo = new AlgoDependentPoint(cons, label, root, complex);
    GeoPoint P = algo.getPoint();
    return P;
  }

  /**
   * Text dependent on coefficients of arithmetic expressions with variables,
   * represented by trees. e.g. text = "Radius: " + r
   */
  final public GeoText DependentText(String label, ExpressionNode root) {
    AlgoDependentText algo = new AlgoDependentText(cons, label, root);
    GeoText t = algo.getGeoText();
    return t;
  }

  /**
   * Vector dependent on arithmetic expression with variables, represented by a
   * tree. e.g. v = u + 3 w
   */
  final public GeoVector DependentVector(String label, ExpressionNode root) {
    AlgoDependentVector algo = new AlgoDependentVector(cons, label, root);
    GeoVector v = algo.getVector();
    return v;
  }

  /**
   * first derivative of deriveable f
   */
  final public GeoElement Derivative(String label, GeoDeriveable f) {

    AlgoDerivative algo = new AlgoDerivative(cons, label, f);
    return algo.getDerivative();
  }

  /**
   * n-th derivative of deriveable f
   */
  final public GeoElement Derivative(String label, GeoDeriveable f,
      NumberValue n) {

    AlgoDerivative algo = new AlgoDerivative(cons, label, f, n);
    return algo.getDerivative();
  }

  /********************
   * ALGORITHMIC PART *
   ********************/

  public void detach(View view) {
    // Application.debug("detach " + view);

    if (!notifyViewsActive)
      viewCnt = oldViewCnt;

    int pos = -1;
    for (int i = 0; i < viewCnt; ++i)
      if (views[i] == view) {
        pos = i;
        views[pos] = null; // delete view
        break;
      }

    // view found
    if (pos > -1) {
      // copy following views
      viewCnt--;
      for (; pos < viewCnt; ++pos)
        views[pos] = views[pos + 1];
    }

    /*
     * System.out.print("  current views: "); for (int i = 0; i < viewCnt; i++)
     * { System.out.print(views[i] + ", "); } Application.debug();
     */

    if (!notifyViewsActive) {
      oldViewCnt = viewCnt;
      viewCnt = 0;
    }
  }

  /**
   * Transpose[matrix] Michael Borcherds
   */
  final public GeoNumeric Determinant(String label, GeoList list) {
    AlgoDeterminant algo = new AlgoDeterminant(cons, label, list);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * diameter line conjugate to direction of g relative to c
   */
  final public GeoLine DiameterLine(String label, GeoLine g, GeoConic c) {
    AlgoDiameterLine algo = new AlgoDiameterLine(cons, label, c, g);
    GeoLine diameter = algo.getDiameter();
    return diameter;
  }

  /**
   * diameter line conjugate to v relative to c
   */
  final public GeoLine DiameterLine(String label, GeoVector v, GeoConic c) {
    AlgoDiameterVector algo = new AlgoDiameterVector(cons, label, c, v);
    GeoLine diameter = algo.getDiameter();
    return diameter;
  }

  /**
   * dilate geoRot by r from S
   */
  final public GeoElement[] Dilate(String label, Dilateable geoRot,
      NumberValue r, GeoPoint S) {
    if (label == null)
      label = transformedGeoLabel(geoRot.toGeoElement());

    if (geoRot.toGeoElement().isLimitedPath())
      // handle segments, rays and arcs separately
      return ((LimitedPath) geoRot).createTransformedObject(TRANSFORM_DILATE,
          label, S, null, null, r);

    // standard case
    AlgoDilate algo = new AlgoDilate(cons, label, geoRot, r, S);
    GeoElement[] geos = {algo.getResult()};
    return geos;
  }

  /**
   * dilate geoRot by r from S
   */
  final public GeoElement[] Dilate(String label, GeoPolygon poly,
      NumberValue r, GeoPoint S) {
    return transformPoly(label, poly, dilatePoints(poly.getPoints(), r, S));
  }

  GeoPoint[] dilatePoints(GeoPoint[] points, NumberValue r, GeoPoint S) {
    // dilate all points
    GeoPoint[] newPoints = new GeoPoint[points.length];
    for (int i = 0; i < points.length; i++) {
      String pointLabel = transformedGeoLabel(points[i]);
      newPoints[i] = (GeoPoint) Dilate(pointLabel, points[i], r, S)[0];
    }
    return newPoints;
  }

  /**
   * Direction vector of line g
   */
  final public GeoVector Direction(String label, GeoLine g) {
    AlgoDirection algo = new AlgoDirection(cons, label, g);
    GeoVector v = algo.getVector();
    return v;
  }

  /**
   * directrix of c
   */
  final public GeoLine Directrix(String label, GeoConic c) {
    AlgoDirectrix algo = new AlgoDirectrix(cons, label, c);
    GeoLine directrix = algo.getDirectrix();
    return directrix;
  }

  /**
   * Distance named label between line g and line h
   */
  final public GeoNumeric Distance(String label, GeoLine g, GeoLine h) {
    AlgoDistanceLineLine algo = new AlgoDistanceLineLine(cons, label, g, h);
    GeoNumeric num = algo.getDistance();
    return num;
  }

  /**
   * Distance named label between point P and line g
   */
  final public GeoNumeric Distance(String label, GeoPoint P, GeoLine g) {
    AlgoDistancePointLine algo = new AlgoDistancePointLine(cons, label, P, g);
    GeoNumeric num = algo.getDistance();
    return num;
  }

  /**
   * Distance named label between points P and Q
   */
  final public GeoNumeric Distance(String label, GeoPoint P, GeoPoint Q) {
    AlgoDistancePoints algo = new AlgoDistancePoints(cons, label, P, Q);
    GeoNumeric num = algo.getDistance();
    return num;
  }

  /**
   * Div[a, b]
   */
  final public GeoNumeric Div(String label, NumberValue a, NumberValue b) {
    AlgoDiv algo = new AlgoDiv(cons, label, a, b);
    GeoNumeric num = algo.getResult();
    return num;
  }

  final public GeoElement DynamicCoordinates(String label, GeoPoint geoPoint,
      NumberValue num1, NumberValue num2) {
    AlgoDynamicCoordinates algo = new AlgoDynamicCoordinates(cons, label,
        geoPoint, num1, num2);
    return algo.getPoint();
  }

  /**
   * Element[list, number]
   */
  final public GeoElement Element(String label, GeoList list, NumberValue n) {
    AlgoListElement algo = new AlgoListElement(cons, label, list, n);
    GeoElement geo = algo.getElement();
    return geo;
  }

  /**
   * ellipse with foci A, B passing thorugh C Michael Borcherds 2008-04-06
   */
  final public GeoConic Ellipse(String label, GeoPoint A, GeoPoint B, GeoPoint C) {
    AlgoEllipseFociPoint algo = new AlgoEllipseFociPoint(cons, label, A, B, C);
    GeoConic ellipse = algo.getEllipse();
    return ellipse;
  }

  /**
   * ellipse with foci A, B and length of first half axis a
   */
  final public GeoConic Ellipse(String label, GeoPoint A, GeoPoint B,
      NumberValue a) {
    AlgoEllipseFociLength algo = new AlgoEllipseFociLength(cons, label, A, B, a);
    GeoConic ellipse = algo.getConic();
    return ellipse;
  }

  /**
   * Sets the print accuracy to at least the given decimals or significant
   * figures. If the current accuracy is already higher, nothing is changed.
   * 
   * @param decimalsOrFigures
   * @return whether the print accuracy was changed
   */
  public boolean ensureTemporaryPrintAccuracy(int decimalsOrFigures) {
    if (useSignificantFigures) {
      if (sf.getSigDigits() < decimalsOrFigures) {
        setTemporaryPrintFigures(decimalsOrFigures);
        return true;
      }
    } else // decimals
    if (nf.getMaximumFractionDigits() < decimalsOrFigures) {
      setTemporaryPrintDecimals(decimalsOrFigures);
      return true;
    }
    return false;
  }

  /**
   * Evaluates a JASYMCA expression and returns the result as a String. e.g. exp
   * = "diff(x^2,x)" returns "2*x"
   * 
   * @param expression
   *          string
   * @return result string (null possible)
   */
  final public String evaluateJASYMCA(String exp) {
    if (ggbCAS == null)
      getGeoGebraCAS();

    return ((geogebra.cas.GeoGebraCAS) ggbCAS).evaluateJASYMCA(exp);
  }

  /**
   * Evaluates a MathPiper expression and returns the result as a String. e.g.
   * exp = "D(x) (x^2)" returns "2*x"
   * 
   * @param expression
   *          string
   * @return result string (null possible)
   */
  final public String evaluateMathPiper(String exp) {
    if (ggbCAS == null)
      getGeoGebraCAS();

    return ((geogebra.cas.GeoGebraCAS) ggbCAS).evaluateMathPiper(exp);
  }

  /**
   * excentricity of c
   */
  final public GeoNumeric Excentricity(String label, GeoConic c) {
    AlgoExcentricity algo = new AlgoExcentricity(cons, label, c);
    GeoNumeric excentricity = algo.getExcentricity();
    return excentricity;
  }

  /**
   * Expand function expression
   * 
   * @author Michael Borcherds 2008-04-04
   */
  final public GeoElement Expand(String label, GeoFunction func) {
    AlgoExpand algo = new AlgoExpand(cons, label, func);
    return algo.getResult();
  }

  final public GeoNumeric Exponential(String label, NumberValue a, NumberValue b) {
    AlgoExponential algo = new AlgoExponential(cons, label, a, b);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * all Extrema of function f (works only for polynomials)
   */
  final public GeoPoint[] Extremum(String[] labels, GeoFunction f) {
    // check if this is a polynomial at the moment
    if (!f.isPolynomialFunction(true))
      return null;

    AlgoExtremumPolynomial algo = new AlgoExtremumPolynomial(cons, labels, f);
    GeoPoint[] g = algo.getRootPoints();
    return g;
  }

  /**
   * Factor Michael Borcherds 2008-04-04
   */
  final public GeoFunction Factor(String label, GeoFunction func) {
    AlgoFactor algo = new AlgoFactor(cons, label, func);
    return algo.getResult();
  }

  final public GeoNumeric FDistribution(String label, NumberValue a,
      NumberValue b, NumberValue c) {
    AlgoFDistribution algo = new AlgoFDistribution(cons, label, a, b, c);
    GeoNumeric num = algo.getResult();
    return num;
  }

  private AlgoElement findExistingIntersectionAlgorithm(GeoElement a,
      GeoElement b) {
    int size = intersectionAlgos.size();
    AlgoElement algo;
    for (int i = 0; i < size; i++) {
      algo = intersectionAlgos.get(i);
      GeoElement[] input = algo.getInput();
      if (a == input[0] && b == input[1] || a == input[1] && b == input[0])
        // we found an existing intersection algorithm
        return algo;
    }
    return null;
  }

  /**
   * First[list,n] Michael Borcherds
   */
  final public GeoList First(String label, GeoList list, GeoNumeric n) {
    AlgoFirst algo = new AlgoFirst(cons, label, list, n);
    GeoList list2 = algo.getResult();
    return list2;
  }

  /**
   * first axis of c
   */
  final public GeoLine FirstAxis(String label, GeoConic c) {
    AlgoAxisFirst algo = new AlgoAxisFirst(cons, label, c);
    GeoLine axis = algo.getAxis();
    return axis;
  }

  /**
   * first axis' length of c
   */
  final public GeoNumeric FirstAxisLength(String label, GeoConic c) {
    AlgoAxisFirstLength algo = new AlgoAxisFirstLength(cons, label, c);
    GeoNumeric length = algo.getLength();
    return length;
  }

  /**
   * Sets construction step to first step of construction protocol. Note:
   * showOnlyBreakpoints() is important here
   */
  public void firstStep() {
    int step = 0;

    if (showOnlyBreakpoints())
      setConstructionStep(getNextBreakpoint(step));
    else
      setConstructionStep(step);
  }

  /**
   * FitExp[list of coords] Hans-Petter Ulven
   */
  final public GeoFunction FitExp(String label, GeoList list) {
    AlgoFitExp algo = new AlgoFitExp(cons, label, list);
    GeoFunction function = algo.getFitExp();
    return function;
  }

  /**
   * FitLineX[list of coords] Michael Borcherds
   */
  final public GeoLine FitLineX(String label, GeoList list) {
    AlgoFitLineX algo = new AlgoFitLineX(cons, label, list);
    GeoLine line = algo.getFitLineX();
    return line;
  }

  /**
   * FitLineY[list of coords] Michael Borcherds
   */
  final public GeoLine FitLineY(String label, GeoList list) {
    AlgoFitLineY algo = new AlgoFitLineY(cons, label, list);
    GeoLine line = algo.getFitLineY();
    return line;
  }

  /**
   * FitLog[list of coords] Hans-Petter Ulven
   */
  final public GeoFunction FitLog(String label, GeoList list) {
    AlgoFitLog algo = new AlgoFitLog(cons, label, list);
    GeoFunction function = algo.getFitLog();
    return function;
  }

  /**
   * FitLogistic[list of coords] Hans-Petter Ulven
   */
  final public GeoFunction FitLogistic(String label, GeoList list) {
    AlgoFitLogistic algo = new AlgoFitLogistic(cons, label, list);
    GeoFunction function = algo.getFitLogistic();
    return function;
  }

  /**
   * FitPoly[list of coords,degree] Hans-Petter Ulven
   */
  final public GeoFunction FitPoly(String label, GeoList list,
      NumberValue degree) {
    AlgoFitPoly algo = new AlgoFitPoly(cons, label, list, degree);
    GeoFunction function = algo.getFitPoly();
    return function;
  }

  /**
   * FitPow[list of coords] Hans-Petter Ulven
   */
  final public GeoFunction FitPow(String label, GeoList list) {
    AlgoFitPow algo = new AlgoFitPow(cons, label, list);
    GeoFunction function = algo.getFitPow();
    return function;
  }

  /**
   * FitPow[list of coords] Hans-Petter Ulven
   */
  final public GeoFunction FitSin(String label, GeoList list) {
    AlgoFitSin algo = new AlgoFitSin(cons, label, list);
    GeoFunction function = algo.getFitSin();
    return function;
  }

  /**
   * Focuses of conic. returns 2 GeoPoints
   */
  final public GeoPoint[] Focus(String[] labels, GeoConic c) {
    AlgoFocus algo = new AlgoFocus(cons, labels, c);
    GeoPoint[] focus = algo.getFocus();
    return focus;
  }

  /**
   * Formats the value of x using the currently set NumberFormat or
   * ScientificFormat. This method also takes getCasPrintForm() into account.
   */
  final public String format(double x) {
    switch (casPrintForm) {
      // number formatting for XML string output
      case ExpressionNode.STRING_TYPE_GEOGEBRA_XML :
        return Double.toString(x);

        // number formatting for CAS
      case ExpressionNode.STRING_TYPE_MATH_PIPER :
      case ExpressionNode.STRING_TYPE_JASYMCA :
        if (Double.isNaN(x))
          return " 1/0 ";
        else if (Double.isInfinite(x))
          return Double.toString(x); // "Infinity" or "-Infintiny"
        else if (isZero(x))
          return "0";
        else {
          double abs = Math.abs(x);
          // number small enough that Double.toString() won't create E notation
          if (abs >= 10E-3 && abs < 10E7) {
            long round = Math.round(x);
            if (isEqual(x, round)) // isInteger
              return Long.toString(round);
            else
              return Double.toString(x);
          }
          // number would produce E notation with Double.toString()
          else {
            // convert scientific notation 1.0E-20 to 1*10^(-20)
            String scientificStr = Double.toString(x);
            StringBuffer sb = new StringBuffer(scientificStr.length() * 2);
            boolean Efound = false;
            for (int i = 0; i < scientificStr.length(); i++) {
              char ch = scientificStr.charAt(i);
              if (ch == 'E') {
                sb.append("*10^(");
                Efound = true;
              } else
                sb.append(ch);
            }
            if (Efound)
              sb.append(")");

            return sb.toString();
          }
        }

        // number formatting for screen output
      default :
        if (Double.isNaN(x))
          return "?";
        else if (Double.isInfinite(x))
          return x > 0 ? "\u221e" : "-\u221e"; // infinity
        else if (isZero(x))
          return "0";
        else if (x == Math.PI)
          return casPrintFormPI;

        // ROUNDING hack
        // NumberFormat and SignificantFigures use ROUND_HALF_EVEN as
        // default which is not changeable, so we need to hack this
        // to get ROUND_HALF_UP like in schools: increase abs(x) slightly
        // x = x * ROUND_HALF_UP_FACTOR;
        // We don't do this for large numbers as
        double abs = Math.abs(x);
        if (abs < 10E7)
          // increase abs(x) slightly to round up
          x = x * ROUND_HALF_UP_FACTOR;

        if (useSignificantFigures)
          return formatSF(x);
        else
          return formatNF(x);
    }
  }

  final public StringBuffer formatAngle(double phi) {
    sbFormatAngle.setLength(0);
    if (Double.isNaN(phi)) {
      sbFormatAngle.append(Plain.undefined);
      return sbFormatAngle;
    }

    if (cons.angleUnit == ANGLE_DEGREE) {
      if (isZero(phi)) {
        sbFormatAngle.append("0\u00b0");
        return sbFormatAngle;
      } else {
        phi = Math.toDegrees(phi);
        if (phi < 0)
          phi += 360;
        else if (phi > 360)
          phi = phi % 360;
        sbFormatAngle.append(format(phi));
        sbFormatAngle.append('\u00b0');

        // Application.printStacktrace("formatAngle: " + sbFormatAngle);

        return sbFormatAngle;
      }
    } else if (isZero(phi)) {
      sbFormatAngle.append("0 rad");
      return sbFormatAngle;
    } else {
      sbFormatAngle.append(format(phi));
      sbFormatAngle.append(" rad");
      return sbFormatAngle;
    }
  }

  /** doesn't show 1 or -1 */
  final private String formatCoeff(double x) {
    if (isEqual(Math.abs(x), 1.0)) {
      if (x > 0.0)
        return "";
      else
        return "-";
    } else
      return format(x);
  }

  /**
   * Uses current NumberFormat nf to format a number.
   */
  final private String formatNF(double x) {
    return nf.format(x);
  }

  final public String formatPiE(double x, NumberFormat numF) {
    /*
     * // E if (x == Math.E) { switch (casPrintForm) { case
     * ExpressionNode.STRING_TYPE_GEOGEBRA: return EULER_STRING; case
     * ExpressionNode.STRING_TYPE_JASYMCA: return "exp(1)"; case
     * ExpressionNode.STRING_TYPE_MATH_PIPER: return "Exp(1)"; default: return
     * formatNF(Math.E); } }
     */

    // PI
    if (x == Math.PI)
      return casPrintFormPI;
    else if (isEqual(x, 0, MAX_DOUBLE_PRECISION))
      return "0";

    // MULTIPLES OF PI/2
    // i.e. x = a * pi/2
    double a = 2 * x / Math.PI;
    int aint = (int) Math.round(a);
    if (sbFormat == null)
      sbFormat = new StringBuffer();
    sbFormat.setLength(0);
    if (isEqual(a, aint, MAX_DOUBLE_PRECISION))
      switch (aint) {
        case 0 :
          return "0";

        case 1 : // pi/2
          sbFormat.append(casPrintFormPI);
          sbFormat.append("/2");
          return sbFormat.toString();

        case -1 : // -pi/2
          sbFormat.append('-');
          sbFormat.append(casPrintFormPI);
          sbFormat.append("/2");
          return sbFormat.toString();

        case 2 : // 2pi/2 = pi
          return casPrintFormPI;

        case -2 : // -2pi/2 = -pi
          sbFormat.append('-');
          sbFormat.append(casPrintFormPI);
          return sbFormat.toString();

        default :
          // even
          long half = aint / 2;
          if (aint == 2 * half) {
            // half * pi
            sbFormat.append(half);
            if (casPrintForm != ExpressionNode.STRING_TYPE_GEOGEBRA)
              sbFormat.append("*");
            sbFormat.append(casPrintFormPI);
            return sbFormat.toString();
          }
          // odd
          else {
            // aint * pi/2
            sbFormat.append(aint);
            if (casPrintForm != ExpressionNode.STRING_TYPE_GEOGEBRA)
              sbFormat.append("*");
            sbFormat.append(casPrintFormPI);
            sbFormat.append("/2");
            return sbFormat.toString();
          }
      }

    // STANDARD CASE
    // use numberformat to get number string
    String str = numF.format(x);
    sbFormat.append(str);
    // if number is in scientific notation and ends with "E0", remove this
    if (str.endsWith("E0"))
      sbFormat.setLength(sbFormat.length() - 2);
    return sbFormat.toString();
  }

  /**
   * Uses current ScientificFormat sf to format a number. Makes sure ".123" is
   * returned as "0.123".
   */
  final private String formatSF(double x) {
    if (sbFormatSF == null)
      sbFormatSF = new StringBuffer();
    else
      sbFormatSF.setLength(0);

    // get scientific format
    String absStr;
    if (x >= 0)
      absStr = sf.format(x);
    else {
      sbFormatSF.append('-');
      absStr = sf.format(-x);
    }

    // make sure ".123" is returned as "0.123".
    if (absStr.charAt(0) == '.')
      sbFormatSF.append('0');
    sbFormatSF.append(absStr);

    return sbFormatSF.toString();
  }

  final public StringBuffer formatSigned(double x) {
    sbFormatSigned.setLength(0);
    if (-MIN_PRECISION < x && x < MIN_PRECISION) {
      sbFormatSigned.append("+ 0");
      return sbFormatSigned;
    }

    if (x > 0.0d) {
      sbFormatSigned.append("+ ");
      sbFormatSigned.append(format(x));
      return sbFormatSigned;
    } else {
      sbFormatSigned.append("- ");
      sbFormatSigned.append(format(-x));
      return sbFormatSigned;
    }
  }

  final public String formatSignedCoefficient(double x) {

    if (isEqual(x, -1.0))
      return "- ";
    if (isEqual(x, 1.0))
      return "+ ";

    return formatSigned(x).toString();

  }

  /**
   * ToFraction[number] Michael Borcherds
   */
  final public GeoText FractionText(String label, GeoNumeric num) {
    AlgoFractionText algo = new AlgoFractionText(cons, label, num);
    GeoText text = algo.getResult();
    return text;
  }

  /**
   * Function in x, e.g. f(x) = 4 x� + 3 x�
   */
  final public GeoFunction Function(String label, Function fun) {
    GeoFunction f = new GeoFunction(cons, label, fun);
    return f;
  }

  /**
   * function limited to interval [a, b]
   */
  final public GeoFunction Function(String label, GeoFunction f, NumberValue a,
      NumberValue b) {
    AlgoFunctionInterval algo = new AlgoFunctionInterval(cons, label, f, a, b);
    GeoFunction g = algo.getFunction();
    return g;
  }

  /**
   * Calculate Function Length between the numbers A and B: integral from A to B
   * on T = sqrt(1+(f')^2)
   */

  final public GeoNumeric FunctionLength(String label, GeoFunction f,
      GeoNumeric A, GeoNumeric B) {

    AlgoLengthFunction algo = new AlgoLengthFunction(cons, label, f, A, B);
    GeoNumeric length = algo.getLength();
    return length;

  }

  /**
   * Calculate Function Length between the points A and B: integral from A to B
   * on T = sqrt(1+(f')^2)
   */

  final public GeoNumeric FunctionLength2Points(String label, GeoFunction f,
      GeoPoint A, GeoPoint B) {

    AlgoLengthFunction2Points algo = new AlgoLengthFunction2Points(cons, label,
        f, A, B);
    GeoNumeric length = algo.getLength();
    return length;

  }

  final public GeoNumeric Gamma(String label, NumberValue a, NumberValue b,
      NumberValue c) {
    AlgoGamma algo = new AlgoGamma(cons, label, a, b, c);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * GCD[list] Michael Borcherds
   */
  final public GeoNumeric GCD(String label, GeoList list) {
    AlgoListGCD algo = new AlgoListGCD(cons, label, list);
    GeoNumeric num = algo.getGCD();
    return num;
  }

  /**
   * GCD[a, b] Michael Borcherds
   */
  final public GeoNumeric GCD(String label, NumberValue a, NumberValue b) {
    AlgoGCD algo = new AlgoGCD(cons, label, a, b);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * Returns this kernel's algebra processor that handles all input and
   * commands.
   */
  public AlgebraProcessor getAlgebraProcessor() {
    if (algProcessor == null)
      algProcessor = new AlgebraProcessor(this);
    return algProcessor;
  }

  /**
   * Returns a list with all currently registered macros.
   */
  public ArrayList<Macro> getAllMacros() {
    if (macroManager == null)
      return null;
    else
      return macroManager.getAllMacros();
  }

  final public int getAngleUnit() {
    return cons.angleUnit;
  }

  final public AnimationManager getAnimatonManager() {
    if (animationManager == null)
      animationManager = new AnimationManager(this);
    return animationManager;
  }

  final public Application getApplication() {
    return app;
  }

  final public int getCASPrintForm() {
    return casPrintForm;
  }

  /**
   * Returns the Construction object of this kernel.
   */
  public Construction getConstruction() {
    return cons;
  }

  /**
   * Returns the ConstructionElement for the given construction index.
   */
  public ConstructionElement getConstructionElement(int index) {
    return cons.getConstructionElement(index);
  }

  /**
   * Returns current construction in I2G format. Intergeo File Format. (Yves
   * Kreis)
   */
  public String getConstructionI2G() {
    return cons.getI2G(Construction.CONSTRUCTION);
  }

  public int getConstructionStep() {
    return cons.getStep();
  }

  /**
   * Returns current construction in XML format. GeoGebra File Format.
   */
  public String getConstructionXML() {
    return cons.getXML();
  }

  /**
   * returns 10^(-PrintDecimals)
   * 
   * final public double getPrintPrecision() { return PRINT_PRECISION; }
   */

  final public int getCoordStyle() {
    return coordStyle;
  }

  public String getDisplayI2G() {
    return cons.getI2G(Construction.DISPLAY);
  }

  final public double getEpsilon() {
    return EPSILON;
  }

  final public EquationSolver getEquationSolver() {
    if (eqnSolver == null)
      eqnSolver = new EquationSolver(this);
    return eqnSolver;
  }

  /**
   * return the Evaluator for ExpressionNode
   * 
   * @return the Evaluator for ExpressionNode
   */
  public ExpressionNodeEvaluator getExpressionNodeEvaluator() {
    return expressionNodeEvaluator;
  }

  final public ExtremumFinder getExtremumFinder() {
    if (extrFinder == null)
      extrFinder = new ExtremumFinder();
    return extrFinder;
  }

  /*
   * returns GeoElement at (row,col) in spreadsheet may return null
   */
  public GeoElement getGeoAt(int col, int row) {
    return lookupLabel(GeoElement.getSpreadsheetCellName(col, row));
  }

  /**
   * Returns this kernel's GeoGebraCAS object.
   */
  public synchronized Object getGeoGebraCAS() {
    if (ggbCAS == null) {
      app.loadCASJar();
      ggbCAS = new geogebra.cas.GeoGebraCAS(this);
    }

    return ggbCAS;
  }

  // intersect conics
  AlgoIntersectConics getIntersectionAlgorithm(GeoConic a, GeoConic b) {
    AlgoElement existingAlgo = findExistingIntersectionAlgorithm(a, b);
    if (existingAlgo != null)
      return (AlgoIntersectConics) existingAlgo;

    // we didn't find a matching algorithm, so create a new one
    AlgoIntersectConics algo = new AlgoIntersectConics(cons, a, b);
    algo.setPrintedInXML(false);
    intersectionAlgos.add(algo); // remember this algorithm
    return algo;
  }

  // intersection of polynomials
  AlgoIntersectPolynomials getIntersectionAlgorithm(GeoFunction a, GeoFunction b) {
    AlgoElement existingAlgo = findExistingIntersectionAlgorithm(a, b);
    if (existingAlgo != null)
      return (AlgoIntersectPolynomials) existingAlgo;

    // we didn't find a matching algorithm, so create a new one
    AlgoIntersectPolynomials algo = new AlgoIntersectPolynomials(cons, a, b);
    algo.setPrintedInXML(false);
    intersectionAlgos.add(algo); // remember this algorithm
    return algo;
  }

  // intersection of polynomials
  AlgoIntersectPolynomialLine getIntersectionAlgorithm(GeoFunction a, GeoLine l) {
    AlgoElement existingAlgo = findExistingIntersectionAlgorithm(a, l);
    if (existingAlgo != null)
      return (AlgoIntersectPolynomialLine) existingAlgo;

    // we didn't find a matching algorithm, so create a new one
    AlgoIntersectPolynomialLine algo = new AlgoIntersectPolynomialLine(cons, a,
        l);
    algo.setPrintedInXML(false);
    intersectionAlgos.add(algo); // remember this algorithm
    return algo;
  }

  // intersect line and conic
  AlgoIntersectLineConic getIntersectionAlgorithm(GeoLine g, GeoConic c) {
    AlgoElement existingAlgo = findExistingIntersectionAlgorithm(g, c);
    if (existingAlgo != null)
      return (AlgoIntersectLineConic) existingAlgo;

    // we didn't find a matching algorithm, so create a new one
    AlgoIntersectLineConic algo = new AlgoIntersectLineConic(cons, g, c);
    algo.setPrintedInXML(false);
    intersectionAlgos.add(algo); // remember this algorithm
    return algo;
  }

  public int getLastConstructionStep() {
    return cons.steps() - 1;
  }

  public String getLibraryJavaScript() {
    return libraryJavaScript;
  }

  /**
   * Returns i-th registered macro
   */
  public Macro getMacro(int i) {
    try {
      return macroManager.getMacro(i);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Returns the macro object for a given macro name. Note: null may be
   * returned.
   */
  public Macro getMacro(String name) {
    return macroManager == null ? null : macroManager.getMacro(name);
  }

  /**
   * Returns the ID of the given macro.
   */
  public int getMacroID(Macro macro) {
    return macroManager == null ? -1 : macroManager.getMacroID(macro);
  }

  /**
   * Returns the number of currently registered macros
   */
  public int getMacroNumber() {
    if (macroManager == null)
      return 0;
    else
      return macroManager.getMacroNumber();
  }

  /**
   * Returns an XML represenation of the given macros in this kernel.
   * 
   * @return
   */
  public String getMacroXML(ArrayList<Macro> macros) {
    if (hasMacros())
      return MacroManager.getMacroXML(macros);
    else
      return "";
  }

  final public int getMaximumFractionDigits() {
    return nf.getMaximumFractionDigits();
  }

  private int getNextBreakpoint(int step) {
    int lastStep = getLastConstructionStep();
    // go to next breakpoint
    while (step <= lastStep) {
      if (cons.getConstructionElement(step).isConsProtocolBreakpoint())
        return step;

      step++;
    }

    return lastStep;
  }

  final public Parser getParser() {
    if (parser == null)
      parser = new Parser(this, cons);
    return parser;
  }

  /**
   * Finds the polynomial coefficients of the given expression and returns it in
   * ascending order. If exp is not a polynomial null is returned.
   * 
   * example: getPolynomialCoeffs("3*a*x^2 + b"); returns ["0", "b", "3*a"]
   */
  final public String[] getPolynomialCoeffs(String exp, String variable) {
    if (ggbCAS == null)
      getGeoGebraCAS();

    return ((geogebra.cas.GeoGebraCAS) ggbCAS).getPolynomialCoeffs(exp,
        variable);
  }

  private int getPreviousBreakpoint(int step) {
    // go to previous breakpoint
    while (step >= 0) {
      if (cons.getConstructionElement(step).isConsProtocolBreakpoint())
        return step;
      step--;
    }
    return -1;
  }

  final public int getPrintDecimals() {
    return nf.getMaximumFractionDigits();
  }

  /*
   * returns number of significant digits, or -1 if using decimal places
   */
  final public int getPrintFigures() {
    if (!useSignificantFigures)
      return -1;
    return sf.getSigDigits();
  }

  final public RegressionMath getRegressionMath() {
    if (regMath == null)
      regMath = new RegressionMath();
    return regMath;
  }

  GeoConic getTransformedConic(int type, GeoConic conic, GeoPoint Q, GeoLine l,
      GeoVector vec, NumberValue n) {
    switch (type) {
      case Kernel.TRANSFORM_TRANSLATE :
        AlgoTranslate algoTrans = new AlgoTranslate(cons, conic, vec);
        return (GeoConic) algoTrans.getResult();

      case Kernel.TRANSFORM_MIRROR_AT_POINT :
      case Kernel.TRANSFORM_MIRROR_AT_LINE :
        AlgoMirror algoMirror = new AlgoMirror(cons, conic, l, Q, null);
        return (GeoConic) algoMirror.getResult();

      case Kernel.TRANSFORM_ROTATE :
        AlgoRotate algoRotate = new AlgoRotate(cons, conic, n);
        return (GeoConic) algoRotate.getResult();

      case Kernel.TRANSFORM_ROTATE_AROUND_POINT :
        AlgoRotatePoint algoRotatePoint = new AlgoRotatePoint(cons, conic, n, Q);
        return (GeoConic) algoRotatePoint.getResult();

      case Kernel.TRANSFORM_DILATE :
        AlgoDilate algoDilate = new AlgoDilate(cons, conic, n, Q);
        return (GeoConic) algoDilate.getResult();

      default :
        return null;
    }
  }

  GeoLine getTransformedLine(int type, GeoLine line, GeoPoint Q, GeoLine l,
      GeoVector vec, NumberValue n) {
    switch (type) {
      case Kernel.TRANSFORM_TRANSLATE :
        AlgoTranslate algoTrans = new AlgoTranslate(cons, line, vec);
        return (GeoLine) algoTrans.getResult();

      case Kernel.TRANSFORM_MIRROR_AT_POINT :
      case Kernel.TRANSFORM_MIRROR_AT_LINE :
        AlgoMirror algoMirror = new AlgoMirror(cons, line, l, Q, null);
        return (GeoLine) algoMirror.getResult();

      case Kernel.TRANSFORM_ROTATE :
        AlgoRotate algoRotate = new AlgoRotate(cons, line, n);
        return (GeoLine) algoRotate.getResult();

      case Kernel.TRANSFORM_ROTATE_AROUND_POINT :
        AlgoRotatePoint algoRotatePoint = new AlgoRotatePoint(cons, line, n, Q);
        return (GeoLine) algoRotatePoint.getResult();

      case Kernel.TRANSFORM_DILATE :
        AlgoDilate algoDilate = new AlgoDilate(cons, line, n, Q);
        return (GeoLine) algoDilate.getResult();

      default :
        return null;
    }
  }

  final public GeoAxis getXAxis() {
    return cons.getXAxis();
  }

  double getXmax() {
    return xmax;
  }

  double getXmin() {
    return xmin;
  }

  double getXscale() {
    return xscale;
  }

  final public GeoAxis getYAxis() {
    return cons.getYAxis();
  }

  double getYmax() {
    return ymax;
  }

  double getYmin() {
    return ymin;
  }

  double getYscale() {
    return yscale;
  }

  /* *******************************************
   * Methods for MyXMLHandler *******************************************
   */
  public boolean handleCoords(GeoElement geo,
      LinkedHashMap<String, String> attrs) {

    if (!(geo instanceof GeoVec3D)) {
      Application.debug("wrong element type for <coords>: " + geo.getClass());
      return false;
    }
    GeoVec3D v = (GeoVec3D) geo;

    try {
      double x = Double.parseDouble(attrs.get("x"));
      double y = Double.parseDouble(attrs.get("y"));
      double z = Double.parseDouble(attrs.get("z"));
      v.setCoords(x, y, z);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Returns whether any macros have been added to this kernel.
   */
  public boolean hasMacros() {
    return macroManager != null && macroManager.getMacroNumber() > 0;
  }

  /**
   * Histogram
   */
  final public GeoNumeric Histogram(String label, GeoList list1, GeoList list2) {
    AlgoHistogram algo = new AlgoHistogram(cons, label, list1, list2);
    GeoNumeric sum = algo.getSum();
    return sum;
  }

  /**
   * hyperbola with foci A, B passing thorugh C Michael Borcherds 2008-04-06
   */
  final public GeoConic Hyperbola(String label, GeoPoint A, GeoPoint B,
      GeoPoint C) {
    AlgoHyperbolaFociPoint algo = new AlgoHyperbolaFociPoint(cons, label, A, B,
        C);
    GeoConic hyperbola = algo.getHyperbola();
    return hyperbola;
  }

  /**
   * hyperbola with foci A, B and length of first half axis a
   */
  final public GeoConic Hyperbola(String label, GeoPoint A, GeoPoint B,
      NumberValue a) {
    AlgoHyperbolaFociLength algo = new AlgoHyperbolaFociLength(cons, label, A,
        B, a);
    GeoConic hyperbola = algo.getConic();
    return hyperbola;
  }

  final public GeoNumeric HyperGeometric(String label, NumberValue a,
      NumberValue b, NumberValue c, NumberValue d) {
    AlgoHyperGeometric algo = new AlgoHyperGeometric(cons, label, a, b, c, d);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * If-then-else construct.
   */
  final public GeoElement If(String label, GeoBoolean condition,
      GeoElement geoIf, GeoElement geoElse) {

    // check if geoIf and geoElse are of same type
    /*
     * if (geoElse == null || geoIf.isNumberValue() && geoElse.isNumberValue()
     * || geoIf.getTypeString().equals(geoElse.getTypeString())) {
     */
    AlgoIf algo = new AlgoIf(cons, label, condition, geoIf, geoElse);
    return algo.getGeoElement();
    /*
     * } else { // incompatible types Application.debug("if incompatible: " +
     * geoIf + ", " + geoElse); return null; }
     */
  }

  /**
   * If-then-else construct for functions. example: If[ x < 2, x^2, x + 2 ]
   */
  final public GeoFunction If(String label, GeoFunction boolFun,
      GeoFunction ifFun, GeoFunction elseFun) {

    AlgoIfFunction algo = new AlgoIfFunction(cons, label, boolFun, ifFun,
        elseFun);
    return algo.getGeoFunction();
  }

  public void initUndoInfo() {
    if (undoActive)
      cons.initUndoInfo();
  }
  /**
   * Insert[list,list,n] Michael Borcherds
   */
  final public GeoList Insert(String label, GeoElement geo, GeoList list,
      GeoNumeric n) {
    AlgoInsert algo = new AlgoInsert(cons, label, geo, list, n);
    GeoList list2 = algo.getResult();
    return list2;
  }

  /**
   * Integral of function f
   */
  final public GeoFunction Integral(String label, GeoFunction f) {
    AlgoIntegral algo = new AlgoIntegral(cons, label, f);
    GeoFunction g = algo.getIntegral();
    return g;
  }

  /**
   * definite integral of function (f - g) in interval [a, b]
   */
  final public GeoNumeric Integral(String label, GeoFunction f, GeoFunction g,
      NumberValue a, NumberValue b) {
    AlgoIntegralFunctions algo = new AlgoIntegralFunctions(cons, label, f, g,
        a, b);
    GeoNumeric num = algo.getIntegral();
    return num;
  }

  /**
   * definite Integral of function f from x=a to x=b
   */
  final public GeoNumeric Integral(String label, GeoFunction f, NumberValue a,
      NumberValue b) {
    AlgoIntegralDefinite algo = new AlgoIntegralDefinite(cons, label, f, a, b);
    GeoNumeric n = algo.getIntegral();
    return n;
  }

  /**
   * IntersectConics yields intersection points named label1, label2, label3,
   * label4 of conics c1, c2
   */
  final public GeoPoint[] IntersectConics(String[] labels, GeoConic a,
      GeoConic b) {
    AlgoIntersectConics algo = getIntersectionAlgorithm(a, b);
    algo.setPrintedInXML(true);
    GeoPoint[] points = algo.getIntersectionPoints();
    GeoElement.setLabels(labels, points);
    return points;
  }

  /**
   * get only one intersection point of two conics that is near to the given
   * location (xRW, yRW)
   */
  final public GeoPoint IntersectConicsSingle(String label, GeoConic a,
      GeoConic b, double xRW, double yRW) {
    AlgoIntersectConics algo = getIntersectionAlgorithm(a, b);
    int index = algo.getClosestPointIndex(xRW, yRW);
    AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, index);
    GeoPoint point = salgo.getPoint();
    return point;
  }

  /**
   * get only one intersection point of two conics
   */
  final public GeoPoint IntersectConicsSingle(String label, GeoConic a,
      GeoConic b, NumberValue index) {
    AlgoIntersectConics algo = getIntersectionAlgorithm(a, b); // index - 1 to
    // start at 0
    AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo,
        (int) index.getDouble() - 1);
    GeoPoint point = salgo.getPoint();
    return point;
  }

  /**
   * Intersects f and l using starting point A (with Newton's root finding)
   */
  final public GeoPoint IntersectFunctionLine(String label, GeoFunction f,
      GeoLine l, GeoPoint A) {

    AlgoIntersectFunctionLineNewton algo = new AlgoIntersectFunctionLineNewton(
        cons, label, f, l, A);
    GeoPoint S = algo.getIntersectionPoint();
    return S;
  }

  /**
   * Intersects f and g using starting point A (with Newton's root finding)
   */
  final public GeoPoint IntersectFunctions(String label, GeoFunction f,
      GeoFunction g, GeoPoint A) {
    AlgoIntersectFunctionsNewton algo = new AlgoIntersectFunctionsNewton(cons,
        label, f, g, A);
    GeoPoint S = algo.getIntersectionPoint();
    return S;
  }

  /**
   * Intersection[list,list] Michael Borcherds
   */
  final public GeoList Intersection(String label, GeoList list, GeoList list1) {
    AlgoIntersection algo = new AlgoIntersection(cons, label, list, list1);
    GeoList list2 = algo.getResult();
    return list2;
  }

  /**
   * IntersectLineConic yields intersection points named label1, label2 of line
   * g and conic c
   */
  final public GeoPoint[] IntersectLineConic(String[] labels, GeoLine g,
      GeoConic c) {
    AlgoIntersectLineConic algo = getIntersectionAlgorithm(g, c);
    algo.setPrintedInXML(true);
    GeoPoint[] points = algo.getIntersectionPoints();
    GeoElement.setLabels(labels, points);
    return points;
  }

  /**
   * get only one intersection point of two conics that is near to the given
   * location (xRW, yRW)
   */
  final public GeoPoint IntersectLineConicSingle(String label, GeoLine g,
      GeoConic c, double xRW, double yRW) {
    AlgoIntersectLineConic algo = getIntersectionAlgorithm(g, c);
    int index = algo.getClosestPointIndex(xRW, yRW);
    AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, index);
    GeoPoint point = salgo.getPoint();
    return point;
  }
  /**
   * get only one intersection point of a line and a conic
   */
  final public GeoPoint IntersectLineConicSingle(String label, GeoLine g,
      GeoConic c, NumberValue index) {
    AlgoIntersectLineConic algo = getIntersectionAlgorithm(g, c); // index - 1
    // to start at
    // 0
    AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo,
        (int) index.getDouble() - 1);
    GeoPoint point = salgo.getPoint();
    return point;
  }

  /**
   * IntersectLines yields intersection point named label of lines g, h
   */
  final public GeoPoint IntersectLines(String label, GeoLine g, GeoLine h) {
    AlgoIntersectLines algo = new AlgoIntersectLines(cons, label, g, h);
    GeoPoint S = algo.getPoint();
    return S;
  }
  /**
   * IntersectPolyomialLine yields all intersection points of polynomial f and
   * line l
   */
  final public GeoPoint[] IntersectPolynomialLine(String[] labels,
      GeoFunction f, GeoLine l) {

    if (!f.isPolynomialFunction(false)) {

      // dummy point
      GeoPoint A = new GeoPoint(cons);
      A.setZero();

      AlgoIntersectFunctionLineNewton algo = new AlgoIntersectFunctionLineNewton(
          cons, labels[0], f, l, A);
      GeoPoint[] ret = {algo.getIntersectionPoint()};
      return ret;

    }

    AlgoIntersectPolynomialLine algo = getIntersectionAlgorithm(f, l);
    algo.setPrintedInXML(true);
    algo.setLabels(labels);
    GeoPoint[] points = algo.getIntersectionPoints();
    return points;
  }

  /**
   * one intersection point of polynomial f and line l near to (xRW, yRW)
   */
  final public GeoPoint IntersectPolynomialLineSingle(String label,
      GeoFunction f, GeoLine l, double xRW, double yRW) {

    if (!f.isPolynomialFunction(false))
      return null;

    AlgoIntersectPolynomialLine algo = getIntersectionAlgorithm(f, l);
    int index = algo.getClosestPointIndex(xRW, yRW);
    AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, index);
    GeoPoint point = salgo.getPoint();
    return point;
  }

  /**
   * get only one intersection point of a line and a function
   */
  final public GeoPoint IntersectPolynomialLineSingle(String label,
      GeoFunction f, GeoLine l, NumberValue index) {
    if (!f.isPolynomialFunction(false))
      return null;

    AlgoIntersectPolynomialLine algo = getIntersectionAlgorithm(f, l);
    AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo,
        (int) index.getDouble() - 1);
    GeoPoint point = salgo.getPoint();
    return point;
  }

  /**
   * IntersectPolynomials yields all intersection points of polynomials a, b
   */
  final public GeoPoint[] IntersectPolynomials(String[] labels, GeoFunction a,
      GeoFunction b) {

    if (!a.isPolynomialFunction(false) || !b.isPolynomialFunction(false)) {

      // dummy point
      GeoPoint A = new GeoPoint(cons);
      A.setZero();

      AlgoIntersectFunctionsNewton algo = new AlgoIntersectFunctionsNewton(
          cons, labels[0], a, b, A);
      GeoPoint[] ret = {algo.getIntersectionPoint()};
      return ret;
    }

    AlgoIntersectPolynomials algo = getIntersectionAlgorithm(a, b);
    algo.setPrintedInXML(true);
    algo.setLabels(labels);
    GeoPoint[] points = algo.getIntersectionPoints();
    return points;
  }

  /**
   * get only one intersection point of two polynomials a, b that is near to the
   * given location (xRW, yRW)
   */
  final public GeoPoint IntersectPolynomialsSingle(String label, GeoFunction a,
      GeoFunction b, double xRW, double yRW) {
    if (!a.isPolynomialFunction(false) || !b.isPolynomialFunction(false))
      return null;

    AlgoIntersectPolynomials algo = getIntersectionAlgorithm(a, b);
    int index = algo.getClosestPointIndex(xRW, yRW);
    AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, index);
    GeoPoint point = salgo.getPoint();
    return point;
  }

  /**
   * get only one intersection point of two polynomials a, b with given index
   */
  final public GeoPoint IntersectPolynomialsSingle(String label, GeoFunction a,
      GeoFunction b, NumberValue index) {
    if (!a.isPolynomialFunction(false) || !b.isPolynomialFunction(false))
      return null;

    AlgoIntersectPolynomials algo = getIntersectionAlgorithm(a, b); // index - 1
    // to start
    // at 0
    AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo,
        (int) index.getDouble() - 1);
    GeoPoint point = salgo.getPoint();
    return point;
  }

  final public GeoNumeric InverseCauchy(String label, NumberValue a,
      NumberValue b, NumberValue c) {
    AlgoInverseCauchy algo = new AlgoInverseCauchy(cons, label, a, b, c);
    GeoNumeric num = algo.getResult();
    return num;
  }

  final public GeoNumeric InverseChiSquared(String label, NumberValue a,
      NumberValue b) {
    AlgoInverseChiSquared algo = new AlgoInverseChiSquared(cons, label, a, b);
    GeoNumeric num = algo.getResult();
    return num;
  }

  final public GeoNumeric InverseExponential(String label, NumberValue a,
      NumberValue b) {
    AlgoInverseExponential algo = new AlgoInverseExponential(cons, label, a, b);
    GeoNumeric num = algo.getResult();
    return num;
  }

  final public GeoNumeric InverseFDistribution(String label, NumberValue a,
      NumberValue b, NumberValue c) {
    AlgoInverseFDistribution algo = new AlgoInverseFDistribution(cons, label,
        a, b, c);
    GeoNumeric num = algo.getResult();
    return num;
  }

  final public GeoNumeric InverseGamma(String label, NumberValue a,
      NumberValue b, NumberValue c) {
    AlgoInverseGamma algo = new AlgoInverseGamma(cons, label, a, b, c);
    GeoNumeric num = algo.getResult();
    return num;
  }

  final public GeoNumeric InverseHyperGeometric(String label, NumberValue a,
      NumberValue b, NumberValue c, NumberValue d) {
    AlgoInverseHyperGeometric algo = new AlgoInverseHyperGeometric(cons, label,
        a, b, c, d);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * InverseNormal[mean,variance,x] Michael Borcherds
   */
  final public GeoNumeric InverseNormal(String label, NumberValue a,
      NumberValue b, NumberValue c) {
    AlgoInverseNormal algo = new AlgoInverseNormal(cons, label, a, b, c);
    GeoNumeric num = algo.getResult();
    return num;
  }

  final public GeoNumeric InversePascal(String label, NumberValue a,
      NumberValue b, NumberValue c) {
    AlgoInversePascal algo = new AlgoInversePascal(cons, label, a, b, c);
    GeoNumeric num = algo.getResult();
    return num;
  }

  final public GeoNumeric InverseTDistribution(String label, NumberValue a,
      NumberValue b) {
    AlgoInverseTDistribution algo = new AlgoInverseTDistribution(cons, label,
        a, b);
    GeoNumeric num = algo.getResult();
    return num;
  }

  final public GeoNumeric InverseWeibull(String label, NumberValue a,
      NumberValue b, NumberValue c) {
    AlgoInverseWeibull algo = new AlgoInverseWeibull(cons, label, a, b, c);
    GeoNumeric num = algo.getResult();
    return num;
  }

  final public GeoNumeric InverseZipf(String label, NumberValue a,
      NumberValue b, NumberValue c) {
    AlgoInverseZipf algo = new AlgoInverseZipf(cons, label, a, b, c);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * Invert[matrix] Michael Borcherds
   */
  final public GeoList Invert(String label, GeoList list) {
    AlgoInvert algo = new AlgoInvert(cons, label, list);
    GeoList list2 = algo.getResult();
    return list2;
  }

  public final boolean isAllowVisibilitySideEffects() {
    return allowVisibilitySideEffects;
  }

  final public boolean isAnimationPaused() {
    return animationManager != null && animationManager.isPaused();
  }

  final public boolean isAnimationRunning() {
    return animationManager != null && animationManager.isRunning();
  }

  final public boolean isAxis(GeoElement geo) {
    return geo == cons.getXAxis() || geo == cons.getYAxis();
  }

  /**
   * States whether the continuity heuristic is active.
   */
  final public boolean isContinuous() {
    return continuous;
  }

  /**
   * Tests if the current construction has no elements.
   * 
   * @return true if the current construction has no GeoElements; false
   *         otherwise.
   */
  public boolean isEmpty() {
    return cons.isEmpty();
  }

  /**
   * Returns whether x is equal to y
   */
  final public boolean isEqual(double x, double y) {
    return x - EPSILON < y && y < x + EPSILON;
  }

  // compares double arrays:
  // yields true if (isEqual(a[i], b[i]) == true) for all i
  final boolean isEqual(double[] a, double[] b) {
    for (int i = 0; i < a.length; ++i)
      if (!isEqual(a[i], b[i]))
        return false;
    return true;
  }

  final public boolean isGeoGebraCASready() {
    return ggbCAS != null;
  }

  /**
   * Returns whether x is greater than y
   */
  final public boolean isGreater(double x, double y) {
    return x > y + EPSILON;
  }

  /**
   * Returns whether x is greater than or equal to y
   */
  final public boolean isGreaterEqual(double x, double y) {
    return x + EPSILON > y;
  }

  final public boolean isInteger(double x) {
    return isEqual(x, Math.round(x));
  }

  /**
   * IsInteger[number] Michael Borcherds
   */
  final public GeoBoolean IsInteger(String label, GeoNumeric geo) {
    AlgoIsInteger algo = new AlgoIsInteger(cons, label, geo);
    GeoBoolean result = algo.getResult();
    return result;
  }

  public boolean isMacroKernel() {
    return false;
  }

  final public boolean isNotifyRepaintActive() {
    return notifyRepaint;
  }

  public boolean isNotifyViewsActive() {
    return notifyViewsActive && !viewReiniting;
  }

  final public boolean isReal(Complex c) {
    return isZero(c.getImaginary());
  }

  /**
   * Returns whether unkown variables are resolved as GeoDummyVariable objects.
   * 
   * @see setSilentMode()
   */
  public final boolean isResolveVariablesForCASactive() {
    return resolveVariablesForCASactive;
  }

  /**
   * Returns whether silent mode is turned on.
   * 
   * @see setSilentMode()
   */
  public final boolean isSilentMode() {
    return silentMode;
  }

  public boolean isTranslateCommandName() {
    return translateCommandName;
  }

  public boolean isUndoActive() {
    return undoActive;
  }

  public boolean isViewReiniting() {
    return viewReiniting;
  }

  /** is abs(x) < epsilon ? */
  final public boolean isZero(double x) {
    return -EPSILON < x && x < EPSILON;
  }

  final boolean isZero(double[] a) {
    for (int i = 0; i < a.length; i++)
      if (!isZero(a[i]))
        return false;
    return true;
  }

  /**
   * Iteration[ f(x), x0, n ]
   */
  final public GeoNumeric Iteration(String label, GeoFunction f,
      NumberValue start, NumberValue n) {
    AlgoIteration algo = new AlgoIteration(cons, label, f, start, n);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * IterationList[ f(x), x0, n ]
   */
  final public GeoList IterationList(String label, GeoFunction f,
      NumberValue start, NumberValue n) {
    AlgoIterationList algo = new AlgoIterationList(cons, label, f, start, n);
    return algo.getResult();
  }

  /**
   * Join[list,list] Michael Borcherds
   */
  final public GeoList Join(String label, GeoList list) {
    AlgoJoin algo = new AlgoJoin(cons, label, list);
    GeoList list2 = algo.getResult();
    return list2;
  }

  /**
   * Keep[boolean condition, list] Michael Borcherds
   */
  final public GeoList KeepIf(String label, GeoFunction boolFun, GeoList list) {
    AlgoKeepIf algo = new AlgoKeepIf(cons, label, boolFun, list);
    GeoList list2 = algo.getResult();
    return list2;
  }

  /**
   * Last[list,n] Michael Borcherds
   */
  final public GeoList Last(String label, GeoList list, GeoNumeric n) {
    AlgoLast algo = new AlgoLast(cons, label, list, n);
    GeoList list2 = algo.getResult();
    return list2;
  }

  /**
   * Sets construction step to last step of construction protocol. Note:
   * showOnlyBreakpoints() is important here
   */
  public void lastStep() {
    int step = getLastConstructionStep();

    if (showOnlyBreakpoints())
      setConstructionStep(getPreviousBreakpoint(step));
    else
      setConstructionStep(step);
  }

  /**
   * LaTeX of geo.
   */
  final public GeoText LaTeX(String label, GeoElement geo) {
    AlgoLaTeX algo = new AlgoLaTeX(cons, label, geo);
    GeoText t = algo.getGeoText();
    return t;
  }

  /**
   * LaTeX of geo.
   */
  final public GeoText LaTeX(String label, GeoElement geo,
      GeoBoolean substituteVars) {
    AlgoLaTeX algo = new AlgoLaTeX(cons, label, geo, substituteVars);
    GeoText t = algo.getGeoText();
    return t;
  }

  /**
   * LCM[list] Michael Borcherds
   */
  final public GeoNumeric LCM(String label, GeoList list) {
    AlgoListLCM algo = new AlgoListLCM(cons, label, list);
    GeoNumeric num = algo.getLCM();
    return num;
  }

  /**
   * LCM[a, b] Michael Borcherds
   */
  final public GeoNumeric LCM(String label, NumberValue a, NumberValue b) {
    AlgoLCM algo = new AlgoLCM(cons, label, a, b);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * Length[list]
   */
  final public GeoNumeric Length(String label, GeoList list) {
    AlgoListLength algo = new AlgoListLength(cons, label, list);
    return algo.getLength();
  }

  /**
   * Length named label of vector v
   */
  final public GeoNumeric Length(String label, GeoVec3D v) {
    AlgoLengthVector algo = new AlgoLengthVector(cons, label, v);
    GeoNumeric num = algo.getLength();
    return num;
  }

  /**
   * ToNumber
   */
  final public GeoNumeric LetterToUnicode(String label, GeoText geo) {
    AlgoLetterToUnicode algo = new AlgoLetterToUnicode(cons, label, geo);
    GeoNumeric ret = algo.getResult();
    return ret;
  }

  /** Line a x + b y + c = 0 named label */
  final public GeoLine Line(String label, double a, double b, double c) {
    GeoLine line = new GeoLine(cons, label, a, b, c);
    return line;
  }

  /**
   * Line named label through Point P parallel to Line l
   */
  final public GeoLine Line(String label, GeoPoint P, GeoLine l) {
    AlgoLinePointLine algo = new AlgoLinePointLine(cons, label, P, l);
    GeoLine g = algo.getLine();
    return g;
  }

  /**
   * Line named label through Points P and Q
   */
  final public GeoLine Line(String label, GeoPoint P, GeoPoint Q) {
    AlgoJoinPoints algo = new AlgoJoinPoints(cons, label, P, Q);
    GeoLine g = algo.getLine();
    return g;
  }

  // PhilippWeissenbacher 2007-04-10

  /**
   * Line named label through Point P with direction of vector v
   */
  final public GeoLine Line(String label, GeoPoint P, GeoVector v) {
    AlgoLinePointVector algo = new AlgoLinePointVector(cons, label, P, v);
    GeoLine g = algo.getLine();
    return g;
  }

  /**
   * Line bisector of points A, B
   */
  final public GeoLine LineBisector(String label, GeoPoint A, GeoPoint B) {
    AlgoLineBisector algo = new AlgoLineBisector(cons, label, A, B);
    GeoLine g = algo.getLine();
    return g;
  }

  // PhilippWeissenbacher 2007-04-10

  /**
   * Line bisector of segment s
   */
  final public GeoLine LineBisector(String label, GeoSegment s) {
    AlgoLineBisectorSegment algo = new AlgoLineBisectorSegment(cons, label, s);
    GeoLine g = algo.getLine();
    return g;
  }

  /**
   * Creates a free list object with the given
   * 
   * @param label
   * @param geoElementList
   *          : list of GeoElement objects
   * @return
   */
  final public GeoList List(String label, ArrayList<GeoElement> geoElementList,
      boolean isIndependent) {
    if (isIndependent) {
      GeoList list = new GeoList(cons);
      int size = geoElementList.size();
      for (int i = 0; i < size; i++)
        list.add(geoElementList.get(i));
      list.setLabel(label);
      return list;
    } else {
      AlgoDependentList algoList = new AlgoDependentList(cons, label,
          geoElementList);
      return algoList.getGeoList();
    }
  }

  /**
   * Creates a dependent list object with the given label, e.g. {3, 2, 1} + {a,
   * b, 2}
   */
  final public GeoList ListExpression(String label, ExpressionNode root) {
    AlgoDependentListExpression algo = new AlgoDependentListExpression(cons,
        label, root);
    return algo.getList();
  }

  /**
   * locus line for Q dependent on P. Note: P must be a point on a path.
   */
  final public GeoLocus Locus(String label, GeoPoint Q, GeoPoint P) {
    if (P.getPath() == null || Q.getPath() != null || !P.isParentOf(Q))
      return null;
    AlgoLocus algo = new AlgoLocus(cons, label, Q, P);
    return algo.getLocus();
  }

  /**
   * Returns a GeoElement for the given label.
   * 
   * @return may return null
   */
  final public GeoElement lookupLabel(String label) {
    return lookupLabel(label, false);
  }

  final public GeoElement lookupLabel(String label, boolean autoCreate) {
    GeoElement geo = cons.lookupLabel(label, autoCreate);

    if (geo == null && resolveVariablesForCASactive)
      // resolve unknown variable as dummy geo to keep its name and
      // avoid an "unknown variable" error message
      geo = new GeoDummyVariable(cons, label);

    return geo;
  }

  /**
   * LowerSum of function f
   */
  final public GeoNumeric LowerSum(String label, GeoFunction f, NumberValue a,
      NumberValue b, NumberValue n) {
    AlgoSumLower algo = new AlgoSumLower(cons, label, f, a, b, n);
    GeoNumeric sum = algo.getSum();
    return sum;
  }

  /**
   * Max[list]
   */
  final public GeoNumeric Max(String label, GeoList list) {
    AlgoListMax algo = new AlgoListMax(cons, label, list);
    GeoNumeric num = algo.getMax();
    return num;
  }

  /**
   * Max[a, b]
   */
  final public GeoNumeric Max(String label, NumberValue a, NumberValue b) {
    AlgoMax algo = new AlgoMax(cons, label, a, b);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * Mean[list] Michael Borcherds
   */
  final public GeoNumeric Mean(String label, GeoList list) {
    AlgoMean algo = new AlgoMean(cons, label, list);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * MeanX[list] Michael Borcherds
   */
  final public GeoNumeric MeanX(String label, GeoList list) {
    AlgoListMeanX algo = new AlgoListMeanX(cons, label, list);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * MeanY[list] Michael Borcherds
   */
  final public GeoNumeric MeanY(String label, GeoList list) {
    AlgoListMeanY algo = new AlgoListMeanY(cons, label, list);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * Median[list] Michael Borcherds
   */
  final public GeoNumeric Median(String label, GeoList list) {
    AlgoMedian algo = new AlgoMedian(cons, label, list);
    GeoNumeric num = algo.getMedian();
    return num;
  }

  /*********************************************
   * CONIC PART
   *********************************************/

  /**
   * Creates Midpoint M = (P + Q)/2 without label (for use as e.g. start point)
   */
  final public GeoPoint Midpoint(GeoPoint P, GeoPoint Q) {

    boolean oldValue = cons.isSuppressLabelsActive();
    cons.setSuppressLabelCreation(true);
    GeoPoint midPoint = Midpoint(null, P, Q);
    cons.setSuppressLabelCreation(oldValue);
    return midPoint;
  }

  /**
   * Midpoint M = (P + Q)/2
   */
  final public GeoPoint Midpoint(String label, GeoPoint P, GeoPoint Q) {
    AlgoMidpoint algo = new AlgoMidpoint(cons, label, P, Q);
    GeoPoint M = algo.getPoint();
    return M;
  }

  /**
   * Midpoint of segment
   */
  final public GeoPoint Midpoint(String label, GeoSegment s) {
    AlgoMidpointSegment algo = new AlgoMidpointSegment(cons, label, s);
    GeoPoint M = algo.getPoint();
    return M;
  }

  /**
   * Min[list]
   */
  final public GeoNumeric Min(String label, GeoList list) {
    AlgoListMin algo = new AlgoListMin(cons, label, list);
    GeoNumeric num = algo.getMin();
    return num;
  }

  /**
   * Min[a, b]
   */
  final public GeoNumeric Min(String label, NumberValue a, NumberValue b) {
    AlgoMin algo = new AlgoMin(cons, label, a, b);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * mirror point Q in conic Michael Borcherds 2008-02-10
   */
  final public GeoElement[] Mirror(String label, GeoPoint Q, GeoConic conic) {
    if (label == null)
      label = transformedGeoLabel(Q);

    AlgoMirror algo = new AlgoMirror(cons, label, Q, conic);
    GeoElement[] geos = {algo.getResult()};
    return geos;
  }

  /**
   * mirror geoMir at line g
   */
  final public GeoElement[] Mirror(String label, GeoPolygon poly, GeoLine g) {
    return transformPoly(label, poly, mirrorPoints(poly.getPoints(), null, g));
  }

  /**
   * mirror geoMir at point Q
   */
  final public GeoElement[] Mirror(String label, GeoPolygon poly, GeoPoint Q) {
    return transformPoly(label, poly, mirrorPoints(poly.getPoints(), Q, null));
  }

  /**
   * mirror geoMir at line g
   */
  final public GeoElement[] Mirror(String label, Mirrorable geoMir, GeoLine g) {
    if (label == null)
      label = transformedGeoLabel(geoMir.toGeoElement());

    if (geoMir.toGeoElement().isLimitedPath()) {
      // handle segments, rays and arcs separately
      GeoElement[] geos = ((LimitedPath) geoMir).createTransformedObject(
          TRANSFORM_MIRROR_AT_LINE, label, null, g, null, null);

      // if (geos[0] instanceof Orientable && geoMir instanceof Orientable)
      // ((Orientable)geos[0]).setOppositeOrientation( (Orientable)geoMir);

      return geos;
    }
    // standard case
    AlgoMirror algo = new AlgoMirror(cons, label, geoMir, g);
    GeoElement[] geos = {algo.getResult()};

    return geos;
  }

  /**
   * mirror geoMir at point Q
   */
  final public GeoElement[] Mirror(String label, Mirrorable geoMir, GeoPoint Q) {
    if (label == null)
      label = transformedGeoLabel(geoMir.toGeoElement());

    if (geoMir.toGeoElement().isLimitedPath())
      // handle segments, rays and arcs separately
      return ((LimitedPath) geoMir).createTransformedObject(
          TRANSFORM_MIRROR_AT_POINT, label, Q, null, null, null);

    // standard case
    AlgoMirror algo = new AlgoMirror(cons, label, geoMir, Q);
    GeoElement[] geos = {algo.getResult()};
    return geos;
  }

  GeoPoint[] mirrorPoints(GeoPoint[] points, GeoPoint Q, GeoLine g) {
    // mirror all points
    GeoPoint[] newPoints = new GeoPoint[points.length];
    for (int i = 0; i < points.length; i++) {
      String pointLabel = transformedGeoLabel(points[i]);
      if (Q == null)
        newPoints[i] = (GeoPoint) Mirror(pointLabel, points[i], g)[0];
      else
        newPoints[i] = (GeoPoint) Mirror(pointLabel, points[i], Q)[0];
    }
    return newPoints;
  }

  /**
   * Mod[a, b]
   */
  final public GeoNumeric Mod(String label, NumberValue a, NumberValue b) {
    AlgoMod algo = new AlgoMod(cons, label, a, b);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * Mode[list] Michael Borcherds
   */
  final public GeoList Mode(String label, GeoList list) {
    AlgoMode algo = new AlgoMode(cons, label, list);
    GeoList list2 = algo.getResult();
    return list2;
  }

  /**
   * Move object at position from to position to in current construction.
   */
  public boolean moveInConstructionList(int from, int to) {
    return cons.moveInConstructionList(from, to);
  }

  /**
   * Name of geo.
   */
  final public GeoText Name(String label, GeoElement geo) {
    AlgoName algo = new AlgoName(cons, label, geo);
    GeoText t = algo.getGeoText();
    return t;
  }

  final public boolean needToShowAnimationButton() {
    return animationManager != null
        && animationManager.needToShowAnimationButton();
  }

  /**
   * creates the construction cons
   */
  protected void newConstruction() {
    cons = new Construction(this);
  }

  /**
   * creates the Evaluator for ExpressionNode
   */
  protected void newExpressionNodeEvaluator() {
    expressionNodeEvaluator = new ExpressionNodeEvaluator();
  }

  /**
   * creates a new MyXMLHandler (used for 3D)
   * 
   * @param cons
   *          construction used in MyXMLHandler constructor
   * @return a new MyXMLHandler
   */
  public MyXMLHandler newMyXMLHandler(Construction cons) {
    return new MyXMLHandler(this, cons);
  }

  /**
   * Sets construction step to next step of construction protocol. Note:
   * showOnlyBreakpoints() is important here
   */
  public void nextStep() {
    int step = cons.getStep() + 1;

    if (showOnlyBreakpoints())
      setConstructionStep(getNextBreakpoint(step));
    else
      setConstructionStep(step);
  }

  /**
   * Normal[mean,variance,x] Michael Borcherds
   */
  final public GeoNumeric Normal(String label, NumberValue a, NumberValue b,
      NumberValue c) {
    AlgoNormal algo = new AlgoNormal(cons, label, a, b, c);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * Tells views to update all labeled elements of current construction.
   * 
   * final public static void notifyUpdateAll() {
   * notifyUpdate(kernelConstruction.getAllGeoElements()); }
   */

  final void notifyAdd(GeoElement geo) {
    if (notifyViewsActive)
      for (int i = 0; i < viewCnt; ++i)
        views[i].add(geo);

    notifyRenameListenerAlgos();
  }

  final public void notifyAddAll(View view) {
    int consStep = cons.getStep();
    notifyAddAll(view, consStep);
  }

  final public void notifyAddAll(View view, int consStep) {
    if (!notifyViewsActive)
      return;

    Iterator<GeoElement> it = cons.getGeoSetConstructionOrder().iterator();
    while (it.hasNext()) {
      GeoElement geo = it.next();

      // stop when not visible for current construction step
      if (!geo.isAvailableAtConstructionStep(consStep))
        break;

      view.add(geo);
    }
  }

  final void notifyClearView() {
    for (int i = 0; i < viewCnt; ++i)
      views[i].clearView();
  }

  private void notifyEuclidianViewAlgos() {
    if (macroManager != null)
      macroManager.notifyEuclidianViewAlgos();

    cons.notifyEuclidianViewAlgos();
  }

  final void notifyRemove(GeoElement geo) {
    if (notifyViewsActive)
      for (int i = 0; i < viewCnt; ++i)
        views[i].remove(geo);

    notifyRenameListenerAlgos();
  }

  final void notifyRename(GeoElement geo) {
    if (notifyViewsActive)
      for (int i = 0; i < viewCnt; ++i)
        views[i].rename(geo);

    notifyRenameListenerAlgos();
  }

  private void notifyRenameListenerAlgos() {
    AlgoElement.updateCascadeAlgos(renameListenerAlgos);
  }

  public final void notifyRepaint() {
    if (notifyRepaint)
      for (int i = 0; i < viewCnt; ++i)
        views[i].repaintView();
  }

  final void notifyReset() {
    for (int i = 0; i < viewCnt; ++i)
      views[i].reset();
  }

  protected final void notifyUpdate(GeoElement geo) {
    if (notifyViewsActive)
      for (int i = 0; i < viewCnt; ++i)
        views[i].update(geo);
  }

  final void notifyUpdateAuxiliaryObject(GeoElement geo) {
    if (notifyViewsActive)
      for (int i = 0; i < viewCnt; ++i)
        views[i].updateAuxiliaryObject(geo);
  }

  /**
   * Object from name
   */
  final public GeoElement Object(String label, GeoText text) {
    AlgoObject algo = new AlgoObject(cons, label, text);
    GeoElement ret = algo.getResult();
    return ret;
  }

  /**
   * Line named label through Point P orthogonal to line l
   */
  final public GeoLine OrthogonalLine(String label, GeoPoint P, GeoLine l) {
    AlgoOrthoLinePointLine algo = new AlgoOrthoLinePointLine(cons, label, P, l);
    GeoLine g = algo.getLine();
    return g;
  }

  /**
   * Line named label through Point P orthogonal to vector v
   */
  final public GeoLine OrthogonalLine(String label, GeoPoint P, GeoVector v) {
    AlgoOrthoLinePointVector algo = new AlgoOrthoLinePointVector(cons, label,
        P, v);
    GeoLine g = algo.getLine();
    return g;
  }

  /**
   * orthogonal vector of line g
   */
  final public GeoVector OrthogonalVector(String label, GeoLine g) {
    AlgoOrthoVectorLine algo = new AlgoOrthoVectorLine(cons, label, g);
    GeoVector n = algo.getVector();
    return n;
  }

  /**
   * orthogonal vector of vector v
   */
  final public GeoVector OrthogonalVector(String label, GeoVector v) {
    AlgoOrthoVectorVector algo = new AlgoOrthoVectorVector(cons, label, v);
    GeoVector n = algo.getVector();
    return n;
  }

  /**
   * Osculating Circle of a function f in point A
   */

  final public GeoConic OsculatingCircle(String label, GeoPoint A, GeoFunction f) {

    AlgoOsculatingCircle algo = new AlgoOsculatingCircle(cons, label, A, f);
    GeoConic circle = algo.getCircle();
    return circle;

  }

  /**
   * Osculating Circle of a curve f in point A
   */

  final public GeoConic OsculatingCircleCurve(String label, GeoPoint A,
      GeoCurveCartesian f) {

    AlgoOsculatingCircleCurve algo = new AlgoOsculatingCircleCurve(cons, label,
        A, f);
    GeoConic circle = algo.getCircle();
    return circle;

  }

  /**
   * parabola with focus F and line l
   */
  final public GeoConic Parabola(String label, GeoPoint F, GeoLine l) {
    AlgoParabolaPointLine algo = new AlgoParabolaPointLine(cons, label, F, l);
    GeoConic parabola = algo.getParabola();
    return parabola;
  }

  /**
   * (parabola) parameter of c
   */
  final public GeoNumeric Parameter(String label, GeoConic c) {
    AlgoParabolaParameter algo = new AlgoParabolaParameter(cons, label, c);
    GeoNumeric length = algo.getParameter();
    return length;
  }

  final public GeoNumeric Pascal(String label, NumberValue a, NumberValue b,
      NumberValue c) {
    AlgoPascal algo = new AlgoPascal(cons, label, a, b, c);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * Perimeter named label of GeoPolygon
   */
  final public GeoNumeric Perimeter(String label, GeoPolygon polygon) {
    AlgoPerimeterPoly algo = new AlgoPerimeterPoly(cons, label, polygon);
    return algo.getCircumference();
  }

  /**
   * PMCC[list] Michael Borcherds
   */
  final public GeoNumeric PMCC(String label, GeoList list) {
    AlgoListPMCC algo = new AlgoListPMCC(cons, label, list);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * PMCC[list,list] Michael Borcherds
   */
  final public GeoNumeric PMCC(String label, GeoList listX, GeoList listY) {
    AlgoDoubleListPMCC algo = new AlgoDoubleListPMCC(cons, label, listX, listY);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /** Point label with cartesian coordinates (x,y) */
  final public GeoPoint Point(String label, double x, double y) {
    GeoPoint p = new GeoPoint(cons);
    p.setCoords(x, y, 1.0);
    p.setMode(COORD_CARTESIAN);
    p.setLabel(label); // invokes add()
    return p;
  }

  /** Point label with cartesian coordinates (x,y) */
  final public GeoPoint Point(String label, double x, double y, boolean complex) {
    GeoPoint p = new GeoPoint(cons);
    p.setCoords(x, y, 1.0);
    if (complex) {
      p.setMode(COORD_COMPLEX);
      // we have to reset the visual style as the constructor
      // did not know that this was a complex number
      p.setConstructionDefaults();
    } else
      p.setMode(COORD_CARTESIAN);
    p.setLabel(label); // invokes add()
    return p;
  }

  /** Point P + v */
  final public GeoPoint Point(String label, GeoPoint P, GeoVector v) {
    AlgoPointVector algo = new AlgoPointVector(cons, label, P, v);
    GeoPoint p = algo.getQ();
    return p;
  }

  /** Point anywhere on path with */
  final public GeoPoint Point(String label, Path path) {
    // try (0,0)
    AlgoPointOnPath algo = new AlgoPointOnPath(cons, label, path, 0, 0);
    GeoPoint p = algo.getP();

    // try (1,0)
    if (!p.isDefined()) {
      p.setCoords(1, 0, 1);
      algo.update();
    }

    // try (random(),0)
    if (!p.isDefined()) {
      p.setCoords(Math.random(), 0, 1);
      algo.update();
    }

    return p;
  }

  /** Point on path with cartesian coordinates (x,y) */
  final public GeoPoint Point(String label, Path path, double x, double y) {
    AlgoPointOnPath algo = new AlgoPointOnPath(cons, label, path, x, y);
    GeoPoint p = algo.getP();
    return p;
  }

  /** Point in region */
  final public GeoPoint PointIn(String label, Region region) {
    return PointIn(label, region, 0, 0); // TODO do as for paths
  }

  /** Point in region with cartesian coordinates (x,y) */
  final public GeoPoint PointIn(String label, Region region, double x, double y) {
    AlgoPointInRegion algo = new AlgoPointInRegion(cons, label, region, x, y);
    Application.debug("PointIn - \n x=" + x + "\n y=" + y);
    GeoPoint p = algo.getP();
    return p;
  }

  /**
   * polar line to P relativ to c
   */
  final public GeoLine PolarLine(String label, GeoPoint P, GeoConic c) {
    AlgoPolarLine algo = new AlgoPolarLine(cons, label, c, P);
    GeoLine polar = algo.getLine();
    return polar;
  }

  /**
   * polygon P[0], ..., P[n-1] The labels name the polygon itself and its
   * segments
   */
  final public GeoElement[] Polygon(String[] labels, GeoPoint[] P) {
    AlgoPolygon algo = new AlgoPolygon(cons, labels, P);
    return algo.getOutput();
  }

  /**
   * Tries to expand a function f to a polynomial.
   */
  final public GeoFunction PolynomialFunction(String label, GeoFunction f) {
    AlgoPolynomialFromFunction algo = new AlgoPolynomialFromFunction(cons,
        label, f);
    return algo.getPolynomial();
  }

  /**
   * Fits a polynomial exactly to a list of coordinates Michael Borcherds
   * 2008-01-22
   */
  final public GeoFunction PolynomialFunction(String label, GeoList list) {
    AlgoPolynomialFromCoordinates algo = new AlgoPolynomialFromCoordinates(
        cons, label, list);
    return algo.getPolynomial();
  }

  /**
   * Sets construction step to previous step of construction protocol Note:
   * showOnlyBreakpoints() is important here
   */
  public void previousStep() {
    int step = cons.getStep() - 1;

    if (showOnlyBreakpoints())
      cons.setStep(getPreviousBreakpoint(step));
    else
      cons.setStep(step);
  }

  /**
   * Product[list] Michael Borcherds
   */
  final public GeoNumeric Product(String label, GeoList list) {
    AlgoProduct algo = new AlgoProduct(cons, label, list);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * Returns the projected point of P on line g.
   */
  final public GeoPoint ProjectedPoint(GeoPoint P, GeoLine g) {
    boolean oldMacroMode = cons.isSuppressLabelsActive();
    cons.setSuppressLabelCreation(true);
    GeoLine perp = OrthogonalLine(null, P, g);
    GeoPoint S = IntersectLines(null, perp, g);
    cons.setSuppressLabelCreation(oldMacroMode);
    return S;
  }

  /**
   * Q1[list] lower quartile Michael Borcherds
   */
  final public GeoNumeric Q1(String label, GeoList list) {
    AlgoQ1 algo = new AlgoQ1(cons, label, list);
    GeoNumeric num = algo.getQ1();
    return num;
  }

  /**
   * Q3[list] upper quartile Michael Borcherds
   */
  final public GeoNumeric Q3(String label, GeoList list) {
    AlgoQ3 algo = new AlgoQ3(cons, label, list);
    GeoNumeric num = algo.getQ3();
    return num;
  }

  /**
   * (circle) radius of c
   */
  final public GeoNumeric Radius(String label, GeoConic c) {
    AlgoRadius algo = new AlgoRadius(cons, label, c);
    GeoNumeric length = algo.getRadius();
    return length;
  }

  /**
   * Random[max,min] Michael Borcherds
   */
  final public GeoNumeric Random(String label, NumberValue a, NumberValue b) {
    AlgoRandom algo = new AlgoRandom(cons, label, a, b);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * RandomBinomial[n,p] Michael Borcherds
   */
  final public GeoNumeric RandomBinomial(String label, NumberValue a,
      NumberValue b) {
    AlgoRandomBinomial algo = new AlgoRandomBinomial(cons, label, a, b);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /********************************************************************
   * TRANSFORMATIONS
   ********************************************************************/

  /**
   * RandomNormal[mean,variance] Michael Borcherds
   */
  final public GeoNumeric RandomNormal(String label, NumberValue a,
      NumberValue b) {
    AlgoRandomNormal algo = new AlgoRandomNormal(cons, label, a, b);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * RandomPoisson[lambda] Michael Borcherds
   */
  final public GeoNumeric RandomPoisson(String label, NumberValue a) {
    AlgoRandomPoisson algo = new AlgoRandomPoisson(cons, label, a);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * Ray named label through Points P and Q
   */
  final public GeoRay Ray(String label, GeoPoint P, GeoPoint Q) {
    AlgoJoinPointsRay algo = new AlgoJoinPointsRay(cons, label, P, Q);
    return algo.getRay();
  }

  /**
   * Ray named label through Point P with direction of vector v
   */
  final public GeoRay Ray(String label, GeoPoint P, GeoVector v) {
    AlgoRayPointVector algo = new AlgoRayPointVector(cons, label, P, v);
    return algo.getRay();
  }

  public void redo() {
    if (undoActive) {
      notifyReset();
      cons.redo();
      notifyReset();
    }
  }

  public boolean redoPossible() {
    return undoActive && cons.redoPossible();
  }

  /**
   * Registers an algorithm that needs to be updated when notifyRename(),
   * notifyAdd(), or notifyRemove() is called.
   */
  void registerRenameListenerAlgo(AlgoElement algo) {
    if (renameListenerAlgos == null)
      renameListenerAlgos = new ArrayList<AlgoElement>();

    if (!renameListenerAlgos.contains(algo))
      renameListenerAlgos.add(algo);
  }

  /**
   * Regular polygon with vertices A and B and n total vertices. The labels name
   * the polygon itself, its segments and points
   */
  final public GeoElement[] RegularPolygon(String[] labels, GeoPoint A,
      GeoPoint B, NumberValue n) {
    AlgoPolygonRegular algo = new AlgoPolygonRegular(cons, labels, A, B, n);
    return algo.getOutput();
  }

  /* ******************************
   * Transformations for polygons *****************************
   */

  /**
   * Removes all macros from the kernel.
   */
  public void removeAllMacros() {
    if (macroManager != null) {
      app.removeMacroCommands();
      macroManager.removeAllMacros();
    }
  }

  void removeIntersectionAlgorithm(AlgoIntersect algo) {
    intersectionAlgos.remove(algo);
  }

  /**
   * Removes a macro from the kernel.
   */
  public void removeMacro(Macro macro) {
    if (macroManager != null)
      macroManager.removeMacro(macro);
  }

  /**
   * RemoveUndefined[list] Michael Borcherds
   */
  final public GeoList RemoveUndefined(String label, GeoList list) {
    AlgoRemoveUndefined algo = new AlgoRemoveUndefined(cons, label, list);
    GeoList list2 = algo.getResult();
    return list2;
  }

  final public void resetPrecision() {
    EPSILON = STANDARD_PRECISION;
  }

  public void restoreCurrentUndoInfo() {
    if (undoActive)
      cons.restoreCurrentUndoInfo();
  }

  final public void restorePrintAccuracy() {
    // get previous values from stacks
    useSignificantFigures = useSignificantFiguresList.pop().booleanValue();
    int sigFigures = noOfSignificantFiguresList.pop().intValue();
    int decDigits = noOfDecimalPlacesList.pop().intValue();

    if (useSignificantFigures)
      setPrintFigures(sigFigures);
    else
      setPrintDecimals(decDigits);

    // Application.debug("list size"+noOfSignificantFiguresList.size());
  }

  /**
   * Reverse[list] Michael Borcherds
   */
  final public GeoList Reverse(String label, GeoList list) {
    AlgoReverse algo = new AlgoReverse(cons, label, list);
    GeoList list2 = algo.getResult();
    return list2;
  }

  /**
   * Root of a function f to given start value a (works only if first derivative
   * of f exists)
   */
  final public GeoPoint Root(String label, GeoFunction f, NumberValue a) {
    AlgoRootNewton algo = new AlgoRootNewton(cons, label, f, a);
    GeoPoint p = algo.getRootPoint();
    return p;
  }

  /**
   * Root of a function f in given interval [a, b]
   */
  final public GeoPoint Root(String label, GeoFunction f, NumberValue a,
      NumberValue b) {
    AlgoRootInterval algo = new AlgoRootInterval(cons, label, f, a, b);
    GeoPoint p = algo.getRootPoint();
    return p;
  }

  /**
   * all Roots of polynomial f (works only for polynomials and functions that
   * can be simplified to factors of polynomials, e.g. sqrt(x) to x)
   */
  final public GeoPoint[] Root(String[] labels, GeoFunction f) {
    // allow functions that can be simplified to factors of polynomials
    if (!f.isPolynomialFunction(true))
      return null;

    AlgoRootsPolynomial algo = new AlgoRootsPolynomial(cons, labels, f);
    GeoPoint[] g = algo.getRootPoints();
    return g;
  }

  /**
   * rotates poly by angle phi around (0,0)
   */
  final public GeoElement[] Rotate(String label, GeoPolygon poly,
      NumberValue phi) {
    return transformPoly(label, poly, rotPoints(poly.getPoints(), phi, null));
  }

  /**
   * rotates poly by angle phi around Q
   */
  final public GeoElement[] Rotate(String label, GeoPolygon poly,
      NumberValue phi, GeoPoint Q) {
    return transformPoly(label, poly, rotPoints(poly.getPoints(), phi, Q));
  }
  /**
   * rotate geoRot by angle phi around Q
   */
  final public GeoElement[] Rotate(String label, PointRotateable geoRot,
      NumberValue phi, GeoPoint Q) {
    if (label == null)
      label = transformedGeoLabel(geoRot.toGeoElement());

    if (geoRot.toGeoElement().isLimitedPath())
      // handle segments, rays and arcs separately
      return ((LimitedPath) geoRot).createTransformedObject(
          TRANSFORM_ROTATE_AROUND_POINT, label, Q, null, null, phi);

    // standard case
    AlgoRotatePoint algo = new AlgoRotatePoint(cons, label, geoRot, phi, Q);
    GeoElement[] geos = {algo.getResult()};
    return geos;
  }
  /**
   * rotate geoRot by angle phi around (0,0)
   */
  final public GeoElement[] Rotate(String label, Rotateable geoRot,
      NumberValue phi) {
    if (label == null)
      label = transformedGeoLabel(geoRot.toGeoElement());

    if (geoRot.toGeoElement().isLimitedPath())
      // handle segments, rays and arcs separately
      return ((LimitedPath) geoRot).createTransformedObject(TRANSFORM_ROTATE,
          label, null, null, null, phi);

    // standard case
    AlgoRotate algo = new AlgoRotate(cons, label, geoRot, phi);
    GeoElement[] geos = {algo.getResult()};
    return geos;
  }
  GeoPoint[] rotPoints(GeoPoint[] points, NumberValue phi, GeoPoint Q) {
    // rotate all points
    GeoPoint[] rotPoints = new GeoPoint[points.length];
    for (int i = 0; i < points.length; i++) {
      String pointLabel = transformedGeoLabel(points[i]);
      if (Q == null)
        rotPoints[i] = (GeoPoint) Rotate(pointLabel, points[i], phi)[0];
      else
        rotPoints[i] = (GeoPoint) Rotate(pointLabel, points[i], phi, Q)[0];
    }
    return rotPoints;
  }
  /**
   * Row of geo.
   */
  final public GeoNumeric Row(String label, GeoElement geo) {
    AlgoRow algo = new AlgoRow(cons, label, geo);
    GeoNumeric ret = algo.getResult();
    return ret;
  }
  /**
   * second axis of c
   */
  final public GeoLine SecondAxis(String label, GeoConic c) {
    AlgoAxisSecond algo = new AlgoAxisSecond(cons, label, c);
    GeoLine axis = algo.getAxis();
    return axis;
  }

  /**
   * second axis' length of c
   */
  final public GeoNumeric SecondAxisLength(String label, GeoConic c) {
    AlgoAxisSecondLength algo = new AlgoAxisSecondLength(cons, label, c);
    GeoNumeric length = algo.getLength();
    return length;
  }

  /**
   * LineSegment named label from Point P to Point Q
   */
  final public GeoSegment Segment(String label, GeoPoint P, GeoPoint Q) {
    AlgoJoinPointsSegment algo = new AlgoJoinPointsSegment(cons, label, P, Q);
    GeoSegment s = algo.getSegment();
    return s;
  }

  /**
   * Creates new point B with distance n from A and new segment AB The labels[0]
   * is for the segment, labels[1] for the new point
   */
  final public GeoElement[] Segment(String[] labels, GeoPoint A, NumberValue n) {
    // this is actually a macro
    String pointLabel = null, segmentLabel = null;
    if (labels != null)
      switch (labels.length) {
        case 2 :
          pointLabel = labels[1];

        case 1 :
          segmentLabel = labels[0];

        default :
      }

    // create a circle around A with radius n
    AlgoCirclePointRadius algoCircle = new AlgoCirclePointRadius(cons, A, n);
    cons.removeFromConstructionList(algoCircle);
    // place the new point on the circle
    AlgoPointOnPath algoPoint = new AlgoPointOnPath(cons, pointLabel,
        algoCircle.getCircle(), A.inhomX + n.getDouble(), A.inhomY);

    // return segment and new point
    GeoElement[] ret = {Segment(segmentLabel, A, algoPoint.getP()),
        algoPoint.getP()};
    return ret;
  }

  /**
   * semicircle with midpoint M through point P
   */
  final public GeoConicPart Semicircle(String label, GeoPoint M, GeoPoint P) {
    AlgoSemicircle algo = new AlgoSemicircle(cons, label, M, P);
    return algo.getSemicircle();
  }

  /***********************************
   * CALCULUS
   ***********************************/

  /**
   * Sequence command: Sequence[ <expression>, <number-var>, <from>, <to>,
   * <step> ]
   * 
   * @return array with GeoList object and its list items
   */
  final public GeoElement[] Sequence(String label, GeoElement expression,
      GeoNumeric localVar, NumberValue from, NumberValue to, NumberValue step) {

    AlgoSequence algo = new AlgoSequence(cons, label, expression, localVar,
        from, to, step);
    return algo.getOutput();
  }

  public final void setAllowVisibilitySideEffects(
      boolean allowVisibilitySideEffects) {
    this.allowVisibilitySideEffects = allowVisibilitySideEffects;
  }

  final public void setAngleUnit(int unit) {
    cons.angleUnit = unit;
  }

  final public void setCASPrintForm(int type) {
    casPrintForm = type;

    switch (casPrintForm) {
      case ExpressionNode.STRING_TYPE_MATH_PIPER :
        casPrintFormPI = "Pi";

      case ExpressionNode.STRING_TYPE_JASYMCA :
      case ExpressionNode.STRING_TYPE_GEOGEBRA_XML :
        casPrintFormPI = "pi";

      default :
        casPrintFormPI = PI_STRING;
    }
  }

  public void setConstructionStep(int step) {
    if (cons.getStep() != step) {
      cons.setStep(step);
      app.setUnsaved();
    }
  }

  /**
   * Turns the continuity heuristic on or off. Note: the macro kernel always
   * turns continuity off.
   */
  public void setContinuous(boolean continuous) {
    this.continuous = continuous;
  }

  public void setCoordStyle(int coordStlye) {
    coordStyle = coordStlye;
  }

  /**
   * SetDifferece[list,list] Michael Borcherds
   */
  final public GeoList SetDifference(String label, GeoList list, GeoList list1) {
    AlgoSetDifference algo = new AlgoSetDifference(cons, label, list, list1);
    GeoList list2 = algo.getResult();
    return list2;
  }

  final public void setEpsilon(double epsilon) {
    EPSILON = epsilon;
    getEquationSolver().setEpsilon(epsilon);
  }

  /**
   * Tells this kernel about the bounds and the scales for x-Axis and y-Axis
   * used in EudlidianView. The scale is the number of pixels per unit. (useful
   * for some algorithms like findminimum). All
   */
  final public void setEuclidianViewBounds(double xmin, double xmax,
      double ymin, double ymax, double xscale, double yscale) {
    this.xmin = xmin;
    this.xmax = xmax;
    this.ymin = ymin;
    this.ymax = ymax;
    this.xscale = xscale;
    this.yscale = yscale;

    notifyEuclidianViewAlgos();
  }

  public void setLibraryJavaScript(String str) {
    Application.debug(str);
    libraryJavaScript = str;

    // libraryJavaScript =
    // "function ggbOnInit() {ggbApplet.evalCommand('A=(1,2)');ggbApplet.registerObjectUpdateListener('A','listener');}function listener() {//java.lang.System.out.println('add listener called'); var x = ggbApplet.getXcoord('A');var y = ggbApplet.getYcoord('A');var len = Math.sqrt(x*x + y*y);if (len > 5) { x=x*5/len; y=y*5/len; }ggbApplet.unregisterObjectUpdateListener('A');ggbApplet.setCoords('A',x,y);ggbApplet.registerObjectUpdateListener('A','listener');}";
    // libraryJavaScript =
    // "function ggbOnInit() {ggbApplet.evalCommand('A=(1,2)');}";
  }

  /**
   * Sets the command name of a macro. Note: if the given name is already used
   * nothing is done.
   * 
   * @return if the command name was really set
   */
  public boolean setMacroCommandName(Macro macro, String cmdName) {
    boolean nameUsed = macroManager.getMacro(cmdName) != null;
    if (nameUsed || cmdName == null || cmdName.length() == 0)
      return false;

    macroManager.setMacroCommandName(macro, cmdName);
    return true;
  }

  final public void setMaximumFractionDigits(int digits) {
    // Application.debug(""+digits);
    useSignificantFigures = false;
    nf.setMaximumFractionDigits(digits);
  }

  final public void setMinPrecision() {
    EPSILON = MIN_PRECISION;
  }

  public void setNotifyRepaintActive(boolean flag) {
    if (flag != notifyRepaint) {
      notifyRepaint = flag;
      if (notifyRepaint)
        notifyRepaint();
    }
  }

  public void setNotifyViewsActive(boolean flag) {
    // Application.debug("setNotifyViews: " + flag);

    if (flag != notifyViewsActive) {
      notifyViewsActive = flag;

      if (flag) {
        // Application.debug("Activate VIEWS");
        viewReiniting = true;

        // "attach" views again
        viewCnt = oldViewCnt;

        // add all geos to all views
        Iterator<GeoElement> it = cons.getGeoSetConstructionOrder().iterator();
        while (it.hasNext()) {
          GeoElement geo = it.next();
          notifyAdd(geo);
        }

        /*
         * Object [] geos =
         * getConstruction().getGeoSetConstructionOrder().toArray(); for (int i
         * = 0 ; i < geos.length ; i++) { GeoElement geo = (GeoElement) geos[i];
         * notifyAdd(geo); }
         */

        // app.setMoveMode();

        notifyEuclidianViewAlgos();
        notifyReset();
        viewReiniting = false;
      } else {
        // Application.debug("Deactivate VIEWS");

        // "detach" views
        notifyClearView();
        oldViewCnt = viewCnt;
        viewCnt = 0;
      }
    }
  }

  final public void setPrintDecimals(int decimals) {
    if (decimals >= 0) {
      useSignificantFigures = false;
      nf.setMaximumFractionDigits(decimals);
      PRINT_PRECISION = Math.pow(10, -decimals);
      ROUND_HALF_UP_FACTOR = decimals < 15 ? ROUND_HALF_UP_FACTOR_DEFAULT : 1;
    }
  }

  final public void setPrintFigures(int figures) {
    if (figures >= 0) {
      useSignificantFigures = true;
      sf.setSigDigits(figures);
      sf.setMaxWidth(16); // for scientific notation
      ROUND_HALF_UP_FACTOR = figures < 15 ? ROUND_HALF_UP_FACTOR_DEFAULT : 1;
    }
  }
  /**
   * Sets whether unknown variables should be resolved as GeoDummyVariable
   * objects.
   */
  public final void setResolveVariablesForCASactive(
      boolean resolveUnkownVarsAsDummyGeos) {
    resolveVariablesForCASactive = resolveUnkownVarsAsDummyGeos;
  }

  public void setShowOnlyBreakpoints(boolean flag) {
    cons.setShowOnlyBreakpoints(flag);
  }

  /**
   * Turns silent mode on (true) or off (false). In silent mode, commands can be
   * used to create objects without any side effects, i.e. no labels are
   * created, algorithms are not added to the construction list and the views
   * are not notified about new objects.
   */
  public final void setSilentMode(boolean silentMode) {

    this.silentMode = silentMode;

    // no new labels, no adding to construction list
    cons.setSuppressLabelCreation(silentMode);

    // no notifying of views
    // ggb3D - 2009-07-17
    // removing :
    // notifyViewsActive = !silentMode;
    // (seems not to work with loading files)

    // Application.printStacktrace(""+silentMode);

  }

  final public void setTemporaryPrintDecimals(int decimals) {
    storeTemporaryRoundingInfoInList();
    setPrintDecimals(decimals);
  }

  final public void setTemporaryPrintFigures(int figures) {
    storeTemporaryRoundingInfoInList();
    setPrintFigures(figures);
  }

  public void setTranslateCommandName(boolean b) {
    translateCommandName = b;
  }

  public void setUndoActive(boolean flag) {
    undoActive = flag;
    if (undoActive)
      initUndoInfo();
  }

  public void setUndoActiveNoReInit(boolean flag) {
    undoActive = flag;
  }

  final public boolean showOnlyBreakpoints() {
    return cons.showOnlyBreakpoints();
  }

  /**
   * SigmaXX[list] Michael Borcherds
   */
  final public GeoNumeric SigmaXX(String label, GeoList list) {
    GeoNumeric num;
    GeoElement geo = list.get(0);
    if (geo.isNumberValue()) { // list of numbers
      AlgoSigmaXX algo = new AlgoSigmaXX(cons, label, list);
      num = algo.getResult();
    } else { // (probably) list of points
      AlgoListSigmaXX algo = new AlgoListSigmaXX(cons, label, list);
      num = algo.getResult();
    }
    return num;
  }

  /**
   * SigmaXX[list,list] Michael Borcherds
   */
  final public GeoNumeric SigmaXX(String label, GeoList listX, GeoList listY) {
    AlgoDoubleListSigmaXX algo = new AlgoDoubleListSigmaXX(cons, label, listX,
        listY);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * SigmaXY[list] Michael Borcherds
   */
  final public GeoNumeric SigmaXY(String label, GeoList list) {
    AlgoListSigmaXY algo = new AlgoListSigmaXY(cons, label, list);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * SigmaXY[list,list] Michael Borcherds
   */
  final public GeoNumeric SigmaXY(String label, GeoList listX, GeoList listY) {
    AlgoDoubleListSigmaXY algo = new AlgoDoubleListSigmaXY(cons, label, listX,
        listY);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * Victor Franco Espino 18-04-2007: End new commands
   */

  /***********************************
   * PACKAGE STUFF
   ***********************************/

  /**
   * SigmaYY[list] Michael Borcherds
   */
  final public GeoNumeric SigmaYY(String label, GeoList list) {
    AlgoListSigmaYY algo = new AlgoListSigmaYY(cons, label, list);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * SigmaYY[list,list] Michael Borcherds
   */
  final public GeoNumeric SigmaYY(String label, GeoList listX, GeoList listY) {
    AlgoDoubleListSigmaYY algo = new AlgoDoubleListSigmaYY(cons, label, listX,
        listY);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * Simplify function expression
   * 
   * @author Michael Borcherds 2008-04-04
   */
  final public GeoElement Simplify(String label, GeoFunction func) {
    AlgoSimplify algo = new AlgoSimplify(cons, label, func);
    return algo.getResult();
  }

  /**
   * Slope of line g
   */
  final public GeoNumeric Slope(String label, GeoLine g) {
    AlgoSlope algo = new AlgoSlope(cons, label, g);
    GeoNumeric slope = algo.getSlope();
    return slope;
  }

  /**
   * Sort[list] Michael Borcherds
   */
  final public GeoList Sort(String label, GeoList list) {
    AlgoSort algo = new AlgoSort(cons, label, list);
    GeoList list2 = algo.getResult();
    return list2;
  }

  /**
   * SD[list] Michael Borcherds
   */
  final public GeoNumeric StandardDeviation(String label, GeoList list) {
    AlgoStandardDeviation algo = new AlgoStandardDeviation(cons, label, list);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /*
   * stores information about the current no of decimal places/sig figures used
   * for when it is (temporarily changed) needs to be in a list as it can be
   * nested
   */
  private void storeTemporaryRoundingInfoInList() {
    if (useSignificantFiguresList == null) {
      useSignificantFiguresList = new Stack<Boolean>();
      noOfSignificantFiguresList = new Stack<Integer>();
      noOfDecimalPlacesList = new Stack<Integer>();
    }

    useSignificantFiguresList.push(new Boolean(useSignificantFigures));
    noOfSignificantFiguresList.push(new Integer(sf.getSigDigits()));
    noOfDecimalPlacesList.push(new Integer(nf.getMaximumFractionDigits()));
  }

  public void storeUndoInfo() {
    if (undoActive)
      cons.storeUndoInfo();
  }

  /**
   * Sum[list] Michael Borcherds
   */
  final public GeoElement Sum(String label, GeoList list) {
    AlgoSum algo = new AlgoSum(cons, label, list);
    GeoElement ret = algo.getResult();
    return ret;
  }

  /**
   * Sum[list,n] Michael Borcherds
   */
  final public GeoElement Sum(String label, GeoList list, GeoNumeric n) {
    AlgoSum algo = new AlgoSum(cons, label, list, n);
    GeoElement ret = algo.getResult();
    return ret;
  }

  /**
   * Sum[list of functions] Michael Borcherds
   */
  final public GeoElement SumFunctions(String label, GeoList list) {
    AlgoSumFunctions algo = new AlgoSumFunctions(cons, label, list);
    GeoElement ret = algo.getResult();
    return ret;
  }

  /*
   * // calc acos(x). returns 0 for x > 1 and pi for x < -1 final static double
   * trimmedAcos(double x) { if (Math.abs(x) <= 1.0d) return Math.acos(x); else
   * if (x > 1.0d) return 0.0d; else if (x < -1.0d) return Math.PI; else return
   * Double.NaN; }
   */

  /**
   * Sum[list of functions,n] Michael Borcherds
   */
  final public GeoElement SumFunctions(String label, GeoList list,
      GeoNumeric num) {
    AlgoSumFunctions algo = new AlgoSumFunctions(cons, label, list, num);
    GeoElement ret = algo.getResult();
    return ret;
  }

  /**
   * Sum[list of points] Michael Borcherds
   */
  final public GeoElement SumPoints(String label, GeoList list) {
    AlgoSumPoints algo = new AlgoSumPoints(cons, label, list);
    GeoElement ret = algo.getResult();
    return ret;
  }

  /**
   * Sum[list of points,n] Michael Borcherds
   */
  final public GeoElement SumPoints(String label, GeoList list, GeoNumeric num) {
    AlgoSumPoints algo = new AlgoSumPoints(cons, label, list, num);
    GeoElement ret = algo.getResult();
    return ret;
  }

  /**
   * Sum[list of points] Michael Borcherds
   */
  final public GeoElement SumText(String label, GeoList list) {
    AlgoSumText algo = new AlgoSumText(cons, label, list);
    GeoText ret = algo.getResult();
    return ret;
  }

  /**
   * Sum[list of text,n] Michael Borcherds
   */
  final public GeoElement SumText(String label, GeoList list, GeoNumeric num) {
    AlgoSumText algo = new AlgoSumText(cons, label, list, num);
    GeoText ret = algo.getResult();
    return ret;
  }

  /**
   * SXX[list] Michael Borcherds
   */
  final public GeoNumeric SXX(String label, GeoList list) {
    GeoNumeric num;
    GeoElement geo = list.get(0);
    if (geo.isNumberValue()) { // list of numbers
      AlgoSXX algo = new AlgoSXX(cons, label, list);
      num = algo.getResult();
    } else { // (probably) list of points
      AlgoListSXX algo = new AlgoListSXX(cons, label, list);
      num = algo.getResult();
    }
    return num;
  }
  /**
   * SXX[list,list] Michael Borcherds
   */
  final public GeoNumeric SXX(String label, GeoList listX, GeoList listY) {
    AlgoDoubleListSXX algo = new AlgoDoubleListSXX(cons, label, listX, listY);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * SXY[list] Michael Borcherds
   */
  final public GeoNumeric SXY(String label, GeoList list) {
    AlgoListSXY algo = new AlgoListSXY(cons, label, list);
    GeoNumeric num = algo.getResult();
    return num;
  }
  /**
   * SXY[list,list] Michael Borcherds
   */
  final public GeoNumeric SXY(String label, GeoList listX, GeoList listY) {
    AlgoDoubleListSXY algo = new AlgoDoubleListSXY(cons, label, listX, listY);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /**
   * SYY[list] Michael Borcherds
   */
  final public GeoNumeric SYY(String label, GeoList list) {
    AlgoListSYY algo = new AlgoListSYY(cons, label, list);
    GeoNumeric num = algo.getResult();
    return num;
  }
  /**
   * Table[list] Michael Borcherds
   */
  final public GeoText TableText(String label, GeoList list, GeoText args) {
    AlgoTableText algo = new AlgoTableText(cons, label, list, args);
    GeoText text = algo.getResult();
    return text;
  }

  /**
   * Take[list,m,n] Michael Borcherds
   */
  final public GeoList Take(String label, GeoList list, GeoNumeric m,
      GeoNumeric n) {
    AlgoTake algo = new AlgoTake(cons, label, list, m, n);
    GeoList list2 = algo.getResult();
    return list2;
  }
  /**
   * tangent to Curve f in point P: (b'(t), -a'(t), a'(t)*b(t)-a(t)*b'(t))
   */
  final public GeoLine Tangent(String label, GeoPoint P, GeoCurveCartesian f) {
    AlgoTangentCurve algo = new AlgoTangentCurve(cons, label, P, f);
    GeoLine t = algo.getTangent();
    t.setToExplicit();
    t.update();
    notifyUpdate(t);
    return t;
  }

  /**
   * tangent to f in x = x(P)
   */
  final public GeoLine Tangent(String label, GeoPoint P, GeoFunction f) {
    AlgoTangentFunctionPoint algo = new AlgoTangentFunctionPoint(cons, label,
        P, f);
    GeoLine t = algo.getTangent();
    t.setToExplicit();
    t.update();
    notifyUpdate(t);
    return t;
  }
  /**
   * tangent to f in x = a
   */
  final public GeoLine Tangent(String label, NumberValue a, GeoFunction f) {
    AlgoTangentFunctionNumber algo = new AlgoTangentFunctionNumber(cons, label,
        a, f);
    GeoLine t = algo.getTangent();
    t.setToExplicit();
    t.update();
    notifyUpdate(t);
    return t;
  }

  /*
   * final private String formatAbs(double x) { if (isZero(x)) return "0"; else
   * return formatNF(Math.abs(x)); }
   */

  /**
   * tangents to c parallel to g
   */
  final public GeoLine[] Tangent(String[] labels, GeoLine g, GeoConic c) {
    AlgoTangentLine algo = new AlgoTangentLine(cons, labels, g, c);
    GeoLine[] tangents = algo.getTangents();
    return tangents;
  }

  /**
   * tangents to c through P
   */
  final public GeoLine[] Tangent(String[] labels, GeoPoint P, GeoConic c) {
    AlgoTangentPoint algo = new AlgoTangentPoint(cons, labels, P, c);
    GeoLine[] tangents = algo.getTangents();
    return tangents;
  }

  /**
   * Taylor series of function f about point x=a of order n
   */
  final public GeoFunction TaylorSeries(String label, GeoFunction f,
      NumberValue a, NumberValue n) {

    AlgoTaylorSeries algo = new AlgoTaylorSeries(cons, label, f, a, n);
    return algo.getPolynomial();
  }

  /**
   * TDistribution[degrees of freedom,x] Michael Borcherds
   */
  final public GeoNumeric TDistribution(String label, NumberValue a,
      NumberValue b) {
    AlgoTDistribution algo = new AlgoTDistribution(cons, label, a, b);
    GeoNumeric num = algo.getResult();
    return num;
  }
  /**
   * Text of geo.
   */
  final public GeoText Text(String label, GeoElement geo) {
    AlgoText algo = new AlgoText(cons, label, geo);
    GeoText t = algo.getGeoText();
    return t;
  }

  /**
   * Text of geo.
   */
  final public GeoText Text(String label, GeoElement geo,
      GeoBoolean substituteVars) {
    AlgoText algo = new AlgoText(cons, label, geo, substituteVars);
    GeoText t = algo.getGeoText();
    return t;
  }
  /**
   * Text of geo.
   */
  final public GeoText Text(String label, GeoElement geo, GeoPoint p) {
    AlgoText algo = new AlgoText(cons, label, geo, p);
    GeoText t = algo.getGeoText();
    return t;
  }

  /**
   * Text of geo.
   */
  final public GeoText Text(String label, GeoElement geo, GeoPoint p,
      GeoBoolean substituteVars) {
    AlgoText algo = new AlgoText(cons, label, geo, p, substituteVars);
    GeoText t = algo.getGeoText();
    return t;
  }
  final public GeoText Text(String label, String text) {
    GeoText t = new GeoText(cons);
    t.setTextString(text);
    t.setLabel(label);
    return t;
  }
  /**
   * ToNumbers
   */
  final public GeoList TextToUnicode(String label, GeoText geo) {
    AlgoTextToUnicode algo = new AlgoTextToUnicode(cons, label, geo);
    GeoList ret = algo.getResult();
    return ret;
  }

  GeoPoint[] transformPoints(int type, GeoPoint[] points, GeoPoint Q,
      GeoLine l, GeoVector vec, NumberValue n) {
    GeoPoint[] result = null;

    switch (type) {
      case TRANSFORM_TRANSLATE :
        result = translatePoints(points, vec);
        break;

      case TRANSFORM_MIRROR_AT_POINT :
        result = mirrorPoints(points, Q, null);
        break;

      case TRANSFORM_MIRROR_AT_LINE :
        result = mirrorPoints(points, null, l);
        break;

      case TRANSFORM_ROTATE :
        result = rotPoints(points, n, null);
        break;

      case TRANSFORM_ROTATE_AROUND_POINT :
        result = rotPoints(points, n, Q);
        break;

      case TRANSFORM_DILATE :
        result = dilatePoints(points, n, Q);
        break;

      default :
        return null;
    }

    // use visibility of points for transformed points
    for (int i = 0; i < points.length; i++) {
      result[i].setEuclidianVisible(points[i].isSetEuclidianVisible());
      notifyUpdate(result[i]);
    }
    return result;
  }
  private GeoElement[] transformPoly(String label, GeoPolygon oldPoly,
      GeoPoint[] transformedPoints) {
    // get label for polygon
    String[] polyLabel = null;
    if (label == null) {
      if (oldPoly.isLabelSet()) {
        polyLabel = new String[1];
        polyLabel[0] = transformedGeoLabel(oldPoly);
      }
    } else {
      polyLabel = new String[1];
      polyLabel[0] = label;
    }

    // use visibility of points for transformed points
    GeoPoint[] oldPoints = oldPoly.getPoints();
    for (int i = 0; i < oldPoints.length; i++) {
      transformedPoints[i].setEuclidianVisible(oldPoints[i]
          .isSetEuclidianVisible());
      notifyUpdate(transformedPoints[i]);
    }

    // build the polygon from the transformed points
    return Polygon(polyLabel, transformedPoints);
  }

  /**
   * translate poly by vector v
   */
  final public GeoElement[] Translate(String label, GeoPolygon poly, GeoVector v) {
    return transformPoly(label, poly, translatePoints(poly.getPoints(), v));
  }

  /**
   * translates vector v to point A. The resulting vector is equal to v and has
   * A as startPoint
   */
  final public GeoVector Translate(String label, GeoVector v, GeoPoint A) {
    AlgoTranslateVector algo = new AlgoTranslateVector(cons, label, v, A);
    GeoVector vec = algo.getTranslatedVector();
    return vec;
  }

  /**
   * translate geoTrans by vector v
   */
  final public GeoElement[] Translate(String label, Translateable geoTrans,
      GeoVector v) {

    if (label == null)
      label = transformedGeoLabel(geoTrans.toGeoElement());

    if (geoTrans.toGeoElement().isLimitedPath())
      // handle segments, rays and arcs separately
      return ((LimitedPath) geoTrans).createTransformedObject(
          TRANSFORM_TRANSLATE, label, null, null, v, null);

    // standard case
    AlgoTranslate algo = new AlgoTranslate(cons, label, geoTrans, v);
    GeoElement[] geos = {algo.getResult()};
    return geos;
  }

  GeoPoint[] translatePoints(GeoPoint[] points, GeoVector v) {
    // rotate all points
    GeoPoint[] newPoints = new GeoPoint[points.length];
    for (int i = 0; i < points.length; i++)
      newPoints[i] = (GeoPoint) Translate(transformedGeoLabel(points[i]),
          points[i], v)[0];
    return newPoints;
  }

  /**
   * Transpose[matrix] Michael Borcherds
   */
  final public GeoList Transpose(String label, GeoList list) {
    AlgoTranspose algo = new AlgoTranspose(cons, label, list);
    GeoList list2 = algo.getResult();
    return list2;
  }

  /**
   * TrapezoidalSum of function f
   */
  final public GeoNumeric TrapezoidalSum(String label, GeoFunction f,
      NumberValue a, NumberValue b, NumberValue n) {
    AlgoSumTrapezoidal algo = new AlgoSumTrapezoidal(cons, label, f, a, b, n);
    GeoNumeric sum = algo.getSum();
    return sum;
  }

  /*******************************************************
   * SAVING
   *******************************************************/

  /**
   * all Turning points of function f (works only for polynomials)
   */
  final public GeoPoint[] TurningPoint(String[] labels, GeoFunction f) {
    // check if this is a polynomial at the moment
    if (!f.isPolynomialFunction(true))
      return null;

    AlgoTurningPointPolynomial algo = new AlgoTurningPointPolynomial(cons,
        labels, f);
    GeoPoint[] g = algo.getRootPoints();
    return g;
  }

  final public void udpateNeedToShowAnimationButton() {
    if (animationManager != null)
      animationManager.udpateNeedToShowAnimationButton();

  }
  public void undo() {
    if (undoActive) {
      notifyReset();
      cons.undo();
      notifyReset();
    }
  }

  public boolean undoPossible() {
    return undoActive && cons.undoPossible();
  }

  /**
   * ToText(number)
   */
  final public GeoText UnicodeToLetter(String label, NumberValue a) {
    AlgoUnicodeToLetter algo = new AlgoUnicodeToLetter(cons, label, a);
    GeoText text = algo.getResult();
    return text;
  }

  /**
   * ToText(list)
   */
  final public GeoText UnicodeToText(String label, GeoList geo) {
    AlgoUnicodeToText algo = new AlgoUnicodeToText(cons, label, geo);
    GeoText ret = algo.getResult();
    return ret;
  }

  /**
   * Union[list,list] Michael Borcherds
   */
  final public GeoList Union(String label, GeoList list, GeoList list1) {
    AlgoUnion algo = new AlgoUnion(cons, label, list, list1);
    GeoList list2 = algo.getResult();
    return list2;
  }

  /**
   * unit orthogonal vector of line g
   */
  final public GeoVector UnitOrthogonalVector(String label, GeoLine g) {
    AlgoUnitOrthoVectorLine algo = new AlgoUnitOrthoVectorLine(cons, label, g);
    GeoVector n = algo.getVector();
    return n;
  }

  /**
   * unit orthogonal vector of vector v
   */
  final public GeoVector UnitOrthogonalVector(String label, GeoVector v) {
    AlgoUnitOrthoVectorVector algo = new AlgoUnitOrthoVectorVector(cons, label,
        v);
    GeoVector n = algo.getVector();
    return n;
  }

  /**
   * unit vector of line g
   */
  final public GeoVector UnitVector(String label, GeoLine g) {
    AlgoUnitVectorLine algo = new AlgoUnitVectorLine(cons, label, g);
    GeoVector v = algo.getVector();
    return v;
  }

  /**
   * unit vector of vector v
   */
  final public GeoVector UnitVector(String label, GeoVector v) {
    AlgoUnitVectorVector algo = new AlgoUnitVectorVector(cons, label, v);
    GeoVector u = algo.getVector();
    return u;
  }

  void unregisterRenameListenerAlgo(AlgoElement algo) {
    if (renameListenerAlgos != null)
      renameListenerAlgos.remove(algo);
  }

  public void updateConstruction() {
    cons.updateConstruction();
    notifyRepaint();
  }

  public void updateLocalAxesNames() {
    cons.updateLocalAxesNames();
  }

  /**
   * UpperSum of function f
   */
  final public GeoNumeric UpperSum(String label, GeoFunction f, NumberValue a,
      NumberValue b, NumberValue n) {
    AlgoSumUpper algo = new AlgoSumUpper(cons, label, f, a, b, n);
    GeoNumeric sum = algo.getSum();
    return sum;
  }

  /**
   * Creates a new algorithm that uses the given macro.
   * 
   * @return output of macro algorithm
   */
  final public GeoElement[] useMacro(String[] labels, Macro macro,
      GeoElement[] input) {
    try {
      AlgoMacro algo = new AlgoMacro(cons, labels, macro, input);
      return algo.getOutput();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Variance[list] Michael Borcherds
   */
  final public GeoNumeric Variance(String label, GeoList list) {
    AlgoVariance algo = new AlgoVariance(cons, label, list);
    GeoNumeric num = algo.getResult();
    return num;
  }

  /** Vector label with cartesian coordinates (x,y) */
  final public GeoVector Vector(String label, double x, double y) {
    GeoVector v = new GeoVector(cons);
    v.setCoords(x, y, 0.0);
    v.setMode(COORD_CARTESIAN);
    v.setLabel(label); // invokes add()
    return v;
  }

  /**
   * Vector (0,0) to P
   */
  final public GeoVector Vector(String label, GeoPoint P) {
    AlgoVectorPoint algo = new AlgoVectorPoint(cons, label, P);
    GeoVector v = algo.getVector();
    v.setEuclidianVisible(true);
    v.update();
    notifyUpdate(v);
    return v;
  }

  /**
   * Vector named label from Point P to Q
   */
  final public GeoVector Vector(String label, GeoPoint P, GeoPoint Q) {
    AlgoVector algo = new AlgoVector(cons, label, P, Q);
    GeoVector v = algo.getVector();
    v.setEuclidianVisible(true);
    v.update();
    notifyUpdate(v);
    return v;
  }

  /**
   * Vertices of conic. returns 4 GeoPoints
   */
  final public GeoPoint[] Vertex(String[] labels, GeoConic c) {
    AlgoVertex algo = new AlgoVertex(cons, labels, c);
    GeoPoint[] vertex = algo.getVertex();
    return vertex;
  }

  final public GeoNumeric Weibull(String label, NumberValue a, NumberValue b,
      NumberValue c) {
    AlgoWeibull algo = new AlgoWeibull(cons, label, a, b, c);
    GeoNumeric num = algo.getResult();
    return num;
  }

  // public String getLibraryJavaScriptXML() {
  // return Util.encodeXML(libraryJavaScript);
  // }

  final public GeoNumeric Zipf(String label, NumberValue a, NumberValue b,
      NumberValue c) {
    AlgoZipf algo = new AlgoZipf(cons, label, a, b, c);
    GeoNumeric num = algo.getResult();
    return num;
  }

}