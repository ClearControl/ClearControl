package rtlib.microscope.lightsheet.illumination.si;

import rtlib.symphony.staves.StaveInterface;

public interface StructuredIlluminatioPatternInterface
{

	public StaveInterface getStave(double pMarginTimeRelativeUnits);

	public int getNumberOfPhases();

}
