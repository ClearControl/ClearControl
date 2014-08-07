package rtlib.kam.memory.impl.direct;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

import org.bridj.Pointer;

import rtlib.core.memory.NativeMemoryAccess;
import rtlib.core.memory.NativeMemoryCleaner;
import rtlib.core.memory.SizedInBytes;
import rtlib.core.rgc.Cleaner;
import rtlib.core.rgc.Freeable;
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
	}

	public RAMDirect(final long pLengthInBytes)
	{
		this(	null,
					NativeMemoryAccess.allocateMemory(pLengthInBytes),
					pLengthInBytes);
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

	@SuppressWarnings("deprecation")
	public ByteBuffer wrapWithByteBuffer()
	{
		Pointer<?> lPointerToAddress = Pointer.pointerToAddress(getAddress(),
																														getSizeInBytes());
		ByteBuffer lByteBuffer = lPointerToAddress.getByteBuffer();

		return lByteBuffer;

	}

}
