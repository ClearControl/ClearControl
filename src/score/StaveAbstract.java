package score;

import java.nio.ShortBuffer;

import score.interfaces.StaveInterface;



public abstract class StaveAbstract extends ScoreAbstract	implements
																													StaveInterface

{
	private final static int cDefaultNumberOfTimePoints = 2048;
	private final static int cDefaultMaximalSignalIntegerAmplitude = 32767;

	private final int mMaxSignalIntegerAmplitude;
	public final short[] mArray;
	private final ShortBuffer mStaveShortBuffer;
	private boolean mIsUpToDate = false;

	public StaveAbstract(String pName)
	{
		this(	pName,
					cDefaultNumberOfTimePoints,
					cDefaultMaximalSignalIntegerAmplitude);
	}

	public StaveAbstract(String pName, final int pNumberOfTimePoints)
	{
		this(	pName,
					pNumberOfTimePoints,
					cDefaultMaximalSignalIntegerAmplitude);
	}

	public StaveAbstract(	final String pName,
												final int pNumberOfTimePoints,
												final int pMaxSignalIntegerAmplitude)
	{
		super(pName);
		mMaxSignalIntegerAmplitude = pMaxSignalIntegerAmplitude;
		final int lArrayLength = pNumberOfTimePoints;
		mArray = new short[lArrayLength];
		mStaveShortBuffer = ShortBuffer.wrap(mArray);
	}

	public void set(final int pTimePoint, final short pIntegerValue)
	{
		mArray[pTimePoint] = pIntegerValue;
	}

	public void set(final int pTimePoint,
									final double pNormalizedDoubleValue)
	{
		final short lShortValue = (short) Math.round(pNormalizedDoubleValue * mMaxSignalIntegerAmplitude);
		set(pTimePoint, lShortValue);
	}

	public void setNormalized(final double pNormalizedTimePoint,
														final double pNormalizedDoubleValue)
	{
		final int lTimePoint = getTimePointFromNormalized(pNormalizedTimePoint);
		final short lShortValue = (short) Math.round(pNormalizedDoubleValue * mMaxSignalIntegerAmplitude);
		set(lTimePoint, lShortValue);
	}

	@Override
	public double getNormalizedTimePoint(final int pIntegerTimePoint)
	{
		final int lNumberOfTimePoints = getNumberOfTimePoints();
		final double lNormalizedTimePoint = (((double) pIntegerTimePoint) / lNumberOfTimePoints);
		return lNormalizedTimePoint;
	}

	@Override
	public int getTimePointFromNormalized(double pNormalizedTimePoint)
	{
		final int lNumberOfTimePoints = getNumberOfTimePoints();
		final int lIntegerTimePoint = (int) Math.round(lNumberOfTimePoints * pNormalizedTimePoint);
		return lIntegerTimePoint;
	}

	public int getNumberOfTimePoints()
	{
		return getStaveBufferLength();
	}

	public int getStaveBufferLength()
	{
		return mArray.length;
	}

	public short[] getStaveArray()
	{
		return mArray;
	}

	public ShortBuffer getStaveBuffer()
	{
		if (!mIsUpToDate)
		{
			updateStaveBuffer();
			mIsUpToDate = true;
		}
		return mStaveShortBuffer;
	}

	public abstract void updateStaveBuffer();

	public void requestUpdate()
	{
		mIsUpToDate = false;
	}

	public boolean isUpToDate()
	{
		return mIsUpToDate;
	}

	public void setUpToDate(final boolean pIsUpToDate)
	{
		mIsUpToDate = pIsUpToDate;
	}

	@Override
	public boolean isCompatibleWith(StaveInterface pStave)
	{
		final boolean lSameNumberOfTimePoints = getNumberOfTimePoints() == pStave.getNumberOfTimePoints();
		return lSameNumberOfTimePoints;
	}

	@Override
	public int getMaximalSignalIntegerAmplitude()
	{
		return cDefaultMaximalSignalIntegerAmplitude;
	}

}
