package rtlib.symphony.staves;

import rtlib.symphony.interfaces.StaveInterface;

public class GalvoScannerStave extends RampStave implements
																								StaveInterface
{

	public GalvoScannerStave(final String pName)
	{
		super("Galvo Scanner" + pName);
	}

}
