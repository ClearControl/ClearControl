package rtlib.symphony.staves;

import rtlib.symphony.functions.Ramp;
import rtlib.symphony.functions.Set;
import rtlib.symphony.interfaces.StaveInterface;

public class RampStave extends StaveAbstract implements
																						StaveInterface
{
	public volatile double mSyncStart, mSyncStop, mStartValue,
			mStopValue, mOutsideValue;
	public volatile boolean mNoJump = false;

	public RampStave(final String pName)
	{
		super("Ramp Stave - " + pName);
	}

	@Override
	public void updateStaveBuffer()
	{
		Set.write(this, 0);
		if (mNoJump)
		{
			Ramp.write(this, 0, mSyncStart, mOutsideValue, mStartValue, 0);
			Ramp.write(this, mSyncStop, 1, mStopValue, mOutsideValue, 0);
		}
		Ramp.write(	this,
								mSyncStart,
								mSyncStop,
								mStartValue,
								mStopValue,
								0);
	}
}
