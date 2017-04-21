package clearcontrol.devices.cameras.devices.hamamatsu.demo;

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

                              System.out.println(pNewStack);
                              mFrameIndex.incrementAndGet();
                              return super.setEventHook(pOldStack,
                                                        pNewStack);
                            }

                          });

    HamStackCameraQueue lQueue =
                               lOrcaFlash4StackCamera.requestQueue();

    lQueue.clearQueue();

    lQueue.getExposureInSecondsVariable().set(0.01);
    lOrcaFlash4StackCamera.getStackWidthVariable().set(512L);
    lOrcaFlash4StackCamera.getStackHeightVariable().set(512L);

    for (int i = 0; i < 10; i++)
    {
      lQueue.addCurrentStateToQueue();
    }

    Future<Boolean> lPlayQueue =
                               lOrcaFlash4StackCamera.playQueue(lQueue);
    lPlayQueue.get();

    assertTrue(lOrcaFlash4StackCamera.close());

    System.out.println(mFrameIndex.get());

    assertTrue(mFrameIndex.get() == 1);
  }

  /**/

}
