package stack;

import java.nio.ByteBuffer;

import ndarray.implementations.heapbuffer.directbuffer.NDArrayDirectBufferByte;
import recycling.RecyclableInterface;
import recycling.Recycler;

public class Stack implements RecyclableInterface<Stack>
{

	private Recycler<Stack> mStackRecycler;
	private volatile boolean mIsReleased;

	public NDArrayDirectBufferByte mNDimensionalArray;
	public int mBytesPerPixel;
	public long mStackIndex;
	public long mTimeStampInNanoseconds;
	public double[] mVolumeSize;
	public int mNumberOfImagesPerPlane = 1;

	public Stack()
	{
	}

	public Stack(final int pStackDimension)
	{
		mVolumeSize = new double[pStackDimension];
		for (int i = 0; i < pStackDimension; i++)
		{
			mVolumeSize[i] = 1;
		}
	}

	public Stack(	final long pImageIndex,
								final long pTimeStampInNanoseconds,
								final int pWidth,
								final int pHeight,
								final int pDepth,
								final int pBytesPerPixel)
	{
		this(3);

		mStackIndex = pImageIndex;
		mTimeStampInNanoseconds = pTimeStampInNanoseconds;
		mBytesPerPixel = pBytesPerPixel;

		mNDimensionalArray = NDArrayDirectBufferByte.allocateSXYZ(mBytesPerPixel,
																															pWidth,
																															pHeight,
																															pDepth);

	}

	public Stack(	final NDArrayDirectBufferByte pNDArrayDirectBuffer,
								final long pImageIndex,
								final long pTimeStampInNanoseconds)
	{
		this(pNDArrayDirectBuffer.getDimension());
		mStackIndex = pImageIndex;
		mTimeStampInNanoseconds = pTimeStampInNanoseconds;
		mBytesPerPixel = 2;
		mNDimensionalArray = pNDArrayDirectBuffer;
	}

	public ByteBuffer getByteBuffer()
	{
		return mNDimensionalArray.getUnderlyingByteBuffer();
	}

	public int getWidth()
	{
		return mNDimensionalArray.getWidth();
	}

	public int getHeight()
	{
		return mNDimensionalArray.getHeight();
	}

	public int getDepth()
	{
		return mNDimensionalArray.getDepth();
	}

	public int getDimension()
	{
		return mNDimensionalArray.getDimension();
	}

	public int getDimensionWithoutSizeDimension()
	{
		return mNDimensionalArray.getDimension();
	}

	public void copyFrom(	final ByteBuffer pByteBufferToBeCopied,
												final long pImageIndex,
												final int pWidth,
												final int pHeight,
												final int pDepth,
												final int pBytesPerPixel)
	{
		mStackIndex = pImageIndex;
		mBytesPerPixel = pBytesPerPixel;

		final int lBufferLengthInBytes = pByteBufferToBeCopied.limit();
		if (mNDimensionalArray == null || mNDimensionalArray.getArrayLengthInBytes() != lBufferLengthInBytes)
		{
			mNDimensionalArray = NDArrayDirectBufferByte.allocateSXYZ(pBytesPerPixel,
																																pWidth,
																																pHeight,
																																pDepth);

			mVolumeSize = new double[3];
			final ByteBuffer lUnderlyingByteBuffer = mNDimensionalArray.getUnderlyingByteBuffer();
			lUnderlyingByteBuffer.clear();
			pByteBufferToBeCopied.rewind();
			lUnderlyingByteBuffer.put(pByteBufferToBeCopied);
		}
	}

	@Override
	public void initialize(final int... pParameters)
	{
		mBytesPerPixel = pParameters[0];
		final int lWidth = pParameters[1];
		final int lHeight = pParameters[2];
		final int lDepth = pParameters[3];

		final int length = lWidth * lHeight * lDepth * mBytesPerPixel;
		if (mNDimensionalArray == null || mNDimensionalArray.getArrayLengthInBytes() != length)
		{
			mNDimensionalArray = NDArrayDirectBufferByte.allocateSXYZ(mBytesPerPixel,
																																lWidth,
																																lHeight,
																																lDepth);
			mVolumeSize = new double[3];
		}
	}

	public void releaseFrame()
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

	@Override
	public String toString()
	{
		return String.format(	this.getClass().getSimpleName() + " [ BytesPerVoxel=%d, width=%d, height=%d, depth=%d, index=%d, timestampns=%d ]",
													mBytesPerPixel,
													mNDimensionalArray.getWidth(),
													mNDimensionalArray.getHeight(),
													mNDimensionalArray.getDepth(),
													mStackIndex,
													mTimeStampInNanoseconds);
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
	public void setRecycler(final Recycler<Stack> pRecycler)
	{
		mStackRecycler = pRecycler;
	}

}
