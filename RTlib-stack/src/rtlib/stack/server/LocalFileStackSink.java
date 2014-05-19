package rtlib.stack.server;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;

import rtlib.core.units.Magnitudes;
import rtlib.core.variable.VariableInterface;
import rtlib.stack.Stack;

public class LocalFileStackSink extends LocalFileStackBase implements
																													StackSinkInterface,
																													Closeable
{

	private volatile long mFirstTimePointAbsoluteNanoSeconds;

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
			{
				mMetaDataVariableBundleAsFile.write();
			}

			mStackIndexToBinaryFilePositionMap.put(	mNextFreeStackIndex,
																							mNextFreeTypePosition);

			final Long[] lDimensionsWithoutSize = ArrayUtils.toObject(pStack.getNDArray().getDimensions());
			mStackIndexToStackDimensionsMap.put(mNextFreeStackIndex,
																					lDimensionsWithoutSize);

			final FileChannel lBinnaryFileChannel = getFileChannelForBinaryFile(false,
																																					true);
			final long lNewNextFreeTypePosition = pStack.getNDArray()
																									.writeBytesToFileChannel(	lBinnaryFileChannel,
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

			if (mNextFreeStackIndex == 0)
			{
				mFirstTimePointAbsoluteNanoSeconds = pStack.getTimeStampInNanoseconds();
			}
			final double lTimeStampInSeconds = Magnitudes.nano2unit(pStack.getTimeStampInNanoseconds() - mFirstTimePointAbsoluteNanoSeconds);

			mStackIndexToTimeStampInSecondsMap.put(	mNextFreeStackIndex,
																							lTimeStampInSeconds);

			final String lIndexLineString = String.format("%d\t%.4f\t%s\t%d\n",
																										mNextFreeStackIndex,
																										lTimeStampInSeconds,
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
	public void addMetaDataVariable(final String pPrefix,
																	final VariableInterface<?> pVariable)
	{
		mMetaDataVariableBundleAsFile.addVariable(pPrefix, pVariable);
	}

	@Override
	public void removeAllMetaDataVariables()
	{
		mMetaDataVariableBundleAsFile.removeAllVariables();
	}

	@Override
	public void removeMetaDataVariable(final VariableInterface<?> pVariable)
	{
		mMetaDataVariableBundleAsFile.removeVariable(pVariable);
	}

}
