package rtlib.kam.memory.impl.direct;

import java.io.IOException;
import java.nio.channels.FileChannel;

import rtlib.kam.memory.cursor.NDBoundedCursor;
import rtlib.kam.memory.ndarray.NDArrayTyped;
import coremem.MemoryRegionInterface;
import coremem.interfaces.ReadAtAligned;
import coremem.interfaces.ReadWriteBytesFileChannel;
import coremem.interfaces.SizedInBytes;
import coremem.interfaces.WriteAtAligned;
import coremem.offheap.OffHeapMemoryRegion;
import coremem.rgc.Freeable;
import coremem.rgc.FreedException;
import coremem.util.SizeOf;

public class NDArrayTypedDirect<T> extends NDArrayTyped<T> implements
																													SizedInBytes,
																													ReadWriteBytesFileChannel,
																													WriteAtAligned,
																													ReadAtAligned,
																													Freeable
{

	private NDArrayTypedDirect(	MemoryRegionInterface<T> pMemoryRegionInterface,
															Class<T> pType,
															NDBoundedCursor pCursor)
	{
		super(pMemoryRegionInterface, pType, pCursor);
	}

	public NDArrayTypedDirect(long pElementSizeInBytes,
														Class<T> pType,
														NDBoundedCursor pCursor)
	{
		super(OffHeapMemoryRegion.allocate(pCursor.getLengthInElements() * pElementSizeInBytes),
					pType,
					pCursor);
	}

	public static <T> NDArrayTypedDirect<T> allocateNDArray(MemoryRegionInterface<T> pMemoryRegionInterface,
																													Class<T> pType,
																													final NDBoundedCursor pCursor)
	{
		return new NDArrayTypedDirect<T>(	pMemoryRegionInterface,
																			pType,
																			pCursor);
	}

	public static <T> NDArrayTypedDirect<T> allocateTVND(	Class<T> pType,
																												long... pDimensions)
	{
		NDBoundedCursor lNDBoundedCursor = NDBoundedCursor.createNDVectorCursor(pDimensions);
		return new NDArrayTypedDirect<T>(	SizeOf.sizeOf(pType),
																			pType,
																			lNDBoundedCursor);
	}

	public static <T> NDArrayTypedDirect<T> allocateTXYZ(	Class<T> pType,
																												long pWidth,
																												long pHeight,
																												long pDepth)
	{
		NDBoundedCursor lNDBoundedCursor = NDBoundedCursor.createNDVectorCursor(1,
																																						pWidth,
																																						pHeight,
																																						pDepth);
		return new NDArrayTypedDirect<T>(	SizeOf.sizeOf(pType),
																			pType,
																			lNDBoundedCursor);
	}

	public static <T> NDArrayTypedDirect<T> allocateTXY(Class<T> pType,
																											long pWidth,
																											long pHeight)
	{
		return allocateTXYZ(pType, pWidth, pHeight, 1);
	}

	public static <T> NDArrayTypedDirect<T> allocateTX(	Class<T> pType,
																											long pWidth)
	{
		return allocateTXYZ(pType, pWidth, 1, 1);
	}

	public static <T> NDArrayTypedDirect<T> wrapPointerTXYZ(Object pParent,
																													long pNativeAddress,
																													long pLengthInBytes,
																													Class<T> pType,
																													int pWidth,
																													int pHeight,
																													int pDepth)
	{

		NDBoundedCursor lNDBoundedCursor = NDBoundedCursor.createNDVectorCursor(1,
																																						pWidth,
																																						pHeight,
																																						pDepth);
		return new NDArrayTypedDirect<T>(	OffHeapMemoryRegion.wrapPointer(pParent,
																																			pNativeAddress,
																																			pLengthInBytes),
																			pType,
																			lNDBoundedCursor);
	}

	@Override
	public long getSizeInBytes()
	{
		return getMemoryRegionInterface().getSizeInBytes();
	}

	@Override
	public long writeBytesToFileChannel(FileChannel pFileChannel,
																			long pFilePositionInBytes) throws IOException
	{
		return getMemoryRegionInterface().writeBytesToFileChannel(pFileChannel,
																															pFilePositionInBytes);
	}

	@Override
	public long writeBytesToFileChannel(long pBufferPositionInBytes,
																			FileChannel pFileChannel,
																			long pFilePositionInBytes,
																			long pLengthInBytes) throws IOException
	{
		return getMemoryRegionInterface().writeBytesToFileChannel(pBufferPositionInBytes,
																															pFileChannel,
																															pFilePositionInBytes,
																															pLengthInBytes);
	}

	@Override
	public void readBytesFromFileChannel(	FileChannel pFileChannel,
																				long pFilePositionInBytes,
																				long pLengthInBytes) throws IOException
	{
		getMemoryRegionInterface().readBytesFromFileChannel(pFileChannel,
																												pFilePositionInBytes,
																												pLengthInBytes);
	}

	@Override
	public void readBytesFromFileChannel(	long pBufferPositionInBytes,
																				FileChannel pFileChannel,
																				long pFilePositionInBytes,
																				long pLengthInBytes) throws IOException
	{
		getMemoryRegionInterface().readBytesFromFileChannel(pBufferPositionInBytes,
																												pFileChannel,
																												pFilePositionInBytes,
																												pLengthInBytes);
	}

	@Override
	public void free()
	{
		mMemoryRegionInterface.free();
	}

	@Override
	public boolean isFree()
	{
		return mMemoryRegionInterface.isFree();
	}

	@Override
	public String toString()
	{
		return "NDArrayDirect [mDefaultBoundedCursor=" + mDefaultBoundedCursor
						+ ", mMemoryRegionInterface="
						+ mMemoryRegionInterface
						+ "]";
	}

	public NDArrayTypedDirect<T> sliceMajorAxis(long pSliceIndex)
	{
		// TODO: this is very inelegant way to slice, allocates memory forcefully...
		Class<T> lType = this.getType();
		int lDimension = mDefaultBoundedCursor.getDimension();
		long[] lDimensions = mDefaultBoundedCursor.getDimensions();
		long[] lDimensionsSlice = new long[lDimensions.length - 1];
		for (int i = 0; i < lDimensions.length - 1; i++)
			lDimensionsSlice[i] = lDimensions[i];

		long[] lSliceVector = new long[lDimension];
		lSliceVector[lDimension - 1] = pSliceIndex;

		long lSliceLinearIndex = NDBoundedCursor.getIndex(lDimensions,
																											lSliceVector);

		NDBoundedCursor lNDBoundedCursor = NDBoundedCursor.createNDVectorCursor(lDimensionsSlice);
		@SuppressWarnings("unchecked")
		MemoryRegionInterface<T> lRAM = ((MemoryRegionInterface<T>) mMemoryRegionInterface).subRegion(lSliceLinearIndex * SizeOf.sizeOf(lType),
																																																	lNDBoundedCursor.getVolume() * SizeOf.sizeOf(lType));

		NDArrayTypedDirect<T> lNDArraySlice = NDArrayTypedDirect.allocateNDArray(	lRAM,
																																							lType,
																																							lNDBoundedCursor);
		return lNDArraySlice;
	}

	@Override
	public void complainIfFreed() throws FreedException
	{
		if (isFree())
		{
			final String lErrorMessage = "Underlying ressource has been freed!";
			throw new FreedException(lErrorMessage);
		}
	}

}
