package clearcontrol.device.task;

import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableEdgeListener;
import clearcontrol.device.VirtualDevice;

public abstract class SignalStartStopDevice extends VirtualDevice
{

	protected final Variable<Boolean> mStartSignal;
	protected final Variable<Boolean> mStopSignal;
	protected Runnable mStartRunnable = null;
	protected Runnable mStopRunnable = null;

	public SignalStartStopDevice(	final String pDeviceName,
																final boolean pOnlyStart)
	{
		super(pDeviceName);

		mStartSignal = new Variable<Boolean>(pDeviceName + "Start", false);

		mStopSignal = new Variable<Boolean>(pDeviceName + "Stop", false);

		mStartSignal.addEdgeListener(new VariableEdgeListener<Boolean>()
		{
			@Override
			public void fire(final Boolean pCurrentBooleanValue)
			{
				if (pCurrentBooleanValue)
				{
					if (mStartRunnable != null)
						mStartRunnable.run();
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
						if (mStopRunnable != null)
							mStopRunnable.run();
					}
				}
			});
		}
	}

	public void setTaskOnStart(Runnable pStartRunnable)
	{
		mStartRunnable = pStartRunnable;
	}

	public void setTaskOnStop(Runnable pStopRunnable)
	{
		mStopRunnable = pStopRunnable;
	}

	public Variable<Boolean> getStartSignalBooleanVariable()
	{
		return mStartSignal;
	}

	public Variable<Boolean> getStopSignalBooleanVariable()
	{
		return mStopSignal;
	}

}
