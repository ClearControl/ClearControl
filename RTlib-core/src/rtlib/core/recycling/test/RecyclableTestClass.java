package rtlib.core.recycling.test;

import java.util.concurrent.atomic.AtomicBoolean;

import rtlib.core.recycling.RecyclableInterface;
import rtlib.core.recycling.Recycler;

public class RecyclableTestClass implements
																RecyclableInterface<RecyclableTestClass, LongRequest>
{
	// Proper class fields:
	AtomicBoolean mFree = new AtomicBoolean(false);
	double[] mArray;

	// Recycling related fields:
	private Recycler<RecyclableTestClass, LongRequest> mRecycler;
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
	public boolean isCompatible(LongRequest pRequest)
	{
		return mArray.length == pRequest.value;
	}

	@Override
	public void initialize(LongRequest pRequest)
	{
		mArray = new double[Math.toIntExact(pRequest.value)];
	}

	@Override
	public void setReleased(boolean pIsReleased)
	{
		mReleased.set(pIsReleased);
	}

	@Override
	public void setRecycler(Recycler<RecyclableTestClass, LongRequest> pRecycler)
	{
		mRecycler = pRecycler;
	}

}
