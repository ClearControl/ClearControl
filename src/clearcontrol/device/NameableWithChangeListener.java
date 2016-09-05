package clearcontrol.device;

import java.util.concurrent.CopyOnWriteArrayList;

import clearcontrol.device.change.ChangeListener;
import clearcontrol.device.change.HasChangeListenerInterface;
import clearcontrol.device.name.NameableBase;
import clearcontrol.device.name.NameableInterface;

public class NameableWithChangeListener<E> extends NameableBase	implements
																																HasChangeListenerInterface<E>,
																																NameableInterface
{

	private CopyOnWriteArrayList<ChangeListener<E>> mChangeListenerList = new CopyOnWriteArrayList<>();

	public NameableWithChangeListener(final String pDeviceName)
	{
		super(pDeviceName);
	}

	@Override
	public void addChangeListener(ChangeListener<E> pChangeListener)
	{
		mChangeListenerList.add(pChangeListener);
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
