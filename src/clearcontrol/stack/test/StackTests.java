package clearcontrol.stack.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.executors.ClearControlExecutors;
import clearcontrol.core.concurrent.thread.ThreadSleep;
import clearcontrol.stack.ContiguousOffHeapPlanarStackFactory;
import clearcontrol.stack.OffHeapPlanarStack;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.ContiguousMemoryInterface;
import coremem.offheap.OffHeapMemory;
import coremem.offheap.OffHeapMemoryAccess;
import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclerInterface;
import coremem.util.Size;

import org.junit.Test;

/**
 * Stack tests
 *
 * @author royer
 */
public class StackTests
{

  private static final int cMaximumNumberOfObjects = 1024;
  @SuppressWarnings("unused")
  private static final long cMaximumLiveMemoryInBytes = 2L * 1024L
                                                        * 1024L
                                                        * 1024L;
  private static final long cBytesPerPixel = Size.of(short.class);
  private static final long cSizeX = 320;
  private static final long cSizeY = 321;
  private static final long cSizeZ = 100;
  private static final long cBig = 2;

  private static final long cLengthInBytes = cSizeX * cSizeY
                                             * cSizeZ
                                             * cBytesPerPixel;

  /**
   * Test stack life-cycle
   */
  @Test
  public void testLifeCycle()
  {

    final ContiguousMemoryInterface lContiguousMemory =
                                                      OffHeapMemory.allocateShorts(cSizeX
                                                                                   * cSizeY
                                                                                   * cSizeZ);
    final OffHeapPlanarStack lStack =
                                    OffHeapPlanarStack.createStack(lContiguousMemory,
                                                                   cSizeX,
                                                                   cSizeY,
                                                                   cSizeZ);

    assertNotNull(lStack);

    assertNotNull(lStack.getMetaData());

    assertEquals(null, lStack.getMetaData().getVoxelDimX());

    lStack.getMetaData().setVoxelDimX(0.5);
    lStack.getMetaData().setVoxelDimY(1);
    lStack.getMetaData().setVoxelDimZ(3);

    assertEquals(0.5, lStack.getMetaData().getVoxelDimX(), 0);
    assertEquals(1, lStack.getMetaData().getVoxelDimY(), 0);
    assertEquals(3, lStack.getMetaData().getVoxelDimZ(), 0);

    lStack.getMetaData().setIndex(17);
    lStack.getMetaData().setTimeStampInNanoseconds(42);

    assertEquals(17, (long) lStack.getMetaData().getIndex());
    assertEquals(42,
                 (long) lStack.getMetaData()
                              .getTimeStampInNanoseconds());

    assertEquals(cLengthInBytes, lStack.getSizeInBytes());

    assertEquals(3, lStack.getNumberOfDimensions());

    assertEquals(cBytesPerPixel, lStack.getBytesPerVoxel());
    assertEquals(cSizeX, lStack.getWidth());
    assertEquals(cSizeY, lStack.getHeight());
    assertEquals(cSizeZ, lStack.getDepth());

    assertEquals(cSizeX, lStack.getDimensions()[0]);
    assertEquals(cSizeY, lStack.getDimensions()[1]);
    assertEquals(cSizeZ, lStack.getDimensions()[2]);

    assertEquals(cSizeX, lStack.getDimension(0));
    assertEquals(cSizeY, lStack.getDimension(1));
    assertEquals(cSizeZ, lStack.getDimension(2));

    lStack.free();

    assertTrue(lStack.isFree());

  }

  /**
   * Tests recycling
   * 
   * @throws InterruptedException
   *           NA
   */
  @Test
  public void testRecycling() throws InterruptedException
  {
    final long lStartTotalAllocatedMemory =
                                          OffHeapMemoryAccess.getTotalAllocatedMemory();

    final ContiguousOffHeapPlanarStackFactory lOffHeapPlanarStackFactory =
                                                                         new ContiguousOffHeapPlanarStackFactory();

    final RecyclerInterface<StackInterface, StackRequest> lRecycler =
                                                                    new BasicRecycler<StackInterface, StackRequest>(lOffHeapPlanarStackFactory,
                                                                                                                    cMaximumNumberOfObjects);

    final ThreadPoolExecutor lThreadPoolExecutor =
                                                 ClearControlExecutors.getOrCreateThreadPoolExecutor(this,
                                                                                                     Thread.NORM_PRIORITY,
                                                                                                     1,
                                                                                                     1,
                                                                                                     100);

    for (int i = 0; i < 100; i++)
    {
      // System.out.println(i);
      final OffHeapPlanarStack lStack;
      if ((i % 100) < 50)
      {

        lStack =
               OffHeapPlanarStack.getOrWaitWithRecycler(lRecycler,
                                                        10,
                                                        TimeUnit.SECONDS,
                                                        cSizeX * cBig,
                                                        cSizeY * cBig,
                                                        cSizeZ * cBig);
        assertEquals(cLengthInBytes * Math.pow(cBig, 3),
                     lStack.getSizeInBytes(),
                     0);
      }
      else
      {
        lStack = OffHeapPlanarStack.getOrWaitWithRecycler(lRecycler,
                                                          10,
                                                          TimeUnit.SECONDS,
                                                          cSizeX,
                                                          cSizeY,
                                                          cSizeZ);
        assertEquals(cLengthInBytes, lStack.getSizeInBytes());
      }

      assertNotNull(lStack);

      final ContiguousMemoryInterface lContiguousMemory =
                                                        lStack.getContiguousMemory((int) (cSizeZ
                                                                                          / 2));

      for (int k = 0; k < lContiguousMemory.getSizeInBytes(); k++)
      {
        lContiguousMemory.setByteAligned(k, (byte) k);
      }

      final Runnable lRunnable2 = () -> {

        final ContiguousMemoryInterface lContiguousMemory2 =
                                                           lStack.getContiguousMemory((int) (cSizeZ
                                                                                             / 2));
        for (int k = 0; k < lContiguousMemory2.getSizeInBytes(); k++)
        {
          final byte lByte = lContiguousMemory2.getByteAligned(k);
          assertEquals((byte) k, lByte);
        }
        ThreadSleep.sleep(5 + (int) (Math.random() * 10),
                          TimeUnit.MILLISECONDS);

        lStack.release();
        // System.out.println("released!");
      };

      lThreadPoolExecutor.execute(lRunnable2);

      final long lLiveObjectCount =
                                  lRecycler.getNumberOfLiveObjects();
      /*final long lLiveMemoryInBytes =
                                    lRecycler.computeLiveMemorySizeInBytes();
      
      final long lAvailableObjectCount =
                                       lRecycler.getNumberOfAvailableObjects();
      final long lAvailableMemoryInBytes =
                                         lRecycler.computeAvailableMemorySizeInBytes();
      System.out.format("live count=%d, live mem=%d, avail count=%d, avail mem=%d \n",
                        lLiveObjectCount,
                        lLiveMemoryInBytes,
                        lAvailableObjectCount,
                        lAvailableMemoryInBytes);/**/
      assertTrue(lLiveObjectCount > 0);

      final long lTotalAllocatedMemory =
                                       OffHeapMemoryAccess.getTotalAllocatedMemory();
      // System.out.println("lTotalAllocatedMemory=" +
      // lTotalAllocatedMemory);
      assertTrue(lTotalAllocatedMemory > 0);

      Thread.sleep(10);

      System.gc();
      /*
      			System.out.println("totalMemory=" + Runtime.getRuntime()
      																								.totalMemory());
      			System.out.println("freeMemory=" + Runtime.getRuntime()
      																								.freeMemory());
      			System.out.println("maxMemory=" + Runtime.getRuntime()
      																								.maxMemory());/**/

    }

    lThreadPoolExecutor.shutdown();
    lThreadPoolExecutor.awaitTermination(1, TimeUnit.SECONDS);

    lRecycler.free();

    final long lLiveObjectCount = lRecycler.getNumberOfLiveObjects();
    assertEquals(0, lLiveObjectCount);

    final long lEndTotalAllocatedMemory =
                                        OffHeapMemoryAccess.getTotalAllocatedMemory();

    System.gc();
    Thread.sleep(100);
    assertTrue(lEndTotalAllocatedMemory < lStartTotalAllocatedMemory
                                          + 10);

  }
}
