package rtlib.microscope.lightsheet.illumination.si;

import rtlib.symphony.staves.StaveInterface;

public class GenericStructuredIlluminationPattern<S extends StaveInterface> extends
																																						StructuredIlluminatioPatternBase implements
																																																						StructuredIlluminatioPatternInterface
{

	private volatile S mStave;
	private volatile int mNumberOfPhases = 1;

	public GenericStructuredIlluminationPattern(S pStave,
																							int pNumberOfPhases)
	{
		super();
		setStave(pStave);
		mNumberOfPhases = pNumberOfPhases;
	}

	public void setStave(S pStave)
	{
		mStave = pStave;
	}

	@Override
	public StaveInterface getStave(double pMarginTimeRelativeUnits)
	{
		return mStave;
	}

	@Override
	public int getNumberOfPhases()
	{
		return mNumberOfPhases;
	}

	public void setNumberOfPhases(int pNumberOfPhases)
	{
		mNumberOfPhases = pNumberOfPhases;
	}

}
