package clearcontrol.device.startstop;

import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableEdgeListener;
import clearcontrol.device.VirtualDevice;

public abstract class SignalStartStopDevice extends VirtualDevice
{

	protected final Variable<Boolean> mStartSignal;
	protected final Variable<Boolean> mStopSignal;
	protected Runnable mStartRunnable = null;
	protected Runnable mStopRunnable = null;

	public SignalStartStopDevice(final String pDeviceName)
	{
		super(pDeviceName);

		mStartSignal = new Variable<Boolean>(pDeviceName + "Start", false);

		mStopSignal = new Variable<Boolean>(pDeviceName + "Stop", false);

		mStartSignal.addEdgeListener(new VariableEdgeListener<Boolean>()
		{
			@Override
			public void fire(final Boolean pCurrentBooleanValue)
			{
				if (mStartRunnable != null && pCurrentBooleanValue)
					mStartRunnable.run();
			}
		});

		mStopSignal.addEdgeListener(new VariableEdgeListener<Boolean>()
		{
			@Override
			public void fire(final Boolean pCurrentBooleanValue)
			{
				if (mStopRunnable != null && pCurrentBooleanValue)
					mStopRunnable.run();
			}
		});

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
