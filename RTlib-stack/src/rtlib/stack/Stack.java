package rtlib.stack;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.bridj.Pointer;

import rtlib.core.memory.SizeOf;
import rtlib.core.memory.SizedInBytes;
import rtlib.core.memory.TypeId;
import rtlib.core.recycling.RecyclableInterface;
import rtlib.core.recycling.Recycler;
import rtlib.core.rgc.Freeable;
import rtlib.kam.memory.NDStructured;
import rtlib.kam.memory.Typed;
import rtlib.kam.memory.impl.direct.NDArrayDirect;
import rtlib.kam.memory.ram.RAM;

public class Stack<T> implements
											RecyclableInterface<Stack<T>, Long>,
											NDStructured,
											Typed<T>,
											SizedInBytes,
											Freeable
{

	private Recycler<Stack<T>, Long> mStackRecycler;
	private volatile boolean mIsReleased;

	private NDArrayDirect<T> mNDArray;

	private Class<T> mType;

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
								final Class<T> pType)
	{
		super();

		setStackIndex(pImageIndex);
		setTimeStampInNanoseconds(pTimeStampInNanoseconds);
		setType(pType);

		mNDArray = NDArrayDirect.allocateTXYZ(pType,
																					pWidth,
																					pHeight,
																					pDepth);

	}

	@SuppressWarnings("unchecked")
	public Stack(	final NDArrayDirect<T> pNDArrayDirect,
								final long pImageIndex,
								final long pTimeStampInNanoseconds)
	{
		super();
		setStackIndex(pImageIndex);
		setTimeStampInNanoseconds(pTimeStampInNanoseconds);
		setType((Class<T>) Short.class);
		mNDArray = pNDArrayDirect;
	}

	@Override
	public boolean isCompatible(final Long... pParameters)
	{
		final long lTypeId = pParameters[0];
		final long lWidth = pParameters[1];
		final long lHeight = pParameters[2];
		final long lDepth = pParameters[3];
		
		final Class<?> lType = TypeId.idToClass((int) lTypeId);
		final int lBytesPerVoxel = SizeOf.sizeOf(lType);
			

		final long lLengthInBytes = lWidth * lHeight
																* lDepth
																* lBytesPerVoxel;
		return (mNDArray != null && !mNDArray.isFree() && mNDArray.getSizeInBytes() == lLengthInBytes);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(final Long... pParameters)
	{
		final long lTypeId = pParameters[0];
		final long lWidth = pParameters[1];
		final long lHeight = pParameters[2];
		final long lDepth = pParameters[3];

		final Class<?> lType = TypeId.idToClass((int) lTypeId);
		final int lBytesPerVoxel = SizeOf.sizeOf(lType);

		if (!isCompatible(pParameters))
		{
			if (mNDArray != null)
				mNDArray.free();
			mNDArray = (NDArrayDirect<T>) NDArrayDirect.allocateTXYZ(	lType,
																						lWidth,
																						lHeight,
																						lDepth);
		}
	}

	public NDArrayDirect<T> getNDArray()
	{
		complainIfFreed();
		return mNDArray;
	}

	public Pointer<Byte> getPointer()
	{
		RAM lRam = mNDArray.getRAM();
		@SuppressWarnings("unchecked")
		Pointer<Byte> lPointerToAddress = Pointer.pointerToAddress(	lRam.getAddress(),
																																lRam.getSizeInBytes(),
																																null)
																							.as(Byte.class);
		return lPointerToAddress;
	}

	public long getBytesPerVoxel()
	{
		return SizeOf.sizeOf(mType);
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

	public void copyMetaDataFrom(Stack<T> pStack)
	{
		if (mVolumeSize != null)
			mVolumeSize = Arrays.copyOf(pStack.mVolumeSize,
																	mVolumeSize.length);
		setStackIndex(pStack.getIndex());
		setTimeStampInNanoseconds(pStack.getTimeStampInNanoseconds());
		setType(pStack.getType());
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
	public void setRecycler(final Recycler<Stack<T>, Long> pRecycler)
	{
		mStackRecycler = pRecycler;
	}

	public static <T> Stack<T> requestOrWaitWithRecycler(	Recycler<Stack<T>, Long> pRecycler,
																								final long pWaitTime,
																								final TimeUnit pTimeUnit,
																								long pBytesPerVoxel,
																								long pWidth,
																								long pHeight,
																								long pDepth)
	{
		return pRecycler.waitOrRequestRecyclableObject(	pWaitTime,
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

	@Override
	public Class<T> getType()
	{
		return mType;
	}

	public void setType(Class<T> pType)
	{
		mType = pType;
	}


}
