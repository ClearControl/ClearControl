package clearcontrol.core.device.startstop;

import clearcontrol.core.variable.Variable;

/**
 * Interface providing methods for accessing the start and stop signal variables
 * of devices.
 *
 * @author royer
 */
public interface StartStopSignalVariablesInterface
{
  /**
   * Returns start signal variable
   * 
   * @return start signal variable
   */
  Variable<Boolean> getStartSignalVariable();

  /**
   * Returns stop signal variable
   * 
   * @return stop signal variable
   */
  Variable<Boolean> getStopSignalVariable();

}
