/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.gui.toolbar;
import geogebra.Plain;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ToolbarConfigDialog extends JDialog {

  /**
   * 
   */
  private static final long serialVersionUID = -6642249411249245675L;

  private final Application app;
  public ToolbarConfigPanel confPanel;

  public ToolbarConfigDialog(Application app) {
    super(app.getFrame(), true);
    this.app = app;

    setTitle(geogebra.Menu.Toolbar_Customize);

    getContentPane().setLayout(new BorderLayout(5, 5));
    confPanel = new ToolbarConfigPanel(app);
    getContentPane().add(confPanel, BorderLayout.CENTER);
    getContentPane().add(createButtonPanel(), BorderLayout.SOUTH);

    pack();
    setLocationRelativeTo(app.getFrame());
  }

  private void apply() {
    app.getGuiManager().setToolBarDefinition(confPanel.getToolBarString());
    app.updateToolBar();
    app.setUnsaved();
    setVisible(false);
    dispose();
  }

  private JPanel createButtonPanel() {
    JPanel btPanel = new JPanel();
    btPanel.setLayout(new BoxLayout(btPanel, BoxLayout.X_AXIS));
    btPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

    final JButton btDefaultToolbar = new JButton();
    btPanel.add(btDefaultToolbar);
    btDefaultToolbar.setText(geogebra.Menu.Toolbar_ResetDefault);

    btPanel.add(Box.createHorizontalGlue());
    final JButton btApply = new JButton();
    btPanel.add(btApply);
    btApply.setText(Plain.Apply);

    final JButton btCancel = new JButton();
    btPanel.add(Box.createRigidArea(new Dimension(5, 0)));
    btPanel.add(btCancel);
    btCancel.setText(Plain.Cancel);

    ActionListener ac = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btApply)
          apply();
        else if (src == btCancel) {
          setVisible(false);
          dispose();
        } else if (src == btDefaultToolbar)
          confPanel.setToolBarString(app.getGuiManager()
              .getDefaultToolbarString());
      }
    };
    btCancel.addActionListener(ac);
    btApply.addActionListener(ac);
    btDefaultToolbar.addActionListener(ac);

    return btPanel;
  }

}