/* 
 GeoGebra - Dynamic Mathematics for Schools
 Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

/**
 * GeoGebra Application
 *
 * @author Markus Hohenwarter
 */
package geogebra.gui.app;

import geogebra.euclidian.EuclidianView;
import geogebra.gui.view.spreadsheet.SpreadsheetView;
import geogebra.main.Application;
import geogebra.main.DefaultApplication;
import geogebra.main.GeoGebraPreferences;

import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.*;

/**
 * GeoGebra's main window.
 */
public class GeoGebraFrame extends JFrame implements WindowFocusListener {

  private static final int DEFAULT_WIDTH = 900;
  private static final int DEFAULT_HEIGHT = 650;

  private static final long serialVersionUID = 1L;

  private static ArrayList<GeoGebraFrame> instances = new ArrayList<GeoGebraFrame>();
  private static GeoGebraFrame activeInstance;
  public static synchronized GeoGebraFrame createNewWindow(String[] args) {
    // set Application's size, position and font size
    // TODO: Add layout glass pane (F.S.)
    GeoGebraFrame wnd = new GeoGebraFrame();

    final DefaultApplication app = new DefaultApplication(args, wnd, true);

    System.out.println("Load GUI JAR");
    app.loadGUIJar();
    app.getGuiManager().initMenubar();

    // init GUI
    System.out.println("init GUI");
    wnd.app = app;

    wnd.getContentPane().add(app.buildApplicationPanel());
    wnd.setDropTarget(new DropTarget(wnd,
        new geogebra.gui.FileDropTargetListener(app)));
    wnd.addWindowFocusListener(wnd);

    updateAllTitles();
    wnd.setVisible(true);

    // init some things in the background
    System.out.println("init some things in the background");
    if (!app.isApplet()) {
      Thread runner = new Thread() {
        @Override
        public void run() {
          System.out.println("init CAS");
          app.initCAS();

          System.out.println("init properties dialog");
          app.getGuiManager().initPropertiesDialog();

          System.out.println("init file chooser");
          app.getGuiManager().initFileChooser();

          System.out.println("copy Jar files to temp directory");
          app.downloadJarFiles();
        }
      };
      runner.start();
    }

    return wnd;
  }

  /**
   * Returns the active GeoGebra window.
   */
  protected static synchronized GeoGebraFrame getActiveInstance() {
    return activeInstance;
  }

  private static GeoGebraFrame getInstance(int i) {
    return instances.get(i);
  }

  public static int getInstanceCount() {
    return instances.size();
  }

  public static ArrayList<GeoGebraFrame> getInstances() {
    return instances;
  }

  /**
   * Checks all opened GeoGebra instances if their current file is the given
   * file.
   * 
   * @param file
   * @return GeoGebra instance with file open or null
   */
  public static GeoGebraFrame getInstanceWithFile(File file) {
    if (file == null)
      return null;

    try {
      String absPath = file.getCanonicalPath();
      for (int i = 0; i < instances.size(); i++) {
        GeoGebraFrame inst = instances.get(i);
        Application app = inst.app;

        File currFile = app.getCurrentFile();
        if (currFile != null)
          if (absPath.equals(currFile.getCanonicalPath()))
            return inst;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * MacOS X specific initing. Note: this method can only be run on a Mac!
   */
  public static void initMacSpecifics() {
    try {
      // init mac application listener
      MacApplicationListener.initMacApplicationListener();

      // mac menu bar
      // System.setProperty("com.apple.macos.useScreenMenuBar", "true");
      System.setProperty("apple.laf.useScreenMenuBar", "true");
    } catch (Exception e) {
      Application.debug(e + "");
    }
  }

  /**
   * Main method to create inital GeoGebra window.
   * 
   * @param args
   *          : file name parameter
   */


  public static void updateAllTitles() {
    for (int i = 0; i < instances.size(); i++) {
      Application app = instances.get(i).app;
      app.updateTitle();
    }
  }

  protected Application app;

  public GeoGebraFrame() {
    instances.add(this);
    activeInstance = this;
  }

  /**
   * Disposes this frame and removes it from the static instance list.
   */
  @Override
  public void dispose() {
    instances.remove(this);
    if (this == activeInstance)
      activeInstance = null;
  }

  public Application getApplication() {
    return app;
  }

  public int getInstanceNumber() {
    for (int i = 0; i < instances.size(); i++)
      if (this == instances.get(i))
        return i;
    return -1;
  }

  @Override
  public Locale getLocale() {
    Locale defLocale = GeoGebraPreferences.getPref().getDefaultLocale();

    if (defLocale == null)
      return super.getLocale();
    else
      return defLocale;
  }

  public boolean isIconified() {
    return getExtendedState() == JFrame.ICONIFIED;
  }

  public void setApplication(Application app) {
    this.app = app;
  }

  @Override
  public void setVisible(boolean flag) {
    if (flag) {
      updateSize();

      System.out.println("set location");
      int instanceID = instances.size() - 1;
      if (instanceID > 0) {
        System.out.println("move right and down of last instance");
        GeoGebraFrame prevInstance = getInstance(instanceID - 1);
        Point loc = prevInstance.getLocation();

        System.out.println("make sure we stay on screen");
        Dimension d1 = getSize();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        loc.x = Math.min(loc.x + 20, dim.width - d1.width);
        loc.y = Math.min(loc.y + 20, dim.height - d1.height - 25);
        setLocation(loc);
      } else {
        System.out.println("center");
        setLocationRelativeTo(null);
      }

      super.setVisible(true);
      app.getEuclidianView().requestFocusInWindow();
    } else {
      if (!isShowing())
        return;

      instances.remove(this);
      GeoGebraPreferences.getPref().saveFileList();

      if (instances.size() == 0) {
        super.setVisible(false);
        dispose();

        if (!app.isApplet())
          System.exit(0);
      } else {
        super.setVisible(false);
        updateAllTitles();
      }
    }
  }

  public void updateSize() {
    Dimension frameSize;

    // use euclidian view pref size to set frame size
    EuclidianView ev = app.getEuclidianView();
    SpreadsheetView sv = null;

    if (app.getGuiManager().hasSpreadsheetView())
      sv = (SpreadsheetView) app.getGuiManager().getSpreadsheetView();

    // no preferred size
    if (ev.hasPreferredSize()) {
      ev.setMinimumSize(new Dimension(50, 50));
      Dimension evPref = ev.getPreferredSize();
      ev.setPreferredSize(evPref);

      Dimension svPref = null;
      if (sv != null) {
        svPref = sv.getPreferredSize();
        sv.setPreferredSize(svPref);
      }

      // pack frame and correct size to really get the preferred size for
      // euclidian view
      // Michael Borcherds 2007-12-08 BEGIN pack() sometimes fails (only when
      // run from Eclipse??)
      try {
        pack();
      } catch (Exception e) {
        // do nothing
        Application.debug("updateSize: pack() failed");
      }
      // Michael Borcherds 2007-12-08 END
      frameSize = getSize();
      Dimension evSize = ev.getSize();
      Dimension svSize = null;
      if (sv != null)
        svSize = sv.getSize();

      frameSize.width = frameSize.width + evPref.width - evSize.width
          + (sv == null ? 0 : svPref.width - svSize.width);
      frameSize.height = frameSize.height + evPref.height - evSize.height
          + (sv == null ? 0 : svPref.height - svSize.height);
    } else
      frameSize = new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);

    // TODO Redo (F.S.)
    frameSize = app.getPreferredSize();

    System.out.println("check if frame fits on screen");
    Rectangle screenSize = app.getScreenSize();

    if (frameSize.width > screenSize.width
        || frameSize.height > screenSize.height) {
      frameSize.width = screenSize.width;
      frameSize.height = screenSize.height;
      setLocation(0, 0);
    }
    setSize(frameSize);
  }

  public void windowGainedFocus(WindowEvent arg0) {
    activeInstance = this;
    app.updateMenuWindow();
  }

  public void windowLostFocus(WindowEvent arg0) {
  }

}