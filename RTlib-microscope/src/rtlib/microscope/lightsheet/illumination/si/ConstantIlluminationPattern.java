package rtlib.microscope.lightsheet.illumination.si;

import rtlib.symphony.staves.IntervalStave;
import rtlib.symphony.staves.StaveInterface;

public class ConstantIlluminationPattern extends
																				StructuredIlluminatioPatternBase implements
																																				StructuredIlluminatioPatternInterface
{

	private final IntervalStave mStave;

	public ConstantIlluminationPattern()
	{
		super();
		mStave = new IntervalStave("trigger.out.e", 0, 1, 1, 0);
	}

	@Override
	public StaveInterface getStave(double pMarginTimeRelativeUnits)
	{
		mStave.setSyncStart((float) clamp01(pMarginTimeRelativeUnits));
		mStave.setSyncStop((float) clamp01(1 - pMarginTimeRelativeUnits));
		return mStave;
	}

	@Override
	public int getNumberOfPhases()
	{
		return 1;
	}

}
