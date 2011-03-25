package geogebra.gui.menubar;

import geogebra.gui.ToolCreationDialog;
import geogebra.gui.ToolManagerDialog;
import geogebra.gui.toolbar.MyToolbar;
import geogebra.main.Application;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * The "Tools" menu.
 */
class ToolsMenu extends BaseMenu {
  private static final long serialVersionUID = -2012951866084095682L;

  private AbstractAction toolbarConfigAction, showCreateToolsAction,
      showManageToolsAction, modeChangeAction;

  protected ToolsMenu(Application app) {
    super(app, geogebra.Menu.Tools);

    initActions();
    update();
  }

  /**
   * Initialize the actions.
   */
  private void initActions() {
    toolbarConfigAction = new AbstractAction(geogebra.Menu.Toolbar_Customize
        + " ...", app.getEmptyIcon()) {
      private static final long serialVersionUID = 1L;

      public void actionPerformed(ActionEvent e) {
        app.getGuiManager().showToolbarConfigDialog();
      }
    };

    // Florian Sonner 2008-08-13
    modeChangeAction = new AbstractAction() {
      public static final long serialVersionUID = 1L;

      public void actionPerformed(ActionEvent e) {
        app.setMode(Integer.parseInt(e.getActionCommand()));
      }
    };

    showCreateToolsAction = new AbstractAction(geogebra.Menu.Tool_CreateNew
        + " ...", app.getImageIcon("tool.png")) {
      private static final long serialVersionUID = 1L;

      public void actionPerformed(ActionEvent e) {
        ToolCreationDialog tcd = new ToolCreationDialog(app);
        tcd.setVisible(true);
      }
    };

    showManageToolsAction = new AbstractAction(geogebra.Menu.Tool_Manage
        + " ...", app.getImageIcon("document-properties.png")) {
      private static final long serialVersionUID = 1L;

      public void actionPerformed(ActionEvent e) {
        ToolManagerDialog tmd = new ToolManagerDialog(app);
        tmd.setVisible(true);
      }
    };
  }

  @Override
  public void update() {
    updateItems();

    // TODO update labels
  }

  /**
   * Initialize the menu items.
   */
  private void updateItems() {
    removeAll();

    add(toolbarConfigAction);
    addSeparator();
    add(showCreateToolsAction);
    add(showManageToolsAction);
    addSeparator();

    JMenu[] modeMenus = new JMenu[12];
    modeMenus[0] = new JMenu(geogebra.Menu.MovementTools);
    modeMenus[1] = new JMenu(geogebra.Menu.PointTools);
    modeMenus[2] = new JMenu(geogebra.Menu.BasicLineTools);
    modeMenus[3] = new JMenu(geogebra.Menu.SpecialLineTools);
    modeMenus[4] = new JMenu(geogebra.Menu.PolygonTools);
    modeMenus[5] = new JMenu(geogebra.Menu.CircleArcTools);
    modeMenus[6] = new JMenu(geogebra.Menu.ConicSectionTools);
    modeMenus[7] = new JMenu(geogebra.Menu.MeasurementTools);
    modeMenus[8] = new JMenu(geogebra.Menu.TransformationTools);
    modeMenus[9] = new JMenu(geogebra.Menu.SpecialObjectTools);
    modeMenus[10] = new JMenu(geogebra.Menu.GeneralTools);
    modeMenus[11] = new JMenu(geogebra.Menu.CustomTools);

    for (int i = 0; i < modeMenus.length; ++i) {
      modeMenus[i].setIcon(app.getEmptyIcon());
      add(modeMenus[i]);
    }

    MyToolbar toolbar = new MyToolbar(app);
    Vector<Serializable> modes = MyToolbar.createToolBarVec(toolbar
        .getDefaultToolbarString());

    int menuIndex = 0;

    for (Iterator<Serializable> iter = modes.iterator(); iter.hasNext();) {
      Object next = iter.next();
      if (next instanceof Vector<?>) {
        for (Iterator<?> iter2 = ((Vector<?>) next).iterator(); iter2.hasNext();) {
          Object next2 = iter2.next();

          if (next2 instanceof Integer) {
            int mode = ((Integer) next2).intValue();

            if (mode < 0)
              modeMenus[menuIndex].addSeparator();
            else {
              JMenuItem item = new JMenuItem(app.getToolName(mode));// ,
              // app.getModeIcon(mode));
              item.setActionCommand(Integer.toString(mode));
              item.addActionListener(modeChangeAction);
              item.setToolTipText(app.getToolHelp(mode));
              modeMenus[menuIndex].add(item);
            }
          } else
            Application.debug("Nested default toolbar not supported");
        }

        ++menuIndex;
      }
    }

    if (modeMenus[modeMenus.length - 1].getItemCount() == 0)
      modeMenus[modeMenus.length - 1].setEnabled(false);
  }
}
