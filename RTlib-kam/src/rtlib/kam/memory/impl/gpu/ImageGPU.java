package rtlib.kam.memory.impl.gpu;

import rtlib.core.memory.SizeOf;
import rtlib.core.memory.SizedInBytes;
import rtlib.core.rgc.Freeable;
import rtlib.kam.HasPeer;
import rtlib.kam.context.impl.gpu.ContextGPU;
import rtlib.kam.memory.MemoryType;
import rtlib.kam.memory.MemoryTyped;
import rtlib.kam.memory.NDStructured;
import rtlib.kam.memory.ReadWriteToMappableMemory;
import rtlib.kam.memory.ReadWriteToPointerAccessible;
import rtlib.kam.queues.Queue;
import rtlib.kam.queues.QueueableOperations;

import com.nativelibs4java.opencl.CLImage;
import com.nativelibs4java.opencl.CLImageFormat;
import com.nativelibs4java.opencl.CLImageFormat.ChannelDataType;
import com.nativelibs4java.opencl.CLImageFormat.ChannelOrder;
import com.nativelibs4java.opencl.CLMem;
import com.nativelibs4java.opencl.CLQueue;

public abstract class ImageGPU<T> implements
																	MemoryTyped,
																	HasPeer<CLImage>,
																	SizedInBytes,
																	Freeable,
																	NDStructured,
																	QueueableOperations<CLQueue>,
																	ReadWriteToPointerAccessible,
																	ReadWriteToMappableMemory

{

	protected ContextGPU mOpenCLContext;
	protected CLImage mCLImage;
	protected boolean mIsFree = false;
	protected Class<T> mElementType;
	protected int mBytesPerVoxel;
	protected boolean mRead, mWrite;
	protected long mWidth = 1, mHeight = 1, mDepth = 1;
	private Queue<CLQueue> mQueue;

	protected ImageGPU(	ContextGPU pOpenCLContext,
											final Class<T> pElementType,
											final boolean pRead,
											final boolean pWrite,
											final long pWidth,
											final long pHeight,
											final long pDepth)
	{
		super();
		mOpenCLContext = pOpenCLContext;
		mElementType = pElementType;
		mQueue = mOpenCLContext.getDefaultQueue();
		mBytesPerVoxel = SizeOf.sizeOf(pElementType);
		mWidth = pWidth;
		mHeight = pHeight;
		mDepth = pDepth;
		mRead = pRead;
		mWrite = pWrite;
	}

	protected CLImageFormat getImageFormat()
	{
		ChannelOrder lChannelOrder = ChannelOrder.R;
		ChannelDataType lChannelDataType = null;

		if (SizeOf.isFloat(mElementType))
		{
			if (mBytesPerVoxel == 2)
				lChannelDataType = ChannelDataType.HalfFloat;
			else if (mBytesPerVoxel == 4)
				lChannelDataType = ChannelDataType.Float;
		}
		else
		{
			if (mBytesPerVoxel == 1)
				lChannelDataType = ChannelDataType.UnsignedInt8;
			else if (mBytesPerVoxel == 2)
				lChannelDataType = ChannelDataType.UnsignedInt16;
			else if (mBytesPerVoxel == 4)
				lChannelDataType = ChannelDataType.UnsignedInt32;
		}

		CLImageFormat lCLImageFormat = new CLImageFormat(	lChannelOrder,
																											lChannelDataType);
		return lCLImageFormat;
	}

	protected CLMem.Usage getMemoryUsage()
	{
		CLMem.Usage lUsage = null;
		if (mRead && mWrite)
			lUsage = CLMem.Usage.InputOutput;
		else if (mRead)
			lUsage = CLMem.Usage.Output;
		else if (mWrite)
			lUsage = CLMem.Usage.Input;
		return lUsage;
	}

	@Override
	public MemoryType getMemoryType()
	{
		complainIfFreed();
		return MemoryType.GPUTEXMEM;
	}

	@Override
	public void free()
	{
		if (mIsFree == false)
		{
			mCLImage.release();
			mIsFree = true;
		}
	}

	@Override
	public boolean isFree()
	{
		return mIsFree;
	}

	@Override
	public abstract long getDimension();

	@Override
	public long getWidth()
	{
		return mWidth;
	}

	@Override
	public long getHeight()
	{
		return mHeight;
	}

	@Override
	public long getDepth()
	{
		return mDepth;
	}

	@Override
	public long getSizeAlongDimension(int pDimensionIndex)
	{
		if (pDimensionIndex == 0)
			return 1;
		if (pDimensionIndex == 1)
			return mWidth;
		if (pDimensionIndex == 2)
			return mHeight;
		if (pDimensionIndex == 3)
			return mDepth;
		return -1;
	}

	@Override
	public long getVolume()
	{
		return mWidth * mHeight * mDepth;
	}

	@Override
	public long getLengthInElements()
	{
		complainIfFreed();
		return getVolume();
	}

	@Override
	public long getSizeInBytes()
	{
		complainIfFreed();
		return getLengthInElements() * mBytesPerVoxel;
	}

	@Override
	public boolean isVectorized()
	{
		complainIfFreed();
		return false;
	}

	protected CLMem.MapFlags getMappingFlags()
	{
		CLMem.MapFlags lUsage = null;
		if (mRead && mWrite)
			lUsage = CLMem.MapFlags.ReadWrite;
		if (mRead)
			lUsage = CLMem.MapFlags.Read;
		else if (mWrite)
			lUsage = CLMem.MapFlags.Write;
		return lUsage;
	}

	@Override
	public Queue<CLQueue> getCurrentQueue()
	{
		return mQueue;
	}

	@Override
	public void setCurrentQueue(Queue<CLQueue> pQueue)
	{
		mQueue = pQueue;
	}

	@Override
	public CLImage getPeer()
	{
		return mCLImage;
	}

	@Override
	public String toString()
	{
		return "ImageGPU [mOpenCLContext=" + mOpenCLContext
						+ ", mCLImage="
						+ mCLImage
						+ ", mIsFree="
						+ mIsFree
						+ ", mElementType="
						+ mElementType
						+ ", mBytesPerVoxel="
						+ mBytesPerVoxel
						+ ", mRead="
						+ mRead
						+ ", mWrite="
						+ mWrite
						+ ", mWidth="
						+ mWidth
						+ ", mHeight="
						+ mHeight
						+ ", mDepth="
						+ mDepth
						+ ", mQueue="
						+ mQueue
						+ ", getMemoryType()="
						+ getMemoryType()
						+ "]";
	}

}
