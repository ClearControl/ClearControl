package clearcontrol.stack.sourcesink.source;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.units.Magnitude;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import clearcontrol.stack.sourcesink.FileStackBase;
import clearcontrol.stack.sourcesink.StackSinkSourceInterface;
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
  }

  @Override
  public void setLocation(File pRootFolder, String pName)
  {
    super.setLocation(pRootFolder, pName);
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
  public StackInterface getStack(long pStackIndex)
  {
    return getStack(cDefaultChannel, pStackIndex);
  }

  @Override
  public StackInterface getStack(final String pChannel,
                                 final long pStackIndex)
  {
    return getStack(pChannel, pStackIndex, 1, TimeUnit.NANOSECONDS);
  }

  @Override
  public StackInterface getStack(final String pChannel,
                                 final long pStackIndex,
                                 final long pTime,
                                 final TimeUnit pTimeUnit)
  {
    if (mStackRecycler == null)
    {
      return null;
    }
    try
    {

      final StackRequest lStackRequest = getStackRequest(pChannel,
                                                         pStackIndex);

      final StackInterface lStack =
                                  mStackRecycler.getOrWait(pTime,
                                                           pTimeUnit,
                                                           lStackRequest);

      String lFileName =
                       String.format(StackSinkSourceInterface.cFormat,
                                     pStackIndex);
      File lFile = new File(getChannelFolder(pChannel), lFileName);

      if (!lFile.exists())
        return null;

      FileChannel lBinnaryFileChannel = getFileChannel(lFile, true);

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
                                       getStackTimeStampInSeconds(pChannel,
                                                                  pStackIndex);
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
      clear();

      ArrayList<String> lChannelList = getCurrentChannelList();

      for (String lChannel : lChannelList)
      {
        final Scanner lIndexFileScanner =
                                        new Scanner(getIndexFile(lChannel));

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

          final long lWidth =
                            Long.parseLong(lDimensionsStringArray[0]);
          final long lHeight =
                             Long.parseLong(lDimensionsStringArray[1]);
          final long lDepth =
                            Long.parseLong(lDimensionsStringArray[2]);

          final StackRequest lStackRequest =
                                           StackRequest.build(lWidth,
                                                              lHeight,
                                                              lDepth);

          setStackTimeStampInSeconds(lChannel,
                                     lStackIndex,
                                     lTimeStampInSeconds);
          setStackRequest(lChannel, lStackIndex, lStackRequest);
        }

        lIndexFileScanner.close();
      }

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
