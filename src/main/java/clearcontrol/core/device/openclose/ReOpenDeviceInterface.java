package clearcontrol.core.device.openclose;

/**
 * ReOpen Device Interface. Certain devices require to be reopened when certain
 * configuration/parameters changes are applied to them.
 *
 * @author royer
 */
public interface ReOpenDeviceInterface
{
  /**
   * Returns true if this device requires to be reopened.
   * 
   * @return true if reopen necessary, false otherwise
   */
  boolean isReOpenDeviceNeeded();

  /**
   * Requests this device to reopen.
   */
  void requestReOpen();

  /**
   * Clears re-open flag
   */
  void clearReOpen();

  /**
   * Reopens device
   */
  void reopen();
}
