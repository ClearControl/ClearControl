package clearcontrol.stack.sourcesink.source;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.units.OrderOfMagnitude;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import clearcontrol.stack.metadata.StackMetaData;
import clearcontrol.stack.sourcesink.FileStackBase;
import clearcontrol.stack.sourcesink.StackSinkSourceInterface;
import coremem.offheap.OffHeapMemory;
import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclerInterface;

import org.bridj.CLong;
import org.bridj.Pointer;
import sqeazy.bindings.SqeazyLibrary;

/**
 * Sqeazy file stack source
 *
 * @author steinbac
 */
public class SqeazyFileStackSource extends FileStackBase implements
                                   FileStackSourceInterface,
                                   AutoCloseable
{

  private RecyclerInterface<StackInterface, StackRequest> mStackRecycler;

  private OffHeapMemory mCompressedBytes;

  /**
   * Instantiates a raw file stack source
   * 
   * @param pStackRecycler
   *          stack recycler
   * 
   */
  public SqeazyFileStackSource(final BasicRecycler<StackInterface, StackRequest> pStackRecycler)
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

  @SuppressWarnings("unchecked")
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
                       String.format(StackSinkSourceInterface.cBasename
                                     + StackSinkSourceInterface.cSqeazyFileExtension,
                                     pStackIndex);

      File lFile = new File(getChannelFolder(pChannel), lFileName);

      if (!lFile.exists())
        return null;

      FileChannel lBinaryFileChannel = getFileChannel(lFile, true);
      final long lCompressedDataLength = lBinaryFileChannel.size();

      // we could be smarter here and only reallocate if we need a bigger
      // buffer....
      if (mCompressedBytes == null
          || mCompressedBytes.getSizeInBytes() != lCompressedDataLength)
        mCompressedBytes =
                         OffHeapMemory.allocateBytes(lCompressedDataLength);

      mCompressedBytes.readBytesFromFileChannel(lBinaryFileChannel,
                                                0,
                                                lCompressedDataLength);

      // checking the decoded size
      final Pointer<CLong> lPointerToDestinationLength =
                                                       Pointer.allocateCLong();

      SqeazyLibrary.SQY_Decompressed_Length((Pointer<Byte>) mCompressedBytes.getBridJPointer(Byte.class),
                                            lPointerToDestinationLength);

      // decompress into lDecodedBytes
      final Pointer<Byte> lDecodedBytes =
                                        lStack.getContiguousMemory()
                                              .getBridJPointer(Byte.class);
      final int lReturnValue =
                             SqeazyLibrary.SQY_Decode_UI16(mCompressedBytes.getBridJPointer(Byte.class),
                                                           lCompressedDataLength,
                                                           lDecodedBytes,
                                                           1);

      if (lReturnValue != 0)
        throw new RuntimeException("Error while peforming sqy compression, error code:  "
                                   + lReturnValue);

      // NOte: we don't need to write 'fragmented memory' as this is a just used
      // as a view into a contiguous buffer...

      final double lTimeStampInSeconds =
                                       getStackTimeStampInSeconds(pChannel,
                                                                  pStackIndex);
      lStack.getMetaData()
            .setTimeStampInNanoseconds((long) OrderOfMagnitude.unit2nano(lTimeStampInSeconds));
      lStack.getMetaData().setIndex(pStackIndex);

      lStack.getMetaData()
            .addAll(getStackMetaData(pChannel, pStackIndex));

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

      ArrayList<String> lChannelList = getChannelList();

      for (String lChannel : lChannelList)
      {
        readIndexFile(lChannel);
        readMetaDataFile(lChannel);
      }

      return true;
    }
    catch (final FileNotFoundException e)
    {
      e.printStackTrace();
      return false;
    }
  }

  protected void readMetaDataFile(String lChannel) throws FileNotFoundException
  {
    final Scanner lMetaDataFileScanner =
                                       new Scanner(getMetadataFile(lChannel));

    int lStackIndex = 0;
    while (lMetaDataFileScanner.hasNextLine())
    {
      final String lLine = lMetaDataFileScanner.nextLine();

      StackMetaData lStackMetaData = new StackMetaData();

      lStackMetaData.fromString(lLine);

      setStackMetaData(lChannel, lStackIndex, lStackMetaData);

      lStackIndex++;
    }

    lMetaDataFileScanner.close();
  }

  protected void readIndexFile(String lChannel) throws FileNotFoundException
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

      final long lWidth = Long.parseLong(lDimensionsStringArray[0]);
      final long lHeight = Long.parseLong(lDimensionsStringArray[1]);
      final long lDepth = Long.parseLong(lDimensionsStringArray[2]);

      final StackRequest lStackRequest = StackRequest.build(lWidth,
                                                            lHeight,
                                                            lDepth);

      setStackTimeStampInSeconds(lChannel,
                                 lStackIndex,
                                 lTimeStampInSeconds);
      setStackRequest(lChannel, lStackIndex, lStackRequest);
    }

    lIndexFileScanner.close();
  }

  @Override
  public void close() throws IOException
  {

  }

}
