package stackserver;


import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Formatter;

import stack.Stack;

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
	public boolean appendStack(	final Stack pStack)
	{

		try
		{
			mVariableBundleAsFile.write();
			mStackIndexToTimeStampInNanosecondsMap.put(	mNextFreeStackIndex,
			                                           	pStack.timestampns);
			mStackIndexToBinaryFilePositionMap.put(	mNextFreeStackIndex,
																							mNextFreeTypePosition);
			
			//mStackIndexToStackDimensionsMap.put(mNextFreeStackIndex, pStack.ndarray.);

			final long lNewNextFreeTypePosition = pStack.ndarray.writeToFileChannel(mBinaryFileChannel,
																																			mNextFreeTypePosition);

			mBinaryFileChannel.force(false);

			mIndexFileFormatter.format(	"%d\t%d\t%d\n",
																	mNextFreeStackIndex,
																	pStack.timestampns,
																	mNextFreeTypePosition);
			mIndexFileFormatter.flush();

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
