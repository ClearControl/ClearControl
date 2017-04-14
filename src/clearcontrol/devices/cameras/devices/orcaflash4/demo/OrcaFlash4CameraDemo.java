package clearcontrol.devices.cameras.devices.orcaflash4.demo;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import clearcontrol.core.variable.Variable;
import clearcontrol.devices.cameras.StackCameraRealTimeQueue;
import clearcontrol.devices.cameras.devices.orcaflash4.OrcaFlash4StackCamera;
import clearcontrol.gui.video.video2d.videowindow.VideoWindow;
import clearcontrol.stack.ContiguousOffHeapPlanarStackFactory;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.enums.NativeTypeEnum;
import coremem.recycling.BasicRecycler;

import org.junit.Test;

/**
 *
 *
 * @author royer
 */
public class OrcaFlash4CameraDemo
{
  AtomicLong mFrameIndex = new AtomicLong(0);

  /**
   * @throws InterruptedException
   * @throws ExecutionException
   */
  @Test
  public void testAcquireSingleFrames() throws InterruptedException,
                                        ExecutionException
  {
    mFrameIndex.set(0);
    final OrcaFlash4StackCamera lOrcaFlash4StackCamera =
                                                       OrcaFlash4StackCamera.buildWithInternalTriggering(0,
                                                                                                         false);

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
                              System.out.println("testbody: hashcode="
                                                 + pNewStack.hashCode()
                                                 + " index="
                                                 + pNewStack.getIndex());/**/
                              System.out.println(pNewStack);
                              mFrameIndex.incrementAndGet();

                              pNewStack.release();
                              return super.setEventHook(pOldStack,
                                                        pNewStack);
                            }

                          });

    assertTrue(lOrcaFlash4StackCamera.open());

    lOrcaFlash4StackCamera.getStackDepthVariable().set(1L);
    lOrcaFlash4StackCamera.getExposureInMicrosecondsVariable()
                          .addAll(100.0);
    lOrcaFlash4StackCamera.ensureEnough2DFramesAreAvailable(100);

    Thread.sleep(1000);

    assertTrue(lOrcaFlash4StackCamera.start());

    Thread.sleep(2000);

    assertTrue(lOrcaFlash4StackCamera.stop());

    assertTrue(lOrcaFlash4StackCamera.close());

    System.out.println(mFrameIndex.get());

    assertTrue(mFrameIndex.get() >= 190);
  }

  /**
   * @throws InterruptedException
   * @throws ExecutionException
   */
  @Test
  public void testAcquireStack() throws InterruptedException,
                                 ExecutionException
  {
    mFrameIndex.set(0);
    final OrcaFlash4StackCamera lOrcaFlash4StackCamera =
                                                       OrcaFlash4StackCamera.buildWithInternalTriggering(0,
                                                                                                         false);

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
                              /*System.out.println("testbody: hashcode=" + pNewStack.hashCode()
                              										+ " index="
                              										+ pNewStack.getIndex());/**/
                              System.out.println(pNewStack);
                              mFrameIndex.incrementAndGet();
                              return super.setEventHook(pOldStack,
                                                        pNewStack);
                            }

                          });

    // assertTrue(lOrcaFlash4StackCamera.open());

    lOrcaFlash4StackCamera.setBinning(4);

    lOrcaFlash4StackCamera.getStackModeVariable().set(true);
    lOrcaFlash4StackCamera.getExposureInMicrosecondsVariable()
                          .addAll(500.0);
    lOrcaFlash4StackCamera.getStackWidthVariable().set(1024L);
    lOrcaFlash4StackCamera.getStackHeightVariable().set(1024L);
    /*lOrcaFlash4StackCamera.getStackDepthVariable().set(128L);
    lOrcaFlash4StackCamera.ensureEnough2DFramesAreAvailable(128L);/**/

    StackCameraRealTimeQueue lQueue =
                                    lOrcaFlash4StackCamera.requestQueue();

    lQueue.clearQueue();

    for (int i = 0; i < 500; i++)
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

  /**
   * @throws InterruptedException
   * @throws IOException
   * @throws ExecutionException
   */
  @Test
  public void testDisplayVideo() throws InterruptedException,
                                 IOException,
                                 ExecutionException
  {
    final int lWidth = 256;
    final int lHeight = 256;

    final VideoWindow lVideoWindow =
                                   new VideoWindow("VideoWindow test",
                                                   NativeTypeEnum.UnsignedShort,
                                                   lWidth,
                                                   lHeight);

    lVideoWindow.setDisplayOn(true);
    lVideoWindow.setVisible(true);

    mFrameIndex.set(0);
    final OrcaFlash4StackCamera lOrcaFlash4StackCamera =
                                                       OrcaFlash4StackCamera.buildWithInternalTriggering(0,
                                                                                                         false);

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
                              try
                              {
                                /*System.out.println("testbody: hashcode=" + pNewStack.hashCode()
                                										+ " index="
                                										+ pNewStack.getIndex());/**/
                                System.out.println("mCounter="
                                                   + mFrameIndex.get());
                                System.out.println(pNewStack);

                                // assertTrue(mFrameIndex.get() ==
                                // pNewStack.getIndex());

                                lVideoWindow.sendBuffer(pNewStack.getContiguousMemory(0),
                                                        lWidth,
                                                        lHeight);
                                // INFO: we are not waiting for the
                                // buffer to be
                                // copied, that's BAD but for
                                // display it is not
                                // a big deal.

                                pNewStack.release();

                                mFrameIndex.incrementAndGet();
                                return super.setEventHook(pOldStack,
                                                          pNewStack);
                              }
                              catch (Throwable e)
                              {

                                e.printStackTrace();
                              }
                              return super.setEventHook(pOldStack,
                                                        pNewStack);
                            }

                          });

    assertTrue(lOrcaFlash4StackCamera.open());

    lOrcaFlash4StackCamera.setBinning(2);

    lOrcaFlash4StackCamera.getExposureInMicrosecondsVariable()
                          .addAll(500.0);
    lOrcaFlash4StackCamera.getStackWidthVariable().set((long) lWidth);
    lOrcaFlash4StackCamera.getStackHeightVariable()
                          .set((long) lHeight);
    lOrcaFlash4StackCamera.getStackDepthVariable().set(1L);
    lOrcaFlash4StackCamera.ensureEnough2DFramesAreAvailable(100);

    Thread.sleep(1000);

    StackCameraRealTimeQueue lQueue =
                                    lOrcaFlash4StackCamera.requestQueue();

    lQueue.clearQueue();

    for (int i = 0; i < 500; i++)
    {
      lQueue.addCurrentStateToQueue();
    }

    while (lVideoWindow.isVisible())
    {
      Future<Boolean> lPlayQueue =
                                 lOrcaFlash4StackCamera.playQueue(lQueue);
      lPlayQueue.get();
      Thread.sleep(100);
    }

    /*
    lVideoWindow.start();
    assertTrue(lOrcaFlash4StackCamera.start());
    
    while (lVideoWindow.isVisible())
    {
    	Thread.sleep(100);
    }
    
    assertTrue(lOrcaFlash4StackCamera.stop());
    lVideoWindow.stop();
    // Thread.sleep(1000); /**/

    assertTrue(lOrcaFlash4StackCamera.close());

    System.out.println(mFrameIndex.get());

    assertTrue(mFrameIndex.get() >= 1000);

    lVideoWindow.close();
  }

  /**/

}
