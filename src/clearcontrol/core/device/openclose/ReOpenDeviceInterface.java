package clearcontrol.core.device.openclose;

/**
 *
 *
 * @author royer
 */
public interface ReOpenDeviceInterface
{
  /**
   * @return
   */
  boolean isReOpenDeviceNeeded();

  /**
   * 
   */
  void requestReOpen();

  /**
   * 
   */
  void clearReOpen();

  /**
   * 
   */
  void reopen();
}
