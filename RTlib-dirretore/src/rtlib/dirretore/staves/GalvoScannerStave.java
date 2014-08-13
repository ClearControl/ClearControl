package rtlib.dirretore.staves;

import rtlib.dirretore.interfaces.StaveInterface;

public class GalvoScannerStave extends RampStave implements
																								StaveInterface
{

	public GalvoScannerStave(final String pName)
	{
		super("Galvo Scanner" + pName);
	}

}
