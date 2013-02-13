package score.staves;

import score.functions.HalfHalfHolePattern;
import score.interfaces.StaveInterface;

public class LaserTriggerHalfDotsStave extends TriggerStave	implements
																														StaveInterface
{
	public volatile boolean mEnablePattern = true;

	public LaserTriggerHalfDotsStave(String pName)
	{
		super("Laser Trigger - " + pName);
	}

	@Override
	public void updateStaveBuffer()
	{
		super.updateStaveBuffer();
		if (mEnablePattern)
			HalfHalfHolePattern.write(this);
	}

}
