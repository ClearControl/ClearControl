package rtlib.microscope.lsm.component.lightsheet.si;

import rtlib.hardware.signalgen.staves.StaveInterface;

public interface StructuredIlluminationPatternInterface
{

	public StaveInterface getStave(double pMarginTimeRelativeUnits);

	public int getNumberOfPhases();

}
