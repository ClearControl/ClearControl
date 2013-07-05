package stack;

import java.nio.ByteBuffer;

import ndarray.implementations.heapbuffer.directbuffer.NDArrayDirectBufferByte;
import recycling.RecyclableInterface;
import recycling.Recycler;

public class Stack implements RecyclableInterface
{

	private Recycler<Stack> mFrameRecycler;
	private volatile boolean isReleased;

	public NDArrayDirectBufferByte ndarray;
	public int bpp;
	public long index;
	public long timestampns;

	public Stack()
	{
	}

	public Stack(	final long pImageIndex,
								final long pTimeStampInNanoseconds,
								final int pWidth,
								final int pHeight,
								final int pDepth,
								final int pBytesPerPixel)
	{
		index = pImageIndex;
		timestampns = pTimeStampInNanoseconds;
		bpp = pBytesPerPixel;

		ndarray = NDArrayDirectBufferByte.allocateSXYZ(	bpp,
																										pWidth,
																										pHeight,
																										pDepth);
	}

	public Stack(	final NDArrayDirectBufferByte pNDArrayDirectBuffer,
								final long pImageIndex,
								final long pTimeStampInNanoseconds)
	{
		index = pImageIndex;
		timestampns = pTimeStampInNanoseconds;
		bpp = 2;
		ndarray = pNDArrayDirectBuffer;
	}

	public ByteBuffer getByteBuffer()
	{
		return ndarray.getUnderlyingByteBuffer();
	}

	public int getWidth()
	{
		return ndarray.getWidth();
	}

	public int getHeight()
	{
		return ndarray.getHeight();
	}

	public int getDepth()
	{
		return ndarray.getDepth();
	}

	public int getDimension()
	{
		return ndarray.getDimension();
	}

	public int getDimensionWithoutSizeDimension()
	{
		return ndarray.getDimension();
	}

	public void copyFrom(	final ByteBuffer pByteBufferToBeCopied,
												final long pImageIndex,
												final int pWidth,
												final int pHeight,
												final int pDepth,
												final int pBytesPerPixel)
	{
		index = pImageIndex;
		bpp = pBytesPerPixel;

		final int lBufferLengthInBytes = pByteBufferToBeCopied.limit();
		if (ndarray == null || ndarray.getArrayLengthInBytes() != lBufferLengthInBytes)
		{
			ndarray = NDArrayDirectBufferByte.allocateSXYZ(	pBytesPerPixel,
																											pWidth,
																											pHeight,
																											pDepth);
			final ByteBuffer lUnderlyingByteBuffer = ndarray.getUnderlyingByteBuffer();
			lUnderlyingByteBuffer.clear();
			pByteBufferToBeCopied.rewind();
			lUnderlyingByteBuffer.put(pByteBufferToBeCopied);
		}
	}

	@Override
	public void initialize(final int... pParameters)
	{
		bpp = pParameters[0];
		final int lWidth = pParameters[1];
		final int lHeight = pParameters[2];
		final int lDepth = pParameters[3];

		final int length = lWidth * lHeight * lDepth * bpp;
		if (ndarray == null || ndarray.getArrayLengthInBytes() != length)
		{
			ndarray = NDArrayDirectBufferByte.allocateSXYZ(	bpp,
																											lWidth,
																											lHeight,
																											lDepth);
		}
	}

	public void releaseFrame()
	{
		if (mFrameRecycler != null)
		{
			if (isReleased)
				throw new RuntimeException("Object " + this.hashCode()
																		+ " Already released!");
			isReleased = true;

			mFrameRecycler.release(this);
		}
	}

	@Override
	public String toString()
	{
		return String.format(	this.getClass().getSimpleName() + " [ BytesPerVoxel=%d, width=%d, height=%d, depth=%d, index=%d, timestampns=%d ]",
													bpp,
													ndarray.getWidth(),
													ndarray.getHeight(),
													ndarray.getDepth(),
													index,
													timestampns);
	}

	public boolean isReleased()
	{
		return isReleased;
	}

	@Override
	public void setReleased(final boolean isReleased)
	{
		this.isReleased = isReleased;
	}

	@Override
	public void setRecycler(final Recycler pRecycler)
	{
		mFrameRecycler = pRecycler;
	}

}
