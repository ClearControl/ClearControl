package score.staves;

import score.StaveAbstract;
import score.functions.Interval;
import score.functions.Set;
import score.interfaces.StaveInterface;

public class TriggerStave extends StaveAbstract	implements
																								StaveInterface
{
	public volatile double mSyncStart, mSyncStop;
	public volatile boolean mReverse = false;

	public TriggerStave(final String pName)
	{
		super(pName);
	}

	@Override
	public void updateStaveBuffer()
	{
		Set.write(this, 0);
		if (mReverse)
			Interval.write(this, mSyncStart, mSyncStop, 0, 1);
		else
			Interval.write(this, mSyncStart, mSyncStop, 1, 0);
	}

}
