package clearcontrol.core.device.update;

/**
 * Update listener. Updatable devices can notify update listeners just after an
 * update has been triggered.
 *
 * @author royer
 */
public interface UpdateListener
{
  /**
   * Notifies of an update
   * 
   * @param pUpdatable
   *          Updatable object (most likely a device from which this event
   *          originates)
   */
  public void notifyUpdate(UpdatableInterface pUpdatable);
}
