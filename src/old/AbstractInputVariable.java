package variable;

import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractInputVariable<L>
{
	protected final CopyOnWriteArrayList<L> mListenerList = new CopyOnWriteArrayList<L>();

	public final void addListener(final L pListenerInterface)
	{
		if (!mListenerList.contains(pListenerInterface))
		{
			mListenerList.add(pListenerInterface);
		}
	}

	public final CopyOnWriteArrayList<L> getListenerList()
	{
		return mListenerList;
	}
	


}
