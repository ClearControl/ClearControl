package clearcontrol.devices.lasers.devices.cobolt;

import jssc.SerialPortException;
import clearcontrol.com.serial.SerialDevice;
import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.lasers.LaserDeviceBase;
import clearcontrol.devices.lasers.LaserDeviceInterface;
import clearcontrol.devices.lasers.devices.cobolt.adapters.GetCurrentPowerAdapter;
import clearcontrol.devices.lasers.devices.cobolt.adapters.GetSetTargetPowerAdapter;
import clearcontrol.devices.lasers.devices.cobolt.adapters.GetWorkingHoursAdapter;
import clearcontrol.devices.lasers.devices.cobolt.adapters.SetPowerOnOffAdapter;
import clearcontrol.devices.lasers.devices.cobolt.adapters.protocol.ProtocolCobolt;
import clearcontrol.devices.lasers.devices.cobolt.models.CoboltDeviceEnum;

/**
 * Cobolt laser device
 *
 * @author royer
 */
public class CoboltLaserDevice extends LaserDeviceBase
                               implements LaserDeviceInterface
{
  private final SerialDevice mSerialDevice;

  private final CoboltDeviceEnum mCoboltModel;
  private final double mMaxPowerInMilliWatt;

  /**
   * Instanciates a Cobolt laser device given a Cobolt device name, max power in
   * milliwats, and device index ( from which further information is collected
   * from the machine configuration file)
   * 
   * @param pCoboltModelName
   *          Cobolt laser model name
   * @param pMaxPowerInMilliWatt
   *          max power in milliwatt
   * @param pDeviceIndex
   *          device index.
   */
  public CoboltLaserDevice(final String pCoboltModelName,
                           final int pMaxPowerInMilliWatt,
                           final int pDeviceIndex)
  {
    this(pCoboltModelName,
         pMaxPowerInMilliWatt,
         MachineConfiguration.get()
                             .getSerialDevicePort("laser.cobolt",
                                                  pDeviceIndex,
                                                  "NULL"));
  }

  /**
   * Instanciates a Cobolt laser device.
   * 
   * @param pCoboltModelName
   *          Cobolt model name
   * @param pMaxPowerInMilliWatt
   *          max power in milliwatt
   * @param pPortName
   *          port name
   */
  public CoboltLaserDevice(final String pCoboltModelName,
                           final int pMaxPowerInMilliWatt,
                           final String pPortName)
  {
    super("Cobolt" + pCoboltModelName);

    mSerialDevice = new SerialDevice("Cobolt" + pCoboltModelName,
                                     pPortName,
                                     115200);

    mCoboltModel = CoboltDeviceEnum.valueOf(pCoboltModelName);
    mMaxPowerInMilliWatt = pMaxPowerInMilliWatt;

    mDeviceIdVariable = new Variable<Integer>("DeviceId",
                                              mCoboltModel.ordinal());

    mWavelengthVariable =
                        new Variable<Integer>("WavelengthInNanoMeter",
                                              mCoboltModel.getWavelengthInNanoMeter());

    mSpecInMilliWattPowerVariable =
                                  new Variable<Number>("SpecPowerInMilliWatt",
                                                       mMaxPowerInMilliWatt);

    mMaxPowerInMilliWattVariable =
                                 new Variable<Number>("MaxPowerInMilliWatt",
                                                      mMaxPowerInMilliWatt);

    mSetOperatingModeVariable = new Variable<Integer>("OperatingMode",
                                                      0);

    final SetPowerOnOffAdapter lSetPowerOnOffAdapter =
                                                     new SetPowerOnOffAdapter();
    mPowerOnVariable =
                     mSerialDevice.addSerialVariable("PowerOn",
                                                     lSetPowerOnOffAdapter);

    mLaserOnVariable = new Variable<Boolean>("LaserOn", false);

    final GetWorkingHoursAdapter lGetWorkingHoursAdapter =
                                                         new GetWorkingHoursAdapter();
    mWorkingHoursVariable =
                          mSerialDevice.addSerialVariable("WorkingHours",
                                                          lGetWorkingHoursAdapter);

    final GetSetTargetPowerAdapter lGetSetTargetPowerAdapter =
                                                             new GetSetTargetPowerAdapter();
    mTargetPowerInMilliWattVariable =
                                    mSerialDevice.addSerialVariable("TargetPowerMilliWatt",
                                                                    lGetSetTargetPowerAdapter);

    final GetCurrentPowerAdapter lGetCurrentPowerAdapter =
                                                         new GetCurrentPowerAdapter();
    mCurrentPowerInMilliWattVariable =
                                     mSerialDevice.addSerialVariable("CurrentPowerInMilliWatt",
                                                                     lGetCurrentPowerAdapter);
  }

  @Override
  public boolean open()
  {
    boolean lResult = mSerialDevice.open();
    mSerialDevice.getSerial()
                 .setLineTerminationCharacter(ProtocolCobolt.cMessageTerminationCharacter);

    if (lResult)
    {
      // Clears 'faults'
      sendCommand("cf\r");

      // This command is needed otherwise the laser cannot be controlled via
      // Serial,
      // the documentation is really poor, and it took some work to figure this
      // out...
      sendCommand("@cobas 0\r");

      /*sendCommand("f?\r");
      sendCommand("ilk?\r");
      sendCommand("@cobas?\r");
      sendCommand("l?\r");
      sendCommand("p?\r");
      sendCommand("pa?\r");
      sendCommand("i?\r");
      sendCommand("leds?\r");
      sendCommand("@cobasdr?\r");
      sendCommand("@cobasky?\r");/**/
    }

    getPowerOnVariable().set(true);

    return lResult;
  }

  @Override
  public boolean start()
  {

    return true;
  }

  @Override
  public boolean stop()
  {
    return true;
  }

  @Override
  public boolean close()
  {
    setTargetPowerInMilliWatt(0);
    setLaserOn(false);
    setLaserPowerOn(false);
    return mSerialDevice.close();
  }

  /**
   * Sends command and collects answer from device.
   * 
   * @param pCommandString
   *          command string
   * @return answer received
   */
  public String sendCommand(String pCommandString)
  {
    try
    {
      // System.out.print(pCommandString.replace('\r', ' ').trim() + " --> ");
      String lAnswer =
                     mSerialDevice.getSerial()
                                  .writeStringAndGetAnswer(pCommandString);
      // System.out.println(lAnswer.trim());
      return lAnswer;
    }
    catch (SerialPortException e)
    {
      e.printStackTrace();
      return null;
    }
  }

}
