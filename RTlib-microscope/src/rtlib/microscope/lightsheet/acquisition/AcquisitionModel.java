package rtlib.microscope.lightsheet.acquisition;

import rtlib.core.variable.types.doublev.DoubleVariable;

public class AcquisitionModel
{

	private final DoubleVariable mStartZ = new DoubleVariable("StartZ",
																														25);
	private final DoubleVariable mStopZ = new DoubleVariable(	"StopZ",
																														75);

	private final DoubleVariable mNumberOfPlanes = new DoubleVariable("NumberOfPlanes",
																																		64);
	private final DoubleVariable mZStep = new DoubleVariable(	"ZStep",
																														0.5);

}
