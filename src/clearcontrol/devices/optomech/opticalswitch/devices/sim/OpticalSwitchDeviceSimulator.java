package clearcontrol.devices.optomech.opticalswitch.devices.sim;

import clearcontrol.core.device.VirtualDevice;
import clearcontrol.core.device.sim.SimulationDeviceInterface;
import clearcontrol.core.log.LoggingFeature;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.optomech.opticalswitch.OpticalSwitchDeviceInterface;

/**
 * Optical switch device simulator
 *
 * @author royer
 */
public class OpticalSwitchDeviceSimulator extends VirtualDevice
                                          implements
                                          OpticalSwitchDeviceInterface,
                                          LoggingFeature,
                                          SimulationDeviceInterface

{

  private final Variable<Boolean>[] mOpticalSwitchOnOffVariableArray;
  private int mNumberOfSwitches;

  /**
   * Instantiates an optical switch device simulator
   * 
   * @param pDeviceName
   *          device name
   * @param pNumberOfSwitches
   *          number of switches
   */
  @SuppressWarnings("unchecked")
  public OpticalSwitchDeviceSimulator(String pDeviceName,
                                      final int pNumberOfSwitches)
  {
    super(pDeviceName);

    mNumberOfSwitches = pNumberOfSwitches;

    mOpticalSwitchOnOffVariableArray =
                                     new Variable[mNumberOfSwitches];

    for (int i = 0; i < pNumberOfSwitches; i++)
    {
      mOpticalSwitchOnOffVariableArray[i] =
                                          new Variable<Boolean>("Switch"
                                                                + i,
                                                                false);

      final int fi = i;
      mOpticalSwitchOnOffVariableArray[i].addSetListener((o, n) -> {
        if (isSimLogging())
          info(pDeviceName + ": switch " + fi + " new state: " + n);
      });
    }

  }

  @Override
  public boolean open()
  {
    for (Variable<Boolean> lSwitchVariable : mOpticalSwitchOnOffVariableArray)
      lSwitchVariable.set(true);

    return true;
  }

  @Override
  public boolean close()
  {
    for (Variable<Boolean> lSwitchVariable : mOpticalSwitchOnOffVariableArray)
      lSwitchVariable.set(false);

    return true;
  }

  @Override
  public int getNumberOfSwitches()
  {
    return mNumberOfSwitches;
  }

  @Override
  public Variable<Boolean> getSwitchVariable(int pSwitchIndex)
  {
    return mOpticalSwitchOnOffVariableArray[pSwitchIndex];
  }

  @Override
  public String getSwitchName(int pSwitchIndex)
  {
    return "optical switch " + pSwitchIndex;
  }

}
