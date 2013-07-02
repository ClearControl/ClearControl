package stackserver;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Formatter;

import stack.Stack;
import variable.VariableInterface;

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
	public boolean appendStack(final Stack pStack)
	{

		try
		{
			if (getNumberOfStacks() == 0)
				mMetaDataVariableBundleAsFile.write();
			
			mStackIndexToTimeStampInNanosecondsMap.put(	mNextFreeStackIndex,
																									pStack.timestampns);
			mStackIndexToBinaryFilePositionMap.put(	mNextFreeStackIndex,
																							mNextFreeTypePosition);

			final int[] lDimensionsWithoutSize = pStack.ndarray.getDimensions();
			mStackIndexToStackDimensionsMap.put(mNextFreeStackIndex,
																					lDimensionsWithoutSize);

			final long lNewNextFreeTypePosition = pStack.ndarray.writeToFileChannel(mBinaryFileChannel,
																																							mNextFreeTypePosition);

			mBinaryFileChannel.force(false);

			final String lDimensionsString = Arrays.toString(lDimensionsWithoutSize);
			final String lTruncatedDimensionsString = lDimensionsString.substring(1,
																																						lDimensionsString.length() - 1);

			mIndexFileFormatter.format(	"%d\t%d\t%s\t%d\n",
																	mNextFreeStackIndex,
																	pStack.timestampns,
																	lTruncatedDimensionsString,
																	mNextFreeTypePosition);

			mIndexFileFormatter.flush();

			mNextFreeTypePosition = lNewNextFreeTypePosition;

			pStack.releaseFrame();

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

	@Override
	public void addMetaDataVariable(String pPrefix,
																	VariableInterface<?> pVariable)
	{
		mMetaDataVariableBundleAsFile.addVariable(pPrefix, pVariable);
	}

	@Override
	public void removeAllMetaDataVariables()
	{
		mMetaDataVariableBundleAsFile.removeAllVariables();
	}

}
