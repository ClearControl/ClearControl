package device;

import variable.booleanv.BooleanEventListenerInterface;
import variable.booleanv.BooleanVariable;

public abstract class BaseDisplayDevice	implements
																				VirtualDevice,
																				Displayable
{
	private BooleanVariable mStartStopSignal = new BooleanVariable(false);

	public BaseDisplayDevice()
	{
		super();
		mStartStopSignal.detectEdgeWith(new BooleanEventListenerInterface()
		{
			@Override
			public void fire(	Object pBooleanEventSource,
												final boolean pCurrentBooleanValue)
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

	public abstract boolean start();

	public abstract boolean stop();

}
