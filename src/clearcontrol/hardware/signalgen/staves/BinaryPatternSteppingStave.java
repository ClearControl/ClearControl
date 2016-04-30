package clearcontrol.hardware.signalgen.staves;

public class BinaryPatternSteppingStave extends PatternSteppingStave implements
																																		StaveInterface
{

	private volatile int mPatternPeriod = 9;
	private volatile int mPatternPhaseIndex = 0;
	private volatile int mPatternOnLength = 1;
	private volatile int mPatternPhaseIncrement = 1;

	public BinaryPatternSteppingStave(final String pName)
	{
		super(pName);
	}

	public BinaryPatternSteppingStave(final String pName,
																		float pSyncStart,
																		float pSyncStop,
																		int pNumberOfSteps,
																		int pPeriod,
																		int pPhaseIndex,
																		int pOnLength,
																		int pPhaseIncrement)
	{
		super(pName);
		setNumberOfSteps(pNumberOfSteps);
		setSyncStart(pSyncStart);
		setSyncStop(pSyncStop);
		mPatternPeriod = pPeriod;
		mPatternPhaseIndex = pPhaseIndex;
		mPatternOnLength = pOnLength;
		mPatternPhaseIncrement = pPhaseIncrement;
	}

	@Override
	public float function(int pIndex)
	{
		final int modulo = (pIndex + getPatternPhaseIndex()) % getPatternPeriod();
		return modulo < getPatternOnLength() ? 1 : 0;
	}

	public int getPatternPeriod()
	{
		return mPatternPeriod;
	}

	public void setPatternPeriod(int pPatternPeriod)
	{
		mPatternPeriod = pPatternPeriod;
	}

	public int getPatternPhaseIndex()
	{
		return mPatternPhaseIndex;
	}

	public void setPatternPhaseIndex(int pPatternPhaseIndex)
	{
		mPatternPhaseIndex = pPatternPhaseIndex;
	}

	public int getPatternOnLength()
	{
		return mPatternOnLength;
	}

	public void setPatternOnLength(int pPatternOnLength)
	{
		mPatternOnLength = pPatternOnLength;
	}

	public int getPatternPhaseIncrement()
	{
		return mPatternPhaseIncrement;
	}

	public void setPatternPhaseIncrement(int pPatternPhaseIncrement)
	{
		mPatternPhaseIncrement = pPatternPhaseIncrement;
	}

	@Override
	public StaveInterface copy()
	{
		return new BinaryPatternSteppingStave(getName(),
																					getSyncStart(),
																					getSyncStop(),
																					getNumberOfSteps(),
																					getPatternPeriod(),
																					getPatternPhaseIndex(),
																					getPatternOnLength(),
																					getPatternPhaseIncrement());
	}

}
