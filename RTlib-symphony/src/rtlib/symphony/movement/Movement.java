package rtlib.symphony.movement;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import rtlib.core.device.NameableAbstract;
import rtlib.symphony.staves.ConstantStave;
import rtlib.symphony.staves.StaveInterface;

public class Movement extends NameableAbstract implements
																							MovementInterface
{

	public static final int cDefaultNumberOfStavesPerMovement = 16;

	private double mDeltaTimeInMicroseconds;
	private final StaveInterface[] mStaveListArray;
	private final boolean mIsSync = false;
	private final boolean mIsSyncOnRisingEdge = false;
	private final int mSyncChannel = 0;

	private ShortBuffer mMovementShortBuffer;
	private boolean mIsUpToDateBasedOnStaveList = false;

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
																														final double pMinDeltaTimeInMicroseconds,
																														final int pMaxNumberOfTimePointsPerMovement)
	{

		final int lMaxNumberOfTimePointsFittingInTotalDuration = Math.min(pMaxNumberOfTimePointsPerMovement,
																																			(int) (pTotalDurationInMicroseconds / pMinDeltaTimeInMicroseconds));

		setNumberOfTimePoints(lMaxNumberOfTimePointsFittingInTotalDuration);

		final double lDeltaTimeInMicroseconds = pTotalDurationInMicroseconds / lMaxNumberOfTimePointsFittingInTotalDuration;

		setDeltaTimeInMicroseconds(lDeltaTimeInMicroseconds);

	}

	public void setTotalDurationInMicrosecondsAndNumberOfPoints(final double pTotalDurationInMicroseconds,
																															final int pNumberOfPoints,
																															final int pMaxNumberOfTimePointsPerMovement)
	{
		final int lNumberOfTimePointsForTotalDuration = Math.min(	pMaxNumberOfTimePointsPerMovement,
																															pNumberOfPoints);

		setNumberOfTimePoints(lNumberOfTimePointsForTotalDuration);

		final double lDeltaTimeInMicroseconds = pTotalDurationInMicroseconds / lNumberOfTimePointsForTotalDuration;

		setDeltaTimeInMicroseconds(lDeltaTimeInMicroseconds);

	}

	@Override
	public void setDeltaTimeInMicroseconds(final double pDeltaTimeInMicroeconds)
	{
		mDeltaTimeInMicroseconds = pDeltaTimeInMicroeconds;
	}

	@Override
	public double getDeltaTimeInMicroseconds()
	{
		return mDeltaTimeInMicroseconds;
	}

	@Override
	public int getNumberOfTimePoints()
	{
		final StaveInterface lFirstStave = getFirstStave();
		return lFirstStave.getNumberOfTimePoints();
	}

	public void setNumberOfTimePoints(final int pNumberOfTimePoints)
	{
		for (final StaveInterface lStave : mStaveListArray)
		{
			lStave.setNumberOfTimePoints(pNumberOfTimePoints);
		}
	}

	@Override
	public boolean setStave(final int pStaveIndex,
													final StaveInterface pNewStave)
	{
		mStaveListArray[pStaveIndex] = pNewStave;
		mIsUpToDateBasedOnStaveList = false;
		return true;
	}

	@Override
	public StaveInterface getStave(final int pStaveIndex)
	{
		return mStaveListArray[pStaveIndex];
	}

	private StaveInterface getFirstStave()
	{
		return getStave(0);
	}

	@Override
	public int computeMovementBufferLength()
	{
		// final StaveInterface lFirstStave = getFirstStave();
		final int lStaveBufferLength = getNumberOfTimePoints();
		final int lMovementBufferLength = mStaveListArray.length * lStaveBufferLength;
		return lMovementBufferLength;
	}

	@Override
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
		for (final StaveInterface lStave : mStaveListArray)
		{
			final ShortBuffer lStaveShortBuffer = lStave.getStaveBuffer();
			lStaveShortBuffer.rewind();
		}

		int lTimePointIndex = 0;
		while (mMovementShortBuffer.hasRemaining())
		{
			for (final StaveInterface lStave : mStaveListArray)
			{
				final ShortBuffer lStaveShortBuffer = lStave.getStaveBuffer();
				if (lStaveShortBuffer.hasRemaining())
				{
					final short lShortValue = lStaveShortBuffer.get(lTimePointIndex);
					mMovementShortBuffer.put(lShortValue);
				}
				else
				{
					mMovementShortBuffer.put((short) 0);
				}
			}
			lTimePointIndex++;
		}
		mMovementShortBuffer.flip();

	}

	@Override
	public boolean isUpToDate()
	{
		boolean lIsUpToDate = mIsUpToDateBasedOnStaveList;
		for (final StaveInterface lStave : mStaveListArray)
		{
			lIsUpToDate &= lStave.isUpToDate();
		}
		return lIsUpToDate;
	}

	@Override
	public int getNumberOfStaves()
	{
		final int lNumberOfChannels = mStaveListArray.length;
		return lNumberOfChannels;
	}

	public void requestUpdateAllStaves()
	{
		for (final StaveInterface lStave : mStaveListArray)
		{
			lStave.requestUpdate();
		}
	}

	@Override
	public double getDurationInMilliseconds()
	{
		return getNumberOfTimePoints() * (getDeltaTimeInMicroseconds() * 0.001);
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

	public static Movement getNullMovement()
	{
		final Movement lNullMovement = new Movement("NullMovement");
		return lNullMovement;
	}

}
