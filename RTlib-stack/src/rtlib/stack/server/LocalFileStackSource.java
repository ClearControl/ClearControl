package rtlib.stack.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;
import rtlib.core.units.Magnitudes;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import coremem.recycling.BasicRecycler;

public class LocalFileStackSource<T extends NativeType<T>, A extends ArrayDataAccess<A>>	extends
																																													LocalFileStackBase<T, A> implements
																																																									StackSourceInterface<T, A>,
																																																									AutoCloseable
{

	private BasicRecycler<StackInterface<T, A>, StackRequest<T>> mStackBasicRecycler;

	public LocalFileStackSource(T pType,
															final BasicRecycler<StackInterface<T, A>, StackRequest<T>> pStackRecycler,
															final File pRootFolder,
															final String pName) throws IOException
	{
		super(pType, pRootFolder, pName, true);
		mStackBasicRecycler = pStackRecycler;
		mMetaDataVariableBundleAsFile.read();
		update();
	}

	@Override
	public long getNumberOfStacks()
	{
		return super.getNumberOfStacks();
	}

	@Override
	public void setStackRecycler(final BasicRecycler<StackInterface<T, A>, StackRequest<T>> pStackRecycler)
	{
		mStackBasicRecycler = pStackRecycler;

	}

	@Override
	public StackInterface<T, A> getStack(final long pStackIndex)
	{
		return getStack(pStackIndex, 1, TimeUnit.NANOSECONDS);
	}

	@Override
	public StackInterface<T, A> getStack(	final long pStackIndex,
																				long pTime,
																				TimeUnit pTimeUnit)
	{
		if (mStackBasicRecycler == null)
		{
			return null;
		}
		try
		{
			final long lPositionInFileInBytes = mStackIndexToBinaryFilePositionMap.get(pStackIndex);

			final StackRequest<T> lStackRequest = mStackIndexToStackRequestMap.get(pStackIndex);

			final StackInterface<T, A> lStack = mStackBasicRecycler.getOrWait(pTime,
																																				pTimeUnit,
																																				lStackRequest);

			final FileChannel lBinarylFileChannel = getFileChannelForBinaryFile(true,
																																					true);

			if (lStack.getContiguousMemory() != null)
				lStack.getContiguousMemory()
							.readBytesFromFileChannel(lBinarylFileChannel,
																				lPositionInFileInBytes,
																				lStack.getSizeInBytes());
			else
				lStack.getFragmentedMemory()
							.readBytesFromFileChannel(lBinarylFileChannel,
																				lPositionInFileInBytes,
																				lStack.getSizeInBytes());
			lBinarylFileChannel.close();

			final double lTimeStampInSeconds = mStackIndexToTimeStampInSecondsMap.get(pStackIndex);
			lStack.setTimeStampInNanoseconds((long) Magnitudes.unit2nano(lTimeStampInSeconds));
			lStack.setIndex(pStackIndex);

			return lStack;
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public boolean update()
	{
		try
		{
			mMetaDataVariableBundleAsFile.read();

			final Scanner lIndexFileScanner = new Scanner(mIndexFile);

			while (lIndexFileScanner.hasNextLine())
			{
				final String lLine = lIndexFileScanner.nextLine();
				final String[] lSplittedLine = lLine.split("\t", -1);
				final long lStackIndex = Long.parseLong(lSplittedLine[0].trim());
				final double lTimeStampInSeconds = Double.parseDouble(lSplittedLine[1].trim());
				final String[] lDimensionsStringArray = lSplittedLine[2].split(", ");

				final long lWidth = Long.parseLong(lDimensionsStringArray[1]);
				final long lHeight = Long.parseLong(lDimensionsStringArray[2]);
				final long lDepth = Long.parseLong(lDimensionsStringArray[3]);

				final StackRequest<T> lStackRequest = StackRequest.build(	mType,
																																	lWidth,
																																	lHeight,
																																	lDepth);

				final long lPositionInFile = Long.parseLong(lSplittedLine[3].trim());
				mStackIndexToTimeStampInSecondsMap.put(	lStackIndex,
																								lTimeStampInSeconds);
				mStackIndexToBinaryFilePositionMap.put(	lStackIndex,
																								lPositionInFile);
				mStackIndexToStackRequestMap.put(lStackIndex, lStackRequest);
			}

			lIndexFileScanner.close();
			return true;
		}
		catch (final FileNotFoundException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	private Long[] convertStringArrayToLongArray(final String[] pStringArray)
	{
		final Long[] lIntArray = new Long[pStringArray.length];
		for (int i = 0; i < pStringArray.length; i++)
		{
			lIntArray[i] = Long.parseLong(pStringArray[i].trim());
		}
		return lIntArray;
	}

	@Override
	public void close() throws IOException
	{
		mMetaDataVariableBundleAsFile.close();
	}

}
