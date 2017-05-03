package clearcontrol.core.device.switches;

import clearcontrol.core.variable.Variable;

/**
 * Switching device interface
 *
 * @author royer
 */
public interface SwitchingDeviceInterface
{
  /**
   * Returns the name of a given switch
   * 
   * @param pSwitchIndex
   *          switch index
   * @return switch name
   */
  String getSwitchName(int pSwitchIndex);

  /**
   * Returns the number of switches
   * 
   * @return number of switches
   */
  int getNumberOfSwitches();

  /**
   * Sets the state of a given switch
   * 
   * @param pSwitchIndex
   *          switch index
   * @param pSwitchState
   *          switch state
   */
  default void setSwitch(int pSwitchIndex, boolean pSwitchState)
  {
    getSwitchVariable(pSwitchIndex).set(pSwitchState);
  };

  /**
   * Returns a switch variable
   * 
   * @param pSwitchIndex
   *          switch index
   * @return switch variable
   */
  Variable<Boolean> getSwitchVariable(int pSwitchIndex);

}
