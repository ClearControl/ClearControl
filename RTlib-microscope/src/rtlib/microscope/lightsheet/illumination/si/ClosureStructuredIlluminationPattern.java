package rtlib.microscope.lightsheet.illumination.si;

import rtlib.symphony.staves.ClosurePatternSteppingStave;

public class ClosureStructuredIlluminationPattern	extends
																									GenericStructuredIlluminationPattern<ClosurePatternSteppingStave>	implements
																																																										StructuredIlluminatioPatternInterface
{

	public ClosureStructuredIlluminationPattern(ClosurePatternSteppingStave.SteppingFunction pSteppingFunction,
																							int pNumberOfPhases)
	{
		super(new ClosurePatternSteppingStave("trigger.out.e",
																					pSteppingFunction),
					pNumberOfPhases);
	}

	public void setSteppingFunction(ClosurePatternSteppingStave.SteppingFunction pSteppingFunction)
	{
		setStave(new ClosurePatternSteppingStave(	"trigger.out.e",
																							pSteppingFunction));
	}


}
