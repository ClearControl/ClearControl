package clearcontrol.stack.sourcesink.source;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.units.Magnitude;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import clearcontrol.stack.sourcesink.LocalFileStackBase;
import coremem.ContiguousMemoryInterface;
import coremem.fragmented.FragmentedMemoryInterface;
import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclerInterface;

/**
 * Local file stack source
 *
 * @author royer
 */
public class LocalFileStackSource extends LocalFileStackBase
                                  implements
                                  StackSourceInterface,
                                  AutoCloseable
{

  private static final long cSingleReadLimit = 64_000_000;
  private RecyclerInterface<StackInterface, StackRequest> mStackBasicRecycler;
  private FileChannel mBinarylFileChannel;

  /**
   * Instanciates a local file stack source
   * 
   * @param pStackRecycler
   *          stack recycler
   * @param pRootFolder
   *          root folder
   * @param pName
   *          name
   * @throws IOException
   *           thrown if there is an IO problem
   */
  public LocalFileStackSource(final BasicRecycler<StackInterface, StackRequest> pStackRecycler,
                              final File pRootFolder,
                              final String pName) throws IOException
  {
    super(pRootFolder, pName, true);
    mStackBasicRecycler = pStackRecycler;
    update();
  }

  @Override
  public long getNumberOfStacks()
  {
    return super.getNumberOfStacks();
  }

  @Override
  public void setStackRecycler(final RecyclerInterface<StackInterface, StackRequest> pStackRecycler)
  {
    mStackBasicRecycler = pStackRecycler;

  }

  @Override
  public StackInterface getStack(final long pStackIndex)
  {
    return getStack(pStackIndex, 1, TimeUnit.NANOSECONDS);
  }

  @Override
  public StackInterface getStack(final long pStackIndex,
                                 long pTime,
                                 TimeUnit pTimeUnit)
  {
    if (mStackBasicRecycler == null)
    {
      return null;
    }
    try
    {
      final long lPositionInFileInBytes =
                                        mStackIndexToBinaryFilePositionMap.get(pStackIndex);

      final StackRequest lStackRequest =
                                       mStackIndexToStackRequestMap.get(pStackIndex);

      final StackInterface lStack =
                                  mStackBasicRecycler.getOrWait(pTime,
                                                                pTimeUnit,
                                                                lStackRequest);

      mBinarylFileChannel = getFileChannelForBinaryFile(true, true);

      if (lStack.getContiguousMemory() != null)
        lStack.getContiguousMemory()
              .readBytesFromFileChannel(mBinarylFileChannel,
                                        lPositionInFileInBytes,
                                        lStack.getSizeInBytes());
      else
      {
        FragmentedMemoryInterface lFragmentedMemory =
                                                    lStack.getFragmentedMemory();
        if (lStack.getSizeInBytes() > cSingleReadLimit)
        {
          int lNumberOfFragments =
                                 lFragmentedMemory.getNumberOfFragments();

          long lPosition = lPositionInFileInBytes;

          for (int i = 0; i < lNumberOfFragments; i++)
          {
            ContiguousMemoryInterface lContiguousMemoryInterface =
                                                                 lFragmentedMemory.get(i);

            long lSizeInBytes =
                              lContiguousMemoryInterface.getSizeInBytes();
            lContiguousMemoryInterface.readBytesFromFileChannel(mBinarylFileChannel,
                                                                lPosition,
                                                                lSizeInBytes);

            lPosition += lSizeInBytes;
          }

        }
        else
          lFragmentedMemory.readBytesFromFileChannel(mBinarylFileChannel,
                                                     lPositionInFileInBytes,
                                                     lStack.getSizeInBytes());

      }

      final double lTimeStampInSeconds =
                                       mStackIndexToTimeStampInSecondsMap.get(pStackIndex);
      lStack.getMetaData()
            .setTimeStampInNanoseconds((long) Magnitude.unit2nano(lTimeStampInSeconds));
      lStack.getMetaData().setIndex(pStackIndex);

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

      final Scanner lIndexFileScanner = new Scanner(mIndexFile);

      while (lIndexFileScanner.hasNextLine())
      {
        final String lLine = lIndexFileScanner.nextLine();
        final String[] lSplittedLine = lLine.split("\t", -1);
        final long lStackIndex =
                               Long.parseLong(lSplittedLine[0].trim());
        final double lTimeStampInSeconds =
                                         Double.parseDouble(lSplittedLine[1].trim());
        final String[] lDimensionsStringArray =
                                              lSplittedLine[2].split(", ");

        final long lWidth = Long.parseLong(lDimensionsStringArray[1]);
        final long lHeight =
                           Long.parseLong(lDimensionsStringArray[2]);
        final long lDepth = Long.parseLong(lDimensionsStringArray[3]);

        final StackRequest lStackRequest = StackRequest.build(lWidth,
                                                              lHeight,
                                                              lDepth);

        final long lPositionInFile =
                                   Long.parseLong(lSplittedLine[3].trim());
        mStackIndexToTimeStampInSecondsMap.put(lStackIndex,
                                               lTimeStampInSeconds);
        mStackIndexToBinaryFilePositionMap.put(lStackIndex,
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

  @Override
  public void close() throws IOException
  {

  }

}
