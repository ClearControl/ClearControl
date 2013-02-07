package score.staves;

import score.StaveAbstract;
import score.functions.Interval;
import score.interfaces.StaveInterface;

public class TriggerStave extends StaveAbstract	implements
																								StaveInterface
{
	public volatile double mSyncStart, mSyncStop;

	public TriggerStave(final String pName)
	{
		super(pName);
	}

	@Override
	public void updateStaveBuffer()
	{
		Interval.write(this, mSyncStart, mSyncStop, 1, 0);
	}

}
