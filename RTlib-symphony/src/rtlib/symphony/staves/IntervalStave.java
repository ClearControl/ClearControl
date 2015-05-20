package rtlib.symphony.staves;


public class IntervalStave extends StaveAbstract	implements
																								StaveInterface
{

	private volatile float mSyncStart = 0;
	private volatile float mSyncStop = 1;
	private volatile float mInsideValue = 1;
	private volatile float mOutsideValue = 0;

	public IntervalStave(final String pName)
	{
		super(pName);
	}

	public IntervalStave(	final String pName,
												float pSyncStart,
												float pSyncStop,
												float pInsideValue,
												float pOutsideValue)
	{
		super(pName);
		setSyncStart(pSyncStart);
		setSyncStop(pSyncStop);
		setInsideValue(pInsideValue);
		setOutsideValue(pOutsideValue);
	}

	@Override
	public float getValue(float pNormalizedTime)
	{
		if ((pNormalizedTime < getSyncStart()) || (pNormalizedTime > getSyncStop()))
			return getOutsideValue();
		else
			return getInsideValue();
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

	public float getInsideValue()
	{
		return mInsideValue;
	}

	public void setInsideValue(float pIntervalValue)
	{
		mInsideValue = pIntervalValue;
	}

	public float getOutsideValue()
	{
		return mOutsideValue;
	}

	public void setOutsideValue(float pOutsideValue)
	{
		mOutsideValue = pOutsideValue;
	}

	@Override
	public StaveInterface copy()
	{
		return new IntervalStave(	getName(),
															getSyncStart(),
															getSyncStop(),
															getInsideValue(),
															getOutsideValue());
	}

}
