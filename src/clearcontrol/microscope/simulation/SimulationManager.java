package clearcontrol.microscope.simulation;

import java.util.ArrayList;

import clearcontrol.core.device.VirtualDevice;
import clearcontrol.core.device.name.ReadOnlyNameableInterface;
import clearcontrol.core.device.sim.SimulationDeviceInterface;
import clearcontrol.core.log.LoggingFeature;
import clearcontrol.core.variable.Variable;
import clearcontrol.microscope.MicroscopeInterface;

/**
 * SimulationManager handles logging and other functionality required for
 * simulated devices.
 * 
 * @author royer
 *
 */
public class SimulationManager extends VirtualDevice implements
                               ReadOnlyNameableInterface,
                               LoggingFeature
{
  private final MicroscopeInterface<?> mMicroscopeInterface;

  private final Variable<Boolean> mLoggingOnVariable =
                                                     new Variable<Boolean>("LoggingOn");

  /**
   * Constructs an LoggingManager.
   * 
   * @param pMicroscopeInterface
   *          microscope
   */
  public SimulationManager(MicroscopeInterface<?> pMicroscopeInterface)
  {
    super("Simulation Manager");
    mMicroscopeInterface = pMicroscopeInterface;

    mLoggingOnVariable.addSetListener((o, n) -> {

      if (o == n)
        return;

      if (mMicroscopeInterface == null)
      {
        return;
      }

      info("Loggin for simulated devices is turned "
           + (n ? "on" : "off"));

      ArrayList<Object> lAllDeviceList =
                                       mMicroscopeInterface.getDeviceLists()
                                                           .getAllDeviceList();

      for (Object lDevice : lAllDeviceList)
      {
        if (lDevice instanceof SimulationDeviceInterface)
        {
          SimulationDeviceInterface lSimulationDeviceInterface =
                                                               (SimulationDeviceInterface) lDevice;

          lSimulationDeviceInterface.getSimLoggingVariable().set(n);
        }
      }
    });

  }

  /**
   * Returns logging-is-on variable
   * 
   * @return logging-is-on variable
   */
  public Variable<Boolean> getLoggingOnVariable()
  {
    return mLoggingOnVariable;
  }

}
