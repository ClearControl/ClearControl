package clearcontrol.stack;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclerInterface;
import coremem.rgc.FreeableBase;

public abstract class StackBase extends FreeableBase implements
																										StackInterface
{

	protected RecyclerInterface<StackInterface, StackRequest> mStackRecycler;
	protected volatile boolean mIsReleased;

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
	public void copyMetaDataFrom(final StackInterface pStack)
	{
		mVoxelSizeInRealUnits = getVoxelSizeInRealUnits();
		setIndex(pStack.getIndex());
		setTimeStampInNanoseconds(pStack.getTimeStampInNanoseconds());

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
		if (mStackRecycler != null)
		{
			// System.out.println(this);
			// System.out.println("getNumberOfAvailableObjects=" +
			// mStackBasicRecycler.getNumberOfAvailableObjects());
			// System.out.println("getNumberOfLiveObjects=" +
			// mStackBasicRecycler.getNumberOfLiveObjects());
			mStackRecycler.release(this);
		}
	}

	@Override
	public void setRecycler(final RecyclerInterface<StackInterface, StackRequest> pRecycler)
	{
		mStackRecycler = pRecycler;
	}

	public static StackInterface requestOrWaitWithRecycler(	final BasicRecycler<StackInterface, StackRequest> pRecycler,
																													final long pWaitTime,
																													final TimeUnit pTimeUnit,
																													final long pWidth,
																													final long pHeight,
																													final long pDepth)
	{
		final StackRequest lStackRequest = new StackRequest(pWidth,
																												pHeight,
																												pDepth);

		return pRecycler.getOrWait(pWaitTime, pTimeUnit, lStackRequest);
	}

	@Override
	public String toString()
	{
		return String.format(	"StackBase [mStackIndex=%s, mTimeStampInNanoseconds=%s, mVoxelSizeInRealUnits=%s, mNumberOfImagesPerPlane=%s, mIsReleased=%s, mStackBasicRecycler=%s]",
													mStackIndex,
													mTimeStampInNanoseconds,
													Arrays.toString(mVoxelSizeInRealUnits),
													mNumberOfImagesPerPlane,
													mIsReleased,
													mStackRecycler);
	}

}
