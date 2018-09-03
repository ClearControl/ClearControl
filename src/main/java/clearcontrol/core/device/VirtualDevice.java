package clearcontrol.core.device;

import clearcontrol.core.device.change.HasChangeListenerInterface;
import clearcontrol.core.device.name.NameableInterface;
import clearcontrol.core.device.openclose.OpenCloseDeviceInterface;

/**
 * Virtual device base class.
 *
 * @author royer
 */
public class VirtualDevice extends
                           NameableWithChangeListener<VirtualDevice>
                           implements
                           OpenCloseDeviceInterface,
                           HasChangeListenerInterface<VirtualDevice>,
                           NameableInterface
{

  /**
   * INstanciates a virtual device with a given name
   * 
   * @param pDeviceName
   *          device name
   */
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
