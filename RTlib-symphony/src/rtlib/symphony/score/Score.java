package rtlib.symphony.score;

import static java.lang.Math.max;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import rtlib.core.device.NameableAbstract;
import rtlib.symphony.movement.MovementInterface;

public class Score extends NameableAbstract implements ScoreInterface
{

	private final ArrayList<MovementInterface> mMovementList = new ArrayList<MovementInterface>();

	private ShortBuffer mScoreShortBuffer;
	private boolean mIsUpToDateBasedOnMovementList = false;

	public Score(final String pName)
	{
		super(pName);
	}

	@Override
	public void addMovement(final MovementInterface pMovement)
	{
		mIsUpToDateBasedOnMovementList = false;
		mMovementList.add(pMovement);
	}

	@Override
	public void addMovementMultipleTimes(	final MovementInterface pMovement,
																				final int pNumberOfTimes)
	{
		for (int i = 0; i < pNumberOfTimes; i++)
		{
			addMovement(pMovement);
		}
	}

	@Override
	public void insertMovementAt(	final int pIndex,
																final MovementInterface pMovement)
	{
		mIsUpToDateBasedOnMovementList = false;
		mMovementList.add(pIndex, pMovement);
	}

	@Override
	public void removeMovementAt(final int pIndex)
	{
		mIsUpToDateBasedOnMovementList = false;
		mMovementList.remove(pIndex);
	}

	@Override
	public MovementInterface getMovement(int pMovementIndex)
	{
		return mMovementList.get(pMovementIndex);
	}

	@Override
	public void clear()
	{
		mIsUpToDateBasedOnMovementList = false;
		mMovementList.clear();
	}

	@Override
	public long getTotalNumberOfTimePoints()
	{
		int lTotalNumberOfTimePoints = 0;
		for (final MovementInterface lMovement : mMovementList)
		{
			lTotalNumberOfTimePoints += lMovement.getNumberOfTimePoints();
		}
		return lTotalNumberOfTimePoints;
	}

	public ShortBuffer getScoreBuffer()
	{
		if (!isUpToDate())
		{
			updateScoreBuffer();
			mIsUpToDateBasedOnMovementList = true;
		}

		return mScoreShortBuffer;
	}

	@Override
	public boolean isUpToDate()
	{
		boolean lIsUpToDate = mIsUpToDateBasedOnMovementList;
		for (final MovementInterface lMovement : mMovementList)
		{
			lIsUpToDate &= lMovement.isUpToDate();
		}
		return lIsUpToDate;
	}

	private void updateScoreBuffer()
	{
		final int lScoreBufferLength = computeScoreBufferLength();
		final int lCurrentScoreBufferCapacity = mScoreShortBuffer == null	? 0
																																			: mScoreShortBuffer.capacity();
		if (lCurrentScoreBufferCapacity < lScoreBufferLength)
		{
			final int lScoreBufferLengthInBytes = lScoreBufferLength * 2;
			final ByteBuffer lByteBuffer = ByteBuffer.allocateDirect(lScoreBufferLengthInBytes)
																								.order(ByteOrder.nativeOrder());
			mScoreShortBuffer = lByteBuffer.asShortBuffer();
		}

		mScoreShortBuffer.limit(lScoreBufferLength);
		mScoreShortBuffer.clear();
		for (final MovementInterface lMovement : mMovementList)
		{
			final ShortBuffer lMovementShortBuffer = lMovement.getMovementBuffer();
			lMovementShortBuffer.rewind();
			mScoreShortBuffer.put(lMovementShortBuffer);
		}
		mScoreShortBuffer.flip();

		mIsUpToDateBasedOnMovementList = true;
	}

	private int computeScoreBufferLength()
	{
		int lScoreBufferLength = 0;
		for (final MovementInterface lMovement : mMovementList)
		{
			lScoreBufferLength += lMovement.computeMovementBufferLength();
		}
		return lScoreBufferLength;
	}

	@Override
	public ArrayList<MovementInterface> getMovements()
	{
		return mMovementList;
	}

	@Override
	public int getNumberOfMovements()
	{
		return mMovementList.size();
	}

	@Override
	public int getMaxNumberOfStaves()
	{
		int lMaxNumberOfStaves = 0;

		for (final MovementInterface lMovement : mMovementList)
			lMaxNumberOfStaves = max(	lMaxNumberOfStaves,
																lMovement.getNumberOfStaves());

		return lMaxNumberOfStaves;
	}

	@Override
	public String toString()
	{
		return String.format("Score-%s", getName());
	}


}
