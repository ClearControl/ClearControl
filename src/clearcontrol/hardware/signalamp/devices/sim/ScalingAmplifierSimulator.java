package clearcontrol.hardware.signalamp.devices.sim;

import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.device.sim.SimulationDeviceInterface;
import clearcontrol.hardware.signalamp.ScalingAmplifierBaseDevice;
import clearcontrol.hardware.signalamp.ScalingAmplifierDeviceInterface;

public class ScalingAmplifierSimulator extends
                                       ScalingAmplifierBaseDevice
                                       implements
                                       ScalingAmplifierDeviceInterface,
                                       LoggingInterface,
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
