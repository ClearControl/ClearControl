package clearcontrol.device.change;

public interface HasChangeListenerInterface<O>
{
	public void addChangeListener(ChangeListener<O> pChangeListener);

	public void removeChangeListener(ChangeListener<O> pChangeListener);
}
