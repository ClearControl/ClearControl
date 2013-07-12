package stackserver;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

import variable.bundle.VariableBundle;
import variable.persistence.VariableBundleAsFile;

public abstract class LocalFileStackBase extends StackBase implements
																													Closeable
{
	protected final File mFolder;
	protected final File mBinaryFile;
	protected long mNextFreeStackIndex;
	protected long mNextFreeTypePosition;

	protected final File mIndexFile;
	protected final File mMetaDataFile;
	protected final VariableBundleAsFile mMetaDataVariableBundleAsFile;

	public LocalFileStackBase(final File pRootFolder,
														final String pName,
														final boolean pReadOnly) throws IOException
	{
		super();
		mFolder = new File(pRootFolder, pName);
		mBinaryFile = new File(mFolder, "/data/data.bin");
		
		if(!pReadOnly && mBinaryFile.exists())
			throw new IOException(this.getClass().getSimpleName()+": Cannot overwrite an existing file!");
		
		if (!pReadOnly)
		{
			File lParentFile = mBinaryFile.getParentFile();
			lParentFile.mkdirs();
		}

		mNextFreeTypePosition = 0;

		mIndexFile = new File(mFolder, "/data/index.txt");
		if (!pReadOnly)
			mIndexFile.getParentFile().mkdirs();

		mMetaDataFile = new File(mFolder, "/metadata.txt");
		if (!pReadOnly)
			mMetaDataFile.getParentFile().mkdirs();

		mMetaDataVariableBundleAsFile = new VariableBundleAsFile(	pName + "MetaData",
																															mMetaDataFile,
																															false);
	}

	protected FileChannel getFileChannelForBinaryFile(final boolean pReadOnly) throws IOException
	{
		FileChannel lFileChannel;
		if (pReadOnly)
		{
			lFileChannel = FileChannel.open(mBinaryFile.toPath(),
																			StandardOpenOption.READ);
		}
		else
		{
			lFileChannel = FileChannel.open(mBinaryFile.toPath(),
																			StandardOpenOption.APPEND,
																			StandardOpenOption.WRITE,
																			StandardOpenOption.CREATE);
		}
		return lFileChannel;
	}

	@Override
	public VariableBundle getMetaDataVariableBundle()
	{
		return mMetaDataVariableBundleAsFile;
	}

	@Override
	public void close() throws IOException
	{
		mMetaDataVariableBundleAsFile.close();
	}

}
