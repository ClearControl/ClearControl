package rtlib.dirretore.staves;

import rtlib.dirretore.interfaces.StaveInterface;

public class CameraTriggerStave extends TriggerStave implements
																										StaveInterface
{

	public CameraTriggerStave(final String pName)
	{
		super("Camera Trigger - " + pName);
	}


}
