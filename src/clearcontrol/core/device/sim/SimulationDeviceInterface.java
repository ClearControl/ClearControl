package clearcontrol.core.device.sim;

import java.util.concurrent.ConcurrentHashMap;

import clearcontrol.core.variable.Variable;

/**
 *
 *
 * @author royer
 */
public interface SimulationDeviceInterface
{
  public static ConcurrentHashMap<Object, Variable<Boolean>> sLoggingVariableMap =
                                                                                 new ConcurrentHashMap<>();

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

  default public void setSimLogging(boolean pSimulationLoggingOnFlag)
  {
    getSimLoggingVariable().set(pSimulationLoggingOnFlag);
  }

  default public boolean isSimLogging()
  {
    return getSimLoggingVariable().get();
  }

}
