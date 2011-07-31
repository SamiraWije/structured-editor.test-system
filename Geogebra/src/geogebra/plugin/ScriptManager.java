package geogebra.plugin;

import geogebra.kernel.GeoElement;
import geogebra.main.Application;
import geogebra.main.View;

import java.util.ArrayList;
import java.util.HashMap;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class ScriptManager {

  /**
   * Implements the View interface for Java to JavaScript communication, see
   * addChangeListener() and removeChangeListener()
   */
  private class JavaToJavaScriptView implements View {

    /**
     * Calls all registered add listeners.
     * 
     * @see registerAddListener()
     */
    public void add(GeoElement geo) {
      if (addListeners != null && geo.isLabelSet()) {
        Object[] args = {geo.getLabel()};
        notifyListeners(addListeners, args);
      }
    }

    /**
     * Calls all registered clear listeners.
     * 
     * @see registerClearListener()
     */
    public void clearView() {
      /*
       * This code would make sense for a "reload"
       * 
       * // try to keep all update listeners if (updateListenerMap != null) {
       * HashMap newGeoJSfunMap = new HashMap();
       * 
       * // go through all geos and update their maps Iterator it =
       * updateListenerMap.keySet().iterator(); while (it.hasNext()) { // try to
       * find new geo with same label GeoElement oldGeo = (GeoElement)
       * it.next(); GeoElement newGeo = kernel.lookupLabel(oldGeo.getLabel());
       * 
       * if (newGeo != null) // add mapping to new map
       * newGeoJSfunMap.put(newGeo,(String) updateListenerMap.get(oldGeo)); }
       * 
       * // use new map updateListenerMap.clear(); updateListenerMap =
       * newGeoJSfunMap; }
       */

      app.getGgbApi().lastGeoElementsIteratorSize = 0; // ulven 29.08.05: should
                                                       // have been a method...
      updateListenerMap = null;
      if (clearListeners != null)
        notifyListeners(clearListeners, null);
    }

    /**
     * Calls all JavaScript functions (listeners) using the specified arguments.
     */
    private synchronized void notifyListeners(ArrayList<String> listeners,
        Object[] args) {
      int size = listeners.size();
      for (int i = 0; i < size; i++) {
        String jsFunction = listeners.get(i);
        callJavaScript(jsFunction, args);
      }
    }

    /**
     * Calls all registered remove listeners.
     * 
     * @see registerRemoveListener()
     */
    public void remove(GeoElement geo) {
      if (removeListeners != null && geo.isLabelSet()) {
        Object[] args = {geo.getLabel()};
        notifyListeners(removeListeners, args);
      }
    }

    /**
     * Calls all registered rename listeners.
     * 
     * @see registerRenameListener()
     */
    public void rename(GeoElement geo) {
      if (renameListeners != null && geo.isLabelSet()) {
        Object[] args = {geo.getOldLabel(), geo.getLabel()};
        notifyListeners(renameListeners, args);
      }
    }

    public void repaintView() {
      // no repaint should occur here: views that are
      // part of the applet do this on their own
    }

    public void reset() {
    }

    /**
     * Calls all registered update and updateObject listeners.
     * 
     * @see registerUpdateListener()
     */
    public synchronized void update(GeoElement geo) {
      // update listeners
      if (updateListeners != null && geo.isLabelSet()) {
        Object[] args = {geo.getLabel()};
        notifyListeners(updateListeners, args);
      }

      // updateObject listeners
      if (updateListenerMap != null) {
        String jsFunction = updateListenerMap.get(geo);
        if (jsFunction != null) {
          Object[] args = {geo.getLabel()};
          callJavaScript(jsFunction, args);
        }
      }
    }

    public void updateAuxiliaryObject(GeoElement geo) {
      update(geo);
    }
  }

  private final Application app;

  // maps between GeoElement and JavaScript function names
  private HashMap<GeoElement, String> updateListenerMap;

  /*
   * Change listener implementation Java to JavaScript
   */

  private ArrayList<String> addListeners, removeListeners, renameListeners,
      updateListeners, clearListeners;
  private JavaToJavaScriptView javaToJavaScriptView;
  public ScriptManager(Application app) {
    this.app = app;

    // evalScript("ggbOnInit();");
  }

  protected void callJavaScript(String jsFunction, Object[] args) {
    if (app.isApplet())
      app.getApplet().callJavaScript(jsFunction, args);
    else {

      StringBuffer sb = new StringBuffer();
      sb.append(jsFunction);
      sb.append("(");
      for (int i = 0; i < args.length; i++) {
        sb.append('"');
        sb.append(args[i].toString());
        sb.append('"');
        if (i < args.length - 1)
          sb.append(",");
      }
      sb.append(");");

      // Application.debug(sb.toString());

      evalScript(sb.toString());

    }
  }

  public boolean evalScript(String script) {
    boolean success = true;

    app.loadJavaScriptJar();

    Context cx = Context.enter();
    try {
      // Initialize the standard objects (Object, Function, etc.)
      // This must be done before scripts can be executed. Returns
      // a scope object that we use in later calls.
      Scriptable scope = cx.initStandardObjects();

      // initialise the JavaScript variable applet so that we can call
      // GgbApi functions, eg ggbApplet.evalCommand()
      Object wrappedOut = Context.javaToJS(app.getGgbApi(), scope);
      ScriptableObject.putProperty(scope, "ggbApplet", wrappedOut);

      // JavaScript to execute
      // String s = "ggbApplet.evalCommand('F=(2,3)')";

      cx.evaluateString(scope, script + app.getKernel().getLibraryJavaScript(),
          "<cmd>", 1, null);

      // Convert the result to a string and print it.
      // Application.debug("script result: "+(Context.toString(result)));
    } catch (Exception e) {
      success = false;
      e.printStackTrace();
    } finally {
      // Exit from the context.
      Context.exit();
    }

    return success;

  }
  /*
   * Change listener implementation Java to JavaScript
   */

  private synchronized void initJavaScript() {

    if (app.isApplet())
      app.getApplet().initJavaScript();
  }

  private synchronized void initJavaScriptView() {
    if (javaToJavaScriptView == null) {
      javaToJavaScriptView = new JavaToJavaScriptView();
      app.getKernel().attach(javaToJavaScriptView); // register view
      initJavaScript();
    }
  }

  /**
   * Registers a JavaScript function as an add listener for the applet's
   * construction. Whenever a new object is created in the GeoGebraApplet's
   * construction, the JavaScript function JSFunctionName is called using the
   * name of the newly created object as a single argument.
   */
  public synchronized void registerAddListener(String JSFunctionName) {
    if (JSFunctionName == null || JSFunctionName.length() == 0)
      return;

    // init view
    initJavaScriptView();

    // init list
    if (addListeners == null)
      addListeners = new ArrayList<String>();
    addListeners.add(JSFunctionName);
    Application.debug("registerAddListener: " + JSFunctionName);
  }

  /**
   * Registers a JavaScript function as a clear listener for the applet's
   * construction. Whenever the construction in the GeoGebraApplet's is cleared
   * (i.e. all objects are removed), the JavaScript function JSFunctionName is
   * called using no arguments.
   */
  public synchronized void registerClearListener(String JSFunctionName) {
    if (JSFunctionName == null || JSFunctionName.length() == 0)
      return;

    // init view
    initJavaScriptView();

    // init list
    if (clearListeners == null)
      clearListeners = new ArrayList<String>();
    clearListeners.add(JSFunctionName);
    Application.debug("registerClearListener: " + JSFunctionName);
  }

  /**
   * Registers a JavaScript update listener for an object. Whenever the object
   * with the given name changes, a JavaScript function named JSFunctionName is
   * called using the name of the changed object as the single argument. If
   * objName previously had a mapping JavaScript function, the old value is
   * replaced.
   * 
   * Example: First, set a change listening JavaScript function:
   * ggbApplet.setChangeListener("A", "myJavaScriptFunction"); Then the GeoGebra
   * Applet will call the Javascript function myJavaScriptFunction("A");
   * whenever object A changes.
   */
  public synchronized void registerObjectUpdateListener(String objName,
      String JSFunctionName) {
    if (JSFunctionName == null || JSFunctionName.length() == 0)
      return;
    GeoElement geo = app.getKernel().lookupLabel(objName);
    if (geo == null)
      return;

    // init view
    initJavaScriptView();

    // init map and view
    if (updateListenerMap == null)
      updateListenerMap = new HashMap<GeoElement, String>();

    // add map entry
    updateListenerMap.put(geo, JSFunctionName);
    Application.debug("registerUpdateListener: object: " + objName
        + ", function: " + JSFunctionName);
  }

  /**
   * Registers a JavaScript function as a remove listener for the applet's
   * construction. Whenever an object is deleted in the GeoGebraApplet's
   * construction, the JavaScript function JSFunctionName is called using the
   * name of the deleted object as a single argument.
   */
  public synchronized void registerRemoveListener(String JSFunctionName) {
    if (JSFunctionName == null || JSFunctionName.length() == 0)
      return;

    // init view
    initJavaScriptView();

    // init list
    if (removeListeners == null)
      removeListeners = new ArrayList<String>();
    removeListeners.add(JSFunctionName);
    Application.debug("registerRemoveListener: " + JSFunctionName);
  }

  /**
   * Registers a JavaScript function as a rename listener for the applet's
   * construction. Whenever an object is renamed in the GeoGebraApplet's
   * construction, the JavaScript function JSFunctionName is called using the
   * name of the deleted object as a single argument.
   */
  public synchronized void registerRenameListener(String JSFunctionName) {
    if (JSFunctionName == null || JSFunctionName.length() == 0)
      return;

    // init view
    initJavaScriptView();

    // init list
    if (renameListeners == null)
      renameListeners = new ArrayList<String>();
    renameListeners.add(JSFunctionName);
    Application.debug("registerRenameListener: " + JSFunctionName);
  }

  /**
   * Registers a JavaScript function as an update listener for the applet's
   * construction. Whenever any object is updated in the GeoGebraApplet's
   * construction, the JavaScript function JSFunctionName is called using the
   * name of the updated object as a single argument.
   */
  public synchronized void registerUpdateListener(String JSFunctionName) {
    if (JSFunctionName == null || JSFunctionName.length() == 0)
      return;

    // init view
    initJavaScriptView();

    // init list
    if (updateListeners == null)
      updateListeners = new ArrayList<String>();
    updateListeners.add(JSFunctionName);
    Application.debug("registerUpdateListener: " + JSFunctionName);
  }

  /**
   * Removes a previously registered add listener
   * 
   * @see registerAddListener()
   */
  public synchronized void unregisterAddListener(String JSFunctionName) {
    if (addListeners != null) {
      addListeners.remove(JSFunctionName);
      Application.debug("unregisterAddListener: " + JSFunctionName);
    }
  }

  /**
   * Removes a previously registered clear listener
   * 
   * @see registerClearListener()
   */
  public synchronized void unregisterClearListener(String JSFunctionName) {
    if (clearListeners != null) {
      clearListeners.remove(JSFunctionName);
      Application.debug("unregisterClearListener: " + JSFunctionName);
    }
  }
  /**
   * Removes a previously set change listener for the given object.
   * 
   * @see setChangeListener
   */
  public synchronized void unregisterObjectUpdateListener(String objName) {
    if (updateListenerMap != null) {
      GeoElement geo = app.getKernel().lookupLabel(objName);
      if (geo != null) {
        updateListenerMap.remove(geo);
        Application.debug("unregisterUpdateListener for object: " + objName);
      }
    }
  }

  /**
   * Removes a previously registered remove listener
   * 
   * @see registerRemoveListener()
   */
  public synchronized void unregisterRemoveListener(String JSFunctionName) {
    if (removeListeners != null) {
      removeListeners.remove(JSFunctionName);
      Application.debug("unregisterRemoveListener: " + JSFunctionName);
    }
  }

  /**
   * Removes a previously registered rename listener.
   * 
   * @see registerRenameListener()
   */
  public synchronized void unregisterRenameListener(String JSFunctionName) {
    if (renameListeners != null) {
      renameListeners.remove(JSFunctionName);
      Application.debug("unregisterRenameListener: " + JSFunctionName);
    }
  }

  /**
   * Removes a previously registered update listener.
   * 
   * @see registerRemoveListener()
   */
  public synchronized void unregisterUpdateListener(String JSFunctionName) {
    if (updateListeners != null) {
      updateListeners.remove(JSFunctionName);
      Application.debug("unregisterUpdateListener: " + JSFunctionName);
    }
  }

}
