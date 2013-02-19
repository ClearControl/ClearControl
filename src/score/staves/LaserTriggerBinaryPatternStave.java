package score.staves;

import score.functions.BinaryPattern;
import score.interfaces.StaveInterface;

public class LaserTriggerBinaryPatternStave extends TriggerStave	implements
																														StaveInterface
{
	public volatile boolean mEnablePattern = true;
	public volatile int mPatternPeriod = 8;
	public volatile int mPatternOnLength = 1;

	public LaserTriggerBinaryPatternStave(String pName)
	{
		super("Laser Trigger - " + pName);
	}

	@Override
	public void updateStaveBuffer()
	{
		super.updateStaveBuffer();
		if (mEnablePattern)
			BinaryPattern.write(this,mPatternPeriod,mPatternOnLength);
	}

}
