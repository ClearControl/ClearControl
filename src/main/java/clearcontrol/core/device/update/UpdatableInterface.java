package clearcontrol.core.device.update;

/**
 * Updatable device. Devices implementing this interface have some internal
 * state that need to be kept up-to-date. For example, changes in some variable
 * values might require to recompute some other values.
 *
 * @author royer
 */
public interface UpdatableInterface
{

  /**
   * Ensures that the device internal state is up-to-date.
   */
  public void ensureIsUpToDate();

  /**
   * Returns true if the device is up-to-date i.e. there has been no changes
   * that would require an update.
   * 
   * @return true if up-to-date, false otherwise
   */
  public boolean isUpToDate();

  /**
   * Sets the up-to-date flag
   * 
   * @param pIsUpToDate
   *          new up-to-date flag value
   */
  public void setUpToDate(boolean pIsUpToDate);

  /**
   * Requests update (sets the up-to-date flag to false).
   * 
   */
  public void requestUpdate();

}