package rtlib.kam.memory.impl.gpu;

import org.bridj.Pointer;

import rtlib.core.memory.NativeMemoryAccess;
import rtlib.core.memory.SizeOf;
import rtlib.core.memory.SizedInBytes;
import rtlib.core.rgc.Freeable;
import rtlib.kam.HasPeer;
import rtlib.kam.context.Context;
import rtlib.kam.context.HasContext;
import rtlib.kam.context.impl.gpu.ContextGPU;
import rtlib.kam.memory.MapAndReadWrite;
import rtlib.kam.memory.MappableMemory;
import rtlib.kam.memory.MemoryType;
import rtlib.kam.memory.MemoryTyped;
import rtlib.kam.memory.PointerAccessible;
import rtlib.kam.memory.ReadWriteToMappableMemory;
import rtlib.kam.memory.ReadWriteToPointerAccessible;
import rtlib.kam.queues.Queue;
import rtlib.kam.queues.QueueableOperations;

import com.nativelibs4java.opencl.CLBuffer;
import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLMem;
import com.nativelibs4java.opencl.CLMem.Usage;
import com.nativelibs4java.opencl.CLQueue;

public class BufferGPU<T> implements
													MemoryTyped,
													HasPeer<CLBuffer<T>>,
													MappableMemory,
													SizedInBytes,
													Freeable,
													QueueableOperations<CLQueue>,
													ReadWriteToPointerAccessible,
													ReadWriteToMappableMemory,
													MapAndReadWrite,
													HasContext<CLContext>

{

	protected ContextGPU mOpenCLContext;
	protected CLBuffer<T> mCLBuffer;
	protected Class<T> mElementType;
	protected boolean mIsFree = false;
	protected int mBytesPerElement;
	protected long mLengthInElements;
	protected boolean mRead;
	protected boolean mWrite;
	private Pointer<T> mMappingPointer;
	private Queue<CLQueue> mQueue;

	public BufferGPU(	ContextGPU pOpenCLContext,
										final Class<T> pElementType,
										final long pLength,
										final boolean pRead,
										final boolean pWrite)
	{
		super();
		mOpenCLContext = pOpenCLContext;
		mElementType = pElementType;
		mQueue = mOpenCLContext.getDefaultQueue();
		mBytesPerElement = SizeOf.sizeOf(pElementType);
		mLengthInElements = pLength;
		mRead = pRead;
		mWrite = pWrite;

		Usage lMemoryUsage = getMemoryUsage();

		CLContext lClContext = mOpenCLContext.getPeer();

		mCLBuffer = lClContext.createBuffer(lMemoryUsage,
																				pElementType,
																				pLength);

	}

	protected CLMem.Usage getMemoryUsage()
	{
		CLMem.Usage lUsage = null;
		if (mRead && mWrite)
			lUsage = CLMem.Usage.InputOutput;
		if (mRead)
			lUsage = CLMem.Usage.Input;
		else if (mWrite)
			lUsage = CLMem.Usage.Output;
		return lUsage;
	}

	@Override
	public MemoryType getMemoryType()
	{
		complainIfFreed();
		return MemoryType.GPUMEM;
	}

	@Override
	public void free()
	{
		if (mIsFree == false)
		{
			mCLBuffer.release();
			mIsFree = true;
		}
	}

	@Override
	public boolean isFree()
	{
		return mIsFree;
	}

	@Override
	public long getSizeInBytes()
	{
		complainIfFreed();
		return mLengthInElements * mBytesPerElement;
	}

	protected CLMem.MapFlags getMappingFlags()
	{
		CLMem.MapFlags lUsage = null;
		if (mRead && mWrite)
			lUsage = CLMem.MapFlags.ReadWrite;
		else if (mRead)
			lUsage = CLMem.MapFlags.Read;
		else if (mWrite)
			lUsage = CLMem.MapFlags.Write;
		return lUsage;
	}

	@Override
	public long map()
	{
		if (!isCurrentlyMapped())
			mMappingPointer = mCLBuffer.map(getCurrentQueue().getPeer(),
																			getMappingFlags());

		return mMappingPointer.getPeer();
	}

	@Override
	public void force()
	{

	}

	@Override
	public void unmap()
	{
		if (isCurrentlyMapped())
			mCLBuffer.unmap(getCurrentQueue().getPeer(), mMappingPointer);
		mMappingPointer = null;
	}

	@Override
	public boolean isCurrentlyMapped()
	{
		return mMappingPointer != null;
	}

	@Override
	public void mapAndReadFrom(PointerAccessible pPointerAccessible)
	{
		long lAddressDest = map();
		NativeMemoryAccess.copyMemory(pPointerAccessible.getAddress(),
																	lAddressDest,
																	getSizeInBytes());
		unmap();
	}

	@Override
	public void mapAndWriteTo(PointerAccessible pPointerAccessible)
	{
		long lAddressOrg = map();
		NativeMemoryAccess.copyMemory(lAddressOrg,
																	pPointerAccessible.getAddress(),
																	getSizeInBytes());
		unmap();
	}

	@Override
	public void readFromMapped(MappableMemory pMappableMemory)
	{
		final long lAddress = pMappableMemory.map();

		Pointer<T> lPointerToAddress = Pointer.pointerToAddress(lAddress,
																														mElementType,
																														null);
		readFrom(lPointerToAddress, 0, mLengthInElements);
		getCurrentQueue().waitForCompletion();
		pMappableMemory.unmap();
	}

	@Override
	public void writeToMapped(MappableMemory pMappableMemory)
	{
		final long lAddress = pMappableMemory.map();

		Pointer<T> lPointerToAddress = Pointer.pointerToAddress(lAddress,
																														mElementType,
																														null);
		writeTo(lPointerToAddress, 0, mLengthInElements);
		getCurrentQueue().waitForCompletion();
		pMappableMemory.unmap();
	}

	@Override
	public void readFrom(PointerAccessible pPointerAccessible)
	{
		Pointer<T> lPointerToAddress = Pointer.pointerToAddress(pPointerAccessible.getAddress(),
																														mElementType,
																														null);
		readFrom(lPointerToAddress, 0, mLengthInElements);
	}

	@Override
	public void writeTo(PointerAccessible pPointerAccessible)
	{
		Pointer<T> lPointerToAddress = Pointer.pointerToAddress(pPointerAccessible.getAddress(),
																														mElementType,
																														null);
		writeTo(lPointerToAddress, 0, mLengthInElements);
	}

	public void readFrom(	Pointer<T> pPointer,
												long pOffsetInElements,
												long pLengthInElements)
	{
		mCLBuffer.write(getCurrentQueue().getPeer(),
										pOffsetInElements,
										pLengthInElements,
										pPointer,
										false);
	}

	public void writeTo(Pointer<T> pPointer,
											long pOffsetInElements,
											long pLengthInElements)
	{
		mCLBuffer.read(	(getCurrentQueue().getPeer()),
										pOffsetInElements,
										pLengthInElements,
										pPointer,
										false);
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
	public Context<CLContext> getContext()
	{
		return mOpenCLContext;
	}

	@Override
	public CLBuffer<T> getPeer()
	{
		return mCLBuffer;
	}

}
