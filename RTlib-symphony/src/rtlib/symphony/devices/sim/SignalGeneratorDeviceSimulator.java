package rtlib.symphony.devices.sim;

import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.symphony.devices.SignalGeneratorInterface;
import rtlib.symphony.score.CompiledScore;

public class SignalGeneratorDeviceSimulator implements SignalGeneratorInterface
{

	private final BooleanVariable mTriggerVariable;

	private int mNumberOfFramesPerMovement = 1;

	public SignalGeneratorDeviceSimulator(int pNumberOfFramesPerMovement)
	{
		super();
		mNumberOfFramesPerMovement = pNumberOfFramesPerMovement;

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
	public boolean play(CompiledScore pCompiledScore)
	{
		final int lNumberOfMovements = pCompiledScore.getNumberOfMovements();

		try
		{
			Thread.sleep(lNumberOfMovements / 2);
		}
		catch (final InterruptedException e)
		{
		}
		mTriggerVariable.setValue(false);
		mTriggerVariable.setValue(true);

		return false;
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
