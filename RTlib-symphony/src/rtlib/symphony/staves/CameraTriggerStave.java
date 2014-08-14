package rtlib.symphony.staves;

import rtlib.symphony.interfaces.StaveInterface;

public class CameraTriggerStave extends TriggerStave implements
																										StaveInterface
{

	public CameraTriggerStave(final String pName)
	{
		super("Camera Trigger - " + pName);
	}


}
