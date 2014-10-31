package rtlib.kam.memory.impl.direct;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

import org.bridj.Pointer;
import org.bridj.Pointer.Releaser;
import org.bridj.PointerIO;

import rtlib.core.memory.InvalidNativeMemoryAccessException;
import rtlib.core.memory.NativeMemoryAccess;
import rtlib.core.memory.NativeMemoryCleaner;
import rtlib.core.memory.SizedInBytes;
import rtlib.core.rgc.Cleaner;
import rtlib.core.rgc.Freeable;
import rtlib.core.rgc.RessourceGarbageCollector;
import rtlib.kam.memory.MemoryType;
import rtlib.kam.memory.PointerAccessible;
import rtlib.kam.memory.ReadWriteBytesFileChannel;
import rtlib.kam.memory.impl.file.RAMFile;
import rtlib.kam.memory.ram.RAM;
import rtlib.kam.memory.ram.RAMAbstract;
import rtlib.kam.memory.ram.Resizable;
import rtlib.kam.memory.ram.UnsupportedMemoryResizingException;

public class RAMDirect extends RAMAbstract implements
																					PointerAccessible,
																					Resizable,
																					SizedInBytes,
																					RAM,
																					Freeable,
																					ReadWriteBytesFileChannel

{
	protected Object mParent = null;

	public static final RAMDirect wrapPointer(final Object pParent,
																						final long pAddress,
																						final long pLengthInBytes)
	{
		return new RAMDirect(pParent, pAddress, pLengthInBytes);
	};

	public static RAMDirect allocate(long pLengthInBytes)
	{
		return new RAMDirect(pLengthInBytes);
	}

	public RAMDirect(	final Object pParent,
										final long pAddress,
										final long pLengthInBytes)
	{
		super();
		mParent = pParent;
		mAddressInBytes = pAddress;
		mLengthInBytes = pLengthInBytes;
		if (pParent != null)
			RessourceGarbageCollector.register(this);
	}

	public RAMDirect(final long pLengthInBytes)
	{
		this(	null,
					NativeMemoryAccess.allocateMemory(pLengthInBytes),
					pLengthInBytes);
	}

	public RAMDirect subRegion(	final long pOffset,
															final long pLenghInBytes)
	{
		if (mAddressInBytes + pOffset + pLenghInBytes > mAddressInBytes + mLengthInBytes)
			throw new InvalidNativeMemoryAccessException(String.format(	"Cannot instanciate RAMDirect on subregion staring at offset %d and length %d  ",
																																	pOffset,
																																	pLenghInBytes));
		RAMDirect lRAMDirect = new RAMDirect(	this,
																					mAddressInBytes + pOffset,
																					pLenghInBytes);
		return lRAMDirect;
	}

	@Override
	public MemoryType getMemoryType()
	{
		complainIfFreed();
		return MemoryType.CPURAMDIRECT;
	}

	@Override
	public long resize(long pNewLength)
	{
		complainIfFreed();
		if (mParent != null)
			throw new UnsupportedMemoryResizingException("Cannot resize externally allocated memory region!");
		try
		{
			mAddressInBytes = NativeMemoryAccess.reallocateMemory(mAddressInBytes,
																														pNewLength);
			mLengthInBytes = pNewLength;
		}
		catch (Throwable e)
		{
			final String lErrorMessage = String.format(	"Could not resize memory region from %d to %d ",
																									mLengthInBytes,
																									pNewLength);
			// error("KAM", lErrorMessage);
			throw new UnsupportedMemoryResizingException(lErrorMessage, e);
		}
		return mLengthInBytes;
	}

	@Override
	public void free()
	{
		if (mParent == null && mAddressInBytes != 0)
		{
			NativeMemoryAccess.freeMemory(mAddressInBytes);
		}
		mAddressInBytes = 0;
		mParent = null;
		super.free();
	}

	@Override
	public Cleaner getCleaner()
	{
		if (mParent != null)
			return new NativeMemoryCleaner(null);
		return new NativeMemoryCleaner(mAddressInBytes);
	}

	@Override
	public long writeBytesToFileChannel(FileChannel pFileChannel,
																			long pFilePositionInBytes) throws IOException
	{
		return writeBytesToFileChannel(	0,
																		pFileChannel,
																		pFilePositionInBytes,
																		getSizeInBytes());
	}

	@Override
	public long writeBytesToFileChannel(long pPositionInBufferInBytes,
																			FileChannel pFileChannel,
																			long pFilePositionInBytes,
																			long pLengthInBytes) throws IOException
	{
		RAMFile lRAMFile = new RAMFile(	pFileChannel,
																		pFilePositionInBytes,
																		pLengthInBytes,
																		StandardOpenOption.CREATE,
																		StandardOpenOption.WRITE);
		lRAMFile.map();
		copyRangeTo(pPositionInBufferInBytes, lRAMFile, 0, pLengthInBytes);
		lRAMFile.unmap();
		lRAMFile.free();
		return pFilePositionInBytes + pLengthInBytes;
	}

	@Override
	public void readBytesFromFileChannel(	FileChannel pFileChannel,
																				long pFilePositionInBytes,
																				long pLengthInBytes) throws IOException
	{
		readBytesFromFileChannel(	0,
															pFileChannel,
															pFilePositionInBytes,
															pLengthInBytes);
	}

	@Override
	public void readBytesFromFileChannel(	long pPositionInBufferInBytes,
																				FileChannel pFileChannel,
																				long pFilePositionInBytes,
																				long pLengthInBytes) throws IOException
	{
		RAMFile lRAMFile = new RAMFile(	pFileChannel,
																		pFilePositionInBytes,
																		pLengthInBytes,
																		StandardOpenOption.READ);
		lRAMFile.map();
		lRAMFile.copyRangeTo(	0,
													this,
													pPositionInBufferInBytes,
													pLengthInBytes);
		lRAMFile.unmap();
		lRAMFile.free();
	}

	@SuppressWarnings(
	{ "deprecation", "unchecked", "rawtypes" })
	public Pointer getBridJPointer(Class pTargetClass)
	{
		RAMDirect mThis = this;
		PointerIO<?> lPointerIO = PointerIO.getInstance(pTargetClass);
		Releaser lReleaser = new Releaser()
		{
			RAMDirect mRAMDirect = mThis;
			@Override
			public void release(Pointer<?> pP)
			{
				mRAMDirect = null;
			}
		};
		Pointer<?> lPointerToAddress = Pointer.pointerToAddress(getAddress(),
																														getSizeInBytes(),
																														lPointerIO,
																														lReleaser);

		return lPointerToAddress;

	}

	public ByteBuffer passNativePointerToByteBuffer(Class<?> pTargetClass)
	{
		ByteBuffer lByteBuffer = getBridJPointer(pTargetClass).getByteBuffer();

		return lByteBuffer;

	}

	@Override
	public String toString()
	{
		return "RAMDirect [mParent=" + mParent
						+ ", mAddressInBytes="
						+ mAddressInBytes
						+ ", mLengthInBytes="
						+ mLengthInBytes
						+ ", mIsFree="
						+ mIsFree
						+ ", getMemoryType()="
						+ getMemoryType()
						+ "]";
	}


}
