/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */
package geogebra.gui.view.consprotocol;

import geogebra.Plain;
import geogebra.main.Application;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Navigation buttons for the construction protocol
 */
public class ConstructionProtocolNavigation extends JPanel
    implements
      ActionListener {

  /**
   * Steps through the construction automatically.
   */
  private class AutomaticPlayer implements ActionListener {
    private final Timer timer; // for animation

    /**
     * Creates a new player to step through the construction automatically.
     * 
     * @param delay
     *          in seconds between steps
     */
    protected AutomaticPlayer(double delay) {
      timer = new Timer((int) (delay * 1000), this);
    }

    public synchronized void actionPerformed(ActionEvent e) {
      prot.nextStep();
      if (prot.getCurrentStepNumber() == prot.getLastStepNumber())
        stopAnimation();
    }

    protected synchronized void startAnimation() {
      // dispatch events to play button
      app.startDispatchingEventsTo(btPlay);
      isPlaying = true;
      btPlay.setIcon(new ImageIcon(app.getPauseImage()));
      btPlay.setText(Plain.Pause);
      setComponentsEnabled(false);
      app.setWaitCursor();

      if (prot.getCurrentStepNumber() == prot.getLastStepNumber())
        prot.firstStep();

      timer.start();
    }

    protected synchronized void stopAnimation() {
      timer.stop();

      // unblock application events
      app.stopDispatchingEvents();
      isPlaying = false;
      btPlay.setIcon(new ImageIcon(app.getPlayImage()));
      btPlay.setText(Plain.Play);
      setComponentsEnabled(true);
      app.setDefaultCursor();
    }
  }
  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;
  private JButton btFirst, btPrev, btPlay, btNext, btLast, btOpenWindow;
  private final JLabel lbSteps;
  private final JSpinner spDelay;
  private double playDelay = 2; // in seconds

  private JPanel playPanel;
  private final Application app;
  private final ConstructionProtocol prot;

  private boolean showPlayButton = true, showConsProtButton = true;
  private AutomaticPlayer player;

  private boolean isPlaying;

  /**
   * Creates a new navigation bar to step through the construction protocol.
   * 
   * @param internalNavigation
   *          : true if navigation bar is part of the protocol window
   */
  public ConstructionProtocolNavigation(ConstructionProtocol prot) {
    this.prot = prot;
    app = prot.getApplication();

    SpinnerModel model = new SpinnerNumberModel(2, // initial value
        1, // min
        10, // max
        1); // step
    spDelay = new JSpinner(model);
    lbSteps = new JLabel();

    initGUI();
  }

  public void actionPerformed(ActionEvent e) {
    Object source = e.getSource();

    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    if (source == btFirst)
      prot.firstStep();
    else if (source == btLast)
      prot.lastStep();
    else if (source == btPrev)
      prot.previousStep();
    else if (source == btNext)
      prot.nextStep();
    else if (source == btPlay)
      if (isPlaying)
        player.stopAnimation();
      else {
        player = new AutomaticPlayer(playDelay);
        player.startAnimation();
      }

    if (prot.isVisible())
      prot.scrollToConstructionStep();

    setCursor(Cursor.getDefaultCursor());
  }

  /**
   * Returns delay between frames of automatic construction protocol playing in
   * seconds.
   * 
   * @return
   */
  public double getPlayDelay() {
    return playDelay;
  }

  public void initGUI() {
    removeAll();

    btFirst = new JButton(app.getImageIcon("nav_skipback.png"));
    btLast = new JButton(app.getImageIcon("nav_skipforward.png"));
    btPrev = new JButton(app.getImageIcon("nav_rewind.png"));
    btNext = new JButton(app.getImageIcon("nav_fastforward.png"));

    btFirst.addActionListener(this);
    btLast.addActionListener(this);
    btPrev.addActionListener(this);
    btNext.addActionListener(this);

    JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    leftPanel.add(btFirst);
    leftPanel.add(btPrev);
    leftPanel.add(lbSteps);
    leftPanel.add(btNext);
    leftPanel.add(btLast);

    playPanel = new JPanel();
    playPanel.setVisible(showPlayButton);
    playPanel.add(Box.createRigidArea(new Dimension(20, 10)));
    btPlay = new JButton();
    btPlay.setIcon(new ImageIcon(app.getPlayImage()));
    btPlay.addActionListener(this);

    spDelay.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        try {
          playDelay = Double.parseDouble(spDelay.getValue().toString());
        } catch (Exception ex) {
          playDelay = 2;
        }
      }
    });

    playPanel.add(btPlay);
    playPanel.add(spDelay);
    playPanel.add(new JLabel("s"));

    btOpenWindow = new JButton();
    btOpenWindow.setIcon(app.getImageIcon("table.gif"));
    btOpenWindow.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        app.getGuiManager().showConstructionProtocol();
      }
    });

    // add panels together to center
    setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
    add(leftPanel);
    add(playPanel);
    add(btOpenWindow);
    add(Box.createRigidArea(new Dimension(20, 10)));

    setLabels();
    setPlayDelay(playDelay);
    update();
  }

  public boolean isConsProtButtonVisible() {
    return showConsProtButton;
  }

  public boolean isPlayButtonVisible() {
    return showPlayButton;
  }

  /**
   * Registers this navigation bar at its protocol to be informed about updates.
   */
  public void register() {
    prot.registerNavigationBar(this);
    update();
  }

  private void setComponentsEnabled(boolean flag) {
    Component comps[] = getComponents();
    for (Component comp : comps)
      comp.setEnabled(flag);
    btPlay.setEnabled(true);
    lbSteps.setEnabled(true);
  }

  public void setConsProtButtonVisible(boolean flag) {
    showConsProtButton = flag;
    btOpenWindow.setVisible(flag);
  }

  public void setLabels() {
    if (btPlay != null)
      btPlay.setText(Plain.Play);
    if (btOpenWindow != null)
      btOpenWindow.setToolTipText(Plain.ConstructionProtocol);
  }

  public void setPlayButtonVisible(boolean flag) {
    showPlayButton = flag;
    playPanel.setVisible(flag);
  }

  public void setPlayDelay(double delay) {
    playDelay = delay;

    try {
      spDelay.setValue(new Double(playDelay));
    } catch (Exception e) {
      spDelay.setValue(new Integer((int) Math.round(playDelay)));

    }
  }

  /**
   * Unregisters this navigation bar from its protocol.
   */
  public void unregister() {
    prot.unregisterNavigationBar(this);
  }

  /**
   * Updates the texts that show the current construction step and the number of
   * construction steps.
   */
  protected void update() {
    int currentStep = prot.getCurrentStepNumber();
    int stepNumber = prot.getLastStepNumber();
    lbSteps.setText(currentStep + " / " + stepNumber);
  }
}
