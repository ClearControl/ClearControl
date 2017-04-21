package clearcontrol.devices.cameras.devices.hamamatsu.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import clearcontrol.core.variable.Variable;
import clearcontrol.devices.cameras.devices.hamamatsu.HamStackCamera;
import clearcontrol.devices.cameras.devices.hamamatsu.HamStackCameraQueue;
import clearcontrol.stack.ContiguousOffHeapPlanarStackFactory;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.recycling.BasicRecycler;

import org.junit.Test;

/**
 * Hamamatsu stack camera demo
 *
 * @author royer
 */
public class HamStackCameraDemo
{
  AtomicLong mFrameIndex = new AtomicLong(0);

  /**
   * test stack acquisition
   * 
   * @throws InterruptedException
   *           NA
   * @throws ExecutionException
   *           NA
   */
  @Test
  public void testAcquireStack() throws InterruptedException,
                                 ExecutionException
  {
    long lWidth = 768;
    long lHeight = 768;
    long lDepth = 17;

    int lRepeats = 13;

    mFrameIndex.set(0);
    final HamStackCamera lOrcaFlash4StackCamera =
                                                HamStackCamera.buildWithInternalTriggering(0);

    final ContiguousOffHeapPlanarStackFactory lOffHeapPlanarStackFactory =
                                                                         new ContiguousOffHeapPlanarStackFactory();

    BasicRecycler<StackInterface, StackRequest> lRecycler =
                                                          new BasicRecycler<>(lOffHeapPlanarStackFactory,
                                                                              6,
                                                                              6,
                                                                              true);

    lOrcaFlash4StackCamera.setStackRecycler(lRecycler);

    lOrcaFlash4StackCamera.getStackVariable()
                          .sendUpdatesTo(new Variable<StackInterface>("Receiver")
                          {

                            @Override
                            public StackInterface setEventHook(final StackInterface pOldStack,
                                                               final StackInterface pNewStack)
                            {
                              mFrameIndex.incrementAndGet();
                              System.out.println(pNewStack);

                              assertEquals(lWidth,
                                           pNewStack.getWidth());
                              assertEquals(lHeight,
                                           pNewStack.getHeight());
                              assertEquals(lDepth,
                                           pNewStack.getDepth());

                              pNewStack.release();
                              return super.setEventHook(pOldStack,
                                                        pNewStack);
                            }

                          });

    lOrcaFlash4StackCamera.getExposureInSecondsVariable().set(0.01);
    lOrcaFlash4StackCamera.getStackWidthVariable().set(lWidth);
    lOrcaFlash4StackCamera.getStackHeightVariable().set(lHeight);

    HamStackCameraQueue lQueue =
                               lOrcaFlash4StackCamera.requestQueue();

    lQueue.clearQueue();

    for (int i = 0; i < lDepth; i++)
    {
      lQueue.addCurrentStateToQueue();
    }

    lQueue.finalizeQueue();

    for (int r = 0; r < lRepeats; r++)
    {

      Future<Boolean> lPlayQueue =
                                 lOrcaFlash4StackCamera.playQueue(lQueue);
      lPlayQueue.get();
    }

    assertTrue(lOrcaFlash4StackCamera.close());

    System.out.println(mFrameIndex.get());

    assertTrue(mFrameIndex.get() == lRepeats);
  }

  /**/

}
