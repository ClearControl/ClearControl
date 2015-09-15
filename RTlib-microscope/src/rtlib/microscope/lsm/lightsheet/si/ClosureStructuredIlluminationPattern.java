package rtlib.microscope.lsm.lightsheet.si;

import rtlib.symphony.staves.ClosurePatternSteppingStave;
import rtlib.symphony.staves.SteppingFunction;

public class ClosureStructuredIlluminationPattern	extends
													GenericStructuredIlluminationPattern<ClosurePatternSteppingStave>	implements
																														StructuredIlluminationPatternInterface
{

	public ClosureStructuredIlluminationPattern(SteppingFunction pSteppingFunction,
												int pNumberOfPhases)
	{
		super(	new ClosurePatternSteppingStave("trigger.out.e",
												pSteppingFunction),
				pNumberOfPhases);
	}

	public void setSteppingFunction(SteppingFunction pSteppingFunction)
	{
		setStave(new ClosurePatternSteppingStave(	"trigger.out.e",
													pSteppingFunction));
	}

}
