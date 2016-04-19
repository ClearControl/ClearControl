package rtlib.hardware.signalgen.devices.sim;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import rtlib.core.variable.Variable;
import rtlib.hardware.signalgen.SignalGeneratorBase;
import rtlib.hardware.signalgen.SignalGeneratorInterface;
import rtlib.hardware.signalgen.score.ScoreInterface;

public class SignalGeneratorSimulatorDevice	extends
																						SignalGeneratorBase	implements
																																SignalGeneratorInterface
{

	private final Variable<Boolean> mTriggerVariable;

	public SignalGeneratorSimulatorDevice()
	{
		super(SignalGeneratorSimulatorDevice.class.getSimpleName());

		mTriggerVariable = new Variable<Boolean>(	getName() + "Trigger",
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
	public Variable<Boolean> getTriggerVariable()
	{
		return mTriggerVariable;
	}

}
