package clearcontrol.devices.stages.devices.tst.variables;

import aptj.APTJDevice;
import clearcontrol.core.log.LoggingFeature;
import clearcontrol.core.variable.Variable;

/**
 * TST Stage variable base
 *
 * @author royer
 */
public abstract class TSTBooleanVariableBase extends Variable<Boolean>
                                             implements LoggingFeature
{

  protected final APTJDevice mAPTJDevice;

  /**
   * Instantiates a TST boolean variable
   * 
   * @param pVariableName
   *          variable name
   * @param pAPTJDevice
   *          APTJ device
   */
  public TSTBooleanVariableBase(String pVariableName,
                                APTJDevice pAPTJDevice)
  {
    super(pVariableName, false);
    mAPTJDevice = pAPTJDevice;
  }

  /**
   * Returns the APTJ device
   * 
   * @return APTJ device
   */
  public APTJDevice getAPTJDevice()
  {
    return mAPTJDevice;
  }

}
