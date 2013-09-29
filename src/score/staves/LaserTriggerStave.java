package score.staves;

import score.interfaces.StaveInterface;

public class LaserTriggerStave extends TriggerStave	implements
																										StaveInterface
{

	public LaserTriggerStave(final String pName)
	{
		super("Laser Trigger - " + pName);
	}

}
