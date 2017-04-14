package clearcontrol.core.device.openclose;

/**
 * Open and close device interface
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
