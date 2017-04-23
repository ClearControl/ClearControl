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
import clearcontrol.stack.sourcesink.FileStackBase;
import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclerInterface;

/**
 * Raw file stack source
 *
 * @author royer
 */
public class RawFileStackSource extends FileStackBase implements
                                StackSourceInterface,
                                AutoCloseable
{

  private RecyclerInterface<StackInterface, StackRequest> mStackRecycler;

  /**
   * Instantiates a raw file stack source
   * 
   * @param pStackRecycler
   *          stack recycler
   * @throws IOException
   *           thrown if there is an IO problem
   */
  public RawFileStackSource(final BasicRecycler<StackInterface, StackRequest> pStackRecycler) throws IOException
  {
    super(true);
    mStackRecycler = pStackRecycler;
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
    mStackRecycler = pStackRecycler;

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
    if (mStackRecycler == null)
    {
      return null;
    }
    try
    {

      final StackRequest lStackRequest =
                                       mStackIndexToStackRequestMap.get(pStackIndex);

      final StackInterface lStack =
                                  mStackRecycler.getOrWait(pTime,
                                                           pTimeUnit,
                                                           lStackRequest);

      String lFileName = String.format("tp%d.raw", pStackIndex);
      File lFile = new File(mStacksFolder, lFileName);

      if (!lFile.exists())
        return null;

      FileChannel lBinnaryFileChannel = getFileChannel(lFile, false);

      if (lStack.getContiguousMemory() != null)
        lStack.getContiguousMemory()
              .readBytesFromFileChannel(lBinnaryFileChannel,
                                        0,
                                        lStack.getSizeInBytes());
      else
        lStack.getFragmentedMemory()
              .readBytesFromFileChannel(lBinnaryFileChannel,
                                        0,
                                        lStack.getSizeInBytes());

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

        final long lWidth = Long.parseLong(lDimensionsStringArray[0]);
        final long lHeight =
                           Long.parseLong(lDimensionsStringArray[1]);
        final long lDepth = Long.parseLong(lDimensionsStringArray[2]);

        final StackRequest lStackRequest = StackRequest.build(lWidth,
                                                              lHeight,
                                                              lDepth);

        mStackIndexToTimeStampInSecondsMap.put(lStackIndex,
                                               lTimeStampInSeconds);
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
