package clearcontrol.devices.lasers.devices.omicron;

import clearcontrol.com.serial.SerialDevice;
import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.devices.lasers.LaserDeviceBase;
import clearcontrol.devices.lasers.LaserDeviceInterface;
import clearcontrol.devices.lasers.devices.omicron.adapters.GetCurrentPowerAdapter;
import clearcontrol.devices.lasers.devices.omicron.adapters.GetDeviceIdAdapter;
import clearcontrol.devices.lasers.devices.omicron.adapters.GetMaxPowerAdapter;
import clearcontrol.devices.lasers.devices.omicron.adapters.GetSetTargetPowerAdapter;
import clearcontrol.devices.lasers.devices.omicron.adapters.GetSpecPowerAdapter;
import clearcontrol.devices.lasers.devices.omicron.adapters.GetWavelengthAdapter;
import clearcontrol.devices.lasers.devices.omicron.adapters.GetWorkingHoursAdapter;
import clearcontrol.devices.lasers.devices.omicron.adapters.SetLaserOnOffAdapter;
import clearcontrol.devices.lasers.devices.omicron.adapters.SetOperatingModeAdapter;
import clearcontrol.devices.lasers.devices.omicron.adapters.SetPowerOnOffAdapter;
import clearcontrol.devices.lasers.devices.omicron.adapters.protocol.ProtocolOmicron;

/**
 * Omicron laser device.
 *
 * @author royer
 */
public class OmicronLaserDevice extends LaserDeviceBase
                                implements LaserDeviceInterface
{
  private final SerialDevice mSerialDevice;
  private boolean mAnalog = true, mDigital = true;

  private final GetSetTargetPowerAdapter mGetSetTargetPowerAdapter;

  /**
   * Instanciates anOmicron laser device from a device id. The laser details are
   * obytained from the current machine configuration file.
   * 
   * @param pDeviceIndex
   *          device index
   */
  public OmicronLaserDevice(final int pDeviceIndex)
  {
    this(MachineConfiguration.get().getSerialDevicePort(
                                                        "laser.omicron",
                                                        pDeviceIndex,
                                                        "NULL"));
  }

  /**
   * Instanciates an Omicron laser device given a device index, and two flags
   * about whether this laser is controlled via analog or digital controls.
   * 
   * @param pDeviceIndex
   *          device index
   * @param pDigitalControl
   *          digital control flag
   * @param pAnalogControl
   *          analog control flag
   */
  public OmicronLaserDevice(final int pDeviceIndex,
                            boolean pDigitalControl,
                            boolean pAnalogControl)
  {
    this(MachineConfiguration.get().getSerialDevicePort(
                                                        "laser.omicron",
                                                        pDeviceIndex,
                                                        "NULL"));
    mAnalog = pAnalogControl;
    mDigital = pDigitalControl;
  }

  /**
   * Instanciates an Omicron laser connect to the given serial communication
   * port
   * 
   * @param pPortName
   *          port name
   */
  public OmicronLaserDevice(final String pPortName)
  {
    super("OmicronLaserDevice" + pPortName);

    mSerialDevice = new SerialDevice("OmicronLaserDevice",
                                     pPortName,
                                     ProtocolOmicron.cBaudRate);

    final GetDeviceIdAdapter lGetDeviceIdAdapter =
                                                 new GetDeviceIdAdapter();
    mDeviceIdVariable =
                      mSerialDevice.addSerialVariable("DeviceId",
                                                      lGetDeviceIdAdapter);

    final GetWavelengthAdapter lGetWavelengthAdapter =
                                                     new GetWavelengthAdapter();
    mWavelengthVariable =
                        mSerialDevice.addSerialVariable("WavelengthInNanoMeter",
                                                        lGetWavelengthAdapter);

    final GetSpecPowerAdapter lGetSpecPowerAdapter =
                                                   new GetSpecPowerAdapter();
    mSpecInMilliWattPowerVariable =
                                  mSerialDevice.addSerialVariable("SpecPowerInMilliWatt",
                                                                  lGetSpecPowerAdapter);

    final GetMaxPowerAdapter lGetMaxPowerAdapter =
                                                 new GetMaxPowerAdapter();
    mMaxPowerInMilliWattVariable =
                                 mSerialDevice.addSerialVariable("MaxPowerInMilliWatt",
                                                                 lGetMaxPowerAdapter);

    final SetOperatingModeAdapter lSetOperatingModeAdapter =
                                                           new SetOperatingModeAdapter();
    mSetOperatingModeVariable =
                              mSerialDevice.addSerialVariable("OperatingMode",
                                                              lSetOperatingModeAdapter);

    final SetPowerOnOffAdapter lSetPowerOnOffAdapter =
                                                     new SetPowerOnOffAdapter();
    mPowerOnVariable =
                     mSerialDevice.addSerialVariable("PowerOn",
                                                     lSetPowerOnOffAdapter);

    final SetLaserOnOffAdapter lSetLaserOnOffAdapter =
                                                     new SetLaserOnOffAdapter();
    mLaserOnVariable =
                     mSerialDevice.addSerialVariable("LaserOn",
                                                     lSetLaserOnOffAdapter);

    final GetWorkingHoursAdapter lGetWorkingHoursAdapter =
                                                         new GetWorkingHoursAdapter();
    mWorkingHoursVariable =
                          mSerialDevice.addSerialVariable("WorkingHours",
                                                          lGetWorkingHoursAdapter);

    mGetSetTargetPowerAdapter = new GetSetTargetPowerAdapter();
    mTargetPowerInMilliWattVariable =
                                    mSerialDevice.addSerialVariable("TargetPowerInMilliWatt",
                                                                    mGetSetTargetPowerAdapter);

    final GetCurrentPowerAdapter lGetCurrentPowerAdapter =
                                                         new GetCurrentPowerAdapter();
    mCurrentPowerInMilliWattVariable =
                                     mSerialDevice.addSerialVariable("CurrentPowerInMilliWatt",
                                                                     lGetCurrentPowerAdapter);
  }

  @Override
  public boolean open()
  {
    boolean lOpen;
    try
    {
      lOpen = super.open();
      mSerialDevice.open();
      ProtocolOmicron.setNoAdHocMode(mSerialDevice.getSerial());
      setTargetPowerInPercent(0);
      if (mAnalog && mDigital)
        setOperatingMode(5);
      else if (mDigital)
        setOperatingMode(2);
      else if (mAnalog)
        setOperatingMode(4);
      // setPowerOn(true);
      mGetSetTargetPowerAdapter.setMaxPowerInMilliWatt(mMaxPowerInMilliWattVariable.get()
                                                                                   .doubleValue());
      setLaserPowerOn(true);

      return lOpen;
    }
    catch (final Throwable e)
    {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean start()
  {
    try
    {
      final boolean lStartResult = super.start();
      return lStartResult;
    }
    catch (final Throwable e)
    {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean stop()
  {
    try
    {
      return super.stop();
    }
    catch (final Throwable e)
    {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean close()
  {
    try
    {
      setTargetPowerInPercent(0);
      setLaserOn(false);
      setLaserPowerOn(false);
      mSerialDevice.close();
      return super.close();
    }
    catch (final Throwable e)
    {
      e.printStackTrace();
      return false;
    }
  }

}
