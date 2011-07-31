/*****************************************************************************
 *                                                                            *
 *                               T O K E N                                    *
 *                                  for                                       *
 *                         HotEqn Equation Applet                             *
 *                                                                            *
 ******************************************************************************
 *       Liste aller unterstützten Token                                      *
 *       Token werden vom Scanner erkannt und vom Parser ausgewertet.         *
 ******************************************************************************

Copyright 2006 Stefan Müller and Christian Schmid

This file is part of the HotEqn package.

    HotEqn is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; 
    HotEqn is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

 **************   Version 2.0     *********************************************
 *        1997 Chr. Schmid, S. Mueller                                        *
 *                                                                            *
 * 22.12.1997  Separation from HotEqn.java                            (2.00p) * 
 * 30.12.1997  new EqToken constructor                                (2.00s) * 
 * 31.12.1997  <> Angle new                                           (2.00t) *
 * 13.01.1998  new media tracking, cached images                      (2.00z4)* 
 * 18.01.1998  Image cache realized by hash table                     (2.01a) *
 * 27.10.2002  Package atp introduced                                 (3.12)  * 
 **************   Release of Version 4.00 *************************************
 * 14.07.2003  Adapted to XPCom. Same as 3.12,only mHotEqn affected    (4.00) *
 *                                                                            *
 *****************************************************************************/

package geogebra.gui.hoteqn;

class EqToken {
  protected int typ; // type of token
  protected String stringS; // symbol id

  // Tokenliste | Token | int | Bedeutung
  // -------------------------------------------------------------
  public final static int EOF = 0; // End of Equation
  public final static int Id = 1; // Variable
  public final static int Num = 2; // Numeral
  public final static int BeginSym = 3; // logische Klammer {
  public final static int EndSym = 4; // logische Klammer }
  public final static int ANGLE = 5; // Klammer < oder >
  public final static int AndSym = 7; // & Trennzeichen (array)
  public final static int DBackSlash = 8; // \\ Trennzeichen (array)
  public final static int FUNC = 9; // \sin \cos ... nicht kursiv!!

  protected final static int SUP = 10; // ^ Hochstellen
  protected final static int SUB = 11; // _ Tiefstellen
  protected final static int FRAC = 12; // Bruch
  protected final static int SQRT = 13; // Wurzel
  protected final static int VEC = 14; // Vektor
  protected final static int ARRAY = 15; // Vektoren u. Matrizen
  protected final static int LEFT = 16; // Left
  protected final static int RIGHT = 17; // Right
  protected final static int SYMBOP = 18; // Greek and operational symbols
  // without
  // descents
  protected final static int SYMBOPD = 19; // Greek and operational symbols with
  // descents
  protected final static int SYMBOLBIG = 20; // Summe Produkt Integral
  protected final static int ACCENT = 22; // Akzente ^~.´`..
  protected final static int LIM = 24; // Limes
  protected final static int SpaceChar = 25; // space ' '

  protected final static int BEGIN = 50; // begin{array}
  protected final static int END = 51; // end{array}

  protected final static int Null = 99; // Nix (sollte nie erreicht werden)
  protected final static int Invalid = 100; // Falsches Zeichen

  protected final static int Op = 108; // <>#~;:,+-*/=!
  protected final static int Paren = 109; // ( [ \{ \| | ) ] \}
  protected final static int NOT = 110; // negation \not
  protected final static int SPACE = 113; // additional horizantal space
  protected final static int CHOOSE = 114; // { ... \choose ... }
  protected final static int ATOP = 115; // { ... \atop ... }
  protected final static int OverLINE = 116; // overline{...}
  protected final static int UnderLINE = 117; // underline{...}
  protected final static int OverBRACE = 118; // overbrace{...}^{...}
  protected final static int UnderBRACE = 119; // underbrace{...}_{...}
  protected final static int STACKREL = 120; // stackrel{...}{...}
  protected final static int FGColor = 121; // \fgcolor
  protected final static int BGColor = 122; // \bgcolor
  protected final static int FBOX = 123; // \fbox
  protected final static int MBOX = 124; // \mbox

  // Constructor ohne Initialisierung
  public EqToken() {
    typ = 0;
    stringS = "";
  }

  public EqToken(int typ) {
    this.typ = typ;
    stringS = "";
  }

  // Constructor mit Initialisierung
  public EqToken(int typ, String stringS) {
    this.typ = typ;
    this.stringS = stringS;
  }
} // end class EqToken

