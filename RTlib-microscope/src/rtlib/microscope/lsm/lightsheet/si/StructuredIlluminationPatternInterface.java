package rtlib.microscope.lsm.lightsheet.si;

import rtlib.symphony.staves.StaveInterface;

public interface StructuredIlluminationPatternInterface
{

	public StaveInterface getStave(double pMarginTimeRelativeUnits);

	public int getNumberOfPhases();

}
