package stackserver;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Formatter;

import stack.Stack;
import variable.VariableInterface;

public class LocalFileStackSink extends LocalFileStackBase implements
																													StackSinkInterface,
																													Closeable
{

	public LocalFileStackSink(final File pRootFolder, final String pName) throws IOException
	{
		super(pRootFolder, pName, false);

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

			final FileChannel lBinnaryFileChannel = getFileChannelForBinaryFile(false);
			final long lNewNextFreeTypePosition = pStack.ndarray.writeToFileChannel(lBinnaryFileChannel,
																																							mNextFreeTypePosition);

			lBinnaryFileChannel.force(false);
			lBinnaryFileChannel.close();

			final String lDimensionsString = Arrays.toString(lDimensionsWithoutSize);
			final String lTruncatedDimensionsString = lDimensionsString.substring(1,
																																						lDimensionsString.length() - 1);

			final FileChannel lIndexFileChannel = FileChannel.open(	mIndexFile.toPath(),
																												StandardOpenOption.APPEND,
																												StandardOpenOption.WRITE,
																												StandardOpenOption.CREATE);

			final String lIndexLineString = String.format("%d\t%d\t%s\t%d\n",
																										mNextFreeStackIndex,
																										pStack.timestampns,
																										lTruncatedDimensionsString,
																										mNextFreeTypePosition);
			final byte[] lIndexLineStringBytes = lIndexLineString.getBytes();
			final ByteBuffer lIndexLineStringByteBuffer = ByteBuffer.wrap(lIndexLineStringBytes);
			lIndexFileChannel.write(lIndexLineStringByteBuffer);
			lIndexFileChannel.force(true);
			lIndexFileChannel.close();

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

	@Override
	public void removeMetaDataVariable(VariableInterface<?> pVariable)
	{
		mMetaDataVariableBundleAsFile.removeVariable(pVariable);
	}

}
