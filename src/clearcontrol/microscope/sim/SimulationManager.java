package clearcontrol.microscope.sim;

import java.util.ArrayList;

import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.device.VirtualDevice;
import clearcontrol.device.name.ReadOnlyNameableInterface;
import clearcontrol.device.sim.SimulationDeviceInterface;
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
                               LoggingInterface
{
  private final MicroscopeInterface mMicroscopeInterface;

  private final Variable<Boolean> mLoggingOnVariable =
                                                     new Variable<Boolean>("LoggingOn");

  /**
   * Constructs an LoggingManager.
   */
  public SimulationManager(MicroscopeInterface pMicroscopeInterface)
  {
    super("SimulationManager");
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

  public Variable<Boolean> getLoggingOnVariable()
  {
    return mLoggingOnVariable;
  }

}
