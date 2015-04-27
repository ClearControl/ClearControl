package rtlib.symphony.staves;

import java.nio.ShortBuffer;

import rtlib.core.device.NameableAbstract;
import rtlib.symphony.interfaces.StaveInterface;

public abstract class StaveAbstract extends NameableAbstract implements
																														StaveInterface

{
	public final static int cDefaultMaximalSignalIntegerAmplitude = 32767;
	public final static int cDefaultNumberOfTimePoints = 2048;

	private final int mMaxSignalIntegerAmplitude;
	public short[] mArray;
	private ShortBuffer mStaveShortBuffer;
	private boolean mIsUpToDate = false;

	public StaveAbstract(final String pName)
	{
		this(pName, cDefaultNumberOfTimePoints);
	}

	public StaveAbstract(	final String pName,
												final int pNumberOfTimePoints)
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
		setNumberOfTimePoints(pNumberOfTimePoints);
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
		final double lNormalizedTimePoint = (double) pIntegerTimePoint / lNumberOfTimePoints;
		return lNormalizedTimePoint;
	}

	@Override
	public int getTimePointFromNormalized(final double pNormalizedTimePoint)
	{
		final int lNumberOfTimePoints = getNumberOfTimePoints();
		final int lIntegerTimePoint = (int) Math.round(lNumberOfTimePoints * pNormalizedTimePoint);
		return lIntegerTimePoint;
	}

	@Override
	public void setNumberOfTimePoints(final int pNumberOfTimePoints)
	{
		if (mArray != null && pNumberOfTimePoints == mArray.length)
		{
			return;
		}
		final int lArrayLength = pNumberOfTimePoints;
		mArray = new short[lArrayLength];
		mStaveShortBuffer = ShortBuffer.wrap(mArray);
		requestUpdate();
	}

	@Override
	public int getNumberOfTimePoints()
	{
		return getStaveBufferLength();
	}

	@Override
	public int getStaveBufferLength()
	{
		return mArray.length;
	}

	@Override
	public short[] getStaveArray()
	{
		return mArray;
	}

	@Override
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

	@Override
	public void requestUpdate()
	{
		mIsUpToDate = false;
	}

	@Override
	public boolean isUpToDate()
	{
		return mIsUpToDate;
	}

	public void setUpToDate(final boolean pIsUpToDate)
	{
		mIsUpToDate = pIsUpToDate;
	}

	@Override
	public boolean isCompatibleWith(final StaveInterface pStave)
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
