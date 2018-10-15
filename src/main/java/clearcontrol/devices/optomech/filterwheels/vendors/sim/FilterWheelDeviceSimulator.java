package clearcontrol.devices.optomech.filterwheels.devices.sim;

import clearcontrol.core.device.sim.SimulationDeviceInterface;
import clearcontrol.core.log.LoggingFeature;
import clearcontrol.devices.optomech.filterwheels.FilterWheelDeviceBase;
import clearcontrol.devices.optomech.filterwheels.FilterWheelDeviceInterface;

public class FilterWheelDeviceSimulator extends FilterWheelDeviceBase
                                        implements
                                        FilterWheelDeviceInterface,
                                        LoggingFeature,
                                        SimulationDeviceInterface
{

  public FilterWheelDeviceSimulator(String pDeviceName,
                                    int... pValidPositions)
  {
    super(pDeviceName, pValidPositions);

    mPositionVariable.addSetListener((o, n) -> {
      if (isSimLogging())
      {
        String lMessage =
                        String.format("%s: new position: %d corresponding to filter '%s' \n",
                                      pDeviceName,
                                      n,
                                      getPositionName(n));

        info(lMessage);
      }
    });
  }

  @Override
  public boolean open()
  {
    return true;
  }

  @Override
  public boolean close()
  {
    return true;
  }

}
