package score.staves;

import score.interfaces.StaveInterface;

public class GalvoScannerStave extends RampStave implements
																								StaveInterface
{

	public GalvoScannerStave(final String pName)
	{
		super("Galvo Scanner" + pName);
	}

}
