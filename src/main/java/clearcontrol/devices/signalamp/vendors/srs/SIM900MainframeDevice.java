package clearcontrol.devices.signalamp.devices.srs;

import java.util.ArrayList;

import clearcontrol.com.serial.SerialDevice;
import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.device.VirtualDevice;
import clearcontrol.devices.signalamp.devices.srs.adapters.protocol.ProtocolSIM;

public class SIM900MainframeDevice extends VirtualDevice
{
  private final SerialDevice mSerialDevice;

  private final ArrayList<SIMModuleInterface> mSIMModuleList =
                                                             new ArrayList<>();

  public SIM900MainframeDevice(final int pDeviceIndex)
  {
    this(MachineConfiguration.get().getSerialDevicePort(
                                                        "stanford.SIM900",
                                                        pDeviceIndex,
                                                        "NULL"));
  }

  public SIM900MainframeDevice(final String pPortName)
  {
    super("SIM900MainframeDevice" + pPortName);

    mSerialDevice =
                  new SerialDevice("SIM900MainframeDevice",
                                   pPortName,
                                   ProtocolSIM.cBaudRate);
  }

  public SerialDevice getSerialDevice()
  {
    return mSerialDevice;
  }

  @Override
  public boolean open()
  {
    boolean lOpen;
    try
    {
      lOpen = super.open();
      mSerialDevice.open();
      mSerialDevice.getSerial().write("*RST\n");
      mSerialDevice.getSerial().write("FLSH\n");
      mSerialDevice.getSerial().write("SRST\n");
      mSerialDevice.getSerial().write("RPER 510\n");
      mSerialDevice.getSerial().write("TERM D,LF\n");

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
      mSerialDevice.close();
      return super.close();
    }
    catch (final Throwable e)
    {
      e.printStackTrace();
      return false;
    }
  }

  public String wrapCommand(int pPort, String pCommandString)
  {
    pCommandString = pCommandString.replace("\n", "");
    return String.format(ProtocolSIM.cSIM900ForwardCommand,
                         pPort,
                         pCommandString);
  }

}
