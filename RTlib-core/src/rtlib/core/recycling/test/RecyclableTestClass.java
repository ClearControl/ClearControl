package rtlib.core.recycling.test;

import java.util.concurrent.atomic.AtomicBoolean;

import rtlib.core.recycling.RecyclableInterface;
import rtlib.core.recycling.Recycler;

public class RecyclableTestClass implements
																RecyclableInterface<RecyclableTestClass, Long>
{
	// Proper class fields:
	AtomicBoolean mFree = new AtomicBoolean(false);
	double[] mArray;

	// Recycling related fields:
	private Recycler<RecyclableTestClass, Long> mRecycler;
	AtomicBoolean mReleased = new AtomicBoolean(false);

	@Override
	public long getSizeInBytes()
	{
		return 10;
	}

	@Override
	public void free()
	{
		mFree.set(true);
	}

	@Override
	public boolean isFree()
	{
		return mFree.get();
	}

	@Override
	public boolean isCompatible(Long... pParameters)
	{
		final long lLength = pParameters[0];
		return mArray.length == lLength;
	}

	@Override
	public void initialize(Long... pParameters)
	{
		final long lLength = pParameters[0];
		mArray = new double[Math.toIntExact(lLength)];
	}

	@Override
	public void setReleased(boolean pIsReleased)
	{
		mReleased.set(pIsReleased);
	}

	@Override
	public void setRecycler(Recycler<RecyclableTestClass, Long> pRecycler)
	{
		mRecycler = pRecycler;
	}

}
