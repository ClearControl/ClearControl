package clearcontrol.device;

import java.util.ArrayList;

import clearcontrol.device.change.ChangeListener;
import clearcontrol.device.change.HasChangeListenerInterface;
import clearcontrol.device.name.NameableInterface;
import clearcontrol.device.openclose.OpenCloseDeviceAdapter;
import clearcontrol.device.openclose.OpenCloseDeviceInterface;

public class VirtualDevice extends OpenCloseDeviceAdapter	implements
																													OpenCloseDeviceInterface,
																													HasChangeListenerInterface,
																													NameableInterface
{

	private String mDeviceName;

	private ArrayList<ChangeListener> ChangeListenerList = new ArrayList<>();

	public VirtualDevice(final String pDeviceName)
	{
		super();
		mDeviceName = pDeviceName;
	}

	@Override
	public void setName(String pName)
	{
		mDeviceName = pName;
	}

	@Override
	public String getName()
	{
		return mDeviceName;
	}

	@Override
	public void addChangeListener(ChangeListener pChangeListener)
	{
		ChangeListenerList.add(pChangeListener);
	}

	@Override
	public void removeChangeListener(ChangeListener pChangeListener)
	{
		ChangeListenerList.remove(pChangeListener);
	}

	protected void notifyChange()
	{
		//System.out.println("NOTIFY CHANGE");
		for (ChangeListener lChangeListener : ChangeListenerList)
		{
			lChangeListener.changed(this);
		}
	}

	@Override
	public String toString()
	{
		return String.format("NamedDevice [mDeviceName=%s]", mDeviceName);
	}

}
