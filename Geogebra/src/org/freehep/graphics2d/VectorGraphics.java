// Copyright 2000-2004, FreeHEP.
package org.freehep.graphics2d;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

/**
 * The drawing methods which are guaranteed to work for the various output
 * formats of the VectorGraphics system on the Java 2 platform. All methods are
 * re-declared abstract, since this class inherits from Graphics2D and we would
 * not want to actually or accidentally use any of those methods, except for the
 * ones noted.
 * 
 * Some int methods need to call their super.methods otherwise the compiler
 * cannot make a distinction if it needs to convert int to doubles or call the
 * super int method.
 * 
 * Note that many of these routines modify the current transformation matrix. To
 * guard against unintended side effects the following method should be used:
 * 
 * <pre>
 * &lt;code&gt;
 *  Graphics2D tempGraphics = (Graphics2D) originalGraphics.create();
 *  tempGraphics.setStroke(originalGraphics.getStroke());
 *  tempGraphics.rotate(...);
 *  tempGraphics.translate(...);
 *  ...drawing methods on tempGraphics...
 *  tempGraphics.dispose();
 * &lt;/code&gt;
 * </pre>
 * 
 * where <code>originalGraphics</code> is the original <code>Graphics2D</code>
 * object. Note that <code>dispose</code> must be called when the drawing
 * finishes on <code>tempGraphics</code> and that no drawing should be done on
 * <code>originalGraphics</code> until <code>dispose</code> has been called.
 * 
 * @author Charles Loomis
 * @author Mark Donszelmann
 * @version $Id: VectorGraphics.java,v 1.7 2009/08/17 21:44:44 murkle Exp $
 */
public abstract class VectorGraphics extends Graphics2D
    implements
      VectorGraphicsConstants {

  // STATIC stuff below
  private static Hashtable<String, Integer> symbols = new Hashtable<String, Integer>(
      15);

  static {
    symbols.put("vline", new Integer(SYMBOL_VLINE));
    symbols.put("hline", new Integer(SYMBOL_HLINE));
    symbols.put("plus", new Integer(SYMBOL_PLUS));
    symbols.put("cross", new Integer(SYMBOL_CROSS));
    symbols.put("star", new Integer(SYMBOL_STAR));
    symbols.put("circle", new Integer(SYMBOL_CIRCLE));
    symbols.put("box", new Integer(SYMBOL_BOX));
    symbols.put("up_triangle", new Integer(SYMBOL_UP_TRIANGLE));
    symbols.put("dn_triangle", new Integer(SYMBOL_DN_TRIANGLE));
    symbols.put("diamond", new Integer(SYMBOL_DIAMOND));
  }

  private static Hashtable<String, Integer> alignments = new Hashtable<String, Integer>(
      6);

  static {
    alignments.put("baseline", new Integer(TEXT_BASELINE));
    alignments.put("left", new Integer(TEXT_LEFT));
    alignments.put("top", new Integer(TEXT_TOP));
    alignments.put("middle", new Integer(TEXT_CENTER));
    alignments.put("center", new Integer(TEXT_CENTER));
    alignments.put("right", new Integer(TEXT_RIGHT));
    alignments.put("bottom", new Integer(TEXT_BOTTOM));
  }

  // STATIC stuff below
  protected static VectorGraphics create(Graphics g) {
    if (g != null && !(g instanceof VectorGraphics))
      return new PixelGraphics2D(g);
    return (VectorGraphics) g;
  }

  public static int getSymbol(String name) {
    Integer i = symbols.get(name.toLowerCase());
    return i != null ? i.intValue() : SYMBOL_PLUS;
  }

  public static int getTextAlignment(String name) {
    Integer i = alignments.get(name.toLowerCase());
    return i != null ? i.intValue() : TEXT_CENTER;
  }

  public static double getXalignment(double x, double width, int alignment) {
    // horizontal alignment
    switch (alignment) {
      case TEXT_CENTER :
        x = x - width / 2;
        break;
      case TEXT_RIGHT :
        x = x - width;
        break;
      case TEXT_LEFT :
      default :
        break;
    }
    return x;
  }

  public static double getYalignment(double y, double ascent, double descent,
      int alignment) {
    // vertical alignment
    switch (alignment) {
      case TEXT_TOP :
        y = y + ascent - descent;
        break;
      case TEXT_CENTER :
        y = y + (ascent + descent) / 2 - descent;
        break;
      case TEXT_BOTTOM :
        y = y - descent;
        break;
      case TEXT_BASELINE :
      default :
        break;
    }
    return y;
  }

  // //
  // Methods from java.awt.Graphics2D (alphabetical)
  // //
  @Override
  public abstract void addRenderingHints(Map hints);

  // //
  // Methods defined in java.awt.Graphics (alphabetical)
  // //

  public abstract void clearRect(double x, double y, double width, double height);

  @Override
  public abstract void clearRect(int x, int y, int width, int height);

  @Override
  public abstract void clip(Shape s);

  public abstract void clipRect(double x, double y, double width, double height);

  @Override
  public abstract void clipRect(int x, int y, int width, int height);

  @Override
  public abstract void copyArea(int x, int y, int width, int height, int dx,
      int dy);

  @Override
  public abstract Graphics create();

  public abstract Graphics create(double x, double y, double width,
      double height);

  // NOTE: implemented in Graphics, must be implemented here otherwise the
  // compiler
  // cannot choose between converting ints to doubles or calling the
  // superclass.
  @Override
  public Graphics create(int x, int y, int width, int height) {
    return super.create(x, y, width, height);
  }

  @Override
  public abstract void dispose();

  @Override
  public abstract void draw(Shape s);

  /**
   * Draws an arc. Uses Arc2D to call draw(Shape).
   * 
   */
  public abstract void drawArc(double x, double y, double width, double height,
      double startAngle, double arcAngle);

  // NOTE: implemented in Graphics
  // public abstract void draw3DRect(int x, int y,
  // int width, int height,
  // boolean raised);
  @Override
  public abstract void drawArc(int x, int y, int width, int height,
      int startAngle, int arcAngle);

  // NOTE: overridden in Graphics2D
  // public abstract void draw3DRect(int x, int y, int width, int height,
  // boolean raised);
  @Override
  public abstract void drawGlyphVector(GlyphVector g, float x, float y);

  @Override
  public abstract void drawImage(BufferedImage img, BufferedImageOp op, int x,
      int y);

  @Override
  public abstract boolean drawImage(Image img, AffineTransform xform,
      ImageObserver obs);

  @Override
  public abstract boolean drawImage(Image image, int x, int y, Color bgColor,
      ImageObserver observer);

  // NOTE: implemented in Graphics
  // public abstract void drawBytes(byte[] data, int offset,
  // int length,
  // int x, int y);
  // NOTE: implemented in Graphics
  // public abstract void drawChars(char[] data, int offset,
  // int length,
  // int x, int y);
  @Override
  public abstract boolean drawImage(Image image, int x, int y,
      ImageObserver observer);

  @Override
  public abstract boolean drawImage(Image image, int x, int y, int width,
      int height, Color bgColor, ImageObserver observer);

  @Override
  public abstract boolean drawImage(Image image, int x, int y, int width,
      int height, ImageObserver observer);

  @Override
  public abstract boolean drawImage(Image image, int dx1, int dy1, int dx2,
      int dy2, int sx1, int sy1, int sx2, int sy2, Color bgColor,
      ImageObserver observer);

  @Override
  public abstract boolean drawImage(Image image, int dx1, int dy1, int dx2,
      int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer);

  /**
   * Draws a straight line. Uses Line2D to call draw(Shape).
   * 
   */
  public abstract void drawLine(double x1, double y1, double x2, double y2);

  @Override
  public abstract void drawLine(int x1, int y1, int x2, int y2);

  /**
   * Draws an oval. Uses Ellipse2D to call draw(Shape).
   * 
   */
  public abstract void drawOval(double x, double y, double width, double height);

  @Override
  public abstract void drawOval(int x, int y, int width, int height);

  /**
   * Draws a polygon. Uses createShape(...) to call draw(Shape).
   * 
   */
  public abstract void drawPolygon(double[] xPoints, double[] yPoints,
      int nPoints);

  @Override
  public abstract void drawPolygon(int[] xPoints, int[] yPoints, int nPoints);

  /**
   * Draws a polyline. Uses createShape(...) to call draw(Shape).
   * 
   */
  public abstract void drawPolyline(double[] xPoints, double[] yPoints,
      int nPoints);

  // NOTE implemented in Graphics
  // public abstract void drawPolygon(Polygon p);
  @Override
  public abstract void drawPolyline(int[] xPoints, int[] yPoints, int nPoints);

  /**
   * Draws a rectangle. Uses Rectangle2D to call draw(Shape).
   * 
   */
  public abstract void drawRect(double x, double y, double width, double height);

  @Override
  public abstract void drawRect(int x, int y, int width, int height);

  @Override
  public abstract void drawRenderableImage(RenderableImage img,
      AffineTransform xform);

  @Override
  public abstract void drawRenderedImage(RenderedImage img,
      AffineTransform xform);

  /**
   * Draws a rounded rectangle. Uses RoundRectangle2D to call draw(Shape).
   * 
   */
  public abstract void drawRoundRect(double x, double y, double width,
      double height, double arcWidth, double arcHeight);

  @Override
  public abstract void drawRoundRect(int x, int y, int width, int height,
      int arcWidth, int arcHeight);

  @Override
  public abstract void drawString(AttributedCharacterIterator iterator,
      float x, float y);

  @Override
  public abstract void drawString(AttributedCharacterIterator iterator, int x,
      int y);

  /**
   * Draws a string.
   * 
   */
  public abstract void drawString(String str, double x, double y);

  public abstract void drawString(String str, double x, double y,
      int horizontal, int vertical);

  /**
   * Draws a string with a lot of parameters.
   * 
   * @param str
   *          text to be drawn
   * @param x
   *          coordinate to draw string
   * @param y
   *          coordinate to draw string
   * @param horizontal
   *          alignment of the text
   * @param vertical
   *          alignment of the text
   * @param framed
   *          true if text is surrounded by a frame
   * @param frameColor
   *          color of the frame
   * @param frameWidth
   *          witdh of the frame
   * @param banner
   *          true if the frame is filled by a banner
   * @param bannerColor
   *          color of the banner
   */
  public abstract void drawString(String str, double x, double y,
      int horizontal, int vertical, boolean framed, Color frameColor,
      double frameWidth, boolean banner, Color bannerColor);

  // NOTE: overridden in Graphics2D
  // public abstract void drawString(AttributedCharacterIterator iterator, int
  // x, int y);
  // NOTE: redefined in Graphics2D
  // public abstract void drawString(String str, int x, int y);
  @Override
  public abstract void drawString(String str, float x, float y);

  @Override
  public abstract void drawString(String str, int x, int y);

  public abstract void drawString(TagString str, double x, double y);

  public abstract void drawString(TagString str, double x, double y,
      int horizontal, int vertical);

  /**
   * Draws a TagString with a lot of parameters.
   * 
   * @param str
   *          Tagged text to be drawn
   * @param x
   *          coordinate to draw string
   * @param y
   *          coordinate to draw string
   * @param horizontal
   *          alignment of the text
   * @param vertical
   *          alignment of the text
   * @param framed
   *          true if text is surrounded by a frame
   * @param frameColor
   *          color of the frame
   * @param frameWidth
   *          witdh of the frame
   * @param banner
   *          true if the frame is filled by a banner
   * @param bannerColor
   *          color of the banner
   */
  public abstract void drawString(TagString str, double x, double y,
      int horizontal, int vertical, boolean framed, Color frameColor,
      double frameWidth, boolean banner, Color bannerColor);

  public abstract void drawSymbol(double x, double y, double size, int symbol);

  public abstract void drawSymbol(int x, int y, int size, int symbol);

  public abstract void endExport();

  @Override
  public abstract void fill(Shape s);

  public abstract void fillAndDraw(Shape s, Color fillColor);

  public abstract void fillAndDrawSymbol(double x, double y, double size,
      int symbol, Color fillColor);

  public abstract void fillAndDrawSymbol(int x, int y, int size, int symbol,
      Color fillColor);

  /**
   * Fills an arc. Uses Arc2D to call fill(Shape).
   * 
   */
  public abstract void fillArc(double x, double y, double width, double height,
      double startAngle, double arcAngle);

  // NOTE: implemented in Graphics
  // public abstract void fill3DRect(int x, int y,
  // int width, int height,
  // boolean raised);
  @Override
  public abstract void fillArc(int x, int y, int width, int height,
      int startAngle, int arcAngle);

  /**
   * Fills an oval. Uses Ellipse2D to call fill(Shape).
   * 
   */
  public abstract void fillOval(double x, double y, double width, double height);

  @Override
  public abstract void fillOval(int x, int y, int width, int height);

  /**
   * Fills a polygon. Uses createShape(...) to call fill(Shape).
   * 
   */
  public abstract void fillPolygon(double[] xPoints, double[] yPoints,
      int nPoints);

  @Override
  public abstract void fillPolygon(int[] xPoints, int[] yPoints, int nPoints);

  /**
   * Fills a rectangle. Uses Rectangle2D to call fill(Shape).
   * 
   */
  public abstract void fillRect(double x, double y, double width, double height);

  // NOTE: implemented in Graphics
  // public abstract void fillPolygon(Polygon p);
  @Override
  public abstract void fillRect(int x, int y, int width, int height);

  /**
   * Fills a rounded rectangle. Uses RoundRectangle2D to call fill(Shape).
   * 
   */
  public abstract void fillRoundRect(double x, double y, double width,
      double height, double arcWidth, double arcHeight);

  @Override
  public abstract void fillRoundRect(int x, int y, int width, int height,
      int arcWidth, int arcHeight);

  public abstract void fillSymbol(double x, double y, double size, int symbol);

  public abstract void fillSymbol(int x, int y, int size, int symbol);

  // NOTE: overridden in Graphics2D
  // public abstract void fill3DRect(int x, int y,
  // int width, int height,
  // boolean raised);
  @Override
  public abstract Color getBackground();

  // NOTE: implemented in Graphics
  // public abstract void finalize();
  @Override
  public abstract Shape getClip();

  @Override
  public abstract Rectangle getClipBounds();

  @Override
  public abstract Rectangle getClipBounds(Rectangle r);

  // NOTE: implemented in Graphics
  // public abstract Rectangle getClipRect();
  @Override
  public abstract Color getColor();

  public abstract int getColorMode();

  @Override
  public abstract Composite getComposite();

  public abstract String getCreator();

  @Override
  public abstract GraphicsConfiguration getDeviceConfiguration();

  // NOTE: redefines in Graphics2D
  // public abstract void translate(int x, int y);

  /*
   * ============================================================================
   * =========
   * 
   * Methods added to VectorGraphics (alphabetical)
   * 
   * 
   * 
   * 
   * ============================================================================
   * =========
   */

  @Override
  public abstract Font getFont();

  // NOTE: implemented in Graphics
  // public abstract FontMetrics getFontMetrics();
  @Override
  public abstract FontMetrics getFontMetrics(Font font);

  @Override
  public abstract FontRenderContext getFontRenderContext();

  @Override
  public abstract Paint getPaint();

  protected abstract Properties getProperties();

  public abstract String getProperty(String key);

  public abstract Color getPropertyColor(String key);

  public abstract Dimension getPropertyDimension(String key);

  public abstract double getPropertyDouble(String key);

  public abstract int getPropertyInt(String key);

  public abstract Rectangle getPropertyRectangle(String key);

  @Override
  public abstract Object getRenderingHint(RenderingHints.Key inteKey);

  @Override
  public abstract RenderingHints getRenderingHints();

  @Override
  public abstract Stroke getStroke();

  @Override
  public abstract AffineTransform getTransform();

  @Override
  public abstract boolean hit(Rectangle rect, Shape s, boolean onStroke);

  protected abstract void initProperties(Properties defaults);

  public abstract boolean isDeviceIndependent();

  public abstract boolean isProperty(String key);

  public abstract void printComment(String comment);

  @Override
  public abstract void rotate(double theta);

  @Override
  public abstract void rotate(double theta, double x, double y);

  @Override
  public abstract void scale(double sx, double sy);

  @Override
  public abstract void setBackground(Color color);

  public abstract void setClip(double x, double y, double width, double height);

  // NOTE: implemented in Graphics
  // public abstract boolean hitClip(int x, int y, int width, int height);
  @Override
  public abstract void setClip(int x, int y, int width, int height);

  @Override
  public abstract void setClip(Shape clip);

  @Override
  public abstract void setColor(Color c);

  public abstract void setColorMode(int colorMode);

  @Override
  public abstract void setComposite(Composite comp);

  public abstract void setCreator(String creator);

  public abstract void setDeviceIndependent(boolean isDeviceIndependent);

  @Override
  public abstract void setFont(Font font);

  public abstract void setLineWidth(double width);

  public abstract void setLineWidth(int width);

  @Override
  public abstract void setPaint(Paint paint);

  @Override
  public abstract void setPaintMode();

  public abstract void setProperties(Properties newProperties);

  @Override
  public abstract void setRenderingHint(RenderingHints.Key hintKey,
      Object hintValue);

  @Override
  public abstract void setRenderingHints(Map hints);

  @Override
  public abstract void setStroke(Stroke s);

  @Override
  public abstract void setTransform(AffineTransform xform);

  @Override
  public abstract void setXORMode(Color c1);

  @Override
  public abstract void shear(double shx, double shy);

  public abstract void startExport();

  @Override
  public abstract String toString();

  @Override
  public abstract void transform(AffineTransform xform);

  @Override
  public abstract void translate(double tx, double ty);

  @Override
  public abstract void translate(int x, int y);
}
