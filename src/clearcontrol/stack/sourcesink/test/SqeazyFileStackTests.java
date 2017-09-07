package clearcontrol.stack.sourcesink.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import clearcontrol.stack.OffHeapPlanarStack;
import clearcontrol.stack.sourcesink.sink.SqeazyFileStackSink;
import coremem.ContiguousMemoryInterface;
import coremem.buffers.ContiguousBuffer;

import org.apache.commons.io.FileUtils;
import org.bridj.CLong;
import org.bridj.Pointer;
import org.junit.Test;
import sqeazy.bindings.SqeazyLibrary;

/**
 * Sqeazy file stack tests
 *
 * @author steinbac
 */
public class SqeazyFileStackTests
{

  private static final long cDiv = 4;

  private static final long cSizeX = 2048 / cDiv;
  private static final long cSizeY = 2048 / cDiv;
  private static final long cSizeZ = 512 / cDiv;
  private static final int cBytesPerVoxel = 2;

  private static final int cNumberOfStacks = 2;
  private static final int cMaximalNumberOfAvailableStacks = 20;

  @Test
  public void testSqeazyVersion()
  {
    final Pointer<Integer> version = Pointer.allocateInts(3);
    SqeazyLibrary.SQY_Version_Triple(version);
    assertTrue(version.get(0) >= 0);
    assertTrue(version.get(1) >= 3);
    assertTrue(version.get(2) >= 0);
  }

  /**
   * test sqeazy to disentangle problems from java interface issues
   *
   */
  @Test
  public void testSqeazyEncoding()
  {
    final String lPipeline = "bitswap1->lz4";
    final Pointer<Byte> bPipelineName =
                                      Pointer.pointerToCString(lPipeline);

    final int lWidth = 128;
    final int lHeight = 128;
    final int lDepth = 256;

    final int lBufferLengthInShorts = lWidth * lHeight * lDepth;
    final long lBufferLengthInByte = lBufferLengthInShorts * 2;

    final Pointer<Short> lSourceShort =
                                      Pointer.allocateShorts(lBufferLengthInShorts);
    final Pointer<Short> lDestShort =
                                    Pointer.allocateShorts(lBufferLengthInShorts);
    for (int i = 0; i < lBufferLengthInShorts; i++)
    {
      lSourceShort.set(i, (short) (1 << (i % 8)));
      lDestShort.set(i, (short) (0));
    }

    final Pointer<CLong> lSourceShape =
                                      Pointer.pointerToCLongs(lDepth,
                                                              lHeight,
                                                              lWidth);

    assertEquals(true,
                 SqeazyLibrary.SQY_Pipeline_Possible(bPipelineName));

    final Pointer<CLong> lMaxEncodedBytes = Pointer.allocateCLong();
    lMaxEncodedBytes.setCLong(lBufferLengthInByte);
    assertEquals(0,
                 SqeazyLibrary.SQY_Pipeline_Max_Compressed_Length_UI16(bPipelineName,
                                                                       lMaxEncodedBytes));

    final long nil = 0;
    assertEquals(true, lMaxEncodedBytes.get().longValue() > nil);

    final long received_max_encoded_size =
                                         lMaxEncodedBytes.get()
                                                         .longValue();
    assertTrue(received_max_encoded_size > lBufferLengthInByte);
    final Pointer<Byte> bCompressedData =
                                        Pointer.allocateBytes(received_max_encoded_size);
    final Pointer<Byte> bInputData = lSourceShort.as(Byte.class);
    final Pointer<CLong> lEncodedBytes = Pointer.allocateCLong();
    assertEquals(0,
                 SqeazyLibrary.SQY_PipelineEncode_UI16(bPipelineName,
                                                       bInputData,
                                                       lSourceShape,
                                                       3,
                                                       bCompressedData,
                                                       lEncodedBytes));

    assertTrue(lEncodedBytes.getLong() > nil);
    assertTrue(lEncodedBytes.getLong() < lBufferLengthInByte);

  }

  /**
   * test sink and source
   * 
   * @throws IOException
   *           NA
   */
  @Test
  public void testSinkAndSource() throws IOException
  {

    final File lRootFolder =
                           new File(File.createTempFile("test",
                                                        "test")
                                        .getParentFile(),
                                    "LocalFileStackTests" + Math.random());/**/

    // final File lRootFolder = new File("/Volumes/External/Temp");

    lRootFolder.mkdirs();
    System.out.println(lRootFolder);

    {
      final SqeazyFileStackSink lSqyFileStackSink =
                                                  new SqeazyFileStackSink();
      lSqyFileStackSink.setLocation(lRootFolder, "testSink");

      final OffHeapPlanarStack lStack =
                                      OffHeapPlanarStack.createStack(cSizeX,
                                                                     cSizeY,
                                                                     cSizeZ);

      lStack.getMetaData().setIndex(0);
      lStack.getMetaData()
            .setTimeStampInNanoseconds(System.nanoTime());

      assertEquals(cSizeX * cSizeY * cSizeZ, lStack.getVolume());
      // System.out.println(lStack.mNDimensionalArray.getLengthInElements()
      // *
      // 2);

      assertEquals(cSizeX * cSizeY
                   * cSizeZ
                   * cBytesPerVoxel,
                   lStack.getSizeInBytes());

      for (int i = 0; i < cNumberOfStacks; i++)
      {

        final ContiguousMemoryInterface lContiguousMemory =
                                                          lStack.getContiguousMemory();

        ContiguousBuffer lContiguousBuffer =
                                           ContiguousBuffer.wrap(lContiguousMemory);

        while (lContiguousBuffer.hasRemainingShort())
        {
          lContiguousBuffer.writeShort((short) i);
        }

        lContiguousBuffer.rewind();

        while (lContiguousBuffer.hasRemainingShort())
        {
          final short lShort = lContiguousBuffer.readShort();
          assertEquals(i & 0xFFFF, lShort);
        }

        assertTrue(lSqyFileStackSink.appendStack(lStack));
      }

      assertEquals(cNumberOfStacks,
                   lSqyFileStackSink.getNumberOfStacks());

      lSqyFileStackSink.close();
    }

    // {
    // final ContiguousOffHeapPlanarStackFactory lOffHeapPlanarStackFactory =
    // new ContiguousOffHeapPlanarStackFactory();

    // final BasicRecycler<StackInterface, StackRequest> lStackRecycler =
    // new BasicRecycler<StackInterface,
    // StackRequest>(lOffHeapPlanarStackFactory,
    // cMaximalNumberOfAvailableStacks);

    // final SqeazyFileStackSource lSqyFileStackSource =
    // new SqeazyFileStackSource(lStackRecycler);

    // lSqyFileStackSource.setLocation(lRootFolder, "testSink");

    // lSqyFileStackSource.update();

    // assertEquals(cNumberOfStacks,
    // lSqyFileStackSource.getNumberOfStacks());

    // assertEquals(cSizeX,
    // lSqyFileStackSource.getStack(0).getWidth());
    // assertEquals(cSizeY,
    // lSqyFileStackSource.getStack(0).getHeight());
    // assertEquals(cSizeZ,
    // lSqyFileStackSource.getStack(0).getDepth());

    // lSqyFileStackSource.close();
    // }

    try
    {
      FileUtils.deleteDirectory(lRootFolder);
    }
    catch (Exception e)
    {
    }

  }
}
