package clearcontrol.stack.sourcesink.sink;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import clearcontrol.core.units.OrderOfMagnitude;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import clearcontrol.stack.metadata.StackMetaData;
import clearcontrol.stack.sourcesink.FileStackBase;
import clearcontrol.stack.sourcesink.FileStackInterface;
import clearcontrol.stack.sourcesink.StackSinkSourceInterface;
import coremem.fragmented.FragmentedMemoryInterface;

/**
 * Raw file stack sink
 *
 * @author royer
 */
public class RawFileStackSink extends FileStackBase implements
                              FileStackInterface,
                              FileStackSinkInterface,
                              AutoCloseable
{

  private final AtomicLong mFirstTimePointAbsoluteNanoSeconds =
                                                              new AtomicLong();
  private final ConcurrentHashMap<String, AtomicLong> mNextFreeStackIndexMap =
                                                                             new ConcurrentHashMap<>();

  /**
   * Instantiates a raw file stack sink.
   * 
   */
  public RawFileStackSink()
  {
    super(false);
  }

  @Override
  public boolean appendStack(StackInterface pStack)
  {
    return appendStack(cDefaultChannel, pStack);
  }

  @Override
  public boolean appendStack(String pChannel,
                             final StackInterface pStack)
  {

    try
    {
      AtomicLong lNextFreeStackIndex = getIndexForChannel(pChannel);

      writeStackData(lNextFreeStackIndex.get(), pChannel, pStack);
      writeIndexFileEntry(lNextFreeStackIndex.get(),
                          pChannel,
                          pStack);
      writeMetaDataFileEntry(pChannel, pStack);

      setStackRequest(pChannel,
                      lNextFreeStackIndex.get(),
                      StackRequest.buildFrom(pStack));
      lNextFreeStackIndex.incrementAndGet();
      return true;
    }
    catch (final Throwable e)
    {
      e.printStackTrace();
      return false;
    }
  }

  protected AtomicLong getIndexForChannel(String pChannel)
  {
    AtomicLong lNextFreeStackIndex =
                                   mNextFreeStackIndexMap.get(pChannel);
    if (lNextFreeStackIndex == null)
    {
      lNextFreeStackIndex = new AtomicLong(0);
      mNextFreeStackIndexMap.put(pChannel, lNextFreeStackIndex);
    }
    return lNextFreeStackIndex;
  }

  protected void writeStackData(long pIndex,
                                String pChannel,
                                final StackInterface pStack) throws IOException
  {
    String lFileName = String.format(StackSinkSourceInterface.cFormat,
                                     pIndex);
    File lFile = new File(getChannelFolder(pChannel), lFileName);
    FileChannel lBinnaryFileChannel = getFileChannel(lFile, false);
    FragmentedMemoryInterface lFragmentedMemory =
                                                pStack.getFragmentedMemory();

    lFragmentedMemory.writeBytesToFileChannel(lBinnaryFileChannel, 0);

    lBinnaryFileChannel.force(false);
    lBinnaryFileChannel.close();
  }

  protected void writeIndexFileEntry(long pIndex,
                                     String pChannel,
                                     final StackInterface pStack) throws IOException
  {
    long[] lDimensions = pStack.getDimensions();

    final String lDimensionsString = Arrays.toString(lDimensions);

    final FileChannel lIndexFileChannel =
                                        getFileChannel(getIndexFile(pChannel),

                                                       false);

    long lTimeStampInNanoseconds;

    if (pStack.getMetaData() != null
        && pStack.getMetaData().getTimeStampInNanoseconds() != null)
      lTimeStampInNanoseconds = pStack.getMetaData()
                                      .getTimeStampInNanoseconds();
    else
      lTimeStampInNanoseconds = System.nanoTime();

    if (pIndex == 0)
      mFirstTimePointAbsoluteNanoSeconds.set(lTimeStampInNanoseconds);

    final double lTimeStampInSeconds =
                                     OrderOfMagnitude.nano2unit(lTimeStampInNanoseconds
                                                                - mFirstTimePointAbsoluteNanoSeconds.get());

    setStackTimeStampInSeconds(pChannel, pIndex, lTimeStampInSeconds);

    final String lIndexLineString =
                                  String.format("%d\t%.4f\t%s\n",
                                                pIndex,
                                                lTimeStampInSeconds,
                                                lDimensionsString.substring(1,
                                                                            lDimensionsString.length()
                                                                               - 1));
    final byte[] lIndexLineStringBytes = lIndexLineString.getBytes();
    final ByteBuffer lIndexLineStringByteBuffer =
                                                ByteBuffer.wrap(lIndexLineStringBytes);
    lIndexFileChannel.write(lIndexLineStringByteBuffer);
    lIndexFileChannel.force(true);
    lIndexFileChannel.close();
  }

  protected void writeMetaDataFileEntry(String pChannel,
                                        final StackInterface pStack) throws IOException
  {
    final FileChannel lMetaDataFileChannel =
                                           getFileChannel(getMetadataFile(pChannel),
                                                          false);

    StackMetaData lMetaData = pStack.getMetaData();

    final String lMetaDataString = lMetaData.toString() + "\n";
    final byte[] lMetaDataStringBytes = lMetaDataString.getBytes();
    final ByteBuffer lMetaDataStringByteBuffer =
                                               ByteBuffer.wrap(lMetaDataStringBytes);
    lMetaDataFileChannel.write(lMetaDataStringByteBuffer);
    lMetaDataFileChannel.force(true);
    lMetaDataFileChannel.close();
  }

  @Override
  public void close() throws IOException
  {
    super.close();
  }

}
