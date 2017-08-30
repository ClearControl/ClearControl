package clearcontrol.devices.optomech.opticalswitch.devices.arduino;

import clearcontrol.com.serial.SerialDevice;
import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableSetListener;
import clearcontrol.devices.optomech.opticalswitch.OpticalSwitchDeviceInterface;
import clearcontrol.devices.optomech.opticalswitch.devices.arduino.adapters.ArduinoOpticalSwitchPositionAdapter;

/**
 * Arduino Optical Switch device
 *
 * @author royer
 */
public class ArduinoOpticalSwitchDevice extends SerialDevice
                                        implements
                                        OpticalSwitchDeviceInterface

{

  private final Variable<Long> mCommandVariable;

  private final Variable<Boolean>[] mLightSheetOnOff;

  private static final long cAllClosed = 0;
  @SuppressWarnings("unused")
  private static final long cAllOpened = 100;

  /**
   * Instantiates an Arduino Optical Switch device given a device index
   * 
   * @param pDeviceIndex
   *          device index.
   */
  public ArduinoOpticalSwitchDevice(final int pDeviceIndex)
  {
    this(MachineConfiguration.get().getSerialDevicePort(
                                                        "fiberswitch.optojena",
                                                        pDeviceIndex,
                                                        "NULL"));
  }

  /**
   * Instantiates an Arduino Optical Switch device given a port name
   * 
   * @param pPortName
   *          port name
   */
  @SuppressWarnings("unchecked")
  public ArduinoOpticalSwitchDevice(final String pPortName)
  {
    super("ArduinoOpticalSwitch", pPortName, 250000);

    final ArduinoOpticalSwitchPositionAdapter lFiberSwitchPosition =
                                                                   new ArduinoOpticalSwitchPositionAdapter(this);

    mCommandVariable = addSerialVariable("OpticalSwitchPosition",
                                         lFiberSwitchPosition);

    mLightSheetOnOff = new Variable[4];

    final VariableSetListener<Boolean> lBooleanVariableListener =
                                                                (u,
                                                                 v) -> {

                                                                  int lCount =
                                                                             0;
                                                                  for (int i =
                                                                             0; i < mLightSheetOnOff.length; i++)
                                                                    if (mLightSheetOnOff[i].get())
                                                                      lCount++;

                                                                  if (lCount == 1)
                                                                  {
                                                                    for (int i =
                                                                               0; i < mLightSheetOnOff.length; i++)
                                                                      if (mLightSheetOnOff[i].get())
                                                                        mCommandVariable.set((long) (101
                                                                                                     + i));
                                                                  }
                                                                  else
                                                                    for (int i =
                                                                               0; i < mLightSheetOnOff.length; i++)
                                                                    {
                                                                      boolean lOn =
                                                                                  mLightSheetOnOff[i].get();
                                                                      mCommandVariable.set((long) ((i
                                                                                                    + 1)
                                                                                                   * (lOn ? 1
                                                                                                          : -1)));
                                                                    }
                                                                };

    for (int i = 0; i < mLightSheetOnOff.length; i++)
    {

      mLightSheetOnOff[i] = new Variable<Boolean>(
                                                  String.format("LightSheet%dOnOff",
                                                                i),
                                                  false);
      mLightSheetOnOff[i].addSetListener(lBooleanVariableListener);

    }

  }

  @Override
  public boolean open()
  {
    final boolean lIsOpened = super.open();
    mCommandVariable.set(cAllClosed);

    return lIsOpened;
  }

  @Override
  public boolean close()
  {
    final boolean lIsClosed = super.close();
    mCommandVariable.set(cAllClosed);

    return lIsClosed;
  }

  @Override
  public int getNumberOfSwitches()
  {
    return 4;
  }

  @Override
  public Variable<Boolean> getSwitchVariable(int pSwitchIndex)
  {
    return mLightSheetOnOff[pSwitchIndex];
  }

  @Override
  public String getSwitchName(int pSwitchIndex)
  {
    return "optical switch " + pSwitchIndex;
  }

}
