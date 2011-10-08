package geogebra.cas.view.components;

/**
 * CmdBtnEval - Button for Simplify
 */
class CmdBtnEval extends CommandButton_ABS {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private static CmdBtnEval singleton = null;

  // / --- Interface --- ///
  /**
   * @return
   */
  public static CmdBtnEval getInstance() {
    if (singleton == null)
      singleton = new CmdBtnEval();
    return singleton;
  }

}
