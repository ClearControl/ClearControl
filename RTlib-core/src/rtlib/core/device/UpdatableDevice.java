package rtlib.core.device;

public abstract class UpdatableDevice extends NamedVirtualDevice
{

	private volatile boolean mIsUpToDate = false;

	public UpdatableDevice(String pDeviceName)
	{
		super(pDeviceName);
	}

	public abstract void ensureIsUpToDate();

	public boolean isUpToDate()
	{
		return mIsUpToDate;
	}

	public void setUpToDate(boolean pIsUpToDate)
	{
		mIsUpToDate = pIsUpToDate;
	}

	public void requestUpdate()
	{
		mIsUpToDate = false;
	}
}
