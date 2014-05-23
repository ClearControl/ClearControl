package rtlib.core.memory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

import rtlib.core.log.Loggable;

public final class MemoryMappedFile implements Loggable
{
	private static final ByteBuffer cZeroBuffer = ByteBuffer.allocateDirect(1);

	public static final long cPageSize = 4096;

	public static enum MemoryMapAccessMode
	{
		ReadOnly(0), ReadWrite(1), Private(2);

		private final int mValue;

		private MemoryMapAccessMode(final int pValue)
		{
			mValue = pValue;
		}

		public int getValue()
		{
			return mValue;
		}
	}

	public static final MemoryMapAccessMode ReadOnly = MemoryMapAccessMode.ReadOnly;
	public static final MemoryMapAccessMode ReadWrite = MemoryMapAccessMode.ReadWrite;
	public static final MemoryMapAccessMode Private = MemoryMapAccessMode.Private;


	public static final long map(	FileChannel pFileChannel,
																MemoryMapAccessMode pAccessMode,
																final long pFilePosition,
																final long pMappedRegionLength,
																final boolean pExtendIfNeeded) throws MemoryMappedFileException
	{
		long lAddress = 0;
		Method lMemoryMapMethod;
		try
		{
			if (!pFileChannel.isOpen())
				throw new ClosedChannelException();

			if ((pFilePosition % cPageSize) != 0)
				throw new IllegalArgumentException("File position must be page aligned (4096 byte boundaries)");
			if (pFilePosition < 0L)
				throw new IllegalArgumentException("Negative position");
			if (pMappedRegionLength < 0L)
				throw new IllegalArgumentException("Negative size");
			if (pFilePosition + pMappedRegionLength < 0L)
				throw new IllegalArgumentException("Position + size overflow");
			if (pMappedRegionLength > Long.MAX_VALUE)
				throw new IllegalArgumentException("Size exceeds Long.MAX_VALUE");

			
			if(pExtendIfNeeded)
			{
				if(pFileChannel.size()<pFilePosition+pMappedRegionLength)
				{
					long lCurrentPosition = pFileChannel.position();
					pFileChannel.position(pFilePosition + pMappedRegionLength
																- 1);
					// The following ensures that the file has the size requested in the
					// mapping
					cZeroBuffer.clear();
					pFileChannel.write(cZeroBuffer);
					pFileChannel.force(false);
					
					pFileChannel.position(lCurrentPosition);
				}
			}
			
			lMemoryMapMethod = pFileChannel.getClass()
																			.getDeclaredMethod(	"map0",
																													Integer.TYPE,
																													Long.TYPE,
																													Long.TYPE);
			lMemoryMapMethod.setAccessible(true);
			Object lReturnValue = lMemoryMapMethod.invoke(pFileChannel,
																										pAccessMode.getValue(),
																										pFilePosition,
																										pMappedRegionLength);
			
			
			
			final Long lAddressAsLong = (Long) lReturnValue;

			lAddress = lAddressAsLong.longValue();
			NativeMemoryAccess.registerMemoryRegion(lAddress,
																							pMappedRegionLength);
		}
		catch (Throwable e)
		{
			String lErrorMessage = String.format(	"Cannot memory map file: %s at file position %d width length %d",
																						pFileChannel.toString(),
																						pFilePosition,
																						pMappedRegionLength);
			new MemoryMappedFile().error("Native", lErrorMessage);
			throw new MemoryMappedFileException(lErrorMessage, e);
		}

		return lAddress;
	}

	public static final int unmap(FileChannel pFileChannel,
																final long pMemoryMapAddress,
																final long pMappedRegionLength) throws MemoryMappedFileException
	{
		int lIntReturnValue = 0;
		try
		{

			Method lMemoryUnMapMethod = pFileChannel.getClass()
																							.getDeclaredMethod(	"unmap0",
																																	Long.TYPE,
																																	Long.TYPE);
			lMemoryUnMapMethod.setAccessible(true);
			Object lReturnValue = lMemoryUnMapMethod.invoke(null,
																											pMemoryMapAddress,
																											pMappedRegionLength);

			NativeMemoryAccess.deregisterMemoryRegion(pMemoryMapAddress);

			final Integer lReturnAsInteger = (Integer) lReturnValue;

			lIntReturnValue = lReturnAsInteger.intValue();
		}
		catch (Throwable e)
		{
			String lErrorMessage = String.format(	"Cannot unmap memory at address %d with length %d",
																						pMemoryMapAddress,
																						pMappedRegionLength);
			new MemoryMappedFile().error("Native", lErrorMessage);
			throw new MemoryMappedFileException(lErrorMessage, e);
		}

		return lIntReturnValue;
	}

	/*public static final void force(	final FileChannel pFileChannel,
																	boolean pFlushFileMetadataToo) throws IOException
	{
		try
		{
			pFileChannel.force(pFlushFileMetadataToo);
		}
		catch (Throwable e)
		{
			String lErrorMessage = String.format(	"Cannot flush file %s contents (flushing metadata = %s, exception: %s)",
																						pFileChannel,
																						pFlushFileMetadataToo	? "true"
																																	: "false",
																						e.getMessage());
			new MemoryMappedFile().error("Native", lErrorMessage);
			throw new IOException(e);
		}

	}/**/

	public static final long filesize(FileChannel pFileChannel) throws IOException
	{
		return pFileChannel.size();
	}

	public static final void truncate(FileChannel pFileChannel,
																		final long pLength) throws IOException
	{
		try
		{
			pFileChannel.truncate(pLength);
		}
		catch (Throwable e)
		{
			String lErrorMessage = String.format(	"Cannot truncate file %s at length %d (%s)",
																						pFileChannel,
																						pLength,
																						e.getMessage());
			new MemoryMappedFile().error("Native", lErrorMessage);
			throw new IOException(e);
		}

	}

	public static MemoryMapAccessMode bestMode(StandardOpenOption[] pStandardOpenOption)
	{
		boolean lWrite = false;
		boolean lRead = false;

		for (StandardOpenOption lStandardOpenOption : pStandardOpenOption)
		{
			lWrite |= lStandardOpenOption == lStandardOpenOption.CREATE;
			lWrite |= lStandardOpenOption == lStandardOpenOption.CREATE_NEW;
			lWrite |= lStandardOpenOption == lStandardOpenOption.WRITE;
			lWrite |= lStandardOpenOption == lStandardOpenOption.APPEND;
			lWrite |= lStandardOpenOption == lStandardOpenOption.DELETE_ON_CLOSE;
			lWrite |= lStandardOpenOption == lStandardOpenOption.SYNC;
			lRead |= lStandardOpenOption == lStandardOpenOption.READ;
		}

		if (lWrite)
			return MemoryMapAccessMode.ReadWrite;

		if (lRead)
			return MemoryMapAccessMode.ReadOnly;

		return MemoryMapAccessMode.ReadWrite;
	}

}
