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
	protected final FileChannel mBinaryFileChannel;
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
		if (!pReadOnly)
			mBinaryFile.getParentFile().mkdirs();

		mNextFreeTypePosition = 0;

		if (pReadOnly)
		{
			mBinaryFileChannel = FileChannel.open(mBinaryFile.toPath(),
																						StandardOpenOption.READ);
		}
		else
		{
			mBinaryFileChannel = FileChannel.open(mBinaryFile.toPath(),
																						StandardOpenOption.APPEND,
																						StandardOpenOption.WRITE,
																						StandardOpenOption.CREATE_NEW);
		}

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

	@Override
	public VariableBundle getMetaDataVariableBundle()
	{
		return mMetaDataVariableBundleAsFile;
	}

	@Override
	public void close() throws IOException
	{
		mBinaryFileChannel.force(true);
		mBinaryFileChannel.close();
		mMetaDataVariableBundleAsFile.close();
	}

}
