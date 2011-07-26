/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * 
 *
 *  
 */

package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;

/**
 * 
 * @author ggb3D
 * @version
 */
class AlgoTo2D extends AlgoElement3D {

  private static final long serialVersionUID = 1L;
  private final GeoElement3D in; // input
  private GeoElement out; // output

  AlgoTo2D(Construction cons, GeoElement3D in) {
    super(cons);

    this.in = in;

    switch (in.getGeoClassType()) {
      case GeoElement3D.GEO_CLASS_SEGMENT3D :

        GeoPoint P1 = new GeoPoint(cons);
        GeoPoint P2 = new GeoPoint(cons);
        P1.setCoords(0, 0, 1);
        P2.setCoords(1, 0, 1);

        kernel.setSilentMode(true);
        out = kernel.Segment(null, P1, P2);
        kernel.setSilentMode(false);

        break;
      default :
        out = null;
    }

    if (out != null) {
      setInputOutput(); // for AlgoElement
      compute();
    }
  }

  /** Creates new AlgoJoinPoints */
  public AlgoTo2D(Construction cons, String label, GeoElement3D in) { // TODO
                                                                      // remove
                                                                      // public
    this(cons, in);
    out.setLabel(label);
  }

  // recalc
  @Override
  protected final void compute() {

  }

  @Override
  protected String getClassName() {
    return "AlgoTo2D";
  }

  GeoElement3D getIn() {
    return in;
  }

  GeoElement getOut() {
    return out;
  }

  // for AlgoElement
  @Override
  protected void setInputOutput() {

    input = new GeoElement3D[1];
    input[0] = in;

    output = new GeoElement[1];
    output[0] = out;

    // set dependencies
    input[0].addAlgorithm(this);

    // parent of output
    out.setParentAlgorithm(this);
    cons.addToAlgorithmList(this);

  }

  @Override
  final public String toString() {

    return null;
  }
}
