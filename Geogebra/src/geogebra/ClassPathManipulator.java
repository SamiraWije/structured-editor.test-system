/* 
 GeoGebra - Dynamic Mathematics for Schools
 Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package geogebra;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * <h3>Class to manipulate Classpath</h3> Hack: Use reflection to overcome
 * protected modifiers, not nice, but it works...
 * <ul>
 * Interface:
 * <li>addFile(String)
 * <li>addFile(File)
 * <li>addURL(URL)
 * <li>getClassPath():String
 * <li>listClassPath()
 * </ul>
 * 
 * @author H-P Ulven
 * @version 04.06.08
 */
public final class ClassPathManipulator {

  // private static String nl = System.getProperty("line.separator");

  /** Adds a URL to the Classpath */
  public synchronized static boolean addURL(URL u, ClassLoader loader) {
    if (loader == null)
      loader = ClassLoader.getSystemClassLoader();
    URLClassLoader sysloader = (URLClassLoader) loader;
    Class<URLClassLoader> sysclass = URLClassLoader.class;

    // check if URL u is already on classpath
    URL[] classpath = sysloader.getURLs();
    if (classpath != null)
      for (URL element : classpath)
        // u found on classpath
        if (element.equals(u)) {
          System.out
              .println("ClassPathManipulator.addURL(): already on classpath: "
                  + u);
          return true;
        }

    try {
      Class[] parameter = new Class[]{URL.class};
      Method method = sysclass.getDeclaredMethod("addURL", parameter);
      method.setAccessible(true);
      method.invoke(sysloader, new Object[]{u});
      return true;
    } catch (NoSuchMethodException t) {
      System.err
          .println("ClassPathManipulator: addURL gives NoSuchMethodExcepton.");
      return false;
    } catch (IllegalAccessException e) {
      System.err
          .println("ClassPathManipulator: addURL gives IllegalAccesException.");
      return false;
    } catch (InvocationTargetException e) {
      System.err
          .println("ClassPathManipulator: addURL gives InvocationTargetException");
      return false;
    } catch (Throwable t) {
      System.err
          .println("ClassPathManipulator: addURL gives " + t.getMessage());
      return false;
    }
  }

}