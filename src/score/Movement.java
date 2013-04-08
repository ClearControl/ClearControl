package score;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import score.interfaces.MovementInterface;
import score.interfaces.StaveInterface;
import score.staves.ConstantStave;

public class Movement extends NameableAbstract implements
																							MovementInterface
{

	public final static int cMaximumNumberOfTimePointsPerBuffer = StaveAbstract.cMaximumNumberOfTimePointsPerBuffer;
	public static final int cDefaultNumberOfStavesPerMovement = 16;

	private double mDeltaTimeInMicroseconds;
	private final StaveInterface[] mStaveListArray;
	private boolean mIsSync = false;
	private boolean mIsSyncOnRisingEdge = false;
	private int mSyncChannel = 0;

	private ShortBuffer mMovementShortBuffer;
	private boolean mIsUpToDateBasedOnStaveList = false;

	public static final Movement NullMovement = new Movement("NullMovement");

	public Movement(final String pName)
	{
		this(pName, cDefaultNumberOfStavesPerMovement);
	}

	public Movement(final String pName, final int pNumberOfStaves)
	{
		super(pName);
		mStaveListArray = new StaveInterface[pNumberOfStaves];
		for (int i = 0; i < pNumberOfStaves; i++)
		{
			mStaveListArray[i] = new ConstantStave("Zero", 0);
		}
	}

	public void setTotalDurationAndGranularityInMicroseconds(	final double pTotalDurationInMicroseconds,
																														final double pMinDeltaTimeInMicroseconds)
	{
		final int lMaxNumberOfTimePointsPerMovement = getMaxNumberOfTimePointsPerMovement();

		final int lMaxNumberOfTimePointsFittingInTotalDuration = Math.min(lMaxNumberOfTimePointsPerMovement,
																																			(int) (pTotalDurationInMicroseconds / pMinDeltaTimeInMicroseconds));

		setNumberOfTimePoints(lMaxNumberOfTimePointsFittingInTotalDuration);

		final double lDeltaTimeInMicroseconds = pTotalDurationInMicroseconds / lMaxNumberOfTimePointsFittingInTotalDuration;

		setDeltaTimeInMicroseconds(lDeltaTimeInMicroseconds);

	}

	public void setTotalDurationInMicrosecondsAndNumberOfPoints(final double pTotalDurationInMicroseconds,
																															final int pNumberOfPoints)
	{
		final int lMaxNumberOfTimePointsPerMovement = getMaxNumberOfTimePointsPerMovement();

		final int lNumberOfTimePointsForTotalDuration = Math.min(	lMaxNumberOfTimePointsPerMovement,
																															pNumberOfPoints);

		setNumberOfTimePoints(lNumberOfTimePointsForTotalDuration);

		final double lDeltaTimeInMicroseconds = pTotalDurationInMicroseconds / lNumberOfTimePointsForTotalDuration;

		setDeltaTimeInMicroseconds(lDeltaTimeInMicroseconds);

	}

	public void setDeltaTimeInMicroseconds(final double pDeltaTimeInMicroeconds)
	{
		mDeltaTimeInMicroseconds = pDeltaTimeInMicroeconds;
	}

	public double getDeltaTimeInMicroseconds()
	{
		return mDeltaTimeInMicroseconds;
	}

	public int getNumberOfTimePoints()
	{
		StaveInterface lFirstStave = getFirstStave();
		return lFirstStave.getNumberOfTimePoints();
	}

	public void setNumberOfTimePoints(final int pNumberOfTimePoints)
	{
		for (StaveInterface lStave : mStaveListArray)
		{
			lStave.setNumberOfTimePoints(pNumberOfTimePoints);
		}
	}

	@Override
	public int getMaxNumberOfTimePointsPerMovement()
	{
		return StaveAbstract.cMaximumNumberOfTimePointsPerBuffer;
	}

	public boolean setStave(final int pStaveIndex,
													final StaveInterface pNewStave)
	{
		mStaveListArray[pStaveIndex] = pNewStave;
		mIsUpToDateBasedOnStaveList = false;
		return true;
	}

	private StaveInterface getFirstStave()
	{
		return mStaveListArray[0];
	}

	public int computeMovementBufferLength()
	{
		// final StaveInterface lFirstStave = getFirstStave();
		final int lStaveBufferLength = cMaximumNumberOfTimePointsPerBuffer;
		final int lMovementBufferLength = mStaveListArray.length * lStaveBufferLength;
		return lMovementBufferLength;
	}

	public ShortBuffer getMovementBuffer()
	{
		if (!isUpToDate())
		{
			updateMovementBuffer();
			mIsUpToDateBasedOnStaveList = true;
		}

		return mMovementShortBuffer;
	}

	private void updateMovementBuffer()
	{
		final int lMovementBufferLength = computeMovementBufferLength();
		final int lCurrentMovementBufferCapacity = mMovementShortBuffer == null	? 0
																																						: mMovementShortBuffer.capacity();
		if (mMovementShortBuffer == null || lCurrentMovementBufferCapacity < lMovementBufferLength)
		{
			final int lMovementBufferLengthInBytes = lMovementBufferLength * 2;
			mMovementShortBuffer = ByteBuffer.allocateDirect(lMovementBufferLengthInBytes)
																				.order(ByteOrder.nativeOrder())
																				.asShortBuffer();
		}

		mMovementShortBuffer.limit(lMovementBufferLength);
		mMovementShortBuffer.rewind();
		for (StaveInterface lStave : mStaveListArray)
		{
			final ShortBuffer lStaveShortBuffer = lStave.getStaveBuffer();
			lStaveShortBuffer.rewind();
		}

		while (mMovementShortBuffer.hasRemaining())
		{
			for (StaveInterface lStave : mStaveListArray)
			{
				final ShortBuffer lStaveShortBuffer = lStave.getStaveBuffer();
				if (lStaveShortBuffer.hasRemaining())
				{
					final short lShortValue = lStaveShortBuffer.get();
					mMovementShortBuffer.put(lShortValue);
				}
				else
				{
					mMovementShortBuffer.put((short) 0);
				}
			}
		}
		mMovementShortBuffer.flip();

	}

	public boolean isUpToDate()
	{
		boolean lIsUpToDate = mIsUpToDateBasedOnStaveList;
		for (StaveInterface lStave : mStaveListArray)
		{
			lIsUpToDate &= lStave.isUpToDate();
		}
		return lIsUpToDate;
	}

	public int getNumberOfStaves()
	{
		final int lNumberOfChannels = mStaveListArray.length;
		return lNumberOfChannels;
	}

	public void requestUpdateAllStaves()
	{
		for (StaveInterface lStave : mStaveListArray)
		{
			lStave.requestUpdate();
		}
	}

	@Override
	public double getDurationInMilliseconds()
	{
		return StaveAbstract.cMaximumNumberOfTimePointsPerBuffer * (getDeltaTimeInMicroseconds() * 0.001);
	}

	@Override
	public boolean isSync()
	{
		return mIsSync;
	}

	@Override
	public boolean isSyncOnRisingEdge()
	{
		return mIsSyncOnRisingEdge;
	}

	@Override
	public int getSyncChannel()
	{
		return mSyncChannel;
	}

	@Override
	public String toString()
	{
		return String.format("Movement[%s]", getName());
	}

}
