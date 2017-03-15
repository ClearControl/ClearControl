package clearcontrol.hardware.sensors.devices.sim;

import java.util.concurrent.ThreadLocalRandom;

import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.device.sim.SimulationDeviceInterface;
import clearcontrol.hardware.sensors.TemperatureSensorDeviceBase;
import clearcontrol.hardware.sensors.TemperatureSensorDeviceInterface;

public class TemperatureSensorDeviceSimulator extends
                                              TemperatureSensorDeviceBase
                                              implements
                                              TemperatureSensorDeviceInterface,
                                              LoggingInterface,
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
