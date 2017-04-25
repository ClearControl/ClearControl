package clearcontrol.stack.sourcesink.sink;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

import clearcontrol.core.units.Magnitude;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import clearcontrol.stack.metadata.StackMetaData;
import clearcontrol.stack.sourcesink.FileStackBase;
import coremem.fragmented.FragmentedMemoryInterface;

/**
 * Raw file stack sink
 *
 * @author royer
 */
public class RawFileStackSink extends FileStackBase implements
                              FileStackSinkInterface,
                              AutoCloseable
{

  private final AtomicLong mFirstTimePointAbsoluteNanoSeconds =
                                                              new AtomicLong();
  private final AtomicLong mNextFreeStackIndex = new AtomicLong();

  /**
   * Instantiates a raw file stack sink given a root folder and dataset name.
   * 
   * @throws IOException
   *           thrown if there is an IO problems
   */
  public RawFileStackSink() throws IOException
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

      writeStackData(pChannel, pStack);
      writeIndexFileEntry(pChannel, pStack);
      writeMetaDataFileEntry(pChannel, pStack);

      setStackRequest(pChannel,
                      mNextFreeStackIndex.get(),
                      StackRequest.buildFrom(pStack));
      mNextFreeStackIndex.incrementAndGet();
      return true;
    }
    catch (final Throwable e)
    {
      e.printStackTrace();
      return false;
    }
  }

  protected void writeStackData(String pChannel,
                                final StackInterface pStack) throws IOException
  {
    String lFileName = String.format("tp%d.raw",
                                     mNextFreeStackIndex.get());
    File lFile = new File(getChannelFolder(pChannel), lFileName);
    FileChannel lBinnaryFileChannel = getFileChannel(lFile, false);
    FragmentedMemoryInterface lFragmentedMemory =
                                                pStack.getFragmentedMemory();

    lFragmentedMemory.writeBytesToFileChannel(lBinnaryFileChannel, 0);

    lBinnaryFileChannel.force(false);
    lBinnaryFileChannel.close();
  }

  protected void writeIndexFileEntry(String pChannel,
                                     final StackInterface pStack) throws IOException
  {
    long[] lDimensions = pStack.getDimensions();

    final String lDimensionsString = Arrays.toString(lDimensions);

    final FileChannel lIndexFileChannel =
                                        getFileChannel(getIndexFile(pChannel),
                                                       false);

    if (mNextFreeStackIndex.get() == 0)
    {
      mFirstTimePointAbsoluteNanoSeconds.set(pStack.getMetaData()
                                                   .getTimeStampInNanoseconds());
    }
    final double lTimeStampInSeconds =
                                     Magnitude.nano2unit(pStack.getMetaData()
                                                               .getTimeStampInNanoseconds()
                                                         - mFirstTimePointAbsoluteNanoSeconds.get());

    setStackTimeStampInSeconds(pChannel,
                               mNextFreeStackIndex.get(),
                               lTimeStampInSeconds);

    final String lIndexLineString =
                                  String.format("%d\t%.4f\t%s\n",
                                                mNextFreeStackIndex.get(),
                                                lTimeStampInSeconds,
                                                lDimensionsString.substring(1,
                                                                            lDimensionsString.length()
                                                                               - 2));
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
