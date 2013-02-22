package score;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import score.interfaces.MovementInterface;
import score.interfaces.ScoreInterface;

public class CompiledScore
{
	private ArrayList<CompiledMovement> mCompiledMovementList = new ArrayList<CompiledMovement>();
	private String mName;

	private volatile boolean mIsUpToDate = false;
	private ShortBuffer mDeltaTimeShortBuffer;
	private ShortBuffer mNumberTimePointsToPlayShortBuffer;
	private ShortBuffer mMatricesShortBuffer;
	private double mBufferDeltaTimeUnitInNanoseconds;

	public CompiledScore(final String pName, double pBufferDeltaTimeUnitInNanoseconds)
	{
		mName = pName;
		mBufferDeltaTimeUnitInNanoseconds = pBufferDeltaTimeUnitInNanoseconds;
	}

	public void addMovement(MovementInterface pMovement)
	{
		CompiledMovement lCompiledMovement = new CompiledMovement(pMovement);
		mCompiledMovementList.add(lCompiledMovement);
		mIsUpToDate = false;
	}

	public void clear()
	{
		mCompiledMovementList.clear();
		mIsUpToDate = false;
	}

	public ArrayList<CompiledMovement> getMovements()
	{
		return mCompiledMovementList;
	}

	public int getNumberOfMovements()
	{
		return mCompiledMovementList.size();
	}

	public ShortBuffer getDeltaTimeBuffer()
	{
		ensureMovementsBufferIsUpToDate();
		return mDeltaTimeShortBuffer;
	}

	public ShortBuffer getNumberOfTimePointsToPlayBuffer()
	{
		ensureMovementsBufferIsUpToDate();
		return mNumberTimePointsToPlayShortBuffer;
	}

	public ShortBuffer getScoreBuffer()
	{
		ensureMovementsBufferIsUpToDate();
		return mMatricesShortBuffer;
	}

	private void ensureMovementsBufferIsUpToDate()
	{
		if (mIsUpToDate)
			return;

		final int lNumberOfMatrices = mCompiledMovementList.size() + 1;

		int lMatricesBufferLengthInBytes = lNumberOfMatrices * Movement.cDefaultNumberOfStavesPerMovement
																				* StaveAbstract.cMaximumNumberOfTimePointsPerBuffer
																				* 2;

		final int lDeltaTimeAndNumberOfPointsBufferLengthInBytes = 2 * lNumberOfMatrices;

		if (mDeltaTimeShortBuffer == null || mDeltaTimeShortBuffer.capacity() < lDeltaTimeAndNumberOfPointsBufferLengthInBytes)
		{
			mDeltaTimeShortBuffer = ByteBuffer.allocateDirect(lDeltaTimeAndNumberOfPointsBufferLengthInBytes)
																				.order(ByteOrder.nativeOrder())
																				.asShortBuffer();
		}

		if (mNumberTimePointsToPlayShortBuffer == null || mNumberTimePointsToPlayShortBuffer.capacity() < lDeltaTimeAndNumberOfPointsBufferLengthInBytes)
		{
			mNumberTimePointsToPlayShortBuffer = ByteBuffer.allocateDirect(lDeltaTimeAndNumberOfPointsBufferLengthInBytes)
																											.order(ByteOrder.nativeOrder())
																											.asShortBuffer();
		}

		if (mMatricesShortBuffer == null || mMatricesShortBuffer.capacity() < lMatricesBufferLengthInBytes)
		{
			mMatricesShortBuffer = ByteBuffer.allocateDirect(lMatricesBufferLengthInBytes)
																				.order(ByteOrder.nativeOrder())
																				.asShortBuffer();
		}

		mDeltaTimeShortBuffer.clear();
		mNumberTimePointsToPlayShortBuffer.clear();
		mMatricesShortBuffer.clear();

		short lDeltaTime = -1;
		short lNumberOfTimePointsToPlay = 0;
		for (CompiledMovement lCompiledMovement : mCompiledMovementList)
		{
			mDeltaTimeShortBuffer.put(lDeltaTime);
			mNumberTimePointsToPlayShortBuffer.put(lNumberOfTimePointsToPlay);
			lDeltaTime = (short) ((lCompiledMovement.getDeltaTimeInMicroseconds()*1000)/mBufferDeltaTimeUnitInNanoseconds);
			lNumberOfTimePointsToPlay = (short) lCompiledMovement.getNumberOfTimePoints();
			
			ShortBuffer lMovementBuffer = lCompiledMovement.getMovementBuffer();
			lMovementBuffer.rewind();
			mMatricesShortBuffer.put(lMovementBuffer);
		}
		mDeltaTimeShortBuffer.put(lDeltaTime);
		mNumberTimePointsToPlayShortBuffer.put(lNumberOfTimePointsToPlay);
		mMatricesShortBuffer.clear();

		mIsUpToDate = true;
	}

	@Override
	public String toString()
	{
		return String.format("CompiledScore-%s", mName);
	}

}
