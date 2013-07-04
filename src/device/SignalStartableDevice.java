package device;

import variable.booleanv.BooleanEventListenerInterface;
import variable.booleanv.BooleanVariable;

public abstract class SignalStartableDevice implements VirtualDevice
{
	protected BooleanVariable mStartStopSignal = new BooleanVariable(	"Start",
																																		false);

	public SignalStartableDevice()
	{
		super();
		mStartStopSignal.addEdgeListener(new BooleanEventListenerInterface()
		{
			@Override
			public void fire(final boolean pCurrentBooleanValue)
			{
				if (pCurrentBooleanValue)
					start();
				else
					stop();
			}
		});
	}

	public BooleanVariable getStartStopBooleanVariable()
	{
		return mStartStopSignal;
	}

	@Override
	public abstract boolean start();

	@Override
	public abstract boolean stop();

}
