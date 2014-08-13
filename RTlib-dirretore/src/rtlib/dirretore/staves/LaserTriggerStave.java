package rtlib.dirretore.staves;

import rtlib.dirretore.interfaces.StaveInterface;

public class LaserTriggerStave extends TriggerStave	implements
																										StaveInterface
{

	public LaserTriggerStave(final String pName)
	{
		super("Laser Trigger - " + pName);
	}

}
