package score.staves;

import score.interfaces.StaveInterface;

public class CameraTriggerStave extends TriggerStave implements
																										StaveInterface
{

	public CameraTriggerStave(String pName)
	{
		super("Camera Trigger - "+pName);
	}

}
