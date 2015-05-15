package rtlib.symphony.staves;

import rtlib.symphony.functions.BinaryPattern;

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
	public void updateStaveArray()
	{
		super.updateStaveArray();
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
