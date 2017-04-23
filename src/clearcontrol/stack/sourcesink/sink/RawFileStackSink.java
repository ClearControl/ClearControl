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
  public boolean appendStack(final StackInterface pStack)
  {

    try
    {

      writeStackData(pStack);
      writeIndexFileEntry(pStack);
      writeMetaDataFileEntry(pStack);

      mStackIndexToStackRequestMap.put(mNextFreeStackIndex.get(),
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

  protected void writeStackData(final StackInterface pStack) throws IOException
  {
    String lFileName = String.format("tp%d.raw",
                                     mNextFreeStackIndex.get());
    File lFile = new File(mStacksFolder, lFileName);
    FileChannel lBinnaryFileChannel = getFileChannel(lFile, false);
    FragmentedMemoryInterface lFragmentedMemory =
                                                pStack.getFragmentedMemory();

    lFragmentedMemory.writeBytesToFileChannel(lBinnaryFileChannel, 0);

    lBinnaryFileChannel.force(false);
    lBinnaryFileChannel.close();
  }

  protected void writeIndexFileEntry(final StackInterface pStack) throws IOException
  {
    long[] lDimensions = pStack.getDimensions();

    final String lDimensionsString = Arrays.toString(lDimensions);

    final FileChannel lIndexFileChannel = getFileChannel(mIndexFile,
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

    mStackIndexToTimeStampInSecondsMap.put(mNextFreeStackIndex.get(),
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

  protected void writeMetaDataFileEntry(final StackInterface pStack) throws IOException
  {
    final FileChannel lMetaDataFileChannel =
                                           getFileChannel(mMetaDataFile,
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
