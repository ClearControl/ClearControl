package clearcontrol.device.change;

public interface HasChangeListenerInterface<E>
{
	public void addChangeListener(ChangeListener<E> pChangeListener);

	public void removeChangeListener(ChangeListener<E> pChangeListener);

	public void notifyListeners(E pEvent);
}
