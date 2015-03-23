package rtlib.stack.server;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;
import rtlib.core.units.Magnitudes;
import rtlib.core.variable.VariableInterface;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;

public class LocalFileStackSink<T extends NativeType<T>, A extends ArrayDataAccess<A>>	extends
																																												LocalFileStackBase<T, A> implements
																																																								StackSinkInterface<T, A>,
																																																								AutoCloseable
{

	private volatile long mFirstTimePointAbsoluteNanoSeconds;

	public LocalFileStackSink(T pType,
														final File pRootFolder,
														final String pName) throws IOException
	{
		super(pType, pRootFolder, pName, false);

	}

	@Override
	public boolean appendStack(final StackInterface<T, A> pStack)
	{

		try
		{
			if (getNumberOfStacks() == 0)
			{
				mMetaDataVariableBundleAsFile.write();
			}

			mStackIndexToBinaryFilePositionMap.put(	mNextFreeStackIndex,
																							mNextFreeTypePosition);

			final StackRequest<T> lStackRequest = StackRequest.buildFrom(pStack);

			mStackIndexToStackRequestMap.put(	mNextFreeStackIndex,
																				lStackRequest);

			final FileChannel lBinnaryFileChannel = getFileChannelForBinaryFile(false,
																																					true);
			final long lNewNextFreeTypePosition = pStack.getFragmentedMemory()
																									.writeBytesToFileChannel(	lBinnaryFileChannel,
																																						mNextFreeTypePosition);

			lBinnaryFileChannel.force(false);
			lBinnaryFileChannel.close();

			long[] lDimensions = lStackRequest.getDimensions();

			final String lDimensionsString = Arrays.toString(lDimensions);

			// the '2, ' part is to be compatible with the old format, that means 2
			// bytes per voxel:
			final String lTruncatedDimensionsString = "2, " + lDimensionsString.substring(1,
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
