package clearcontrol.stack.sourcesink.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import clearcontrol.stack.ContiguousOffHeapPlanarStackFactory;
import clearcontrol.stack.OffHeapPlanarStack;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import clearcontrol.stack.sourcesink.sink.SqeazyFileStackSink;
import clearcontrol.stack.sourcesink.source.SqeazyFileStackSource;
import coremem.ContiguousMemoryInterface;
import coremem.buffers.ContiguousBuffer;
import coremem.recycling.BasicRecycler;

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

  private static final long cDiv = 8;

  private static final long cSizeX = 2048 / cDiv;
  private static final long cSizeY = 2048 / cDiv;
  private static final long cSizeZ = 512 / cDiv;
  private static final int cBytesPerVoxel = 2;

  private static final int cNumberOfStacks = 2;
  private static final int cMaximalNumberOfAvailableStacks = 20;

  /**
   * adapted from http://javapapers.com/java/glob-with-java-nio/
   */
  static List<String> find_files(String glob,
                                 String start_location) throws IOException
  {

    final List<String> value = new ArrayList<>();

    long n_files = 0;
    DirectoryStream<Path> dir_stream =
                                     Files.newDirectoryStream(Paths.get(start_location),
                                                              glob);
    for (Path path : dir_stream)
    {
      value.add(path.getFileName().toString());
      n_files = n_files + 1;
    }
    dir_stream.close();

    return value;
  }

  /**
   * Tests querying sqeazy version.
   */
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
   * Test sqeazy to disentangle problems from java interface issues
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
                                                       lEncodedBytes,
                                                       1// = number of threads
                 ));

    assertTrue(lEncodedBytes.getLong() > nil);
    assertTrue(lEncodedBytes.getLong() < lBufferLengthInByte);

  }

  /**
   * test sink only
   *
   * @throws IOException
   *           NA
   */
  @Test
  public void testSink() throws IOException
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

    String parent_path = lRootFolder + "/testSink/stacks/default/";
    assertTrue("check if " + parent_path
               + " exists failed",
               Files.exists(Paths.get(parent_path)));

    List<String> stacks_written = find_files("*.sqy", parent_path);
    assertTrue(!stacks_written.isEmpty());
    assertEquals(stacks_written.size(), cNumberOfStacks);

    try
    {
      FileUtils.deleteDirectory(lRootFolder);
    }
    catch (Exception e)
    {
    }

  }

  /**
   * test sink and source
   *
   * @throws IOException
   *           NA
   */
  @SuppressWarnings("deprecation")
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

        final short value = (short) i;
        final ContiguousMemoryInterface lContiguousMemory =
                                                          lStack.getContiguousMemory();

        ContiguousBuffer lContiguousBuffer =
                                           ContiguousBuffer.wrap(lContiguousMemory);

        while (lContiguousBuffer.hasRemainingShort())
        {
          lContiguousBuffer.writeShort(value);

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

    String parent_path = lRootFolder + "/testSink/stacks/default/";
    assertTrue("check if " + parent_path
               + " exists failed",
               Files.exists(Paths.get(parent_path)));

    List<String> stacks_written = find_files("*.sqy", parent_path);
    assertTrue(!stacks_written.isEmpty());
    assertEquals(stacks_written.size(), cNumberOfStacks);

    {
      final ContiguousOffHeapPlanarStackFactory lOffHeapPlanarStackFactory =
                                                                           new ContiguousOffHeapPlanarStackFactory();

      final BasicRecycler<StackInterface, StackRequest> lStackRecycler =
                                                                       new BasicRecycler<StackInterface, StackRequest>(lOffHeapPlanarStackFactory,
                                                                                                                       cMaximalNumberOfAvailableStacks);

      final SqeazyFileStackSource lSqyFileStackSource =
                                                      new SqeazyFileStackSource(lStackRecycler);

      lSqyFileStackSource.setLocation(lRootFolder, "testSink");

      lSqyFileStackSource.update();

      assertEquals(cNumberOfStacks,
                   lSqyFileStackSource.getNumberOfStacks());

      assertEquals(cSizeX,
                   lSqyFileStackSource.getStack(0).getWidth());
      assertEquals(cSizeY,
                   lSqyFileStackSource.getStack(0).getHeight());
      assertEquals(cSizeZ,
                   lSqyFileStackSource.getStack(0).getDepth());

      final short[] expected_values = new short[(int) cSizeX];
      final int cSizeX_as_int = (int) cSizeX;

      for (int i = 0; i < cNumberOfStacks; i++)
      {

        for (int r = 0; r < cSizeX_as_int; r++)
        {
          expected_values[r] = (short) i;
        }
        assertArrayEquals(lSqyFileStackSource.getStack(i)
                                             .getContiguousMemory()
                                             .getBridJPointer(Short.class)
                                             .getShorts(cSizeX_as_int),
                          expected_values);

        assertArrayEquals(lSqyFileStackSource.getStack(i)
                                             .getContiguousMemory()
                                             .getBridJPointer(Short.class)
                                             .getShortsAtOffset(((cSizeX
                                                                  - 1)
                                                                 * cSizeY
                                                                 * cSizeZ),
                                                                cSizeX_as_int),
                          expected_values);

      }

      lSqyFileStackSource.close();
    }

    try
    {
      FileUtils.deleteDirectory(lRootFolder);
    }
    catch (Exception e)
    {
    }

  }

  /**
   * Test write speed
   *
   * @throws IOException
   *           NA
   */
  @Test
  public void testWriteSpeed() throws IOException
  {

    for (int r = 0; r < 1; r++)
    {
      System.gc();

      final File lRootFolder =
                             new File(File.createTempFile("test",
                                                          "test")
                                          .getParentFile(),
                                      "LocalFileStackTests" + Math.random());/**/

      lRootFolder.mkdirs();
      System.out.println(lRootFolder);

      final SqeazyFileStackSink lLocalFileStackSink =
                                                    new SqeazyFileStackSink();
      lLocalFileStackSink.setLocation(lRootFolder, "testSink");

      final OffHeapPlanarStack lStack =
                                      OffHeapPlanarStack.createStack(cSizeX,
                                                                     cSizeY,
                                                                     cSizeZ);

      lStack.getMetaData().setIndex(0);
      lStack.getMetaData()
            .setTimeStampInNanoseconds(System.nanoTime());

      assertEquals(cSizeX * cSizeY * cSizeZ, lStack.getVolume());

      assertEquals(cSizeX * cSizeY
                   * cSizeZ
                   * cBytesPerVoxel,
                   lStack.getSizeInBytes());

      System.out.println("generating data...");
      System.out.println("size: " + lStack.getSizeInBytes()
                         + " bytes!");
      ContiguousMemoryInterface lContiguousMemory =
                                                  lStack.getContiguousMemory();

      ContiguousBuffer lBuffer =
                               ContiguousBuffer.wrap(lContiguousMemory);
      int i = 0;
      while (lBuffer.hasRemainingByte())
      {
        lBuffer.writeByte((byte) i++);
      } /**/

      System.out.println("done generating data...");

      System.out.println("start");
      long lStart = System.nanoTime();
      assertTrue(lLocalFileStackSink.appendStack(lStack));
      long lStop = System.nanoTime();
      System.out.println("stop");

      double lElapsedTimeInSeconds = (lStop - lStart) * 1e-9;

      double lSpeed = (lStack.getSizeInBytes() * 1e-6)
                      / lElapsedTimeInSeconds;

      System.out.format("speed: %g MB/s \n", lSpeed);

      lLocalFileStackSink.close();

      try
      {
        FileUtils.deleteDirectory(lRootFolder);
      }
      catch (Exception e)
      {
        System.out.println(e);
      }

      lStack.free();
    }

  }

}
