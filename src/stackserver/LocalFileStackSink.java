package stackserver;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Formatter;

import ndarray.InterfaceNDArray;

public class LocalFileStackSink extends LocalFileStackBase implements
																													StackSinkInterface,
																													Closeable
{

	protected final Formatter mIndexFileFormatter;

	public LocalFileStackSink(final File pRootFolder, final String pName) throws IOException
	{
		super(pRootFolder, pName, false);
		mIndexFileFormatter = new Formatter(mIndexFile);
	}

	@Override
	public boolean appendStack(	final long pTimeStampInNanoseconds,
															final InterfaceNDArray pStack)
	{

		try
		{
			mVariableBundleAsFile.write();
			mStackIndexToTimeStampInNanosecondsMap.put(	mNextFreeStackIndex,
																									pTimeStampInNanoseconds);
			mStackIndexToBinaryFilePositionMap.put(	mNextFreeStackIndex,
																							mNextFreeTypePosition);

			final long lNewNextFreeTypePosition = mNextFreeTypePosition + pStack.writeToFileChannel(mBinaryFileChannel,
																																															mNextFreeTypePosition);

			mBinaryFileChannel.force(false);

			mIndexFileFormatter.format(	"%d\t%d\t%d\n",
																	mNextFreeStackIndex,
																	pTimeStampInNanoseconds,
																	mNextFreeTypePosition);

			mNextFreeTypePosition = lNewNextFreeTypePosition;

			mNextFreeStackIndex++;
			return true;
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public void close() throws IOException
	{
		super.close();
		mIndexFileFormatter.close();
	}

}
