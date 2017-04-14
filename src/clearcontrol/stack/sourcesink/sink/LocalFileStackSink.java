package clearcontrol.stack.sourcesink.sink;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import clearcontrol.core.units.Magnitude;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import clearcontrol.stack.sourcesink.LocalFileStackBase;
import coremem.ContiguousMemoryInterface;
import coremem.fragmented.FragmentedMemoryInterface;

/**
 * Local file stack sink
 *
 * @author royer
 */
public class LocalFileStackSink extends LocalFileStackBase implements
                                StackSinkInterface,
                                AutoCloseable
{

  private static final long cSingleWriteLimit = 64_000_000L;
  private volatile long mFirstTimePointAbsoluteNanoSeconds;
  private FileChannel mBinnaryFileChannel;

  /**
   * Instanciates a local file stack sink given a root folder and dataset name.
   * 
   * @param pRootFolder
   *          root folder
   * @param pName
   *          name
   * @throws IOException
   *           thrown if there is an IO problems
   */
  public LocalFileStackSink(final File pRootFolder,
                            final String pName) throws IOException
  {
    super(pRootFolder, pName, false);

  }

  @Override
  public boolean appendStack(final StackInterface pStack)
  {

    try
    {

      mStackIndexToBinaryFilePositionMap.put(mNextFreeStackIndex,
                                             mNextFreeTypePosition);

      final StackRequest lStackRequest =
                                       StackRequest.buildFrom(pStack);

      mStackIndexToStackRequestMap.put(mNextFreeStackIndex,
                                       lStackRequest);

      if (mBinnaryFileChannel == null)
        mBinnaryFileChannel =
                            getFileChannelForBinaryFile(false, true);

      long lSizeInBytes = pStack.getSizeInBytes();

      FragmentedMemoryInterface lFragmentedMemory =
                                                  pStack.getFragmentedMemory();

      final long lNewNextFreeTypePosition;

      if (lSizeInBytes > cSingleWriteLimit)
      {
        int lNumberOfFragments =
                               lFragmentedMemory.getNumberOfFragments();

        long lPosition = mNextFreeTypePosition;
        for (int i = 0; i < lNumberOfFragments; i++)
        {
          // System.out.format("chunk: %d \n", i);
          ContiguousMemoryInterface lContiguousMemoryInterface =
                                                               lFragmentedMemory.get(i);

          lContiguousMemoryInterface.writeBytesToFileChannel(mBinnaryFileChannel,
                                                             lPosition);
          mBinnaryFileChannel.force(false);

          lPosition += lContiguousMemoryInterface.getSizeInBytes();
        }

        lNewNextFreeTypePosition = lPosition;

        mBinnaryFileChannel.force(false);
      }
      else
      {
        lNewNextFreeTypePosition =
                                 lFragmentedMemory.writeBytesToFileChannel(mBinnaryFileChannel,
                                                                           mNextFreeTypePosition);
        mBinnaryFileChannel.force(false);
      }

      long[] lDimensions = lStackRequest.getDimensions();

      final String lDimensionsString = Arrays.toString(lDimensions);

      // the '2, ' part is to be compatible with the old format, that
      // means 2
      // bytes per voxel:
      final String lTruncatedDimensionsString = "2, "
                                                + lDimensionsString.substring(1,
                                                                              lDimensionsString.length()
                                                                                 - 1);

      final FileChannel lIndexFileChannel =
                                          FileChannel.open(mIndexFile.toPath(),
                                                           StandardOpenOption.APPEND,
                                                           StandardOpenOption.WRITE,
                                                           StandardOpenOption.CREATE);

      if (mNextFreeStackIndex == 0)
      {
        mFirstTimePointAbsoluteNanoSeconds =
                                           pStack.getMetaData()
                                                 .getTimeStampInNanoseconds();
      }
      final double lTimeStampInSeconds =
                                       Magnitude.nano2unit(pStack.getMetaData()
                                                                 .getTimeStampInNanoseconds()
                                                           - mFirstTimePointAbsoluteNanoSeconds);

      mStackIndexToTimeStampInSecondsMap.put(mNextFreeStackIndex,
                                             lTimeStampInSeconds);

      final String lIndexLineString =
                                    String.format("%d\t%.4f\t%s\t%d\n",
                                                  mNextFreeStackIndex,
                                                  lTimeStampInSeconds,
                                                  lTruncatedDimensionsString,
                                                  mNextFreeTypePosition);
      final byte[] lIndexLineStringBytes =
                                         lIndexLineString.getBytes();
      final ByteBuffer lIndexLineStringByteBuffer =
                                                  ByteBuffer.wrap(lIndexLineStringBytes);
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
    if (mBinnaryFileChannel != null)
      mBinnaryFileChannel.close();
    super.close();
  }

}
