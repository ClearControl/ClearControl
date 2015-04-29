package rtlib.symphony.score;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import rtlib.core.device.NameableAbstract;
import rtlib.symphony.interfaces.MovementInterface;
import rtlib.symphony.interfaces.ScoreInterface;
import rtlib.symphony.movement.Movement;

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
	public boolean addMovement(final MovementInterface pMovement)
	{
		mIsUpToDateBasedOnMovementList = false;
		mMovementList.add(pMovement);
		return true;
	}

	@Override
	public void addMovementMultipleTimes(	final Movement pMovement,
																				final int pNumberOfTimes)
	{
		for (int i = 0; i < pNumberOfTimes; i++)
		{
			addMovement(pMovement);
		}
	}

	@Override
	public void addMovementAt(final int pIndex,
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
	public void clear()
	{
		mIsUpToDateBasedOnMovementList = false;
		mMovementList.clear();
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
	public String toString()
	{
		return String.format("Score-%s", getName());
	}

}
