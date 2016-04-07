package rtlib.symphony.staves;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.signum;

public class RampContinuousStave extends StaveAbstract implements
																											StaveInterface
{
	private volatile float mSyncStart;
	private volatile float mSyncStop;
	private volatile float mStartValue;
	private volatile float mStopValue;
	private volatile float mOutsideValue;
	private volatile boolean mNoJump = false;
	private volatile float mExponent = 1;

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
		this(	pName,
					pSyncStart,
					pSyncStop,
					pStartValue,
					pStopValue,
					pOutsideValue,
					1);
	};

	public RampContinuousStave(	final String pName,
															float pSyncStart,
															float pSyncStop,
															float pStartValue,
															float pStopValue,
															float pOutsideValue,
															float pExponent)
	{
		super(pName);
		setSyncStart(pSyncStart);
		setSyncStop(pSyncStop);
		setStartValue(pStartValue);
		setStopValue(pStopValue);
		setOutsideValue(pOutsideValue);
		setExponent(pExponent);
	}

	@Override
	public float getValue(float pNormalizedTime)
	{
		if (pNormalizedTime < getSyncStart() || pNormalizedTime > getSyncStop())
			return getOutsideValue();

		final float lNormalizedRampTime = (pNormalizedTime - getSyncStart()) / (getSyncStop() - getSyncStart());

		if (mExponent == 1)
		{
			final float lValue = getStartValue() + (getStopValue() - getStartValue())
														* lNormalizedRampTime;
			return lValue;
		}
		else
		{
			final float lExponentiatedValue = getStartValue() + (getStopValue() - getStartValue()) * abspow(	lNormalizedRampTime,
																																																						mExponent);

			return lExponentiatedValue;
		}
	}

	public float abspow(float pValue, float pExponent)
	{
		return (float) (signum(pValue) * pow(abs(pValue), pExponent));
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

	public float getExponent()
	{
		return mExponent;
	}

	public void setExponent(float pExponent)
	{
		mExponent = pExponent;
	}

	@Override
	public StaveInterface copy()
	{
		RampContinuousStave lRampContinuousStave = new RampContinuousStave(	getName(),
																																				getSyncStart(),
																																				getSyncStop(),
																																				getStartValue(),
																																				getStopValue(),
																																				getOutsideValue(),
																																				getExponent());

		return lRampContinuousStave;

	}

}
