package clearcontrol.devices.stages.devices.tst.variables;

import aptj.APTJDevice;
import clearcontrol.core.log.LoggingFeature;
import clearcontrol.core.variable.Variable;

/**
 * TST Stage variable base
 *
 * @author royer
 */
public abstract class TSTDoubleVariableBase extends Variable<Double>
                                            implements LoggingFeature
{

  protected final APTJDevice mAPTJDevice;

  /**
   * Instantiates a TST double variable
   * 
   * @param pVariableName
   *          variable name
   * @param pAPTJDevice
   *          APTJ device
   */
  public TSTDoubleVariableBase(String pVariableName,
                               APTJDevice pAPTJDevice)
  {
    super(pVariableName, 0.0);
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
