package clearcontrol.core.device.sim;

import java.util.concurrent.ConcurrentHashMap;

import clearcontrol.core.variable.Variable;

/**
 * Simulation device interface
 *
 * @author royer
 */
public interface SimulationDeviceInterface
{
  @SuppressWarnings("javadoc")
  static final ConcurrentHashMap<Object, Variable<Boolean>> sLoggingVariableMap =
                                                                                new ConcurrentHashMap<>();

  /**
   * Returns the Simulation logging variable
   * 
   * @return variable
   */
  default public Variable<Boolean> getSimLoggingVariable()
  {
    Variable<Boolean> lVariable = sLoggingVariableMap.get(this);
    if (lVariable == null)
    {
      lVariable = new Variable<Boolean>("Logging", false);
      sLoggingVariableMap.put(this, lVariable);
    }
    return lVariable;
  };

  /**
   * Sets whether to log simulation messages or not
   * 
   * @param pSimulationLoggingOnFlag
   *          true -> log simulation messages
   */
  default public void setSimLogging(boolean pSimulationLoggingOnFlag)
  {
    getSimLoggingVariable().set(pSimulationLoggingOnFlag);
  }

  /**
   * Returns true if simulation messages are logged
   * 
   * @return true -> simulation messages logged
   */
  default public boolean isSimLogging()
  {
    return getSimLoggingVariable().get();
  }

}
