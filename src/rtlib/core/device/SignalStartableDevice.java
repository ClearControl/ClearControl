package rtlib.core.device;

import rtlib.core.variable.ObjectVariable;
import rtlib.core.variable.VariableEdgeListener;

public abstract class SignalStartableDevice	extends
																						NamedVirtualDevice implements
																															OpenCloseDeviceInterface,
																															StartStopDeviceInterface
{

	protected final ObjectVariable<Boolean> mStartSignal;

	protected final ObjectVariable<Boolean> mStopSignal;

	public SignalStartableDevice(final String pDeviceName)
	{
		this(pDeviceName, false);
	}

	public SignalStartableDevice(	final String pDeviceName,
																final boolean pOnlyStart)
	{
		super(pDeviceName);

		mStartSignal = new ObjectVariable<Boolean>(	pDeviceName + "Start",
																								false);

		mStopSignal = new ObjectVariable<Boolean>(pDeviceName + "Stop",
																							false);

		mStartSignal.addEdgeListener(new VariableEdgeListener<Boolean>()
		{
			@Override
			public void fire(final Boolean pCurrentBooleanValue)
			{
				if (pCurrentBooleanValue)
				{
					start();
				}
			}
		});

		if (!pOnlyStart)
		{
			mStopSignal.addEdgeListener(new VariableEdgeListener<Boolean>()
			{
				@Override
				public void fire(final Boolean pCurrentBooleanValue)
				{
					if (pCurrentBooleanValue)
					{
						stop();
					}
				}
			});
		}
	}

	public ObjectVariable<Boolean> getStartSignalBooleanVariable()
	{
		return mStartSignal;
	}

	public ObjectVariable<Boolean> getStopSignalBooleanVariable()
	{
		return mStopSignal;
	}

	@Override
	public abstract boolean start();

	@Override
	public abstract boolean stop();

}
