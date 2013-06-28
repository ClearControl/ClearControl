package stackserver;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Scanner;

import stack.Stack;

import ndarray.InterfaceNDArray;

public class LocalFileStackSource extends LocalFileStackBase implements
																														StackSourceInterface,
																														Closeable
{

	public LocalFileStackSource(final File pRootFolder,
															final String pName) throws IOException
	{
		super(pRootFolder, pName, true);
		mVariableBundleAsFile.read();
	}

	@Override
	public long getNumberOfStacks()
	{
		return super.getNumberOfStacks();
	}

	@Override
	public Stack getStack(final long pStackIndex, final Stack pStack)
	{

		try
		{
			final long lPositionInFileInType = mStackIndexToBinaryFilePositionMap.get(pStackIndex);

			pStack.ndarray.readFromFileChannel(	mBinaryFileChannel,
																					lPositionInFileInType,
																					pStack.ndarray.getArrayLength());

			long lTimeStampNs = mStackIndexToTimeStampInNanosecondsMap.get(pStackIndex);
			pStack.timestampns = lTimeStampNs;
			pStack.index = pStackIndex;

			return pStack;
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			return null;
		}

	}

	public boolean update()
	{
		try
		{
			mVariableBundleAsFile.read();

			final Scanner lIndexFileScanner = new Scanner(mIndexFile);

			while (lIndexFileScanner.hasNextLine())
			{
				final String lLine = lIndexFileScanner.nextLine();
				final String[] lSplittedLine = lLine.split("\t", -1);
				final long lStackIndex = Long.parseLong(lSplittedLine[0]);
				final long lTimeStamp = Long.parseLong(lSplittedLine[1]);
				final long lPositionInFile = Long.parseLong(lSplittedLine[2]);
				mStackIndexToTimeStampInNanosecondsMap.put(	lStackIndex,
																										lTimeStamp);
				mStackIndexToBinaryFilePositionMap.put(	lStackIndex,
																								lPositionInFile);
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

	@Override
	public void close() throws IOException
	{
		mBinaryFileChannel.force(true);
		mBinaryFileChannel.close();

		mVariableBundleAsFile.close();
	}

}
