package clearcontrol.core.device.openclose;

/**
 * Open and close device interface. Devices that implement this interface can be
 * opened and closed. The semantics for opening and closing devices correspond
 * to the allocation and deallocation of long term resources. For example the
 * initialization of a hardware communication channel, or the allocation of
 * large amounts of memory.
 *
 * @author royer
 */
public interface OpenCloseDeviceInterface
{
  /**
   * Opens device.
   * 
   * @return true -> success
   */
  public boolean open();

  /**
   * Closes device.
   * 
   * @return true -> success
   */
  public boolean close();
}
