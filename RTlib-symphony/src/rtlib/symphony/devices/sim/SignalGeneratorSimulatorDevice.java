package rtlib.symphony.devices.sim;

import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.symphony.devices.SignalGeneratorBase;
import rtlib.symphony.devices.SignalGeneratorInterface;
import rtlib.symphony.score.ScoreInterface;

public class SignalGeneratorSimulatorDevice	extends
																						SignalGeneratorBase	implements
																																SignalGeneratorInterface
{

	private final BooleanVariable mTriggerVariable;

	public SignalGeneratorSimulatorDevice()
	{
		super(SignalGeneratorSimulatorDevice.class.getSimpleName());

		mTriggerVariable = new BooleanVariable("Trigger", false);
		
		
	}

	@Override
	public boolean open()
	{
		return true;
	}

	@Override
	public boolean start()
	{
		return true;
	}

	@Override
	public boolean stop()
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
		final int lNumberOfMovements = pScore.getNumberOfMovements();

		try
		{
			Thread.sleep(lNumberOfMovements / 2);
		}
		catch (final InterruptedException e)
		{
		}
		mTriggerVariable.setValue(false);
		mTriggerVariable.setValue(true);

		return true;
	}

	@Override
	public double getTemporalGranularityInMicroseconds()
	{
		return 0;
	}

	@Override
	public BooleanVariable getTriggerVariable()
	{
		return mTriggerVariable;
	}




}
