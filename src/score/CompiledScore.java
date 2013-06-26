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
	private ShortBuffer mSyncShortBuffer;
	private ShortBuffer mMatricesShortBuffer;
	private double mBufferDeltaTimeUnitInNanoseconds;

	public CompiledScore(	final String pName,
												double pBufferDeltaTimeUnitInNanoseconds)
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
		ensureBuffersAreUpToDate();
		return mDeltaTimeShortBuffer;
	}

	public ShortBuffer getSyncBuffer()
	{
		return mSyncShortBuffer;
	}

	public ShortBuffer getScoreBuffer()
	{
		ensureBuffersAreUpToDate();
		return mMatricesShortBuffer;
	}

	private void ensureBuffersAreUpToDate()
	{
		if (mIsUpToDate)
			return;

		final int lNumberOfMatrices = mCompiledMovementList.size() + 1;

		final int lDeltaTimeBufferLengthInBytes = 2 * lNumberOfMatrices;

		if (mDeltaTimeShortBuffer == null || mDeltaTimeShortBuffer.capacity() < lDeltaTimeBufferLengthInBytes)
		{
			mDeltaTimeShortBuffer = ByteBuffer.allocateDirect(lDeltaTimeBufferLengthInBytes)
																				.order(ByteOrder.nativeOrder())
																				.asShortBuffer();
		}

		final int lSyncBufferLengthInBytes = 2 * lNumberOfMatrices;

		if (mSyncShortBuffer == null || mSyncShortBuffer.capacity() < lSyncBufferLengthInBytes)
		{
			mSyncShortBuffer = ByteBuffer.allocateDirect(lSyncBufferLengthInBytes)
																		.order(ByteOrder.nativeOrder())
																		.asShortBuffer();
		}

		int lMatricesBufferLengthInBytes = lNumberOfMatrices * Movement.cDefaultNumberOfStavesPerMovement
																				* getNumberOfTimePointsPerMovement()
																				* 2;

		if (mMatricesShortBuffer == null || mMatricesShortBuffer.capacity() < lMatricesBufferLengthInBytes)
		{
			mMatricesShortBuffer = ByteBuffer.allocateDirect(lMatricesBufferLengthInBytes)
																				.order(ByteOrder.nativeOrder())
																				.asShortBuffer();
		}

		mDeltaTimeShortBuffer.clear();
		mSyncShortBuffer.clear();
		mMatricesShortBuffer.clear();

		short lDeltaTime = -1;
		short lNumberOfTimePointsToPlay = 0;
		short lSync = 0;
		for (CompiledMovement lCompiledMovement : mCompiledMovementList)
		{
			mDeltaTimeShortBuffer.put(lDeltaTime);
			mSyncShortBuffer.put(lSync);
			lDeltaTime = (short) ((lCompiledMovement.getDeltaTimeInMicroseconds() * 1000) / mBufferDeltaTimeUnitInNanoseconds);
			lNumberOfTimePointsToPlay = (short) lCompiledMovement.getNumberOfTimePoints();
			final byte lSyncMode = (byte) (lCompiledMovement.isSync()	? 0
																																: (lCompiledMovement.isSyncOnRisingEdge()	? 1
																																																					: 2));
			final byte lSyncChannel = (byte) lCompiledMovement.getSyncChannel();
			lSync = (short) twoBytesToShort(lSyncChannel, lSyncMode);

			ShortBuffer lMovementBuffer = lCompiledMovement.getMovementBuffer();
			lMovementBuffer.rewind();
			mMatricesShortBuffer.put(lMovementBuffer);
		}
		mDeltaTimeShortBuffer.put(lDeltaTime);
			mSyncShortBuffer.put(lSync);

		mDeltaTimeShortBuffer.flip();
		mSyncShortBuffer.flip();

		mMatricesShortBuffer.flip();

		mIsUpToDate = true;
	}

	@Override
	public String toString()
	{
		return String.format("CompiledScore-%s", mName);
	}

	private static short twoBytesToShort(byte pHigh, byte pLow)
	{
		final short lShort = (short) ((pHigh << 8) | (pLow & 0xFF));
		return lShort;
	}

	public int getNumberOfTimePointsPerMovement()
	{
		if(mCompiledMovementList.isEmpty())
			return -1;
		return mCompiledMovementList.get(0).getNumberOfTimePoints();
	}

}
