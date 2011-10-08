/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.kernel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Manages macros (user defined tools).
 * 
 * @author Markus Hohenwarter
 */
public class MacroManager {

  /**
   * Returns an XML represenation of the specified macros in this kernel.
   */
  public static String getMacroXML(ArrayList<Macro> macros) {
    if (macros == null)
      return "";

    System.out.println("Построение XML'я для сохранения макросов");
    StringBuffer sb = new StringBuffer();
    // save selected macros
    for (int i = 0; i < macros.size(); i++)
      sb.append(macros.get(i).getXML());
    return sb.toString();
  }

  /**
   * maps macro name to macro object
   */
  private final HashMap<String, Macro> macroMap;

  /**
   * Список всех макросов
   */
  private final ArrayList<Macro> macroList;

  public MacroManager() {
    macroMap = new HashMap<String, Macro>();
    macroList = new ArrayList<Macro>();
  }

  public void addMacro(Macro macro) {
    macroMap.put(macro.getCommandName(), macro);
    macroList.add(macro);
  }

  /**
   * Returns an array of all macros handled by this MacroManager.
   * 
   * @return Список макросов
   */
  public ArrayList<Macro> getAllMacros() {
    return macroList;
  }

  public Macro getMacro(int i) {
    return macroList.get(i);
  }

  public Macro getMacro(String name) {
    return macroMap.get(name);
  }

  public int getMacroID(Macro macro) {
    for (int i = 0; i < macroList.size(); i++)
      if (macro == macroList.get(i))
        return i;
    return -1;
  }

  /**
   * Returns the current number of macros handled by this MacroManager.
   */
  public int getMacroNumber() {
    return macroList.size();
  }

  /**
   * Updates all macros that need to be
   */
  final void notifyEuclidianViewAlgos() {
    // save selected macros
    for (int i = 0; i < macroList.size(); i++) {
      Macro macro = macroList.get(i);
      macro.getMacroConstruction().notifyEuclidianViewAlgos();
    }
  }

  public void removeAllMacros() {
    macroMap.clear();
    macroList.clear();
  }

  public void removeMacro(Macro macro) {
    macroMap.remove(macro.getCommandName());
    macroList.remove(macro);
  }

  public void setAllMacrosUnused() {
    for (int i = 0; i < macroList.size(); i++)
      macroList.get(i).setUnused();
  }

  /**
   * Sets the command name of a macro.
   */
  public void setMacroCommandName(Macro macro, String cmdName) {
    macroMap.remove(macro.getCommandName());
    macro.setCommandName(cmdName);
    macroMap.put(macro.getCommandName(), macro);
  }

}
