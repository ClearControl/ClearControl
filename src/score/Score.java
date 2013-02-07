package score;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import score.interfaces.MovementInterface;
import score.interfaces.ScoreInterface;



public class Score extends ScoreAbstract implements ScoreInterface
{
	private int mDeltaTime;
	private ArrayList<MovementInterface> mMovementList = new ArrayList<MovementInterface>();

	private ShortBuffer mScoreShortBuffer;
	private boolean mIsUpToDateBasedOnMovementList = false;

	public Score(final String pName)
	{
		super(pName);
	}

	public void addMovement(final MovementInterface pMovement)
	{
		mIsUpToDateBasedOnMovementList = false;
		mMovementList.add(pMovement);
	}

	public void addMovementMultipleTimes(	Movement pMovement,
																				int pNumberOfTimes)
	{
		for (int i = 0; i < pNumberOfTimes; i++)
			addMovement(pMovement);
	}

	public void addMovementAt(final int pIndex,
														final MovementInterface pMovement)
	{
		mIsUpToDateBasedOnMovementList = false;
		mMovementList.add(pIndex, pMovement);
	}

	public void removeMovementAt(final int pIndex)
	{
		mIsUpToDateBasedOnMovementList = false;
		mMovementList.remove(pIndex);
	}

	public void clear()
	{
		mIsUpToDateBasedOnMovementList = false;
		mMovementList.clear();
	}

	public int getDeltaTime()
	{
		return mDeltaTime;
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

	public boolean isUpToDate()
	{
		boolean lIsUpToDate = mIsUpToDateBasedOnMovementList;
		for (MovementInterface lMovement : mMovementList)
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
			ByteBuffer lByteBuffer = ByteBuffer.allocateDirect(lScoreBufferLengthInBytes);
			lByteBuffer.order(ByteOrder.nativeOrder());
			mScoreShortBuffer = lByteBuffer.asShortBuffer();
		}

		mScoreShortBuffer.limit(lScoreBufferLength);
		mScoreShortBuffer.clear();
		for (MovementInterface lMovement : mMovementList)
		{
			ShortBuffer lMovementShortBuffer = lMovement.getMovementBuffer();
			lMovementShortBuffer.rewind();
			mScoreShortBuffer.put(lMovementShortBuffer);
		}
		mScoreShortBuffer.flip();

	}

	private int computeScoreBufferLength()
	{
		int lScoreBufferLength = 0;
		for (MovementInterface lMovement : mMovementList)
		{
			lScoreBufferLength += lMovement.computeMovementBufferLength();
		}
		return lScoreBufferLength;
	}

	public int getNumberOfMovements()
	{
		return mMovementList.size();
	}

}
