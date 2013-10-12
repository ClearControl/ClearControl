package stackserver;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Scanner;

import recycling.Recycler;
import stack.Stack;
import units.Units;

public class LocalFileStackSource extends LocalFileStackBase implements
																														StackSourceInterface,
																														Closeable
{

	private Recycler<Stack> mStackRecycler;

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
	public void setStackRecycler(final Recycler<Stack> pStackRecycler)
	{
		mStackRecycler = pStackRecycler;

	}

	@Override
	public Stack getStack(final long pStackIndex)
	{
		if (mStackRecycler == null)
		{
			return null;
		}
		try
		{
			final long lPositionInFileInType = mStackIndexToBinaryFilePositionMap.get(pStackIndex);

			final int[] lStackDimensions = mStackIndexToStackDimensionsMap.get(pStackIndex);

			final Stack lStack = mStackRecycler.requestRecyclableObject(lStackDimensions);

			final FileChannel lBinarylFileChannel = getFileChannelForBinaryFile(true);
			lStack.mNDimensionalArray.readFromFileChannel(lBinarylFileChannel,
																										lPositionInFileInType,
																										lStack.mNDimensionalArray.getArrayLength());
			lBinarylFileChannel.close();

			final double lTimeStampInSeconds = mStackIndexToTimeStampInSecondsMap.get(pStackIndex);
			lStack.mTimeStampInNanoseconds = (long) Units.unit2nano(lTimeStampInSeconds);
			lStack.mStackIndex = pStackIndex;

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
				final int[] lDimensions = convertStringArrayToIntArray(lDimensionsStringArray);
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

	private int[] convertStringArrayToIntArray(final String[] pStringArray)
	{
		final int[] lIntArray = new int[pStringArray.length];
		for (int i = 0; i < pStringArray.length; i++)
		{
			lIntArray[i] = Integer.parseInt(pStringArray[i].trim());
		}
		return lIntArray;
	}

	@Override
	public void close() throws IOException
	{
		mMetaDataVariableBundleAsFile.close();
	}

}
