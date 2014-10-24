package rtlib.stack.server;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Scanner;

import rtlib.core.memory.SizeOf;
import rtlib.core.recycling.Recycler;
import rtlib.core.units.Magnitudes;
import rtlib.stack.Stack;
import rtlib.stack.StackRequest;

public class LocalFileStackSource<T> extends LocalFileStackBase	implements
																																StackSourceInterface<T>,
																																Closeable
{

	private Recycler<Stack<T>, StackRequest<T>> mStackRecycler;

	public LocalFileStackSource(final Recycler<Stack<T>, StackRequest<T>> pStackRecycler,
															final File pRootFolder,
															final String pName) throws IOException
	{
		super(pRootFolder, pName, true);
		mStackRecycler = pStackRecycler;
		mMetaDataVariableBundleAsFile.read();
		update();
	}

	@Override
	public long getNumberOfStacks()
	{
		return super.getNumberOfStacks();
	}

	@Override
	public void setStackRecycler(final Recycler<Stack<T>, StackRequest<T>> pStackRecycler)
	{
		mStackRecycler = pStackRecycler;

	}

	@Override
	public Stack<T> getStack(final long pStackIndex)
	{
		if (mStackRecycler == null)
		{
			return null;
		}
		try
		{
			final long lPositionInFileInBytes = mStackIndexToBinaryFilePositionMap.get(pStackIndex);

			@SuppressWarnings("unchecked")
			final StackRequest<T> lStackRequest = (StackRequest<T>) mStackIndexToStackRequestMap.get(pStackIndex);

			final Stack<T> lStack = mStackRecycler.failOrRequestRecyclableObject(lStackRequest);

			final FileChannel lBinarylFileChannel = getFileChannelForBinaryFile(true,
																																					true);
			lStack.getNDArray()
						.readBytesFromFileChannel(lBinarylFileChannel,
																			lPositionInFileInBytes,
																			lStack.getNDArray()
																						.getSizeInBytes());
			lBinarylFileChannel.close();

			final double lTimeStampInSeconds = mStackIndexToTimeStampInSecondsMap.get(pStackIndex);
			lStack.setTimeStampInNanoseconds((long) Magnitudes.unit2nano(lTimeStampInSeconds));
			lStack.setStackIndex(pStackIndex);

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

				final int lBytesPerVoxel = Integer.parseInt(lDimensionsStringArray[0]);
				final Class<?> lType = SizeOf.integralTypeFromSize(	lBytesPerVoxel,
																														false);
				final long lWidth = Long.parseLong(lDimensionsStringArray[1]);
				final long lHeight = Long.parseLong(lDimensionsStringArray[2]);
				final long lDepth = Long.parseLong(lDimensionsStringArray[3]);

				final StackRequest<?> lStackRequest = StackRequest.build(	lType,
																															1,
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
