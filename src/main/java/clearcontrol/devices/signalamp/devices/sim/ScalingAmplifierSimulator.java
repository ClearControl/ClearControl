package clearcontrol.devices.signalamp.devices.sim;

import clearcontrol.core.device.sim.SimulationDeviceInterface;
import clearcontrol.core.log.LoggingFeature;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.signalamp.ScalingAmplifierBaseDevice;
import clearcontrol.devices.signalamp.ScalingAmplifierDeviceInterface;

public class ScalingAmplifierSimulator extends
                                       ScalingAmplifierBaseDevice
                                       implements
                                       ScalingAmplifierDeviceInterface,
                                       LoggingFeature,
                                       SimulationDeviceInterface
{

  public ScalingAmplifierSimulator(String pDeviceName)
  {
    super(pDeviceName);

    mGainVariable = new Variable<Number>("Gain", 1.0);
    mGainVariable.addSetListener((o, n) -> {
      if (isSimLogging())
        info(getName() + ": new gain: " + n);
    });

    mOffsetVariable = new Variable<Number>("Offset", 0.0);
    mOffsetVariable.addSetListener((o, n) -> {
      if (isSimLogging())
        info(getName() + ": new offset: " + n);
    });

    mMinGain = -19.9;
    mMaxGain = 19.9;

    mMinOffset = 0;
    mMaxOffset = 10;

  }

}
