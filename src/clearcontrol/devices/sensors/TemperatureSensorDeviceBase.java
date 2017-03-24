package clearcontrol.devices.sensors;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.core.device.task.PeriodicLoopTaskDevice;
import clearcontrol.core.variable.Variable;

public abstract class TemperatureSensorDeviceBase extends
                                                  PeriodicLoopTaskDevice
                                                  implements
                                                  OpenCloseDeviceInterface,
                                                  TemperatureSensorDeviceInterface
{

  private Variable<Double> mTemperatureVariable;

  public TemperatureSensorDeviceBase(final String pDeviceName)
  {
    super(pDeviceName, 500.0, TimeUnit.MILLISECONDS);
    mTemperatureVariable = new Variable<Double>(pDeviceName
                                                + "TemperatureInCelcius",
                                                Double.NaN);
  }

  @Override
  public boolean open()
  {
    startTask();
    return waitForStarted(100, TimeUnit.MILLISECONDS);
  };

  @Override
  public boolean close()
  {
    stopTask();
    return waitForStopped(100, TimeUnit.MILLISECONDS);
  }

  @Override
  public Variable<Double> getTemperatureInCelciusVariable()
  {
    return mTemperatureVariable;
  }

  @Override
  public double getTemperatureInCelcius()
  {
    return mTemperatureVariable.get();
  }

}
