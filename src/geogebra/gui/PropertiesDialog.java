/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.gui;

import geogebra.Plain;
import geogebra.euclidian.EuclidianView;
import geogebra.gui.view.algebra.AlgebraView;
import geogebra.kernel.ConstructionDefaults;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.Application;
import geogebra.main.GeoElementSelectionListener;
import geogebra.main.View;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;

/**
 * @author Markus Hohenwarter
 */
public class PropertiesDialog extends JDialog
    implements
      WindowListener,
      WindowFocusListener,
      TreeSelectionListener,
      KeyListener,
      GeoElementSelectionListener {

  /**
   * INNER CLASS JList for displaying GeoElements
   * 
   * @see GeoTreeCellRenderer
   * @author Markus Hohenwarter
   */
  private class JTreeGeoElements extends JTree
      implements
        View,
        MouseMotionListener,
        MouseListener {

    private static final long serialVersionUID = 1L;
    private final DefaultTreeModel treeModel;
    private final DefaultMutableTreeNode root;
    private final HashMap<String, DefaultMutableTreeNode> typeNodesMap;

    /*
     * has to be registered as view for GeoElement
     */
    public JTreeGeoElements() {
      // build default tree structure
      root = new DefaultMutableTreeNode();

      // create model from root node
      treeModel = new DefaultTreeModel(root);
      setModel(treeModel);
      setLargeModel(true);
      typeNodesMap = new HashMap<String, DefaultMutableTreeNode>();

      getSelectionModel().setSelectionMode(
          TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
      GeoTreeCellRenderer renderer = new GeoTreeCellRenderer(app);
      setCellRenderer(renderer);
      setRowHeight(-1); // to enable flexible height of cells

      // tree's options
      setRootVisible(true);
      // show lines from parent to children
      // putClientProperty("JTree.lineStyle", "None");
      setInvokesStopCellEditing(true);
      setScrollsOnExpand(true);

      addMouseMotionListener(this);
      addMouseListener(this);
    }

    /**
     * adds a new element to the list
     */
    final public void add(GeoElement geo) {
      if (!geo.isLabelSet() || !geo.hasProperties())
        return;

      // get type node
      String typeString = geo.getObjectType();
      DefaultMutableTreeNode typeNode = typeNodesMap.get(typeString);

      // init type node
      boolean initing = typeNode == null;
      if (initing) {
        String transTypeString = geo.translatedTypeString();
        typeNode = new DefaultMutableTreeNode(transTypeString);
        typeNodesMap.put(typeString, typeNode);

        // find insert pos
        int pos = root.getChildCount();
        for (int i = 0; i < pos; i++) {
          DefaultMutableTreeNode child = (DefaultMutableTreeNode) root
              .getChildAt(i);
          if (transTypeString.compareTo(child.toString()) < 0) {
            pos = i;
            break;
          }
        }

        treeModel.insertNodeInto(typeNode, root, pos);
      }

      // check if already present in type node
      int pos = AlgebraView.binarySearchGeo(typeNode, geo.getLabel());
      if (pos >= 0)
        return;

      // add geo to type node
      DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(geo);
      pos = AlgebraView.getInsertPosition(typeNode, geo);
      treeModel.insertNodeInto(newNode, typeNode, pos);

      // make sure something is selected
      if (getSelectionModel().isSelectionEmpty())
        selectFirstElement();

      /*
       * if (isShowing()) { TreePath geoPath = new TreePath(newNode.getPath());
       * //addSelectionPath(geoPath); makeVisible(geoPath); }
       */
    }

    /**
     * Clears the list.
     */
    private void clear() {
      root.removeAllChildren();
      treeModel.reload();
      typeNodesMap.clear();
    }

    @Override
    public void clearSelection() {
      getSelectionModel().clearSelection();
    }

    public void clearView() {
      clear();
    }

    public void collapseAll() {
      int row = 1;
      while (row < getRowCount()) {
        collapseRow(row);
        row++;
      }
    }

    public void expandAll() {
      int row = 0;
      while (row < getRowCount()) {
        expandRow(row);
        row++;
      }
    }

    /**
     * returns geo's TreePath
     */
    private TreePath getGeoPath(GeoElement geo) {
      String typeString = geo.getObjectType();
      DefaultMutableTreeNode typeNode = typeNodesMap.get(typeString);
      if (typeNode == null)
        return null;

      int pos = AlgebraView.binarySearchGeo(typeNode, geo.getLabel());
      if (pos == -1)
        return null;
      else {
        // add to selection
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) typeNode
            .getChildAt(pos);

        // expand typenode
        TreePath tp = new TreePath(node.getPath());

        return tp;
      }
    }

    /**
     * Returns the tree path of geo
     * 
     * @return returns null if geo is not in tree
     */
    private TreePath getTreePath(GeoElement geo) {
      DefaultMutableTreeNode typeNode = typeNodesMap.get(geo.getObjectType());
      if (typeNode == null)
        return null;

      // find pos of geo
      int pos = AlgebraView.binarySearchGeo(typeNode, geo.getLabel());
      if (pos == -1)
        return null;

      return new TreePath(((DefaultMutableTreeNode) typeNode.getChildAt(pos))
          .getPath());
    }

    /**
     * Handles clicks on the show/hide icon to toggle the show-object status.
     */
    public void mouseClicked(MouseEvent e) {
      if (Application.isControlDown(e) || e.isShiftDown())
        return;

      // get GeoElement at mouse location
      TreePath tp = getPathForLocation(e.getX(), e.getY());
      GeoElement geo = AlgebraView.getGeoElementForPath(tp);

      if (geo != null) {
        // check if we clicked on the 16x16 show/hide icon
        Rectangle rect = getPathBounds(tp);
        boolean iconClicked = rect != null && e.getX() - rect.x < 13; // distance
        // from
        // left
        // border
        if (iconClicked) {
          // icon clicked: toggle show/hide
          geo.setEuclidianVisible(!geo.isSetEuclidianVisible());
          geo.update();
          kernel.notifyRepaint();

          // update properties dialog by selecting this geo again
          geoElementSelected(geo, false);
        }
      }
    }

    /* ********************* */
    /* VIEW IMPLEMENTATION */
    /* ********************* */

    public void mouseDragged(MouseEvent arg0) {
    }

    public void mouseEntered(MouseEvent arg0) {

    }

    public void mouseExited(MouseEvent arg0) {
    }

    public void mouseMoved(MouseEvent e) {
      Point loc = e.getPoint();
      GeoElement geo = AlgebraView.getGeoElementForLocation(this, loc.x, loc.y);
      EuclidianView ev = app.getEuclidianView();

      // tell EuclidianView to handle mouse over
      ev.mouseMovedOver(geo);
      if (geo != null)
        setToolTipText(geo.getLongDescriptionHTML(true, true));
      else
        setToolTipText(null);
    }

    public void mousePressed(MouseEvent arg0) {
    }

    public void mouseReleased(MouseEvent arg0) {
    }

    /**
     * removes an element from the list
     */
    public void remove(GeoElement geo) {
      remove(geo, true);

      // close dialog if no elements left
      if (root.getChildCount() == 0) {
        closeDialog();
        return;
      }

      // make sure something is selected
      if (getSelectionModel().isSelectionEmpty())
        selectFirstElement();
    }

    /**
     * 
     * @param geo
     * @param binarySearch
     *          : true for binary, false for linear search
     */
    public void remove(GeoElement geo, boolean binarySearch) {
      // get type node
      DefaultMutableTreeNode typeNode = typeNodesMap.get(geo.getObjectType());
      if (typeNode == null)
        return;

      int pos = binarySearch ? AlgebraView.binarySearchGeo(typeNode, geo
          .getLabel()) : AlgebraView.linearSearchGeo(typeNode, geo.getLabel());
      if (pos > -1) {
        DefaultMutableTreeNode child = (DefaultMutableTreeNode) typeNode
            .getChildAt(pos);
        treeModel.removeNodeFromParent(child);

        if (typeNode.getChildCount() == 0) {
          // last child
          typeNodesMap.remove(geo.getObjectType());
          treeModel.removeNodeFromParent(typeNode);
        }
      }
    }

    /**
     * renames an element and sorts list
     */
    public void rename(GeoElement geo) {
      // the rename destroyed the alphabetical order,
      // so we have to use linear instead of binary search
      remove(geo, false);
      add(geo);
      geoElementSelected(geo, false);
    }

    final public void repaintView() {
      repaint();
    }

    public void reset() {
      repaint();
    }

    private void selectFirstElement() {
      // select all if list is not empty
      if (root.getChildCount() > 0) {
        DefaultMutableTreeNode typeNode = (DefaultMutableTreeNode) root
            .getFirstChild();
        TreePath tp = new TreePath(((DefaultMutableTreeNode) typeNode
            .getFirstChild()).getPath());
        setSelectionPath(tp); // select
      }
    }

    @Override
    protected void setExpandedState(TreePath path, boolean state) {
      // Ignore all collapse requests of root
      if (path != getPathForRow(0))
        super.setExpandedState(path, state);
    }

    public void setLabels() {
      root.setUserObject(Plain.Objects);

      // iterate through all type nodes and update the labels
      for (String key : typeNodesMap.keySet())
        typeNodesMap.get(key).setUserObject(app.getPlain1(key));
    }

    /**
     * selects object geo in the list of GeoElements
     * 
     * @param addToSelection
     *          : false => clear old selection
     */
    public void setSelected(ArrayList geos, boolean addToSelection) {
      TreePath tp = null;

      TreeSelectionModel lsm = getSelectionModel();
      if (geos == null || geos.size() == 0) {
        lsm.clearSelection();
        selectFirstElement();
      } else {
        // make sure geos are in list, this is needed when MAX_OBJECTS_IN_TREE
        // was
        // exceeded in setViewActive(true)
        // for (int i=0; i < geos.size(); i++) {
        // GeoElement geo = (GeoElement) geos.get(i);
        // add(geo);
        // }

        if (!addToSelection)
          lsm.clearSelection();

        // get paths for all geos
        ArrayList<TreePath> paths = new ArrayList<TreePath>();
        for (int i = 0; i < geos.size(); i++) {
          TreePath result = getGeoPath((GeoElement) geos.get(i));
          if (result != null) {
            tp = result;
            expandPath(result);
            paths.add(result);
          }
        }

        // select geo paths
        TreePath[] selPaths = new TreePath[paths.size()];
        for (int i = 0; i < selPaths.length; i++)
          selPaths[i] = paths.get(i);
        lsm.addSelectionPaths(selPaths);

        if (tp != null && geos.size() == 1)
          scrollPathToVisible(tp);
      }
    }

    /**
     * updates a list of elements
     */
    public void update(GeoElement geo) {
      repaint();
    }

    public void updateAuxiliaryObject(GeoElement geo) {
      repaint();
    }

  } // JTreeGeoElements
  // private static final int MAX_OBJECTS_IN_TREE = 500;
  private static final int MAX_GEOS_FOR_EXPAND_ALL = 15;
  private static final long serialVersionUID = 1L;
  private final Application app;
  private final Kernel kernel;
  private final JTreeGeoElements geoTree;
  private JButton closeButton, defaultsButton, delButton;
  private PropertiesPanel propPanel;

  private JColorChooser colChooser;
  final static int TEXT_FIELD_FRACTION_DIGITS = 3;

  final static int SLIDER_MAX_WIDTH = 170;
  final private static int MIN_WIDTH = 500;

  final private static int MIN_HEIGHT = 300;

  private boolean firstTime = true;

  private boolean viewActive = false;

  /*
   * public void cancel() {
   * setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
   * kernel.detach(geoTree);
   * 
   * // remember current construction step int consStep =
   * kernel.getConstructionStep();
   * 
   * // restore old construction state app.restoreCurrentUndoInfo();
   * 
   * // go to current construction step ConstructionProtocol cp =
   * app.getConstructionProtocol(); if (cp != null) {
   * cp.setConstructionStep(consStep); }
   * 
   * setCursor(Cursor.getDefaultCursor()); setVisible(false); }
   * 
   * public void apply() {
   * setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
   * app.storeUndoInfo(); setCursor(Cursor.getDefaultCursor());
   * setVisible(false); }
   */

  private final ArrayList<Object> selectionList = new ArrayList<Object>();

  private final ArrayList tempArrayList = new ArrayList();

  /**
   * Creates new PropertiesDialog.
   * 
   * @param app
   *          : parent frame
   */
  public PropertiesDialog(Application app) {
    super(app.getFrame(), false);
    this.app = app;
    kernel = app.getKernel();

    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    setResizable(true);

    addWindowListener(this);
    geoTree = new JTreeGeoElements();
    geoTree.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        // some textfields are updated when they lose focus
        // give them a chance to do that before we change the selection
        requestFocusInWindow();
      }
    });
    geoTree.addTreeSelectionListener(this);
    geoTree.addKeyListener(this);

    // build GUI
    initGUI();
  }

  /**
   * Reset the visual style of the selected elements.
   * 
   * TODO Does not work with lists (F.S.)
   */
  private void applyDefaults() {
    GeoElement geo;
    ConstructionDefaults defaults = kernel.getConstruction()
        .getConstructionDefaults();

    for (int i = 0; i < selectionList.size(); ++i) {
      geo = (GeoElement) selectionList.get(i);
      defaults.setDefaultVisualStyles(geo, true);
      geo.updateRepaint();
    }

    propPanel.updateSelection(selectionList.toArray());
  }
  public void cancel() {
    setVisible(false);
  }

  public void closeDialog() {
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    app.storeUndoInfo();
    setCursor(Cursor.getDefaultCursor());
    setVisible(false);
  }

  /**
   * deletes all selected GeoElements from Kernel
   */
  private void deleteSelectedGeos() {
    ArrayList<Object> selGeos = selectionList;

    if (selGeos.size() > 0) {
      Object[] geos = selGeos.toArray();
      for (int i = 0; i < geos.length - 1; i++)
        ((GeoElement) geos[i]).removeOrSetUndefinedIfHasFixedDescendent();

      // select element above last to delete
      GeoElement geo = (GeoElement) geos[geos.length - 1];
      TreePath tp = geoTree.getTreePath(geo);
      if (tp != null) {
        int row = geoTree.getRowForPath(tp);
        tp = geoTree.getPathForRow(row - 1);
        geo.removeOrSetUndefinedIfHasFixedDescendent();
        if (tp != null)
          geoTree.setSelectionPath(tp);
      }
    }
  }
  public void geoElementSelected(GeoElement geo, boolean addToSelection) {
    if (geo == null)
      return;
    tempArrayList.clear();
    tempArrayList.add(geo);
    geoTree.setSelected(tempArrayList, addToSelection);
    // requestFocus();
  }

  /**
   * inits GUI with labels of current language
   */
  public void initGUI() {
    geoTree.setFont(app.plainFont);

    boolean wasShowing = isShowing();
    if (wasShowing)
      setVisible(false);

    // LIST PANEL
    JScrollPane listScroller = new JScrollPane(geoTree);
    listScroller.setMinimumSize(new Dimension(120, 200));
    listScroller.setBackground(geoTree.getBackground());
    listScroller.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

    // delete button
    delButton = new JButton(app.getImageIcon("delete_small.gif"));
    delButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        deleteSelectedGeos();
      }
    });

    // apply defaults button
    defaultsButton = new JButton();
    defaultsButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        applyDefaults();
      }
    });

    closeButton = new JButton();
    closeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        closeDialog();
      }
    });

    // build button panel with some buttons on the left
    // and some on the right
    JPanel buttonPanel = new JPanel(new BorderLayout());
    JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(leftButtonPanel, BorderLayout.WEST);
    buttonPanel.add(rightButtonPanel, BorderLayout.EAST);

    // left buttons
    if (app.letDelete())
      leftButtonPanel.add(delButton);
    leftButtonPanel.add(defaultsButton);

    // right buttons
    rightButtonPanel.add(closeButton);

    // PROPERTIES PANEL
    if (colChooser == null) {
      // init color chooser
      colChooser = new JColorChooser();
      colChooser.setColor(new Color(1, 1, 1, 100));
    }

    // check for null added otherwise you get two listeners for the colChooser
    // when a file is loaded
    if (propPanel == null) {
      propPanel = new PropertiesPanel(app, colChooser, false);
      propPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
    }
    selectionChanged(); // init propPanel

    // put it all together
    Container contentPane = getContentPane();
    contentPane.removeAll();
    // contentPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

    JSplitPane splitPane = new JSplitPane();
    splitPane.setLeftComponent(listScroller);
    splitPane.setRightComponent(propPanel);

    contentPane.setLayout(new BorderLayout());
    contentPane.add(splitPane, BorderLayout.CENTER);
    contentPane.add(buttonPanel, BorderLayout.SOUTH);

    if (wasShowing)
      setVisible(true);

    setLabels();
  }

  /*
   * KeyListener
   */
  public void keyPressed(KeyEvent e) {
    Object src = e.getSource();

    if (src instanceof JTreeGeoElements)
      if (e.getKeyCode() == KeyEvent.VK_DELETE)
        deleteSelectedGeos();
  }
  public void keyReleased(KeyEvent e) {
  }

  public void keyTyped(KeyEvent e) {
  }
  /**
   * handles selection change
   */
  private void selectionChanged() {
    updateSelectedGeos(geoTree.getSelectionPaths());

    Object[] geos = selectionList.toArray();
    propPanel.updateSelection(geos);
    // Util.addKeyListenerToAll(propPanel, this);

    // update selection of application too
    if (app.getMode() == EuclidianView.MODE_SELECTION_LISTENER)
      app.setSelectedGeos(selectionList);
  }

  /**
   * Update the labels of this dialog.
   * 
   * TODO Create "Apply Defaults" phrase (F.S.)
   */
  public void setLabels() {
    setTitle(Plain.Properties);
    geoTree.root.setUserObject(Plain.Objects);

    delButton.setText(Plain.Delete);
    closeButton.setText(geogebra.Menu.Close);
    defaultsButton.setText(geogebra.Menu.ApplyDefaults);

    geoTree.setLabels();
    propPanel.setLabels();
  }

  /**
   * renames first selected GeoElement
   * 
   * private void rename() { ArrayList selGeos = selectionList; if
   * (selGeos.size() > 0) { GeoElement geo = (GeoElement) selGeos.get(0);
   * app.showRenameDialog(geo, false, null);
   * 
   * selectionList.clear(); selectionList.add(geo);
   * geoTree.setSelected(selectionList, false); } }
   */

  private void setViewActive(boolean flag) {
    if (flag == viewActive)
      return;
    viewActive = flag;

    if (flag) {
      geoTree.clear();
      kernel.attach(geoTree);

      // // only add objects if there are less than 200
      // int geoSize =
      // kernel.getConstruction().getGeoSetConstructionOrder().size();
      // if (geoSize < MAX_OBJECTS_IN_TREE)
      kernel.notifyAddAll(geoTree);

      app.setSelectionListenerMode(this);
      addWindowFocusListener(this);
    } else {
      kernel.detach(geoTree);

      removeWindowFocusListener(this);
      app.setSelectionListenerMode(null);
    }
  }

  @Override
  public void setVisible(boolean visible) {
    if (visible)
      setVisibleWithGeos(null);
    else {
      super.setVisible(false);
      setViewActive(false);
    }
  }

  /**
   * shows this dialog and select GeoElement geo at screen position location
   */
  public void setVisibleWithGeos(ArrayList geos) {
    setViewActive(true);

    if (kernel.getConstruction().getGeoSetConstructionOrder().size() < MAX_GEOS_FOR_EXPAND_ALL)
      geoTree.expandAll();
    else
      geoTree.collapseAll();

    geoTree.setSelected(geos, false);
    if (!isShowing()) {
      // pack and center on first showing
      if (firstTime) {
        pack();
        setLocationRelativeTo(app.getMainComponent());
        firstTime = false;
      }

      // ensure min size
      Dimension dim = getSize();
      if (dim.width < MIN_WIDTH) {
        dim.width = MIN_WIDTH;
        setSize(dim);
      }
      if (dim.height < MIN_HEIGHT) {
        dim.height = MIN_HEIGHT;
        setSize(dim);
      }

      super.setVisible(true);
    }
  }

  private ArrayList<Object> updateSelectedGeos(TreePath[] selPath) {
    selectionList.clear();

    if (selPath != null)
      // add all selected paths
      for (int i = 0; i < selPath.length; i++) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath[i]
            .getLastPathComponent();

        if (node == node.getRoot()) {
          // root: add all objects
          selectionList.clear();
          selectionList.addAll(app.getKernel().getConstruction()
              .getGeoSetLabelOrder());
          i = selPath.length;
        } else if (node.getParent() == node.getRoot())
          // type node: select all children
          for (int k = 0; k < node.getChildCount(); k++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) node
                .getChildAt(k);
            selectionList.add(child.getUserObject());
          }
        else
          // GeoElement
          selectionList.add(node.getUserObject());
      }

    return selectionList;
  }

  // Tree selection listener
  public void valueChanged(TreeSelectionEvent e) {
    selectionChanged();
  }
  /**
   * redefines first selected GeoElement
   * 
   * private void redefine() { ArrayList selGeos = selectionList;
   * geoTree.clearSelection(); if (selGeos.size() > 0)
   * app.showRedefineDialog((GeoElement) selGeos.get(0)); }
   */

  /*
   * Window Listener
   */
  public void windowActivated(WindowEvent e) {
    /*
     * if (!isModal()) { geoTree.setSelected(null, false); //selectionChanged();
     * } repaint();
     */
  }
  public void windowClosed(WindowEvent e) {
  }

  public void windowClosing(WindowEvent e) {
    // cancel();
    closeDialog();
  }

  /*
   * Keylistener implementation of PropertiesDialog
   * 
   * 
   * public void keyPressed(KeyEvent e) { int code = e.getKeyCode(); switch
   * (code) { case KeyEvent.VK_ESCAPE : //cancel(); closeDialog(); break;
   * 
   * case KeyEvent.VK_ENTER : // needed for input fields
   * //applyButton.doClick(); break; } }
   * 
   * public void keyReleased(KeyEvent e) { }
   * 
   * public void keyTyped(KeyEvent e) { }
   */

  public void windowDeactivated(WindowEvent e) {
  }

  public void windowDeiconified(WindowEvent e) {
  }

  public void windowGainedFocus(WindowEvent arg0) {
    // make sure this dialog is the current selection listener
    if (app.getMode() != EuclidianView.MODE_SELECTION_LISTENER
        || app.getCurrentSelectionListener() != this) {
      app.setSelectionListenerMode(this);
      selectionChanged();
    }
  }

  public void windowIconified(WindowEvent e) {
  }

  public void windowLostFocus(WindowEvent arg0) {
  }

  public void windowOpened(WindowEvent e) {
  }

} // PropertiesDialog