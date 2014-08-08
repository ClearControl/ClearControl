package rtlib.stack.server;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Scanner;

import rtlib.core.recycling.Recycler;
import rtlib.core.units.Magnitudes;
import rtlib.stack.Stack;

public class LocalFileStackSource<O> extends LocalFileStackBase	implements
																														StackSourceInterface<O>,
																														Closeable
{

	private Recycler<Stack<O>, Long> mStackRecycler;

	public LocalFileStackSource(final File pRootFolder,
															final String pName) throws IOException
	{
		super(pRootFolder, pName, true);
		mMetaDataVariableBundleAsFile.read();
	}

	@Override
	public long getNumberOfStacks()
	{
		return super.getNumberOfStacks();
	}

	@Override
	public void setStackRecycler(final Recycler<Stack<O>, Long> pStackRecycler)
	{
		mStackRecycler = pStackRecycler;

	}

	@Override
	public Stack<O> getStack(final long pStackIndex)
	{
		if (mStackRecycler == null)
		{
			return null;
		}
		try
		{
			final long lPositionInFileInBytes = mStackIndexToBinaryFilePositionMap.get(pStackIndex);

			final Long[] lStackDimensions = mStackIndexToStackDimensionsMap.get(pStackIndex);

			final Stack<O> lStack = mStackRecycler.failOrRequestRecyclableObject(lStackDimensions);

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
				final Long[] lDimensions = convertStringArrayToLongArray(lDimensionsStringArray);
				final long lPositionInFile = Long.parseLong(lSplittedLine[3].trim());
				mStackIndexToTimeStampInSecondsMap.put(	lStackIndex,
																								lTimeStampInSeconds);
				mStackIndexToBinaryFilePositionMap.put(	lStackIndex,
																								lPositionInFile);
				mStackIndexToStackDimensionsMap.put(lStackIndex, lDimensions);
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
