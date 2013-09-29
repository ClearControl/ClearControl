package score.staves;

import score.functions.BinaryPattern;
import score.interfaces.StaveInterface;

public class LaserTriggerBinaryPatternStave extends TriggerStave implements
																																StaveInterface
{
	public volatile boolean mEnablePattern = true;
	public volatile int mPatternPeriod = 8, mPatternPhaseIndex = 0,
			mPatternOnLength = 1, mPatternPhaseIncrement = 1;

	public LaserTriggerBinaryPatternStave(final String pName)
	{
		super("Laser Trigger - " + pName);
	}

	@Override
	public void updateStaveBuffer()
	{
		super.updateStaveBuffer();
		if (mEnablePattern)
		{
			BinaryPattern.mult(	this,
													mPatternPeriod,
													mPatternOnLength,
													mPatternPhaseIndex,
													mPatternPhaseIncrement);
		}
	}

}
