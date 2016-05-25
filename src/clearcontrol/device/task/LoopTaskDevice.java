package clearcontrol.device.task;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.thread.ThreadUtils;
import clearcontrol.core.concurrent.timing.Waiting;
import clearcontrol.core.log.Loggable;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.device.openclose.OpenCloseDeviceInterface;

public abstract class LoopTaskDevice extends TaskDevice	implements
																												OpenCloseDeviceInterface,
																												Loggable,
																												Waiting
{

	private final TimeUnit mTimeUnit;
	private final BoundedVariable<Double> mLoopPeriodVariable;

	private volatile long mDeadline = Long.MIN_VALUE;

	public LoopTaskDevice(final String pDeviceName)
	{
		this(pDeviceName, 0d, TimeUnit.MILLISECONDS);
	}

	public LoopTaskDevice(final String pDeviceName,
												double pPeriod,
												TimeUnit pTimeUnit)
	{
		super(pDeviceName);
		mTimeUnit = pTimeUnit;

		mLoopPeriodVariable = new BoundedVariable<Double>(pDeviceName + "LoopPeriodIn"
																													+ pTimeUnit.name(),
																											pPeriod,
																											0.0,
																											Double.POSITIVE_INFINITY,
																											0.0);

	}

	public void run()
	{
		while (!getStopSignalBooleanVariable().get())
		{
			final long lNow = System.nanoTime();
			final long lFactor = TimeUnit.NANOSECONDS.convert(1, mTimeUnit);
			final long lPeriodInNanoSeconds = (long) (mLoopPeriodVariable.get() * lFactor);
			mDeadline = lNow + lPeriodInNanoSeconds;
			boolean lResult = loop();
			final long lStopTime = System.nanoTime();

			if (lStopTime < mDeadline)
				while (System.nanoTime() < mDeadline)
				{
					ThreadUtils.sleep((mDeadline - System.nanoTime()) / 4,
														TimeUnit.NANOSECONDS);
				}

			if (!lResult)
				stopTask();
		}
	};

	public abstract boolean loop();

	public BoundedVariable<Double> getLoopPeriodVariable()
	{
		return mLoopPeriodVariable;
	}

}
