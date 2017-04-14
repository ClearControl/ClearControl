package clearcontrol.core.device.startstop;

/**
 * Start stop signal device
 *
 * @author royer
 */
public interface StartStopDeviceInterface
{
  /**
   * Start
   * 
   * @return true -> success
   */
  public boolean start();

  /**
   * Stop
   * 
   * @return true -> success
   */
  public boolean stop();
}
