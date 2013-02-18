package score;

import java.nio.ShortBuffer;

import score.interfaces.MovementInterface;

public class CompiledMovement implements MovementInterface
{
	private final ShortBuffer mMovementBuffer;
	private final int mDeltaTimeInMicroseconds;

	public CompiledMovement(MovementInterface pMovement)
	{
		mDeltaTimeInMicroseconds = pMovement.getDeltaTimeInMicroseconds();
		mMovementBuffer = pMovement.getMovementBuffer();
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
