package clearcontrol.devices.signalamp.devices.srs;

import clearcontrol.com.serial.Serial;
import clearcontrol.devices.signalamp.ScalingAmplifierBaseDevice;
import clearcontrol.devices.signalamp.ScalingAmplifierDeviceInterface;
import clearcontrol.devices.signalamp.devices.srs.adapters.GainAdapter;
import clearcontrol.devices.signalamp.devices.srs.adapters.OffsetAdapter;

public class SIM983ScalingAmplifierDevice extends
                                          ScalingAmplifierBaseDevice
                                          implements
                                          ScalingAmplifierDeviceInterface,
                                          SIMModuleInterface
{

  private static final String cDeviceName =
                                          "ScalingAmplifierBaseDevice";
  private SIM900MainframeDevice mSim900MainframeDevice;
  private final int mPort;

  public SIM983ScalingAmplifierDevice(SIM900MainframeDevice pSim900MainframeDevice,
                                      int pPort)
  {
    super(pSim900MainframeDevice + "." + pPort + "." + cDeviceName);
    mSim900MainframeDevice = pSim900MainframeDevice;
    mPort = pPort;

    mMinGain = -19.99;
    mMaxGain = 19.99;

    mMinOffset = -10;
    mMaxOffset = 10;

  }

  @Override
  public boolean open()
  {
    boolean lOpen;
    try
    {
      lOpen = super.open();

      if (mSim900MainframeDevice.getSerialDevice() == null)
        return false;

      Serial lSerial = mSim900MainframeDevice.getSerialDevice()
                                             .getSerial();

      lSerial.format("SNDT %d, \"TERM 2\"", mPort);

      final GainAdapter lGetDeviceIdAdapter =
                                            new GainAdapter(mSim900MainframeDevice,
                                                            mPort);
      mGainVariable =
                    mSim900MainframeDevice.getSerialDevice()
                                          .addSerialVariable("Gain",
                                                             lGetDeviceIdAdapter);

      final OffsetAdapter lGetWavelengthAdapter =
                                                new OffsetAdapter(mSim900MainframeDevice,
                                                                  mPort);
      mOffsetVariable =
                      mSim900MainframeDevice.getSerialDevice()
                                            .addSerialVariable("Offset",
                                                               lGetWavelengthAdapter);

      return lOpen;
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
      mSim900MainframeDevice.getSerialDevice().removeAllVariables();
      return super.close();
    }
    catch (final Throwable e)
    {
      e.printStackTrace();
      return false;
    }
  }

}
