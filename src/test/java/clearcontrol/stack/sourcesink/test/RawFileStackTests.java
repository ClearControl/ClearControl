package clearcontrol.stack.sourcesink.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import clearcontrol.stack.ContiguousOffHeapPlanarStackFactory;
import clearcontrol.stack.OffHeapPlanarStack;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import clearcontrol.stack.sourcesink.sink.RawFileStackSink;
import clearcontrol.stack.sourcesink.source.RawFileStackSource;
import coremem.ContiguousMemoryInterface;
import coremem.buffers.ContiguousBuffer;
import coremem.recycling.BasicRecycler;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

/**
 * Raw file stack tests
 *
 * @author royer
 */
public class RawFileStackTests
{

  private static final long cDiv = 4;

  private static final long cSizeX = 2048 / cDiv;
  private static final long cSizeY = 2048 / cDiv;
  private static final long cSizeZ = 512 / cDiv;
  private static final int cBytesPerVoxel = 2;

  private static final int cNumberOfStacks = 2;
  private static final int cMaximalNumberOfAvailableStacks = 20;

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

      final RawFileStackSink lLocalFileStackSink =
                                                 new RawFileStackSink();
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

      long lStart = System.nanoTime();
      assertTrue(lLocalFileStackSink.appendStack(lStack));
      long lStop = System.nanoTime();

      double lElapsedTimeInSeconds = (lStop - lStart) * 1e-9;

      double lSpeed = (lStack.getSizeInBytes() * 1e-6)
                      / lElapsedTimeInSeconds;

      System.out.format("speed: %g MB/s \n", lSpeed);
      System.out.format("time : %g   ms \n", (lStop - lStart) * 1e-6);

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
      final RawFileStackSink lLocalFileStackSink =
                                                 new RawFileStackSink();
      lLocalFileStackSink.setLocation(lRootFolder, "testSink");

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

        assertTrue(lLocalFileStackSink.appendStack(lStack));
      }

      assertEquals(cNumberOfStacks,
                   lLocalFileStackSink.getNumberOfStacks());

      lLocalFileStackSink.close();
    }

    {
      final ContiguousOffHeapPlanarStackFactory lOffHeapPlanarStackFactory =
                                                                           new ContiguousOffHeapPlanarStackFactory();

      final BasicRecycler<StackInterface, StackRequest> lStackRecycler =
                                                                       new BasicRecycler<StackInterface, StackRequest>(lOffHeapPlanarStackFactory,
                                                                                                                       cMaximalNumberOfAvailableStacks);

      final RawFileStackSource lLocalFileStackSource =
                                                     new RawFileStackSource(lStackRecycler);

      lLocalFileStackSource.setLocation(lRootFolder, "testSink");

      lLocalFileStackSource.update();

      assertEquals(cNumberOfStacks,
                   lLocalFileStackSource.getNumberOfStacks());

      assertEquals(cSizeX,
                   lLocalFileStackSource.getStack(0).getWidth());
      assertEquals(cSizeY,
                   lLocalFileStackSource.getStack(0).getHeight());
      assertEquals(cSizeZ,
                   lLocalFileStackSource.getStack(0).getDepth());

      lLocalFileStackSource.close();
    }

    try
    {
      FileUtils.deleteDirectory(lRootFolder);
    }
    catch (Exception e)
    {
    }

  }
}
