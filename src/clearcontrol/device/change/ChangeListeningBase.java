package clearcontrol.device.change;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Base class for providing basic change listener machinery for derived classes.
 * 
 *  @author royer
 */
public abstract class ChangeListeningBase<O> implements HasChangeListenerInterface<O>
{
	CopyOnWriteArrayList<ChangeListener<O>> mListenersList = new CopyOnWriteArrayList<>();

	/**
	 * Adds a change listener
	 * @param pListener listener to add
	 */
	public void addChangeListener(ChangeListener<O> pListener)
	{
		mListenersList.add(pListener);
	}

	/**
	 * Removed a change listener
	 * @param pListener listener to remove
	 */
	public void removeChangeListener(ChangeListener<O> pListener)
	{
		mListenersList.add(pListener);
	}
	
	/**
	 * Notifies listeners of changes .
	 */
	@SuppressWarnings("unchecked")
	public void notifyListeners()
	{
		for (ChangeListener<O> lListener : mListenersList)
		{
			lListener.changed((O) this);
		}
	}
	
	
}
