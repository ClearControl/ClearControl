package clearcontrol.core.device.change;

/**
 * Classes implementing this interface support change listeners that can be
 * notified of the object's state change.
 *
 * @param <E>
 *          event type
 * @author royer
 */
public interface HasChangeListenerInterface<E>
{
  /**
   * Adds a change listener.
   * 
   * @param pChangeListener
   *          change listener to add
   */
  public void addChangeListener(ChangeListener<E> pChangeListener);

  /**
   * Removes a change listenr
   * 
   * @param pChangeListener
   *          change listener
   */
  public void removeChangeListener(ChangeListener<E> pChangeListener);

  /**
   * Returns true if the given listener is already added.
   * 
   * @param pChangeListener
   *          change listener to check for
   * @return true if already added.
   */
  public boolean isChangeListener(ChangeListener<E> pChangeListener);

  /**
   * Notify listeners of a change by passing an event object
   * 
   * @param pEvent
   *          event
   */
  public void notifyListeners(E pEvent);

}
