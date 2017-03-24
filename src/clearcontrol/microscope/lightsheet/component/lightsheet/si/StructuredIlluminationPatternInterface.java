package clearcontrol.microscope.lightsheet.component.lightsheet.si;

import clearcontrol.devices.signalgen.staves.StaveInterface;

public interface StructuredIlluminationPatternInterface
{

  public StaveInterface getStave(double pMarginTimeRelativeUnits);

  public int getNumberOfPhases();

}
