package rtlib.symphony.devices.sim;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import rtlib.core.variable.ObjectVariable;
import rtlib.symphony.devices.SignalGeneratorBase;
import rtlib.symphony.devices.SignalGeneratorInterface;
import rtlib.symphony.score.ScoreInterface;

public class SignalGeneratorSimulatorDevice	extends
																						SignalGeneratorBase	implements
																																SignalGeneratorInterface
{

	private final ObjectVariable<Boolean> mTriggerVariable;

	public SignalGeneratorSimulatorDevice()
	{
		super(SignalGeneratorSimulatorDevice.class.getSimpleName());

		mTriggerVariable = new ObjectVariable<Boolean>(	getName() + "Trigger",
																										false);

	}

	@Override
	public boolean open()
	{
		return true;
	}

	@Override
	public boolean close()
	{
		return true;
	}

	@Override
	public boolean playScore(ScoreInterface pScore)
	{
		final long lDurationInMilliseconds = pScore.getDuration(TimeUnit.MILLISECONDS);
		try
		{
			Thread.sleep(lDurationInMilliseconds);
		}
		catch (final InterruptedException e)
		{
		}
		mTriggerVariable.setEdge(false, true);
		return true;
	}

	@Override
	public Future<Boolean> playQueue()
	{
		getTriggerVariable().setEdge(false, true);
		return super.playQueue();
	}

	@Override
	public double getTemporalGranularityInMicroseconds()
	{
		return 0;
	}

	@Override
	public ObjectVariable<Boolean> getTriggerVariable()
	{
		return mTriggerVariable;
	}

}
