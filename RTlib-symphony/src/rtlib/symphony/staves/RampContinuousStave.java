package rtlib.symphony.staves;

import static java.lang.Math.abs;

public class RampContinuousStave extends StaveAbstract implements
																											StaveInterface
{
	private volatile float mSyncStart;
	private volatile float mSyncStop;
	private volatile float mStartValue;
	private volatile float mStopValue;
	private volatile float mOutsideValue;
	private volatile boolean mNoJump = false;

	public RampContinuousStave(final String pName)
	{
		super(pName);
	}

	public RampContinuousStave(	final String pName,
															float pSyncStart,
															float pSyncStop,
															float pStartValue,
															float pStopValue,
															float pOutsideValue)
	{
		super(pName);
		setSyncStart(pSyncStart);
		setSyncStop(pSyncStop);
		setStartValue(pStartValue);
		setStopValue(pStopValue);
		setOutsideValue(pOutsideValue);
	}

	@Override
	public float getValue(float pNormalizedTime)
	{
		if (pNormalizedTime < getSyncStart() || pNormalizedTime > getSyncStop())
			return getOutsideValue();

		final float lNormalizedRampTime = (pNormalizedTime - getSyncStart()) / (getSyncStop() - getSyncStart());

		final float lValue = getStartValue() + (getStopValue() - getStartValue())
													* lNormalizedRampTime;

		return lValue;
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

	public float getStartValue()
	{
		return mStartValue;
	}

	public void setStartValue(float pStartValue)
	{
		mStartValue = pStartValue;
	}

	public float getStopValue()
	{
		return mStopValue;
	}

	public void setStopValue(float pStopValue)
	{
		mStopValue = pStopValue;
	}

	public float getRampHeight()
	{
		return abs(mStopValue - mStartValue);
	}

	public float getOutsideValue()
	{
		return mOutsideValue;
	}

	public void setOutsideValue(float pOutsideValue)
	{
		mOutsideValue = pOutsideValue;
	}

	public boolean isNoJump()
	{
		return mNoJump;
	}

	public void setNoJump(boolean pNoJump)
	{
		mNoJump = pNoJump;
	}

	@Override
	public StaveInterface copy()
	{
		return new RampContinuousStave(	getName(),
																		getSyncStart(),
																		getSyncStop(),
																		getStartValue(),
																		getStopValue(),
																		getOutsideValue());
	}

}
