package jasymca;
/* Jasymca	-	- Symbolic Calculator for Mobile Devices
 This version is written for J2ME, CLDC 1.1,  MIDP 2, JSR 75
 or J2SE


 Copyright (C) 2006 - Helmut Dersch  der@hs-furtwangen.de

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2, or (at your option)
 any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  */

/*------------------------------------------------------------*/

import java.util.Vector;

// Strategy:
// (1) Normalize equations
// (2) Solve for all linear variables; use pivoting
// (3) Solve remaining equations using SOLVE
// (4) Keep multiple solutions
// (5) Return as linear factors (x-solution) (might look weird)

class LambdaALGSYS extends Lambda {
  Vector<Variable> clonev(Vector<Variable> v) {
    Vector<Variable> r = new Vector<Variable>(v.size());
    for (int i = 0; i < v.size(); i++)
      r.addElement(v.elementAt(i));
    return r;
  }

  @Override
  public Object lambda(Object x) throws ParseException, JasymcaException {
    Vector<Object> args = getArgs(car(x));
    if (args.size() != 2)
      throw new ParseException("algsys requires 2 arguments.");

    Algebraic expr = ((Algebraic) Jasymca.evalPrefix(args.elementAt(0), true,
        env)).rat();
    Algebraic vars = (Algebraic) Jasymca.evalPrefix(args.elementAt(1), true,
        env);

    if (!(expr instanceof Vektor) || !(vars instanceof Vektor)
        || ((Vektor) expr).coord.length != ((Vektor) vars).coord.length)
      throw new ParseException("Wrong type of arguments to algsys.");

    // Normalize all equations
    expr = new ExpandUser().f_exakt(expr);
    expr = new TrigExpand().f_exakt(expr);
    expr = new NormExp().f_exakt(expr);
    expr = new CollectExp(expr).f_exakt(expr);
    expr = new SqrtExpand().f_exakt(expr);

    Vector<Variable> v = new Vector<Variable>();
    for (Algebraic element : ((Vektor) vars).coord) {
      Algebraic p = element;
      if (!(p instanceof Polynomial))
        throw new ParseException("Wrong type of arguments to algsys.");
      v.addElement(((Polynomial) p).var);
    }

    return solvesys(((Vektor) expr).vector(), v);
  }

  // Solve one of expr and return solution
  // Use coefficients up to (not including) n
  Vektor solve(Vector<Algebraic> expr, Vector<Variable> x, int n)
      throws JasymcaException {
    // Find linear equation
    Algebraic equ = null;
    Variable v = null;
    int i, k, iv = 0, ke = 0;
    for (i = 0; i < n && equ == null; i++) { // Loop through Variables
      v = x.elementAt(i);
      double norm = -1.;
      for (k = 0; k < n; k++) { // Loop through equations
        Algebraic exp = expr.elementAt(k);
        if (exp instanceof Rational)
          exp = ((Rational) exp).nom;
        Algebraic slope = exp.deriv(v);
        if (!slope.equals(Zahl.ZERO) && slope instanceof Zahl) { // v is linear
          double nm = slope.norm() / exp.norm();
          if (nm > norm) {
            norm = nm;
            equ = exp;
            ke = k;
            iv = i;
          }
        }
      }
    }
    if (equ == null)
      for (i = 0; i < n && equ == null; i++) { // Loop through Variables
        v = x.elementAt(i);
        for (k = 0; k < n; k++) { // Loop through equations
          Algebraic exp = expr.elementAt(k);
          if (exp instanceof Rational)
            exp = ((Rational) exp).nom;
          if (exp.depends(v)) {
            equ = exp;
            ke = k;
            iv = i;
            break;
          }
        }
      }
    if (equ == null)
      throw new JasymcaException("Expressions do not depend of Variables.");

    Vektor sol = LambdaSOLVE.solve(equ, v);
    expr.removeElementAt(ke);
    expr.insertElementAt(equ, n - 1);
    x.removeElementAt(iv);
    x.insertElementAt(v, n - 1);
    return sol;
  }

  Vektor solvesys(Vector<Algebraic> expr, Vector<Variable> x)
      throws JasymcaException {
    int nvars = x.size();
    Vector<Vector<Algebraic>> lsg = new Vector<Vector<Algebraic>>();
    Vector<Vector<Variable>> vars = new Vector<Vector<Variable>>();
    lsg.addElement(expr);
    vars.addElement(x);
    int n = nvars;
    while (n > 0) {
      for (int i = 0; i < lsg.size(); i++)
        try {
          Vector<Algebraic> equ = lsg.elementAt(i);
          Vector<Variable> xv = vars.elementAt(i);
          Vektor sol = solve(equ, xv, n);
          lsg.removeElementAt(i);
          vars.removeElementAt(i);
          Variable v = xv.elementAt(n - 1); // Eliminated variable
          for (Algebraic element : sol.coord) { // Insert solution
            Vector<Algebraic> eq = new Vector<Algebraic>();
            for (int j = 0; j < n - 1; j++)
              // Insert solution into remaining equations
              eq.addElement(equ.elementAt(j).value(v, element));
            eq.addElement(element); // Insert solution into current position
            for (int j = n; j < nvars; j++)
              eq.addElement(equ.elementAt(j)); // Copy old solutions
            lsg.insertElementAt(eq, i);
            vars.insertElementAt(clonev(xv), i);
            i++;
          }
        } catch (JasymcaException je) { // Could not find a solution
          lsg.removeElementAt(i);
          vars.removeElementAt(i);
          i--;
        }
      if (lsg.size() == 0)
        throw new JasymcaException("Could not solve equations.");
      n--;
    }
    // Backsubstitution
    for (int i = 0; i < lsg.size(); i++) {
      Vector<Algebraic> equ = lsg.elementAt(i);
      Vector<Variable> xv = vars.elementAt(i);
      for (n = 1; n < nvars; n++) {
        Algebraic y = equ.elementAt(n - 1);
        Variable v = xv.elementAt(n - 1);
        for (int k = n; k < nvars; k++) {
          Algebraic z = equ.elementAt(k);
          equ.removeElementAt(k);
          equ.insertElementAt(z.value(v, y), k);
        }
      }
    }
    // Convert solutions to linear factors
    for (int i = 0; i < lsg.size(); i++) {
      Vector<Algebraic> equ = lsg.elementAt(i);
      Vector<Variable> xv = vars.elementAt(i);
      for (n = 0; n < nvars; n++) {
        Variable v = xv.elementAt(n);
        Algebraic y = new Polynomial(v).sub(equ.elementAt(n));
        equ.removeElementAt(n);
        equ.insertElementAt(y, n);
      }
    }

    // return as Vektor of Vektors
    Vektor[] r = new Vektor[lsg.size()];
    for (int i = 0; i < lsg.size(); i++)
      r[i] = Vektor.create(lsg.elementAt(i));
    return new Vektor(r);
  }

}
