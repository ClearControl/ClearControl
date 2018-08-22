package clearcontrol.stack.sourcesink.sink;

import java.io.File;
import java.io.IOException;
import java.lang.Runtime;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import clearcontrol.core.units.OrderOfMagnitude;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import clearcontrol.stack.metadata.StackMetaData;
import clearcontrol.stack.sourcesink.FileStackBase;
import clearcontrol.stack.sourcesink.FileStackInterface;
import clearcontrol.stack.sourcesink.StackSinkSourceInterface;
import coremem.offheap.OffHeapMemory;

import org.bridj.CLong;
import org.bridj.Pointer;
import sqeazy.bindings.SqeazyLibrary;

/**
 * Raw file stack sink
 *
 * @author steinbac
 */
public class SqeazyFileStackSink extends FileStackBase implements
                                 FileStackInterface,
                                 FileStackSinkInterface,
                                 AutoCloseable
{

  private final AtomicLong mFirstTimePointAbsoluteNanoSeconds =
                                                              new AtomicLong();
  private final ConcurrentHashMap<String, AtomicLong> mNextFreeStackIndexMap =
                                                                             new ConcurrentHashMap<>();

  private final AtomicReference<String> mPipelineName =
                                                      new AtomicReference<String>("rmestbkrd->lz4");

  private final AtomicInteger mNumThreads =
                                          new AtomicInteger(Runtime.getRuntime()
                                                                   .availableProcessors());

  private OffHeapMemory mCompressedData;

  /**
   * Instantiates a raw file stack sink.
   *
   */
  public SqeazyFileStackSink()
  {
    super(false);

  }

  /**
   * Instantiates a raw file stack sink.
   * 
   */
  public SqeazyFileStackSink(String PipelineName, int NumThreads)
  {
    super(false);

    mPipelineName.set(PipelineName);
    mNumThreads.set(NumThreads);
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

    }
    catch (final Throwable e)
    {
      e.printStackTrace();
      return false;
    }

    return true;
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
    String lFileName =
                     String.format(StackSinkSourceInterface.cBasename
                                   + StackSinkSourceInterface.cSqeazyFileExtension,
                                   pIndex);
    File lFile = new File(getChannelFolder(pChannel), lFileName);
    FileChannel lBinnaryFileChannel = getFileChannel(lFile, false);

    final Pointer<Byte> bPipelineName =
                                      Pointer.pointerToCString(mPipelineName.get());

    final long[] lShape = pStack.getDimensions();
    final long lBufferLengthInByte = pStack.getSizeInBytes();

    final Pointer<CLong> lSourceShape =
                                      Pointer.pointerToCLongs(lShape[2],
                                                              lShape[1],
                                                              lShape[0]);
    final Pointer<CLong> lMaxEncodedBytes = Pointer.allocateCLong();
    lMaxEncodedBytes.setCLong(lBufferLengthInByte);
    SqeazyLibrary.SQY_Pipeline_Max_Compressed_Length_UI16(bPipelineName,
                                                          mPipelineName.get()
                                                                       .length(),
                                                          lMaxEncodedBytes);

    if (mCompressedData == null
        || mCompressedData.getSizeInBytes() != lMaxEncodedBytes.getCLong())
    {
      mCompressedData =
                      OffHeapMemory.allocateBytes(lMaxEncodedBytes.getCLong());
    }

    final Pointer<Short> bInputData =
                                    pStack.getContiguousMemory()
                                          .getBridJPointer(Short.class);

    final Pointer<CLong> lEncodedBytes = Pointer.allocateCLong();

    // do the encoding with sqeazy here
    @SuppressWarnings("unchecked")
    int lReturnValue =
                     SqeazyLibrary.SQY_PipelineEncode_UI16(bPipelineName,
                                                           bInputData.as(Byte.class),
                                                           lSourceShape,
                                                           lShape.length,
                                                           (Pointer<Byte>) mCompressedData.getBridJPointer(Byte.class),
                                                           lEncodedBytes,
                                                           mNumThreads.get());

    if (lReturnValue != 0)
    {
      throw new RuntimeException("Error while peforming sqy compression, error code:  "
                                 + lReturnValue);
    }

    mCompressedData.subRegion(0, lEncodedBytes.getCLong())
                   .writeBytesToFileChannel(lBinnaryFileChannel, 0);

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
