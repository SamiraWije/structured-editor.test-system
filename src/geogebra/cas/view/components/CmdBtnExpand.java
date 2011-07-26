package geogebra.cas.view.components;

/**
 * CmdBtnExpand
 */
class CmdBtnExpand extends CommandButton_ABS {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private static CmdBtnExpand singleton = null;

  protected static CmdBtnExpand getInstance() {
    if (singleton == null)
      singleton = new CmdBtnExpand();
    return singleton;
  }

}
