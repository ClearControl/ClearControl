package clearcontrol.device;

import clearcontrol.device.change.HasChangeListenerInterface;
import clearcontrol.device.name.NameableInterface;
import clearcontrol.device.openclose.OpenCloseDeviceInterface;

public class VirtualDevice extends
                           NameableWithChangeListener<VirtualDevice>
                           implements
                           OpenCloseDeviceInterface,
                           HasChangeListenerInterface<VirtualDevice>,
                           NameableInterface
{

  public VirtualDevice(final String pDeviceName)
  {
    super(pDeviceName);
  }

  @Override
  public boolean open()
  {
    return true;
  }

  @Override
  public boolean close()
  {
    return true;
  }
}
