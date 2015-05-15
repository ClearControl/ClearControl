package rtlib.symphony.score;

import static java.lang.Math.max;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import rtlib.symphony.movement.CompiledMovement;
import rtlib.symphony.movement.Movement;
import rtlib.symphony.movement.MovementInterface;

public class CompiledScore implements ScoreInterface
{
	private final ArrayList<MovementInterface> mCompiledMovementList = new ArrayList<>();
	private final String mName;

	private volatile boolean mIsUpToDate = false;
	private IntBuffer mDeltaTimeShortBuffer;
	private IntBuffer mSyncShortBuffer;
	private IntBuffer mNumberfOfTimePointsBuffer;
	private ShortBuffer mMatricesShortBuffer;

	public CompiledScore(final String pName)
	{
		mName = pName;
	}

	public CompiledScore(	Score pScore,
												final double pBufferDeltaTimeUnitInNanoseconds)
	{
		mName = pScore.getName() + "Compiled";

		for (final MovementInterface lMovement : pScore.getMovements())
		{
			addMovement(lMovement);
		}
	}

	public void addScore(ScoreInterface pScore)
	{
		for (final MovementInterface lMovement : pScore.getMovements())
			addMovement(lMovement);
	}

	@Override
	public void addMovement(final MovementInterface pMovement)
	{
		final CompiledMovement lCompiledMovement = new CompiledMovement(pMovement);
		mCompiledMovementList.add(lCompiledMovement);
		mIsUpToDate = false;
	}

	@Override
	public void addMovementMultipleTimes(	MovementInterface pMovement,
																				int pNumberOfTimes)
	{
		for (int i = 0; i < pNumberOfTimes; i++)
			addMovement(pMovement);
	}

	@Override
	public void clear()
	{
		mCompiledMovementList.clear();
		mIsUpToDate = false;
	}

	@Override
	public MovementInterface getMovement(int pMovementIndex)
	{
		return mCompiledMovementList.get(pMovementIndex);
	}

	@Override
	public ArrayList<MovementInterface> getMovements()
	{
		return mCompiledMovementList;
	}

	@Override
	public int getNumberOfMovements()
	{
		return mCompiledMovementList.size();
	}

	public IntBuffer getDeltaTimeBuffer(double pBufferDeltaTimeUnitInNanoseconds)
	{
		ensureBuffersAreUpToDate(pBufferDeltaTimeUnitInNanoseconds);
		return mDeltaTimeShortBuffer;
	}

	public IntBuffer getSyncBuffer(double pBufferDeltaTimeUnitInNanoseconds)
	{
		ensureBuffersAreUpToDate(pBufferDeltaTimeUnitInNanoseconds);
		return mSyncShortBuffer;
	}

	public IntBuffer getNumberOfTimePointsBuffer(double pBufferDeltaTimeUnitInNanoseconds)
	{
		ensureBuffersAreUpToDate(pBufferDeltaTimeUnitInNanoseconds);
		return mNumberfOfTimePointsBuffer;
	}

	public ShortBuffer getScoreBuffer(double pBufferDeltaTimeUnitInNanoseconds)
	{
		ensureBuffersAreUpToDate(pBufferDeltaTimeUnitInNanoseconds);
		return mMatricesShortBuffer;
	}

	private void ensureBuffersAreUpToDate(double pBufferDeltaTimeUnitInNanoseconds)
	{
		if (mIsUpToDate)
		{
			return;
		}

		final int lNumberOfMatrices = mCompiledMovementList.size();

		final int lDeltaTimeBufferLengthInBytes = 4 * lNumberOfMatrices;

		if (mDeltaTimeShortBuffer == null || mDeltaTimeShortBuffer.capacity() < lDeltaTimeBufferLengthInBytes)
		{
			mDeltaTimeShortBuffer = ByteBuffer.allocateDirect(lDeltaTimeBufferLengthInBytes)
																				.order(ByteOrder.nativeOrder())
																				.asIntBuffer();
		}

		final int lSyncBufferLengthInBytes = 4 * lNumberOfMatrices;

		if (mSyncShortBuffer == null || mSyncShortBuffer.capacity() < lSyncBufferLengthInBytes)
		{
			mSyncShortBuffer = ByteBuffer.allocateDirect(lSyncBufferLengthInBytes)
																		.order(ByteOrder.nativeOrder())
																		.asIntBuffer();
		}

		final int lNumberOfTimePointsBufferLengthInBytes = 4 * lNumberOfMatrices;

		if (mNumberfOfTimePointsBuffer == null || mNumberfOfTimePointsBuffer.capacity() < lNumberOfTimePointsBufferLengthInBytes)
		{
			mNumberfOfTimePointsBuffer = ByteBuffer.allocateDirect(lNumberOfTimePointsBufferLengthInBytes)
																							.order(ByteOrder.nativeOrder())
																							.asIntBuffer();
		}

		final int lMatricesBufferLengthInBytes = (int) (Movement.cDefaultNumberOfStavesPerMovement * getTotalNumberOfTimePoints() * 2);

		if (mMatricesShortBuffer == null || mMatricesShortBuffer.capacity() < lMatricesBufferLengthInBytes)
		{
			mMatricesShortBuffer = ByteBuffer.allocateDirect(lMatricesBufferLengthInBytes)
																				.order(ByteOrder.nativeOrder())
																				.asShortBuffer();
		}

		mDeltaTimeShortBuffer.clear();
		mSyncShortBuffer.clear();
		mNumberfOfTimePointsBuffer.clear();
		mMatricesShortBuffer.clear();

		for (final MovementInterface lMovement : mCompiledMovementList)
		{
			final int lDeltaTime = (int) (lMovement.getDeltaTimeInMicroseconds() * 1000 / pBufferDeltaTimeUnitInNanoseconds);
			mDeltaTimeShortBuffer.put(lDeltaTime);

			final byte lSyncMode = (byte) (lMovement.isSync()	? 0
																																: lMovement.isSyncOnRisingEdge() ? 1
																																																				: 2);
			final byte lSyncChannel = (byte) lMovement.getSyncChannel();
			final int lSync = twoBytesToShort(lSyncChannel, lSyncMode);
			mSyncShortBuffer.put(lSync);

			mNumberfOfTimePointsBuffer.put(lMovement.getNumberOfTimePoints());

			final ShortBuffer lMovementBuffer = lMovement.getMovementBuffer();
			lMovementBuffer.rewind();

			mMatricesShortBuffer.put(lMovementBuffer);
		}

		mDeltaTimeShortBuffer.flip();
		mSyncShortBuffer.flip();
		mNumberfOfTimePointsBuffer.flip();
		mMatricesShortBuffer.flip();

		mIsUpToDate = true;
	}

	@Override
	public long getTotalNumberOfTimePoints()
	{
		int lTotalNumberOfTimePoints = 0;
		for (final MovementInterface lMovement : mCompiledMovementList)
		{
			lTotalNumberOfTimePoints += lMovement.getNumberOfTimePoints();
		}
		return lTotalNumberOfTimePoints;
	}

	@Override
	public String toString()
	{
		return String.format("CompiledScore-%s", mName);
	}

	private static short twoBytesToShort(	final byte pHigh,
																				final byte pLow)
	{
		final short lShort = (short) (pHigh << 8 | pLow & 0xFF);
		return lShort;
	}

	@Override
	public boolean isUpToDate()
	{
		return true;
	}

	@Override
	public void removeMovementAt(int pIndex)
	{
		throw new UnsupportedOperationException("Cannot remove movements from a compiled score");
	}

	@Override
	public void insertMovementAt(int pIndex, MovementInterface pMovement)
	{
		throw new UnsupportedOperationException("Cannot remove movements from a compiled score");
	}

	@Override
	public int getMaxNumberOfStaves()
	{
		int lMaxNumberOfStaves = 0;
		
		for(final MovementInterface lMovement : mCompiledMovementList)
			lMaxNumberOfStaves = max(	lMaxNumberOfStaves,
																lMovement.getNumberOfStaves());
		
		return lMaxNumberOfStaves;
	}



}
