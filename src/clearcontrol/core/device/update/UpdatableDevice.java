package clearcontrol.core.device.update;

import clearcontrol.core.device.VirtualDevice;

/**
 * Updatable device
 *
 * @author royer
 */
public abstract class UpdatableDevice extends VirtualDevice
                                      implements UpdatableInterface
{

  private volatile boolean mIsUpToDate = false;

  /**
   * Instantiates an updatable device of given name
   * 
   * @param pDeviceName
   *          device name
   */
  public UpdatableDevice(String pDeviceName)
  {
    super(pDeviceName);
  }

  @Override
  public abstract void ensureIsUpToDate();

  @Override
  public boolean isUpToDate()
  {
    return mIsUpToDate;
  }

  @Override
  public void setUpToDate(boolean pIsUpToDate)
  {
    mIsUpToDate = pIsUpToDate;
  }

  @Override
  public void requestUpdate()
  {
    mIsUpToDate = false;
  }
}
