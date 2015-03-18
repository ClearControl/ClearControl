package rtlib.stack;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;
import coremem.recycling.Recycler;
import coremem.rgc.FreeableBase;

public abstract class StackBase<T extends NativeType<T>, A extends ArrayDataAccess<A>>	extends
																																												FreeableBase implements
																																																		StackInterface<T, A>
{

	protected Recycler<StackInterface<T, A>, StackRequest<T>> mStackRecycler;
	protected volatile boolean mIsReleased;

	protected T mType;
	protected volatile long mStackIndex;
	protected volatile long mTimeStampInNanoseconds;
	protected double[] mVoxelSizeInRealUnits;
	protected volatile long mNumberOfImagesPerPlane = 1;

	public StackBase()
	{
	}

	@Override
	public double getVoxelSizeInRealUnits(final int pIndex)
	{
		if (mVoxelSizeInRealUnits == null)
			return 1;
		return mVoxelSizeInRealUnits[pIndex];
	}

	@Override
	public void setVoxelSizeInRealUnits(final int pIndex,
																			final double pVoxelSizeInRealUnits)
	{
		if (mVoxelSizeInRealUnits == null)
			mVoxelSizeInRealUnits = new double[pIndex + 1];
		if (mVoxelSizeInRealUnits.length <= pIndex)
			mVoxelSizeInRealUnits = Arrays.copyOf(mVoxelSizeInRealUnits,
																						pIndex + 1);
		for (int i = 0; i < mVoxelSizeInRealUnits.length; i++)
			if (mVoxelSizeInRealUnits[i] == 0)
				mVoxelSizeInRealUnits[i] = 1;

		mVoxelSizeInRealUnits[pIndex] = pVoxelSizeInRealUnits;
	}

	@Override
	public double[] getVoxelSizeInRealUnits()
	{
		if (mVoxelSizeInRealUnits == null)
			return null;
		return Arrays.copyOf(	mVoxelSizeInRealUnits,
													mVoxelSizeInRealUnits.length);
	}

	@Override
	public long getIndex()
	{
		return mStackIndex;
	}

	@Override
	public void setIndex(final long pStackIndex)
	{
		mStackIndex = pStackIndex;
	}

	@Override
	public long getTimeStampInNanoseconds()
	{
		return mTimeStampInNanoseconds;
	}

	@Override
	public void setTimeStampInNanoseconds(final long pTimeStampInNanoseconds)
	{
		mTimeStampInNanoseconds = pTimeStampInNanoseconds;
	}

	@Override
	public long getNumberOfImagesPerPlane()
	{
		return mNumberOfImagesPerPlane;
	}

	@Override
	public void setNumberOfImagesPerPlane(final long pNumberOfImagesPerPlane)
	{
		mNumberOfImagesPerPlane = pNumberOfImagesPerPlane;
	}

	@Override
	public void copyMetaDataFrom(final StackInterface<T, A> pStack)
	{
		mVoxelSizeInRealUnits = getVoxelSizeInRealUnits();
		setIndex(pStack.getIndex());
		setTimeStampInNanoseconds(pStack.getTimeStampInNanoseconds());
		setType(pStack.getType());
	}

	@Override
	public void releaseStack()
	{
		if (mStackRecycler != null)
		{
			if (mIsReleased)
			{
				mIsReleased = true;
				throw new RuntimeException("Object " + this.hashCode()
																		+ " Already released!");
			}
			mIsReleased = true;

			mStackRecycler.release(this);
		}
	}

	@Override
	public boolean isReleased()
	{
		return mIsReleased;
	}

	@Override
	public void setReleased(final boolean isReleased)
	{
		mIsReleased = isReleased;
	}

	@Override
	public void setRecycler(final Recycler<StackInterface<T, A>, StackRequest<T>> pRecycler)
	{
		mStackRecycler = pRecycler;
	}

	public static <T extends NativeType<T>, A extends ArrayDataAccess<A>> StackInterface<T, A> requestOrWaitWithRecycler(	final Recycler<StackInterface<T, A>, StackRequest<T>> pRecycler,
																																																												final long pWaitTime,
																																																												final TimeUnit pTimeUnit,
																																																												final T pType,
																																																												final long pWidth,
																																																												final long pHeight,
																																																												final long pDepth)
	{
		final StackRequest<T> lStackRequest = new StackRequest<T>(pType,
																																		pWidth,
																																		pHeight,
																																		pDepth);

		return pRecycler.waitOrRequestRecyclableObject(	pWaitTime,
																										pTimeUnit,
																										lStackRequest);
	}

	@Override
	public T getType()
	{
		return mType;
	}

	public void setType(final T pType)
	{
		mType = pType;
	}

}
