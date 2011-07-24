/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;
import geogebra.util.Util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Vector;

public class GeoImage extends GeoElement
    implements
      Locateable,
      AbsoluteScreenLocateable,
      PointRotateable,
      Mirrorable,
      Translateable,
      Dilateable {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;
  private String fileName = ""; // image file
  private final GeoPoint[] corners; // corners of the image
  private BufferedImage image;
  private int pixelWidth, pixelHeight;
  private boolean inBackground;
  private boolean hasAbsoluteLocation;

  // for absolute screen location
  private int screenX, screenY;
  boolean hasAbsoluteScreenLocation = false;

  // corner points for transformations
  private GeoPoint[] tempPoints;

  private static Vector<GeoImage> instances = new Vector<GeoImage>();

  /**
   * Reloads images from internal image cache
   */
  public static void updateInstances() {
    for (int i = instances.size() - 1; i >= 0; i--) {
      GeoImage geo = instances.get(i);
      geo.setFileName(geo.fileName);
      geo.updateCascade();
    }
  }

  // coords is the 2d result array for (x, y); n is 0, 1, or 2
  private final double[] tempCoords = new double[2];

  public GeoImage(Construction c) {
    super(c);
    setAlphaValue(1f);
    setAlgebraVisible(false); // don't show in algebra view

    // three corners of the image: first, second and fourth
    corners = new GeoPoint[3];

    instances.add(this);
  }

  public GeoImage(Construction c, String label, String fileName) {
    this(c);
    setFileName(fileName);
    setLabel(label);
  }

  /**
   * Copy constructor
   */
  public GeoImage(GeoImage img) {
    this(img.cons);
    set(img);
  }

  /**
   * Calculates the n-th corner point of this image in real world coordinates.
   * Note: if this image has an absolute screen location, result is set to
   * undefined.
   * 
   * @param result
   *          : here the result is stored.
   * @param number
   *          of the corner point 1, 2, 3 or 4)
   */
  public void calculateCornerPoint(GeoPoint result, int n) {
    if (hasAbsoluteScreenLocation) {
      result.setUndefined();
      return;
    }

    if (corners[0] == null)
      initTempPoints();

    switch (n) {
      case 1 : // get A
        result.setCoords(corners[0]);
        break;

      case 2 : // get B
        getInternalCornerPointCoords(tempCoords, 1);
        result.setCoords(tempCoords[0], tempCoords[1], 1.0);
        break;

      case 3 : // get C
        double[] b = new double[2];
        double[] d = new double[2];
        getInternalCornerPointCoords(b, 1);
        getInternalCornerPointCoords(d, 2);
        result.setCoords(d[0] + b[0] - corners[0].inhomX, d[1] + b[1]
            - corners[0].inhomY, 1.0);
        break;

      case 4 : // get D
        getInternalCornerPointCoords(tempCoords, 2);
        result.setCoords(tempCoords[0], tempCoords[1], 1.0);
        break;

      default :
        result.setUndefined();
    }
  }

  @Override
  public GeoElement copy() {
    return new GeoImage(this);
  }

  public void dilate(NumberValue r, GeoPoint S) {
    if (!initTransformPoints())
      return;

    // calculate the new corner points
    for (int i = 0; i < corners.length; i++) {
      tempPoints[i].dilate(r, S);
      corners[i] = tempPoints[i];
    }
  }

  @Override
  protected void doRemove() {
    instances.remove(this);

    // remove background image
    if (inBackground) {
      inBackground = false;
      notifyUpdate();
    }

    super.doRemove();
    for (GeoPoint corner : corners)
      // tell corner
      if (corner != null)
        corner.unregisterLocateable(this);
  }

  public int getAbsoluteScreenLocX() {
    return screenX;
  }

  public int getAbsoluteScreenLocY() {
    return screenY;
  }

  @Override
  protected String getClassName() {
    return "GeoImage";
  }

  final public GeoPoint getCorner(int number) {
    return corners[number];
  }

  private String getCornerPointXML(int number) {
    StringBuffer sb = new StringBuffer();
    sb.append("\t<startPoint number=\"");
    sb.append(number);
    sb.append("\"");

    if (corners[number].isAbsoluteStartPoint()) {
      sb.append(" x=\"" + corners[number].x + "\"");
      sb.append(" y=\"" + corners[number].y + "\"");
      sb.append(" z=\"" + corners[number].z + "\"");
    } else {
      sb.append(" exp=\"");
      boolean oldValue = kernel.isTranslateCommandName();
      kernel.setTranslateCommandName(false);
      sb.append(Util.encodeXML(corners[number].getLabel()));
      kernel.setTranslateCommandName(oldValue);
      sb.append("\"");
    }
    sb.append("/>\n");
    return sb.toString();
  }

  public String getFileName() {
    return fileName;
  }

  @Override
  public int getGeoClassType() {
    return GEO_CLASS_IMAGE;
  }

  final public BufferedImage getImage() {
    return image;
  }

  private void getInternalCornerPointCoords(double[] coords, int n) {
    GeoPoint A = corners[0];
    GeoPoint B = corners[1];
    GeoPoint D = corners[2];

    double xscale = kernel.getXscale();
    double yscale = kernel.getYscale();
    double width = pixelWidth;
    double height = pixelHeight;

    // different scales: change height
    if (xscale != yscale)
      height = height * yscale / xscale;

    switch (n) {
      case 0 : // get A
        coords[0] = A.inhomX;
        coords[1] = A.inhomY;
        break;

      case 1 : // get B
        if (B != null) {
          coords[0] = B.inhomX;
          coords[1] = B.inhomY;
        } else if (D == null) {
          // B and D are not defined
          coords[0] = A.inhomX + width / xscale;
          coords[1] = A.inhomY;
        } else {
          // D is defined, B isn't
          double nx = D.inhomY - A.inhomY;
          double ny = A.inhomX - D.inhomX;
          double factor = width / height;
          coords[0] = A.inhomX + factor * nx;
          coords[1] = A.inhomY + factor * ny;
        }
        break;

      case 2 : // D
        if (D != null) {
          coords[0] = D.inhomX;
          coords[1] = D.inhomY;
        } else if (B == null) {
          // B and D are not defined
          coords[0] = A.inhomX;
          coords[1] = A.inhomY + height / yscale;
        } else {
          // B is defined, D isn't
          double nx = A.inhomY - B.inhomY;
          double ny = B.inhomX - A.inhomX;
          double factor = height / width;
          coords[0] = A.inhomX + factor * nx;
          coords[1] = A.inhomY + factor * ny;
        }
        break;

      default :
        coords[0] = Double.NaN;
        coords[1] = Double.NaN;
    }
  }

  public int getMode() {
    return 0;
  }

  public double getRealWorldLocX() {
    if (corners[0] == null)
      return 0;
    else
      return corners[0].inhomX;
  }

  public double getRealWorldLocY() {
    if (corners[0] == null)
      return 0;
    else
      return corners[0].inhomY;
  }

  public GeoPoint getStartPoint() {
    return corners[0];
  }

  public GeoPoint[] getStartPoints() {
    return corners;
  }

  @Override
  protected String getTypeString() {
    return "Image";
  }

  private String getXMLabsScreenLoc() {
    StringBuffer sb = new StringBuffer();

    sb.append("\t<absoluteScreenLocation x=\"");
    sb.append(screenX);
    sb.append("\" y=\"");
    sb.append(screenY);
    sb.append("\"/>");
    return sb.toString();
  }

  /**
   * returns all class-specific xml tags for getXML
   */
  @Override
  protected String getXMLtags() {
    StringBuffer sb = new StringBuffer();

    // name of image file
    sb.append("\t<file name=\"");
    // Michael Borcherds 2007-12-10 this line restored (not needed now MD5 code
    // put in the correct place)
    sb.append(fileName);
    sb.append("\"/>\n");

    // name of image file
    sb.append("\t<inBackground val=\"");
    sb.append(inBackground);
    sb.append("\"/>\n");

    // locateion of image
    if (hasAbsoluteScreenLocation)
      sb.append(getXMLabsScreenLoc());
    else
      // store location of corners
      for (int i = 0; i < corners.length; i++)
        if (corners[i] != null)
          sb.append(getCornerPointXML(i));

    // sb.append(getXMLvisualTags());
    // sb.append(getBreakpointXML());
    sb.append(super.getXMLtags());

    return sb.toString();
  }

  final public boolean hasAbsoluteLocation() {
    return hasAbsoluteLocation;
  }

  /**
   * Sets the startpoint without performing any checks. This is needed for
   * macros.
   */
  public void initStartPoint(GeoPoint p, int number) {
    corners[number] = p;
  }

  private void initTempPoints() {
    if (tempPoints == null) {
      // temp corner points for transformations and absolute location
      tempPoints = new GeoPoint[3];
      for (int i = 0; i < tempPoints.length; i++)
        tempPoints[i] = new GeoPoint(cons);
    }

    if (corners[0] == null)
      corners[0] = tempPoints[0];
  }

  private boolean initTransformPoints() {
    if (hasAbsoluteScreenLocation || !hasAbsoluteLocation)
      return false;

    initTempPoints();
    calculateCornerPoint(tempPoints[0], 1);
    calculateCornerPoint(tempPoints[1], 2);
    calculateCornerPoint(tempPoints[2], 4);
    return true;
  }

  public boolean isAbsoluteScreenLocActive() {
    return hasAbsoluteScreenLocation;
  }

  @Override
  public boolean isAbsoluteScreenLocateable() {
    return isIndependent();
  }

  public boolean isAlwaysFixed() {
    return false;
  }

  @Override
  final public boolean isDefined() {
    for (int i = 0; i < corners.length; i++)
      if (corners[i] != null && !corners[i].isDefined())
        return false;
    return true;
  }

  // Michael Borcherds 2008-04-30
  @Override
  final public boolean isEqual(GeoElement geo) {
    // return false if it's a different type
    if (!geo.isGeoImage())
      return false;

    // check sizes
    if (((GeoImage) geo).pixelWidth != pixelWidth)
      return false;
    if (((GeoImage) geo).pixelHeight != pixelHeight)
      return false;

    String md5A = fileName.substring(0, fileName.indexOf(File.separator));
    String md5B = ((GeoImage) geo).fileName.substring(0,
        ((GeoImage) geo).fileName.indexOf(File.separator));
    // MD5 checksums equal, so images almost certainly identical
    if (md5A.equals(md5B))
      return true;
    return false;
  }

  @Override
  public boolean isFillable() {
    return true;
  }

  /**
   * Returns wheter this image can be fixed.
   */
  @Override
  public boolean isFixable() {
    return (hasAbsoluteScreenLocation || hasAbsoluteLocation)
        && isIndependent();
  }

  @Override
  public boolean isGeoImage() {
    return true;
  }

  final public boolean isInBackground() {
    return inBackground;
  }

  /**
   * Returns whether this image can be moved in Euclidian View.
   */
  @Override
  final public boolean isMoveable() {
    return (hasAbsoluteScreenLocation || hasAbsoluteLocation) && isChangeable();
  }

  @Override
  public boolean isNumberValue() {
    return false;
  }

  @Override
  public boolean isPolynomialInstance() {
    return false;
  }

  /**
   * Returns whether this image can be rotated in Euclidian View.
   */
  @Override
  final public boolean isRotateMoveable() {
    return !hasAbsoluteScreenLocation && hasAbsoluteLocation && isChangeable();
  }

  @Override
  public boolean isTextValue() {
    return false;
  }

  @Override
  final public boolean isTranslateable() {
    return true;
  }

  public boolean isVector3DValue() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isVectorValue() {
    return false;
  }

  public void mirror(GeoLine g) {
    if (!initTransformPoints())
      return;

    // calculate the new corner points
    for (int i = 0; i < corners.length; i++) {
      tempPoints[i].mirror(g);
      corners[i] = tempPoints[i];
    }
  }

  public void mirror(GeoPoint Q) {
    if (!initTransformPoints())
      return;

    // calculate the new corner points
    for (int i = 0; i < corners.length; i++) {
      tempPoints[i].mirror(Q);
      corners[i] = tempPoints[i];
    }
  }

  public void removeStartPoint(GeoPoint p) {
    for (int i = 0; i < corners.length; i++)
      if (corners[i] == p)
        setCorner(null, i);
  }

  /**
   * rotate this image by angle phi around (0,0)
   */
  final public void rotate(NumberValue phiValue) {
    if (!initTransformPoints())
      return;

    // calculate the new corner points
    for (int i = 0; i < corners.length; i++) {
      tempPoints[i].rotate(phiValue);
      corners[i] = tempPoints[i];
    }
  }

  /**
   * rotate this image by angle phi around Q
   */
  final public void rotate(NumberValue phiValue, GeoPoint Q) {
    if (!initTransformPoints())
      return;

    // calculate the new corner points
    for (int i = 0; i < corners.length; i++) {
      tempPoints[i].rotate(phiValue, Q);
      corners[i] = tempPoints[i];
    }
  }

  @Override
  public void set(GeoElement geo) {
    GeoImage img = (GeoImage) geo;
    setFileName(img.fileName);

    // macro output: don't set corners
    if (cons != geo.cons && isAlgoMacroOutput())
      return;

    // location settings
    hasAbsoluteScreenLocation = img.hasAbsoluteScreenLocation;

    if (hasAbsoluteScreenLocation) {
      screenX = img.screenX;
      screenY = img.screenY;
    } else {
      hasAbsoluteLocation = true;
      for (int i = 0; i < corners.length; i++)
        if (img.corners[i] == null)
          corners[i] = null;
        else {
          initTempPoints();

          tempPoints[i].setCoords(img.corners[i]);
          corners[i] = tempPoints[i];
        }
    }
  }

  public void setAbsoluteScreenLoc(int x, int y) {
    screenX = x;
    screenY = y;
  }

  public void setAbsoluteScreenLocActive(boolean flag) {
    hasAbsoluteScreenLocation = flag;
    if (flag) {
      // remove startpoints
      for (int i = 0; i < 3; i++)
        if (corners[i] != null)
          corners[i].unregisterLocateable(this);
      corners[1] = null;
      corners[2] = null;
    }
  }

  /**
   * Sets a corner of this image.
   * 
   * @param p
   * @param number
   *          : 0, 1 or 2 (first, second and fourth corner)
   */
  public void setCorner(GeoPoint p, int number) {
    // macro output uses initStartPoint() only
    if (isAlgoMacroOutput())
      return;

    if (corners[0] == null && number > 0)
      return;

    // check for circular definition
    if (isParentOf(p))
      // throw new CircularDefinitionException();
      return;

    // set new location
    if (p == null) {
      // remove old dependencies
      if (corners[number] != null)
        corners[number].unregisterLocateable(this);

      // copy old first corner as absolute position
      if (number == 0 && corners[0] != null) {
        GeoPoint temp = new GeoPoint(cons);
        temp.setCoords(corners[0]);
        corners[0] = temp;
      } else
        corners[number] = null;
    } else {
      // check if this point is already available
      for (GeoPoint corner : corners)
        if (p == corner)
          return;

      // remove old dependencies
      if (corners[number] != null)
        corners[number].unregisterLocateable(this);

      corners[number] = p;
      // add new dependencies
      corners[number].registerLocateable(this);
    }

    // absolute screen position should be deactivated
    setAbsoluteScreenLocActive(false);
    updateHasAbsoluteLocation();
  }

  /**
   * Tries to load the image using the given fileName.
   * 
   * @param fileName
   */
  public void setFileName(String fileName) {
    if (this.fileName.equals(fileName))
      return;

    this.fileName = fileName;

    image = app.getExternalImage(fileName);
    if (image != null) {
      pixelWidth = image.getWidth();
      pixelHeight = image.getHeight();
    } else {
      pixelWidth = 0;
      pixelHeight = 0;
    }
    // Michael Borcherds 2007-12-10 MD5 code moved to Application.java
  }

  public void setInBackground(boolean flag) {
    inBackground = flag;
  }

  /* **************************************
   * Transformations *************************************
   */

  public void setMode(int mode) {
  }

  public void setRealWorldLoc(double x, double y) {
    GeoPoint loc = getStartPoint();
    if (loc == null) {
      loc = new GeoPoint(cons);
      setCorner(loc, 0);
    }
    loc.setCoords(x, y, 1.0);
  }
  public void setStartPoint(GeoPoint p) throws CircularDefinitionException {
    setCorner(p, 0);
  }

  public void setStartPoint(GeoPoint p, int number)
      throws CircularDefinitionException {
    setCorner(p, number);
  }

  /**
   * doesn't do anything
   */
  @Override
  public void setUndefined() {
  }

  @Override
  public void setVisualStyle(GeoElement geo) {
    super.setVisualStyle(geo);

    if (geo.isGeoImage())
      inBackground = ((GeoImage) geo).inBackground;
  }

  public void setWaitForStartPoint() {
    // this can be ignored for an image
    // as the position of its startpoint
    // is irrelevant for the rest of the construction
  }

  @Override
  public boolean showInAlgebraView() {
    return false;
  }

  @Override
  protected boolean showInEuclidianView() {
    return image != null && isDefined();
  }

  @Override
  public boolean showToolTipText() {
    return !inBackground;
  }

  @Override
  public String toString() {
    return label;
  }

  @Override
  public String toValueString() {
    return toString();
  }

  public void translate(GeoVector v) {
    if (!initTransformPoints())
      return;

    // calculate the new corner points
    for (int i = 0; i < corners.length; i++) {
      tempPoints[i].translate(v);
      corners[i] = tempPoints[i];
    }
  }

  /**
   * Sets hasAbsoluteLocation flag to true iff all corners are absolute start
   * points (i.e. independent and unlabeled).
   */
  private void updateHasAbsoluteLocation() {
    hasAbsoluteLocation = true;

    for (int i = 0; i < corners.length; i++)
      if (!(corners[i] == null || corners[i].isAbsoluteStartPoint())) {
        hasAbsoluteLocation = false;
        return;
      }
  }

}
