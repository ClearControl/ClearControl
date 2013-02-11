package score.staves;

import score.functions.HalfHalfHolePattern;
import score.interfaces.StaveInterface;

public class LaserTriggerHalfDotsStave extends TriggerStave	implements
																										StaveInterface
{

	

	public LaserTriggerHalfDotsStave(String pName)
	{
		super("Laser Trigger - "+pName);
	}
	
	@Override
	public void updateStaveBuffer()
	{
		super.updateStaveBuffer();
		HalfHalfHolePattern.write(this);
	}


}
