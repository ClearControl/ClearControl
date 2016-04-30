package clearcontrol.microscope.lightsheet.component.lightsheet.si;

import clearcontrol.hardware.signalgen.staves.StaveInterface;

public interface StructuredIlluminationPatternInterface
{

	public StaveInterface getStave(double pMarginTimeRelativeUnits);

	public int getNumberOfPhases();

}
