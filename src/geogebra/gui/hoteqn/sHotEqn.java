/*****************************************************************************
 *                                                                            *
 *                   HotEqn Equation Viewer Component                         *
 *                                                                            *
 ******************************************************************************
 * Java-Coponent to view mathematical Equations provided in the LaTeX language*
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

 ******************************************************************************
 *                                                                            *
 * Constructor:                                                               *
 *   sHotEqn()                Construtor without any initial equation.        *
 *   sHotEqn(String equation) Construtor with initial equation to display.    *
 *   sHotEqn(String equation, Applet app, String name)                        *
 *                            The same as above if used in an applet          *
 *                            with applet name.                               *
 *                                                                            *
 * Public Methods:                                                            *
 *   void setEquation(String equation)  Sets the current equation.            *
 *   String getEquation()               Returns the current equation.         *
 *   void setDebug(boolean debug)       Switches debug mode on and off.       *
 *   boolean isDebug()                  Returns the debug mode.               * 
 *   void setFontname(String fontname)  Sets one of the java fonts.           *
 *   String getFontname()               Returns the current fontname.         *
 *   void setFontsizes(int g1, int g2, int g3, int g4) Sets the fontsizes     *
 *                                      for rendering. Possible values are    *
 *                                      18, 14, 16, 12, 10 and 8.             *
 *   void setBackground(Color BGColor)  Sets the background color.            *
 *                                      Overrides method in class component.  *           
 *   Color getBackground()              Returns the used background color.    *
 *                                      Overrides method in class component.  *           
 *   void setForeground(Color FGColor)  Sets the foreground color.            *
 *                                      Overrides method in class component.  * 
 *   Color getForeground()              Returns the used foreground color.    *
 *                                      Overrides method in class component.  * 
 *   void setBorderColor(Color border)  Sets color of the optional border.    *
 *   Color getBorderColor()             Returns the color of the border.      * 
 *   void setBorder(boolean borderB)    Switches the border on or off.        * 
 *   boolean isBorder()                 Returns wether or not a border is     *
 *                                      displayed.                            *
 *   void setRoundRectBorder(boolean borderB)                                 *
 *                                      Switches between a round and a        *
 *                                      rectangular border.                   *
 *                                      TRUE: round border                    *
 *                                      FALSE: rectangular border             *
 *   boolean isRoundRectBorder()        Returns if the border is round or     *
 *                                      rectangular.                          *
 *   void setEnvColor(Color env)        Sets color of the environment.        *
 *   Color getEnvColor()                Returns the color of the environment. * 
 *   void setHAlign(String halign)      Sets the horizontal alignment.        *
 *                                      Possible values are: left, center and *
 *                                      right.                                *
 *   String getHAlign()                 Returns the horizontal alignment.     *
 *   void setVAlign(String valign)      Sets the vertical alignment.          *
 *                                      Possible values are: top, middle and  *
 *                                      bottom.                               *
 *   public String getVAlign()          Returns the vertical alignment.       *
 *   void setEditable(boolean editableB)  Makes the component almost editable.*
 *                                      Parts of the displayed equation are   *
 *                                      selectable when editable is set true. *
 *                                      This is turned on by default.         *
 *   boolean isEditable()               Returns wether or not the equation    *
 *                                      is editable (selectable).             *
 *   String getSelectedArea()           Return selected area of an equation.  *
 *   Dimension getPreferredSize()       Returns the prefered size required to *
 *                                      display the entire shown equation.    *
 *                                      Overrides method in class component.  *
 *   Dimension getMinimumSize()         This method return the same value as  *
 *                                      getPreferedSize                       *
 *                                      Overrides method in class component.  *
 *   Dimension getSizeof(String equation) Returns the size required to        *
 *                                      display the given equation.           *
 *   void addActionListener(ActionListener listener)                          *
 *                                      Adds the specified action listener to *
 *                                      receive action events from this text  *
 *                                      field.                                *
 *   void removeActionListener(ActionListener listener)                       *
 *                                      Removes the specified action listener *
 *                                      to receive action events from this    *
 *                                      text field.                           *
 *   Image getImage()                   Returns the HotEqn image              *  
 *                                                                            *
 ******************************************************************************/

// **** localWidth u. localHeight nur bei getPreferredSize() zurückgeben

package geogebra.gui.hoteqn;

// package bHotEqn;  // for Bean-compilation to avoid double filenames

//import atp.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;

import javax.swing.JComponent;

public class sHotEqn extends JComponent
    implements
      MouseListener,
      MouseMotionListener {

  /**
   * 
   */
  private static final long serialVersionUID = 4289417857740036459L;
  ActionListener actionListener; // Post action events to listeners
  private final cHotEqnImpl impl;
  // ************************* Constructor ()
  // ****************************************
  public sHotEqn() {
    this("cHotEqn", null, "cHotEqn");
  }

  public sHotEqn(String equation) {
    this(equation, null, "cHotEqn");
  }

  public sHotEqn(String equation, Applet app, String nameS) {
    impl = new cHotEqnImpl(this, equation, app, nameS);
    addMouseListener(this);
    addMouseMotionListener(this);
  }

  // ************************* Public Methods
  // ***********************************

  public void addActionListener(ActionListener listener) {
    actionListener = AWTEventMulticaster.add(actionListener, listener);
    enableEvents(AWTEvent.MOUSE_EVENT_MASK);
  }
  @Override
  public Color getBackground() {
    return impl.getBackgroundImpl();
  }

  public Color getBorderColor() {
    return impl.getBorderColorImpl();
  }

  public Color getEnvColor() {
    return impl.getEnvColorImpl();
  }

  public String getEquation() {
    return impl.getEquationImpl();
  }
  public String getFontname() {
    return impl.getFontnameImpl();
  }

  @Override
  public Color getForeground() {
    return impl.getForegroundImpl();
  }
  public String getHAlign() {
    return impl.getHAlignImpl();
  }

  public Image getImage() {
    return impl.getImageImpl();
  }

  @Override
  public Dimension getMinimumSize() {
    return impl.getMinimumSizeImpl();
  }

  @Override
  public Dimension getPreferredSize() {
    return impl.getPreferredSizeImpl();
  }
  public String getSelectedArea() {
    return impl.getSelectedAreaImpl();
  }

  @Override
  public Dimension getSize() {
    return impl.getSizeImpl();
  }
  public Dimension getSizeof(String equation) {
    return impl.getSizeofImpl(equation);
  }

  public String getVAlign() {
    return impl.getVAlignImpl();
  }
  public boolean isBorder() {
    return impl.isBorderImpl();
  }

  public boolean isDebug() {
    return impl.isDebugImpl();
  }
  public boolean isEditable() {
    return impl.isEditableImplImpl();
  }

  public boolean isRoundRectBorder() {
    return impl.isRoundRectBorderImpl();
  }
  public void mouseClicked(MouseEvent ev) {
  }

  public void mouseDragged(MouseEvent ev) {
  }
  public void mouseEntered(MouseEvent ev) {
  }
  public void mouseExited(MouseEvent ev) {
  }

  public void mouseMoved(MouseEvent ev) {
  }

  public void mousePressed(MouseEvent ev) {
  }
  public void mouseReleased(MouseEvent ev) {
  }

  @Override
  public synchronized void paintComponent(Graphics g) {
    impl.generateImageImpl(g, 0, 0);
  } // paint
  public synchronized void paintComponent(Graphics g, int x, int y) {
    impl.generateImageImpl(g, x, y);
  } // paint

  public void printStatus(String s) {
    impl.printStatusImpl(s);
  }

  // ************************* Eventhandler
  // *************************************

  @Override
  public void processMouseEvent(MouseEvent ev) {
    impl.processMouseEventImpl(ev);
    super.processMouseEvent(ev);
  }
  @Override
  public void processMouseMotionEvent(MouseEvent ev) {
    impl.processMouseMotionEventImpl(ev);
  }
  public void removeActionListener(ActionListener listener) {
    actionListener = AWTEventMulticaster.remove(actionListener, listener);
  }
  @Override
  public void setBackground(Color BGColor) {
    impl.setBackgroundImpl(BGColor);
  }
  public void setBorder(boolean borderB) {
    impl.setBorderImpl(borderB);
  }
  public void setBorderColor(Color BorderColor) {
    impl.setBorderColorImpl(BorderColor);
  }
  public void setDebug(boolean debug) {
    impl.setDebugImpl(debug);
  }

  public void setEditable(boolean editableB) {
    impl.setEditableImpl(editableB);
  }

  public void setEnvColor(Color EnvColor) {
    impl.setEnvColorImpl(EnvColor);
  }

  public void setEquation(String equation) {
    impl.setEquationImpl(equation);
  }

  public void setFontname(String fontname) {
    impl.setFontnameImpl(fontname);
  }

  public void setFontsizes(int gsize1, int gsize2, int gsize3, int gsize4) {
    impl.setFontsizesImpl(gsize1, gsize2, gsize3, gsize4);
  }

  /**
   * Changes style of current font.
   * 
   * @param style
   *          : Font.PLAIN, Font.ITALIC or Font.BOLD
   * @author Markus Hohenwarter
   * @date Jan 25, 2008
   */
  public void setFontStyle(int style) {
    impl.setFontStyle(style);
  }

  @Override
  public void setForeground(Color FGColor) {
    impl.setForegroundImpl(FGColor);
  }

  public void setHAlign(String halign) {
    impl.setHAlignImpl(halign);
  }

  public void setRoundRectBorder(boolean roundRectBorderB) {
    impl.setRoundRectBorderImpl(roundRectBorderB);
  }
  public void setVAlign(String valign) {
    impl.setVAlignImpl(valign);
  }

}
