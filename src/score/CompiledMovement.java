package score;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import score.interfaces.MovementInterface;

public class CompiledMovement implements MovementInterface
{
	private final ShortBuffer mMovementBuffer;
	private final int mDeltaTimeInMicroseconds;

	public CompiledMovement(MovementInterface pMovement)
	{
		mDeltaTimeInMicroseconds = pMovement.getDeltaTimeInMicroseconds();

		ShortBuffer lMovementBuffer = pMovement.getMovementBuffer();
		final int lMovementBufferlength = lMovementBuffer.limit();

		mMovementBuffer = ByteBuffer.allocateDirect(2*lMovementBufferlength)
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
	public int getDeltaTimeInMicroseconds()
	{
		return mDeltaTimeInMicroseconds;
	}

	@Override
	public void setDeltaTimeInMicroseconds(int pDeltaTimeInMicroseconds)
	{
		throw new UnsupportedOperationException(this.getClass()
																								.getSimpleName() + " are final and cannot be modified");
	}

}
