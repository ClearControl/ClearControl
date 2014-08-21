package rtlib.symphony.devices.sim;

import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.symphony.devices.SignalGeneratorInterface;
import rtlib.symphony.score.CompiledScore;

public class SignalGeneratorDeviceSimulator implements SignalGeneratorInterface
{

	private BooleanVariable mTriggerVariable;

	@Override
	public boolean open()
	{
		return true;
	}

	@Override
	public boolean start()
	{
		return false;
	}

	@Override
	public boolean stop()
	{
		return false;
	}

	@Override
	public boolean close()
	{
		return false;
	}

	@Override
	public boolean play(CompiledScore pCompiledScore)
	{
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
