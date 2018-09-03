package clearcontrol.gui.video.video3d.demo;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.video.video3d.Stack3DDisplay;
import clearcontrol.stack.ContiguousOffHeapPlanarStackFactory;
import clearcontrol.stack.OffHeapPlanarStack;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.ContiguousMemoryInterface;
import coremem.buffers.ContiguousBuffer;
import coremem.offheap.OffHeapMemory;
import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclerInterface;

import org.junit.Test;

public class VideoFrame3DDisplayDemos
{
  private static final int cMaximumNumberOfObjects = 1024;

  @Test
  public void demoContiguousStackNoRecycler() throws InterruptedException
  {

    final long lResolutionX = 320;
    final long lResolutionY = lResolutionX + 1;
    final long lResolutionZ = lResolutionX / 2;

    final ContiguousMemoryInterface lContiguousMemory =
                                                      OffHeapMemory.allocateShorts(lResolutionX
                                                                                   * lResolutionY
                                                                                   * lResolutionZ);

    final ContiguousBuffer lContiguousBuffer =
                                             new ContiguousBuffer(lContiguousMemory);

    @SuppressWarnings("unchecked")
    final OffHeapPlanarStack lStack =
                                    OffHeapPlanarStack.createStack(lContiguousMemory,
                                                                   lResolutionX,
                                                                   lResolutionY,
                                                                   lResolutionZ);

    final Stack3DDisplay lVideoFrame3DDisplay =
                                              new Stack3DDisplay("Test");

    final Variable<StackInterface> lFrameReferenceVariable =
                                                           lVideoFrame3DDisplay.getInputStackVariable();

    lVideoFrame3DDisplay.open();
    lVideoFrame3DDisplay.setVisible(true);

    /*while (!lVideoFrame3DDisplay.isShowing())
            ThreadUtils.sleep(10, TimeUnit.MILLISECONDS);/**/

    for (int i = 0; i < 32000; i++)
    {

      lContiguousBuffer.rewind();
      for (int z = 0; z < lResolutionZ; z++)
      {
        for (int y = 0; y < lResolutionY; y++)
        {
          for (int x = 0; x < lResolutionX; x++)
          {
            final short lValue = (short) (i + x ^ y ^ z);
            lContiguousBuffer.writeShort(lValue);
          }
        }
      }

      lFrameReferenceVariable.set(lStack);
      Thread.sleep(10);

      if (!lVideoFrame3DDisplay.isVisible())
      {
        System.out.println("NOT SHOWING!");
        break;
      }
    }

    lVideoFrame3DDisplay.close();

    Thread.sleep(1000);

  }

  @Test
  public void demoWithStackRecycler() throws InterruptedException
  {

    for (int r = 0; r < 3; r++)
    {
      final long lResolutionX = 320;
      final long lResolutionY = lResolutionX + 1;
      final long lResolutionZ = lResolutionX / 2;

      final ContiguousOffHeapPlanarStackFactory lOffHeapPlanarStackFactory =
                                                                           new ContiguousOffHeapPlanarStackFactory();

      final RecyclerInterface<StackInterface, StackRequest> lRecycler =
                                                                      new BasicRecycler<StackInterface, StackRequest>(lOffHeapPlanarStackFactory,
                                                                                                                      cMaximumNumberOfObjects);

      final Stack3DDisplay lVideoFrame3DDisplay =
                                                new Stack3DDisplay("Test");

      final Variable<StackInterface> lFrameReferenceVariable =
                                                             lVideoFrame3DDisplay.getInputStackVariable();

      lVideoFrame3DDisplay.open();
      lVideoFrame3DDisplay.setVisible(true);

      for (int i = 0; i < 32000; i++)
      {

        final StackInterface lStack =
                                    OffHeapPlanarStack.getOrWaitWithRecycler(lRecycler,
                                                                             10,
                                                                             TimeUnit.MILLISECONDS,
                                                                             lResolutionX,
                                                                             lResolutionY,
                                                                             lResolutionZ);
        final ContiguousBuffer lContiguousBuffer =
                                                 new ContiguousBuffer(lStack.getContiguousMemory());
        lContiguousBuffer.rewind();
        for (int z = 0; z < lResolutionZ; z++)
        {
          for (int y = 0; y < lResolutionY; y++)
          {
            for (int x = 0; x < lResolutionX; x++)
            {
              final short lValue = (short) (i + x ^ y ^ z);
              lContiguousBuffer.writeShort(lValue);
            }
          }
        }

        lFrameReferenceVariable.set(lStack);
        // Thread.sleep(1);

        if (i % 100 == 0)
        {
          System.out.println("lRecycler.getNumberOfAvailableObjects()="
                             + lRecycler.getNumberOfAvailableObjects());
          System.out.println("lRecycler.getNumberOfLiveObjects()="
                             + lRecycler.getNumberOfLiveObjects());
        }

        if (!lVideoFrame3DDisplay.isVisible())
          break;
      }

      lVideoFrame3DDisplay.close();

      // Thread.sleep(1000);

    }
  }

}
