package rtlib.stack;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclerInterface;
import coremem.rgc.FreeableBase;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;

public abstract class StackBase<T extends NativeType<T>, A extends ArrayDataAccess<A>>	extends
																						FreeableBase implements
																									StackInterface<T, A>
{

	protected RecyclerInterface<StackInterface<T, A>, StackRequest<T>> mStackBasicRecycler;
	protected volatile boolean mIsReleased;

	protected T mType;
	protected volatile long mStackIndex;
	protected volatile long mTimeStampInNanoseconds;
	protected double[] mVoxelSizeInRealUnits;
	protected volatile long mNumberOfImagesPerPlane = 1;
	protected volatile int mChannel = 0;

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
			mVoxelSizeInRealUnits = Arrays.copyOf(	mVoxelSizeInRealUnits,
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
	public void setChannel(int pChannel)
	{
		mChannel = pChannel;
	}

	@Override
	public int getChannel()
	{
		return mChannel;
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

	@Override
	public void copyMetaDataFrom(final StackInterface<T, A> pStack)
	{
		mVoxelSizeInRealUnits = getVoxelSizeInRealUnits();
		setIndex(pStack.getIndex());
		setTimeStampInNanoseconds(pStack.getTimeStampInNanoseconds());
		setType(pStack.getType());
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
	public void release()
	{
		if (mStackBasicRecycler != null)
		{
			// System.out.println(this);
			// System.out.println("getNumberOfAvailableObjects=" +
			// mStackBasicRecycler.getNumberOfAvailableObjects());
			// System.out.println("getNumberOfLiveObjects=" +
			// mStackBasicRecycler.getNumberOfLiveObjects());
			mStackBasicRecycler.release(this);
		}
	}

	@Override
	public void setRecycler(final RecyclerInterface<StackInterface<T, A>, StackRequest<T>> pRecycler)
	{
		mStackBasicRecycler = pRecycler;
	}

	public static <T extends NativeType<T>, A extends ArrayDataAccess<A>> StackInterface<T, A> requestOrWaitWithRecycler(	final BasicRecycler<StackInterface<T, A>, StackRequest<T>> pRecycler,
																															final long pWaitTime,
																															final TimeUnit pTimeUnit,
																															final T pType,
																															final long pWidth,
																															final long pHeight,
																															final long pDepth)
	{
		final StackRequest<T> lStackRequest = new StackRequest<T>(	pType,
																	pWidth,
																	pHeight,
																	pDepth);

		return pRecycler.getOrWait(	pWaitTime,
									pTimeUnit,
									lStackRequest);
	}

	@Override
	public String toString()
	{
		return String.format(	"StackBase [mStackIndex=%s, mTimeStampInNanoseconds=%s, mType=%s, mVoxelSizeInRealUnits=%s, mNumberOfImagesPerPlane=%s, mIsReleased=%s, mStackBasicRecycler=%s]",
								mStackIndex,
								mTimeStampInNanoseconds,
								mType,
								Arrays.toString(mVoxelSizeInRealUnits),
								mNumberOfImagesPerPlane,
								mIsReleased,
								mStackBasicRecycler);
	}

}
