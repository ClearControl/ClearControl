package rtlib.symphony.staves;

import static java.lang.Math.floor;

public class BinaryPatternSteppingStave extends StaveAbstract	implements
																														StaveInterface
{

	private volatile int mPatternPeriod = 9;
	private volatile int mPatternPhaseIndex = 0;
	private volatile int mPatternOnLength = 1;
	private volatile int mPatternPhaseIncrement = 1;
	private volatile float mSyncStart = 0;
	private volatile float mSyncStop = 1;
	private volatile int mNumberOfSteps = 1024;

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
	}

	@Override
	public float getValue(float pNormalizedTime)
	{
		if (!isEnabled())
			return 1;

		if (pNormalizedTime < getSyncStart() || pNormalizedTime > getSyncStop())
			return 0;

		if (getPatternPeriod() == 0)
			return 0;

		final float lNormalizedRampTime = (pNormalizedTime - getSyncStart()) / (getSyncStop() - getSyncStart());

		final int lNormalizedSteppingRampTime = (int) floor(getNumberOfSteps() * lNormalizedRampTime);

		final int modulo = (lNormalizedSteppingRampTime + getPatternPhaseIndex()) % getPatternPeriod();
		if (modulo < getPatternOnLength())
		{
			return 1;
		}
		else
		{
			return 0;
		}
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

	public float getSyncStart()
	{
		return mSyncStart;
	}

	public void setSyncStart(float pSyncStart)
	{
		mSyncStart = pSyncStart;
	}

	public float getSyncStop()
	{
		return mSyncStop;
	}

	public void setSyncStop(float pSyncStop)
	{
		mSyncStop = pSyncStop;
	}

	public int getNumberOfSteps()
	{
		return mNumberOfSteps;
	}

	public void setNumberOfSteps(int pNumberOfSteps)
	{
		mNumberOfSteps = pNumberOfSteps;
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
