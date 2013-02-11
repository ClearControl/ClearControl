package score.staves;

import score.functions.Ramp;
import score.functions.Set;
import score.interfaces.StaveInterface;

public class RampStave extends TriggerStave implements StaveInterface
{
	public volatile double mSyncStart, mSyncStop, mStartValue,
			mStopValue, mOutsideValue;
	public volatile boolean mNoJump = true;

	public RampStave(String pName)
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
