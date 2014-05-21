package rtlib.stack;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.bridj.Pointer;

import rtlib.core.memory.SizedInBytes;
import rtlib.core.recycling.RecyclableInterface;
import rtlib.core.recycling.Recycler;
import rtlib.core.rgc.Freeable;
import rtlib.kam.memory.NDStructured;
import rtlib.kam.memory.impl.direct.NDArrayDirect;
import rtlib.kam.memory.ram.RAM;

public class Stack implements
									RecyclableInterface<Stack, Long>,
									NDStructured,
									SizedInBytes,
									Freeable
{

	private Recycler<Stack, Long> mStackRecycler;
	private volatile boolean mIsReleased;

	private NDArrayDirect mNDArray;

	private volatile long mBytesPerVoxel;

	private volatile long mStackIndex;
	private volatile long mTimeStampInNanoseconds;
	protected double[] mVolumeSize;
	private volatile long mNumberOfImagesPerPlane = 1;

	@SuppressWarnings("unused")
	private Stack()
	{
	}

	public Stack(	final long pImageIndex,
								final long pTimeStampInNanoseconds,
								final long pWidth,
								final long pHeight,
								final long pDepth,
								final long pBytesPerVoxel)
	{
		super();

		setStackIndex(pImageIndex);
		setTimeStampInNanoseconds(pTimeStampInNanoseconds);
		setBytesPerVoxel(pBytesPerVoxel);

		mNDArray = NDArrayDirect.allocateSXYZ(getBytesPerVoxel(),
																					pWidth,
																					pHeight,
																					pDepth);

	}

	public Stack(	final NDArrayDirect pNDArrayDirect,
								final long pImageIndex,
								final long pTimeStampInNanoseconds)
	{
		super();
		setStackIndex(pImageIndex);
		setTimeStampInNanoseconds(pTimeStampInNanoseconds);
		setBytesPerVoxel(2);
		mNDArray = pNDArrayDirect;
	}

	@Override
	public boolean isCompatible(final Long... pParameters)
	{
		setBytesPerVoxel(pParameters[0]);
		final long lWidth = pParameters[1];
		final long lHeight = pParameters[2];
		final long lDepth = pParameters[3];

		final long lLengthInBytes = lWidth * lHeight
																* lDepth
																* getBytesPerVoxel();
		return (mNDArray != null && !mNDArray.isFree() && mNDArray.getSizeInBytes() == lLengthInBytes);
	}

	@Override
	public void initialize(final Long... pParameters)
	{
		setBytesPerVoxel(pParameters[0]);
		final long lWidth = pParameters[1];
		final long lHeight = pParameters[2];
		final long lDepth = pParameters[3];

		if (!isCompatible(pParameters))
		{
			if (mNDArray != null)
				mNDArray.free();
			mNDArray = NDArrayDirect.allocateSXYZ(getBytesPerVoxel(),
																						lWidth,
																						lHeight,
																						lDepth);
		}
	}

	public NDArrayDirect getNDArray()
	{
		complainIfFreed();
		return mNDArray;
	}

	public Pointer<Byte> getPointer()
	{
		RAM lRam = mNDArray.getRAM();
		@SuppressWarnings("unchecked")
		Pointer<Byte> lPointerToAddress = (Pointer<Byte>) Pointer.pointerToAddress(	lRam.getAddress(),
																																								lRam.getSizeInBytes(),
																																								null)
																															.as(Byte.class);
		return lPointerToAddress;
	}

	public long getBytesPerVoxel()
	{
		return mBytesPerVoxel;
	}

	private void setBytesPerVoxel(long pBytesPerVoxel)
	{
		mBytesPerVoxel = pBytesPerVoxel;
	}

	@Override
	public long getSizeAlongDimension(int pDimensionIndex)
	{
		return mNDArray.getSizeAlongDimension(pDimensionIndex);
	}

	@Override
	public long getDimension()
	{
		return mNDArray.getDimension();
	}

	@Override
	public long getLengthInElements()
	{
		return mNDArray.getLengthInElements();
	}

	@Override
	public long getSizeInBytes()
	{
		return mNDArray.getSizeInBytes();
	}

	public double getVolumePhysicalDimension(final int pIndex)
	{
		if (mVolumeSize == null)
			return 1;
		return mVolumeSize[pIndex];
	}

	public void setVolumePhysicalDimension(	final int pIndex,
																					final double pPhysicalDimension)
	{
		if (mVolumeSize == null)
			mVolumeSize = new double[pIndex + 1];
		if (mVolumeSize.length <= pIndex)
			mVolumeSize = Arrays.copyOf(mVolumeSize, pIndex + 1);
		for (int i = 0; i < mVolumeSize.length; i++)
			if (mVolumeSize[i] == 0)
				mVolumeSize[i] = 1;

		mVolumeSize[pIndex] = pPhysicalDimension;
	}

	public long getIndex()
	{
		return mStackIndex;
	}

	public void setStackIndex(long pStackIndex)
	{
		mStackIndex = pStackIndex;
	}

	public long getTimeStampInNanoseconds()
	{
		return mTimeStampInNanoseconds;
	}

	public void setTimeStampInNanoseconds(long pTimeStampInNanoseconds)
	{
		mTimeStampInNanoseconds = pTimeStampInNanoseconds;
	}

	public long getNumberOfImagesPerPlane()
	{
		return mNumberOfImagesPerPlane;
	}

	public void setNumberOfImagesPerPlane(long pNumberOfImagesPerPlane)
	{
		mNumberOfImagesPerPlane = pNumberOfImagesPerPlane;
	}

	public void copyMetaDataFrom(Stack pStack)
	{
		mVolumeSize = Arrays.copyOf(pStack.mVolumeSize,
																mVolumeSize.length);
		setStackIndex(pStack.getIndex());
		setTimeStampInNanoseconds(pStack.getTimeStampInNanoseconds());
		setBytesPerVoxel(pStack.getBytesPerVoxel());
	}

	public void releaseStack()
	{
		if (mStackRecycler != null)
		{
			if (mIsReleased)
			{
				throw new RuntimeException("Object " + this.hashCode()
																		+ " Already released!");
			}
			mIsReleased = true;

			mStackRecycler.release(this);
		}
	}

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
	public void setRecycler(final Recycler<Stack, Long> pRecycler)
	{
		mStackRecycler = pRecycler;
	}

	public static Stack requestOrWaitWithRecycler(Recycler<Stack, Long> pRecycler,
																								final long pWaitTime,
																								final TimeUnit pTimeUnit,
																								long pBytesPerVoxel,
																								long pWidth,
																								long pHeight,
																								long pDepth)
	{
		return pRecycler.requestOrWaitRecyclableObject(	pWaitTime,
																										pTimeUnit,
																										pBytesPerVoxel,
																										pWidth,
																										pHeight,
																										pDepth);
	}

	@Override
	public void free()
	{
		mNDArray.free();
	}

	@Override
	public boolean isFree()
	{
		return mNDArray.isFree();
	}

	@Override
	public long getVolume()
	{
		return mNDArray.getVolume();
	}

	@Override
	public boolean isVectorized()
	{
		return mNDArray.isVectorized();
	}

	@Override
	public String toString()
	{
		return String.format(	this.getClass().getSimpleName() + " [ BytesPerVoxel=%d, width=%d, height=%d, depth=%d, index=%d, timestampns=%d ]",
													getBytesPerVoxel(),
													mNDArray.getWidth(),
													mNDArray.getHeight(),
													mNDArray.getDepth(),
													getIndex(),
													getTimeStampInNanoseconds());
	}

}
