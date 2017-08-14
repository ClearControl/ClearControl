package clearcontrol.devices.sensors.devices.sim;

import java.util.concurrent.ThreadLocalRandom;

import clearcontrol.core.device.sim.SimulationDeviceInterface;
import clearcontrol.core.log.LoggingFeature;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.sensors.TemperatureSensorDeviceBase;
import clearcontrol.devices.sensors.TemperatureSensorDeviceInterface;

public class TemperatureSensorDeviceSimulator extends
                                              TemperatureSensorDeviceBase
                                              implements
                                              TemperatureSensorDeviceInterface,
                                              LoggingFeature,
                                              SimulationDeviceInterface

{

  public TemperatureSensorDeviceSimulator(String pDeviceName)
  {
    super(pDeviceName);
    getLoopPeriodVariable().set(200.0);
  }

  @Override
  public boolean loop()
  {
    try
    {
      final Variable<Double> lTemperatureInCelciusVariable =
                                                           getTemperatureInCelciusVariable();
      final ThreadLocalRandom lThreadLocalRandom =
                                                 ThreadLocalRandom.current();
      final double lTemperatureInCelcius = 24
                                           + lThreadLocalRandom.nextDouble();
      lTemperatureInCelciusVariable.set(lTemperatureInCelcius);

      if (isSimLogging())
        info("new temperature: " + lTemperatureInCelcius + " ºC");
    }
    catch (Throwable e)
    {
      e.printStackTrace();
    }

    return true;
  }
}
