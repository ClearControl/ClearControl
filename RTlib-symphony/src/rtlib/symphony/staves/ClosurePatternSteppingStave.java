package rtlib.symphony.staves;


public class ClosurePatternSteppingStave extends PatternSteppingStave implements
																																		StaveInterface
{

	private final SteppingFunction mSteppingFunction;

	public interface SteppingFunction
	{
		float function(int pIndex);
	};
	
	public ClosurePatternSteppingStave(	final String pName,
																			SteppingFunction pSteppingFunction)
	{
		super(pName);
		mSteppingFunction = pSteppingFunction;
	}

	public SteppingFunction getSteppingFunction()
	{
		return mSteppingFunction;
	}

	@Override
	public float function(int pIndex)
	{
		return getSteppingFunction().function(pIndex);
	}

	@Override
	public StaveInterface copy()
	{
		return new ClosurePatternSteppingStave(	getName(),
																						getSteppingFunction());
	}

}