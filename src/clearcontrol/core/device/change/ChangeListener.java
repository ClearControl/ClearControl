package clearcontrol.core.device.change;

/**
 * Change listener
 *
 * @param <E>
 *          event type
 * @author royer
 */
public interface ChangeListener<E>
{
  /**
   * Called when an evet is received.
   * 
   * @param pEvent
   *          event
   */
  public void changed(E pEvent);
}
