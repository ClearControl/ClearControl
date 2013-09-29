package score;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import score.interfaces.MovementInterface;

public class CompiledMovement implements MovementInterface
{
	private final String mName;
	private final ShortBuffer mMovementBuffer;
	// private final int mMaxNumberOfTimePointsPerBuffer;
	private final int mNumberOfTimePoints;
	private final int mNumberOfStaves;
	private final double mDeltaTimeInMicroseconds;
	private final boolean mIsSync;
	private final boolean mIsSyncOnRisingEdge;
	private final int mSyncChannel;

	public CompiledMovement(final MovementInterface pMovement)
	{

		mName = pMovement.getName();
		mDeltaTimeInMicroseconds = pMovement.getDeltaTimeInMicroseconds();
		mNumberOfStaves = pMovement.getNumberOfStaves();
		mNumberOfTimePoints = pMovement.getNumberOfTimePoints();
		mIsSync = pMovement.isSync();
		mIsSyncOnRisingEdge = pMovement.isSyncOnRisingEdge();
		mSyncChannel = pMovement.getSyncChannel();

		final ShortBuffer lMovementBuffer = pMovement.getMovementBuffer();
		final int lMovementBufferlength = lMovementBuffer.limit();

		mMovementBuffer = ByteBuffer.allocateDirect(2 * lMovementBufferlength)
																.order(ByteOrder.nativeOrder())
																.asShortBuffer();

		lMovementBuffer.rewind();
		mMovementBuffer.put(lMovementBuffer);
	}

	@Override
	public boolean isUpToDate()
	{
		return true;
	}

	@Override
	public ShortBuffer getMovementBuffer()
	{
		return mMovementBuffer;
	}

	@Override
	public int computeMovementBufferLength()
	{
		return mMovementBuffer.limit();
	}

	@Override
	public double getDeltaTimeInMicroseconds()
	{
		return mDeltaTimeInMicroseconds;
	}

	@Override
	public void setDeltaTimeInMicroseconds(final double pDeltaTimeInMicroseconds)
	{
		throw new UnsupportedOperationException(this.getClass()
																								.getSimpleName() + " are final and cannot be modified");
	}

	@Override
	public double getDurationInMilliseconds()
	{
		return mNumberOfTimePoints * (mDeltaTimeInMicroseconds * 0.001);
	}

	@Override
	public int getNumberOfTimePoints()
	{
		return mNumberOfTimePoints;
	}

	@Override
	public int getNumberOfStaves()
	{
		return mNumberOfStaves;
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
		return String.format("CompiledMovement-%s", mName);
	}

	@Override
	public String getName()
	{
		return mName;
	}

}
