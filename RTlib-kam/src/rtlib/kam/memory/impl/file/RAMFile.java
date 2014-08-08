package rtlib.kam.memory.impl.file;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import org.bridj.Pointer;

import rtlib.core.memory.MemoryMappedFile;
import rtlib.core.memory.MemoryMappedFileException;
import rtlib.core.memory.NativeMemoryAccess;
import rtlib.core.memory.SizedInBytes;
import rtlib.core.rgc.Cleaner;
import rtlib.core.rgc.Freeable;
import rtlib.kam.memory.MappableMemory;
import rtlib.kam.memory.MemoryType;
import rtlib.kam.memory.ram.RAM;
import rtlib.kam.memory.ram.RAMMappedAbstract;
import rtlib.kam.memory.ram.Resizable;
import rtlib.kam.memory.ram.UnsupportedMemoryResizingException;

public class RAMFile extends RAMMappedAbstract implements
																							MappableMemory,
																							Resizable,
																							SizedInBytes,
																							RAM,
																							Freeable

{

	private FileChannel mFileChannel;
	private StandardOpenOption[] mStandardOpenOption;
	private long mFilePositionInBytes;
	private long mPositionWithinPageInBytes;
	private long mPagePositionInBytes;

	private long mPageAlignedMappingAddressInBytes;
	private long mMappingLengthInBytes;

	public RAMFile createNewRAMFile(File pFile,
																	final long pLengthInBytes) throws IOException
	{
		return new RAMFile(	pFile,
												0,
												pLengthInBytes,
												StandardOpenOption.CREATE_NEW,
												StandardOpenOption.READ,
												StandardOpenOption.WRITE);
	}

	public RAMFile createNewSparseRAMFile(File pFile,
																				final long pLengthInBytes) throws IOException
	{
		return new RAMFile(	pFile,
												0,
												pLengthInBytes,
												StandardOpenOption.CREATE_NEW,
												StandardOpenOption.READ,
												StandardOpenOption.WRITE,
												StandardOpenOption.SPARSE);
	}

	public RAMFile openExistingRAMFile(	File pFile,
																			final long pLengthInBytes) throws IOException
	{
		return openExistingRAMFile(pFile, 0, pLengthInBytes);
	}

	public RAMFile openExistingRAMFile(	File pFile,
																			final long pPositionInBytes,
																			final long pLengthInBytes) throws IOException
	{
		return new RAMFile(	pFile,
												pPositionInBytes,
												pLengthInBytes,
												StandardOpenOption.READ,
												StandardOpenOption.WRITE);
	}

	public RAMFile openReadOnlyExistingRAMFile(	File pFile,
																							final long pLengthInBytes) throws IOException
	{
		return openReadOnlyExistingRAMFile(pFile, 0, pLengthInBytes);
	}

	public RAMFile openReadOnlyExistingRAMFile(	File pFile,
																							final long pPositionInBytes,
																							final long pLengthInBytes) throws IOException
	{
		return new RAMFile(	pFile,
												pPositionInBytes,
												pLengthInBytes,
												StandardOpenOption.READ);
	}

	public RAMFile(	File pFile,
									final long pLengthInBytes,
									StandardOpenOption... pStandardOpenOption) throws IOException
	{
		this(pFile, 0, pLengthInBytes, pStandardOpenOption);
	}

	public RAMFile(	File pFile,
									final long pPositionInBytes,
									final long pLengthInBytes,
									StandardOpenOption... pStandardOpenOption) throws IOException
	{
		this(	FileChannel.open(	pFile.toPath(),
														obtainStandardOptions(pFile,
																									pStandardOpenOption)),
					pPositionInBytes,
					pLengthInBytes,
					pStandardOpenOption);

	}

	public RAMFile(	FileChannel pFileChannel,
									final long pPositionInBytes,
									final long pLengthInBytes,
									StandardOpenOption... pStandardOpenOption) throws IOException
	{
		super();
		mFileChannel = pFileChannel;
		mFilePositionInBytes = pPositionInBytes;
		mPositionWithinPageInBytes = pPositionInBytes % MemoryMappedFile.cPageSize;
		mPagePositionInBytes = pPositionInBytes - mPositionWithinPageInBytes;
		mMappingLengthInBytes = mPositionWithinPageInBytes + pLengthInBytes;
		mLengthInBytes = pLengthInBytes;
		mStandardOpenOption = pStandardOpenOption;
	}

	static StandardOpenOption[] obtainStandardOptions(File pFile,
																										StandardOpenOption... pStandardOpenOption)
	{
		StandardOpenOption[] lStandardOpenOption = pStandardOpenOption;
		if (pStandardOpenOption == null || pStandardOpenOption.length == 0)
		{
			if (pFile.exists())
				lStandardOpenOption = new StandardOpenOption[]
				{ StandardOpenOption.READ, StandardOpenOption.WRITE };
			else
				lStandardOpenOption = new StandardOpenOption[]
				{ StandardOpenOption.CREATE_NEW,
					StandardOpenOption.READ,
					StandardOpenOption.WRITE };
		}
		return lStandardOpenOption;
	}

	@Override
	public long map()
	{
		if (isCurrentlyMapped() && mAddressInBytes != 0)
			return mAddressInBytes;
		try
		{
			mPageAlignedMappingAddressInBytes = MemoryMappedFile.map(	mFileChannel,
																																MemoryMappedFile.bestMode(mStandardOpenOption),
																																mPagePositionInBytes,
																																mMappingLengthInBytes,
																																mFileChannel.size() < mPagePositionInBytes + mMappingLengthInBytes);
			mAddressInBytes = mPageAlignedMappingAddressInBytes + mPositionWithinPageInBytes;

			setCurrentlyMapped(true);
			return mAddressInBytes;
		}
		catch (MemoryMappedFileException | IOException e)
		{
			throw new MemoryMappedFileException(e);
		}

	}

	@Override
	public void force()
	{
		try
		{
			mFileChannel.force(true);
		}
		catch (IOException e)
		{
			final String lErrorMessage = String.format("Could not force memory mapping consistency! ");
			throw new MemoryMappedFileException(lErrorMessage, e);
		}
	}

	@Override
	public void unmap()
	{
		if (!isCurrentlyMapped())
			return;

		// force();
		MemoryMappedFile.unmap(	mFileChannel,
														mPageAlignedMappingAddressInBytes,
														mMappingLengthInBytes);
		setCurrentlyMapped(false);
		mPageAlignedMappingAddressInBytes = 0;
		mAddressInBytes = 0;
		mMappingLengthInBytes = 0;
	}

	@Override
	public MemoryType getMemoryType()
	{
		complainIfFreed();
		return MemoryType.FILERAM;
	}

	@Override
	public long resize(long pNewLength)
	{
		final String lErrorMessage = String.format("Could not resize memory mapped file! ");
		// error("KAM", lErrorMessage);
		throw new UnsupportedMemoryResizingException(lErrorMessage);
	}

	@Override
	public void free()
	{
		try
		{
			unmap();
			super.free();
		}
		catch (Throwable e)
		{
			final String lErrorMessage = String.format("Could not unmap memory mapped file! ");
			throw new MemoryMappedFileException(lErrorMessage, e);
		}

	}

	static class RAMFileCleaner implements Cleaner
	{
		private long mAddressToClean;
		private FileChannel mFileChannelToClean;
		private long mMappedRegionLength;

		public RAMFileCleaner(FileChannel pFileChannel,
													final long pMemoryMapAddress,
													final long pMappedRegionLength)
		{
			mFileChannelToClean = pFileChannel;
			mAddressToClean = pMemoryMapAddress;
			mMappedRegionLength = pMappedRegionLength;
		}

		@Override
		public void run()
		{
			if (NativeMemoryAccess.isAllocatedMemory(mAddressToClean))
				MemoryMappedFile.unmap(	mFileChannelToClean,
																mAddressToClean,
																mMappedRegionLength);
		}

	}

	@Override
	public Cleaner getCleaner()
	{
		return new RAMFileCleaner(mFileChannel,
															mPageAlignedMappingAddressInBytes,
															mMappingLengthInBytes);
	}

	@Override
	@SuppressWarnings("deprecation")
	public ByteBuffer passNativePointerToByteBuffer()
	{
		Pointer<?> lPointerToAddress = Pointer.pointerToAddress(getAddress(),
																														getSizeInBytes());
		ByteBuffer lByteBuffer = lPointerToAddress.getByteBuffer();

		return lByteBuffer;

	}

	@Override
	public String toString()
	{
		return "RAMFile [mFileChannel=" + mFileChannel
						+ ", mStandardOpenOption="
						+ Arrays.toString(mStandardOpenOption)
						+ ", mFilePositionInBytes="
						+ mFilePositionInBytes
						+ ", mPositionWithinPageInBytes="
						+ mPositionWithinPageInBytes
						+ ", mPagePositionInBytes="
						+ mPagePositionInBytes
						+ ", mPageAlignedMappingAddressInBytes="
						+ mPageAlignedMappingAddressInBytes
						+ ", mMappingLengthInBytes="
						+ mMappingLengthInBytes
						+ ", mIsMapped="
						+ mIsMapped
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
