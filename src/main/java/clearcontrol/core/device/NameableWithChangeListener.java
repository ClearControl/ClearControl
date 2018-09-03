package clearcontrol.core.device;

import java.util.concurrent.CopyOnWriteArrayList;

import clearcontrol.core.device.change.ChangeListener;
import clearcontrol.core.device.change.HasChangeListenerInterface;
import clearcontrol.core.device.name.NameableBase;
import clearcontrol.core.device.name.NameableInterface;

/**
 * Base class extended by classes that need both name and change listener
 * functionality
 *
 * @param <E>
 *          event type
 * @author royer
 */
public abstract class NameableWithChangeListener<E>
                                                extends NameableBase
                                                implements
                                                HasChangeListenerInterface<E>,
                                                NameableInterface
{

  private CopyOnWriteArrayList<ChangeListener<E>> mChangeListenerList =
                                                                      new CopyOnWriteArrayList<>();

  /**
   * Instanciates with given name
   * 
   * @param pName
   *          name
   */
  public NameableWithChangeListener(final String pName)
  {
    super(pName);
  }

  @Override
  public void addChangeListener(ChangeListener<E> pChangeListener)
  {
    mChangeListenerList.add(pChangeListener);
  }

  @Override
  public boolean isChangeListener(ChangeListener<E> pChangeListener)
  {
    return mChangeListenerList.contains(pChangeListener);
  }

  @Override
  public void removeChangeListener(ChangeListener<E> pChangeListener)
  {
    mChangeListenerList.remove(pChangeListener);
  }

  @Override
  public void notifyListeners(E pEvent)
  {
    for (ChangeListener<E> lChangeListener : mChangeListenerList)
    {
      lChangeListener.changed(pEvent);
    }
  }

}
