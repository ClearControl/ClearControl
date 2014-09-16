package rtlib.kam.memory.impl.file;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import org.bridj.Pointer;
import org.bridj.Pointer.Releaser;
import org.bridj.PointerIO;

import rtlib.core.memory.InvalidNativeMemoryAccessException;
import rtlib.core.memory.SizedInBytes;
import rtlib.core.memory.map.MemoryMappedFile;
import rtlib.core.memory.map.MemoryMappedFileException;
import rtlib.core.memory.map.MemoryMappedFileUtils;
import rtlib.core.rgc.Cleaner;
import rtlib.core.rgc.Freeable;
import rtlib.kam.memory.MappableMemory;
import rtlib.kam.memory.MemoryType;
import rtlib.kam.memory.impl.direct.RAMDirect;
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
	private MemoryMappedFile mMemoryMappedFile;

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
			mMemoryMappedFile = new MemoryMappedFile(	mFileChannel,
																								MemoryMappedFileUtils.bestMode(mStandardOpenOption),
																								mFilePositionInBytes,
																								mLengthInBytes,
																								mFileChannel.size() < mFilePositionInBytes + mLengthInBytes);
			mAddressInBytes = mMemoryMappedFile.getAddressAtFilePosition(mFilePositionInBytes);
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
		try
		{
			mMemoryMappedFile.close();
			setCurrentlyMapped(false);
		}
		catch (IOException e)
		{
			throw new RuntimeException(	"Exception while unmapping " + this.getClass()
																																			.getSimpleName(),
																	e);
		}
		finally
		{
			mMemoryMappedFile = null;
			mAddressInBytes = 0;
			mLengthInBytes = 0;
		}
	}

	@Override
	public RAMDirect subRegion(long pOffset, long pLenghInBytes)
	{
		if (mAddressInBytes + pOffset + pLenghInBytes > mAddressInBytes + mLengthInBytes)
			throw new InvalidNativeMemoryAccessException(String.format(	"Cannot instanciate RAMDirect from RAMFile on subregion staring at offset %d and length %d  ",
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

	@SuppressWarnings("rawtypes")
	@Override
	public Pointer getBridJPointer(Class pTargetClass)
	{
		PointerIO<?> lPointerIO = PointerIO.getInstance(pTargetClass);
		Releaser lReleaser = new Releaser()
		{
			@Override
			public void release(Pointer<?> pP)
			{
				// TODO: should this really be empty?
			}
		};
		Pointer lPointerToAddress = Pointer.pointerToAddress(	getAddress(),
																														getSizeInBytes(),
																														lPointerIO,
																														lReleaser);
		return lPointerToAddress;
	}

	@Override
	public ByteBuffer passNativePointerToByteBuffer(Class<?> pTargetClass)
	{

		ByteBuffer lByteBuffer = getBridJPointer(pTargetClass).getByteBuffer();

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
						+ ", mLengthInBytes="
						+ mLengthInBytes
						+ ", mIsMapped="
						+ isCurrentlyMapped()
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

	@Override
	public Cleaner getCleaner()
	{
		// no need to return a cleaner since MemoryMappedFile already cleans behind
		// itself.
		return null;
	}


}
