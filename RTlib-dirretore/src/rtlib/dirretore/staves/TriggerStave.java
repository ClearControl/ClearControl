package rtlib.dirretore.staves;

import rtlib.dirretore.functions.Interval;
import rtlib.dirretore.functions.Set;
import rtlib.dirretore.interfaces.StaveInterface;

public class TriggerStave extends StaveAbstract	implements
																								StaveInterface
{
	public volatile boolean mEnabled = false;
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
		if (!mEnabled)
		{
			return;
		}

		if (mReverse)
		{
			Interval.add(this, mSyncStart, mSyncStop, 0, 1);
		}
		else
		{
			Interval.add(this, mSyncStart, mSyncStop, 1, 0);
		}
	}
}
