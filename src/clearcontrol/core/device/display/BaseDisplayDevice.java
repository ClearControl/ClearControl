package clearcontrol.core.device.display;

import clearcontrol.core.device.VirtualDevice;
import clearcontrol.core.device.openclose.OpenCloseDeviceInterface;

public abstract class BaseDisplayDevice extends VirtualDevice
                                        implements
                                        OpenCloseDeviceInterface,
                                        DisplayableInterface
{

  public BaseDisplayDevice(final String pDeviceName)
  {
    super(pDeviceName);
  }

}
