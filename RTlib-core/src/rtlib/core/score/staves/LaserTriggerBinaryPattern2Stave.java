package rtlib.core.score.staves;

import rtlib.core.score.functions.BinaryPattern2;
import rtlib.core.score.interfaces.StaveInterface;

public class LaserTriggerBinaryPattern2Stave extends TriggerStave	implements
																																	StaveInterface
{
	public volatile boolean mEnablePattern = true;
	public volatile double mPatternLineLength = 100,
			mPatternPeriod = 1.5, mPatternPhaseIndex = 0,
			mPatternOnLength = 0.5, mPatternPhaseIncrement = 1;

	public LaserTriggerBinaryPattern2Stave(final String pName)
	{
		super("Laser Trigger - " + pName);
	}

	@Override
	public void updateStaveBuffer()
	{
		super.updateStaveBuffer();
		if (mEnablePattern)
		{
			BinaryPattern2.mult(this,
													mPatternLineLength,
													mPatternPeriod,
													mPatternOnLength,
													mPatternPhaseIndex,
													mPatternPhaseIncrement);
		}
	}

}
