package clearcontrol.stack.processor.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.variable.VariableListener;
import clearcontrol.microscope.stacks.StackRecyclerManager;
import clearcontrol.stack.ContiguousOffHeapPlanarStackFactory;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import clearcontrol.stack.processor.AsynchronousPoolStackProcessorPipeline;
import clearcontrol.stack.processor.StackProcessorInterface;
import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclerInterface;

import org.junit.Test;

/**
 * Asynchronous pipeline tests
 *
 * @author royer
 */
public class AsynchronousPoolStackProcessorPipelineTests
{

  /**
   * Test
   * 
   * @throws InterruptedException
   *           NA
   */
  @Test
  public void test() throws InterruptedException
  {

    StackRecyclerManager lStackRecyclerManager =
                                               new StackRecyclerManager();

    final AsynchronousPoolStackProcessorPipeline lAsynchronousPoolStackProcessorPipeline =
                                                                                         new AsynchronousPoolStackProcessorPipeline("Test",
                                                                                                                                    lStackRecyclerManager,
                                                                                                                                    10,
                                                                                                                                    4);

    final ContiguousOffHeapPlanarStackFactory lOffHeapPlanarStackFactory =
                                                                         new ContiguousOffHeapPlanarStackFactory();

    final RecyclerInterface<StackInterface, StackRequest> lRecycler0 =
                                                                     new BasicRecycler<StackInterface, StackRequest>(lOffHeapPlanarStackFactory,
                                                                                                                     10);

    final StackProcessorInterface lStackProcessor1 =
                                                   new StackProcessorInterface()
                                                   {

                                                     @Override
                                                     public void setActive(boolean pIsActive)
                                                     {

                                                     }

                                                     @Override
                                                     public boolean isActive()
                                                     {
                                                       return true;
                                                     }

                                                     @Override
                                                     public StackInterface process(StackInterface pStack,
                                                                                   RecyclerInterface<StackInterface, StackRequest> pStackRecycler)
                                                     {

                                                       final StackInterface lNewStack =
                                                                                      pStackRecycler.getOrWait(1,
                                                                                                               TimeUnit.SECONDS,
                                                                                                               StackRequest.buildFrom(pStack));

                                                       lNewStack.getContiguousMemory()
                                                                .setByteAligned(0,
                                                                                (byte) (pStack.getContiguousMemory()
                                                                                              .getByteAligned(0)
                                                                                        + 1));
                                                       pStack.release();
                                                       return lNewStack;
                                                     }
                                                   };

    final StackProcessorInterface lStackProcessor2 =
                                                   new StackProcessorInterface()
                                                   {

                                                     @Override
                                                     public void setActive(boolean pIsActive)
                                                     {

                                                     }

                                                     @Override
                                                     public boolean isActive()
                                                     {
                                                       return true;
                                                     }

                                                     @Override
                                                     public StackInterface process(StackInterface pStack,
                                                                                   RecyclerInterface<StackInterface, StackRequest> pStackRecycler)
                                                     {
                                                       final StackInterface lNewStack =
                                                                                      pStackRecycler.getOrWait(1,
                                                                                                               TimeUnit.SECONDS,
                                                                                                               StackRequest.buildFrom(pStack));

                                                       lNewStack.getContiguousMemory()
                                                                .setByteAligned(0,
                                                                                (byte) (pStack.getContiguousMemory()
                                                                                              .getByteAligned(0)
                                                                                        + 1));
                                                       pStack.release();
                                                       return lNewStack;
                                                     }
                                                   };

    lAsynchronousPoolStackProcessorPipeline.addStackProcessor(lStackProcessor1,
                                                              "recycler",
                                                              10,
                                                              10);

    lAsynchronousPoolStackProcessorPipeline.addStackProcessor(lStackProcessor2,
                                                              "recycler",
                                                              10,
                                                              10);

    assertTrue(lAsynchronousPoolStackProcessorPipeline.open());

    lAsynchronousPoolStackProcessorPipeline.getOutputVariable()
                                           .addListener(new VariableListener<StackInterface>()
                                           {

                                             @Override
                                             public void setEvent(StackInterface pCurrentValue,
                                                                  StackInterface pNewValue)
                                             {
                                               assertEquals(2,
                                                            pNewValue.getContiguousMemory()
                                                                     .getByteAligned(0),
                                                            0);

                                               pNewValue.release();
                                             }

                                             @Override
                                             public void getEvent(StackInterface pCurrentValue)
                                             {

                                             }
                                           });

    for (int i = 0; i < 1000; i++)
    {
      final StackInterface lStack =
                                  lRecycler0.getOrWait(100,
                                                       TimeUnit.SECONDS,
                                                       StackRequest.build(12,
                                                                          13,
                                                                          14));
      // System.out.println(lStack);
      lStack.getContiguousMemory().setByteAligned(0, (byte) 0);

      lAsynchronousPoolStackProcessorPipeline.getInputVariable()
                                             .set(lStack);

      Thread.sleep(1);
    }

    assertTrue(lAsynchronousPoolStackProcessorPipeline.close());

  }
}
