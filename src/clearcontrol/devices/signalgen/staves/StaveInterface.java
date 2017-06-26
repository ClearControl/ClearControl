package clearcontrol.devices.signalgen.staves;

import clearcontrol.core.device.name.NameableInterface;

/**
 * Stave Interface
 * 
 * At the most abstract level, a stave has duration in time and knows the signal
 * value at any point in normalized relative time [0,1].
 *
 * @author Loic Royer (2015)
 *
 */
public interface StaveInterface extends NameableInterface, Cloneable
{

  /**
   * Makes a copy of this stave
   * 
   * @return field-for-field copy
   */
  StaveInterface duplicate();

  /**
   * Sets whether this stave is enabled.
   * 
   * @param pEnabled
   *          true if enabled, false otherwise
   */
  void setEnabled(boolean pEnabled);

  /**
   * Returns whether this stave is enabled.
   * 
   * @return true if enabled, false otherwise
   */
  boolean isEnabled();

  /**
   * Returns value at given normalized time.
   * 
   * @param pNormalizedTime
   *          normalized time within [0,1]
   * @return value
   */
  float getValue(float pNormalizedTime);

}
