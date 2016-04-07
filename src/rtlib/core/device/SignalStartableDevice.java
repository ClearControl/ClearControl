package rtlib.core.device;

import rtlib.core.variable.Variable;
import rtlib.core.variable.VariableEdgeListener;

public abstract class SignalStartableDevice	extends
																						NamedVirtualDevice implements
																															OpenCloseDeviceInterface,
																															StartStopDeviceInterface
{

	protected final Variable<Boolean> mStartSignal;

	protected final Variable<Boolean> mStopSignal;

	public SignalStartableDevice(final String pDeviceName)
	{
		this(pDeviceName, false);
	}

	public SignalStartableDevice(	final String pDeviceName,
																final boolean pOnlyStart)
	{
		super(pDeviceName);

		mStartSignal = new Variable<Boolean>(	pDeviceName + "Start",
																								false);

		mStopSignal = new Variable<Boolean>(pDeviceName + "Stop",
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

	public Variable<Boolean> getStartSignalBooleanVariable()
	{
		return mStartSignal;
	}

	public Variable<Boolean> getStopSignalBooleanVariable()
	{
		return mStopSignal;
	}

	@Override
	public abstract boolean start();

	@Override
	public abstract boolean stop();

}
