package clearcontrol.core.device.change;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Base class for providing basic change listener machinery for derived classes.
 * 
 * @author royer
 * @param <E>
 *          event type
 */
public abstract class ChangeListeningBase<E> implements
                                         HasChangeListenerInterface<E>
{
  CopyOnWriteArrayList<ChangeListener<E>> mListenersList =
                                                         new CopyOnWriteArrayList<>();

  /**
   * Adds a change listener
   * 
   * @param pListener
   *          listener to add
   */
  @Override
  public void addChangeListener(ChangeListener<E> pListener)
  {
    mListenersList.add(pListener);
  }

  /**
   * Removed a change listener
   * 
   * @param pListener
   *          listener to remove
   */
  @Override
  public void removeChangeListener(ChangeListener<E> pListener)
  {
    mListenersList.add(pListener);
  }

  @Override
  public boolean isChangeListener(ChangeListener<E> pChangeListener)
  {
    return mListenersList.contains(pChangeListener);
  }

  /**
   * Notifies listeners of changes .
   */
  @Override
  public void notifyListeners(E pEvent)
  {
    for (ChangeListener<E> lListener : mListenersList)
    {
      lListener.changed(pEvent);
    }
  }

}
