package rtlib.core.memory.map;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.FileChannel;

import rtlib.core.memory.NativeMemoryAccess;
import rtlib.core.rgc.Cleanable;
import rtlib.core.rgc.Cleaner;

public class MemoryMappedFile implements Closeable, Cleanable
{

	
	private FileChannel mFileChannel;
	private MemoryMappedFileAccessMode mAccessMode;
	private boolean mExtendIfNeeded;

	private long mRequestedFilePosition;
	private long mRequestedMappedRegionLength;

	private long mActualMappingFilePosition;
	private long mActualMappingRegionLength;

	private long mMappingPointerAddress;



	public MemoryMappedFile(FileChannel pFileChannel,
													MemoryMappedFileAccessMode pAccessMode,
													final long pFilePosition,
													final long pMappedRegionLength,
													final boolean pExtendIfNeeded)
	{
		super();
		mFileChannel = pFileChannel;
		mAccessMode = pAccessMode;
		mRequestedFilePosition = pFilePosition;
		mRequestedMappedRegionLength = pMappedRegionLength;
		mExtendIfNeeded = pExtendIfNeeded;

		mActualMappingFilePosition = mRequestedFilePosition - (mRequestedFilePosition % MemoryMappedFileUtils.cAllocationGranularity);
		mActualMappingRegionLength = (mRequestedFilePosition % MemoryMappedFileUtils.cAllocationGranularity) + mRequestedMappedRegionLength;
		
		mMappingPointerAddress = MemoryMappedFileUtils.map(	mFileChannel,
																												mAccessMode,
																												mActualMappingFilePosition,
																												mActualMappingRegionLength,
																												mExtendIfNeeded);
	}


	public long getAddressAtFilePosition(long pFilePosition)
	{
		if (pFilePosition < mRequestedFilePosition)
			throw new IndexOutOfBoundsException("File position index invalid: accessing before the mapped file region");

		if (mRequestedFilePosition + mRequestedMappedRegionLength <= pFilePosition)
			throw new IndexOutOfBoundsException("File position index invalid: accessing after the mapped file region");
		
		return mMappingPointerAddress + (pFilePosition - mActualMappingFilePosition);
	}


	@Override
	public void close() throws IOException
	{
		MemoryMappedFileUtils.unmap(mFileChannel,
																mMappingPointerAddress,
																mActualMappingRegionLength);

	}

	static class MemoryMappedFileCleaner implements Cleaner
	{
		private long mAddressToClean;
		private FileChannel mFileChannelToClean;
		private long mMappedRegionLength;

		public MemoryMappedFileCleaner(FileChannel pFileChannel,
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
				MemoryMappedFileUtils.unmap(mFileChannelToClean,
																		mAddressToClean,
																		mMappedRegionLength);
		}

	}

	@Override
	public Cleaner getCleaner()
	{
		return new MemoryMappedFileCleaner(mFileChannel,
															mMappingPointerAddress,
															mActualMappingRegionLength);
	}

}
