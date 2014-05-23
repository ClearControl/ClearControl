package rtlib.core.score.staves;

import rtlib.core.score.interfaces.StaveInterface;

public class CameraTriggerStave extends TriggerStave implements
																										StaveInterface
{

	public CameraTriggerStave(final String pName)
	{
		super("Camera Trigger - " + pName);
	}

}
