package clearcontrol.device.change;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Base class for providing basic change listener machinery for derived classes.
 * 
 *  @author royer
 */
public abstract class ChangeListeningBase<E> implements HasChangeListenerInterface<E>
{
	CopyOnWriteArrayList<ChangeListener<E>> mListenersList = new CopyOnWriteArrayList<>();

	/**
	 * Adds a change listener
	 * @param pListener listener to add
	 */
	@Override
	public void addChangeListener(ChangeListener<E> pListener)
	{
		mListenersList.add(pListener);
	}

	/**
	 * Removed a change listener
	 * @param pListener listener to remove
	 */
	@Override
	public void removeChangeListener(ChangeListener<E> pListener)
	{
		mListenersList.add(pListener);
	}
	
	/**
	 * Notifies listeners of changes .
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void notifyListeners(E pEvent)
	{
		for (ChangeListener<E> lListener : mListenersList)
		{
			lListener.changed(pEvent);
		}
	}
	
	
}
