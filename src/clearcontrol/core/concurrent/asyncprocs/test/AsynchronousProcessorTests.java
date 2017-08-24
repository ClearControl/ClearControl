package clearcontrol.core.concurrent.asyncprocs.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.asyncprocs.AsynchronousProcessorBase;
import clearcontrol.core.concurrent.asyncprocs.AsynchronousProcessorInterface;
import clearcontrol.core.concurrent.asyncprocs.AsynchronousProcessorPool;
import clearcontrol.core.concurrent.asyncprocs.ProcessorInterface;
import clearcontrol.core.concurrent.thread.ThreadSleep;

import org.junit.Test;

/**
 * Asynchronous processor tests
 *
 * @author royer
 */
public class AsynchronousProcessorTests
{

  /**
   * test simple 2 processor pipeline
   * 
   * @throws IOException
   *           N/A
   */
  @Test
  public void testSimple2ProcessorPipeline() throws IOException
  {
    final AsynchronousProcessorInterface<String, String> lProcessorA =
                                                                     new AsynchronousProcessorBase<String, String>("A",
                                                                                                                   10)
                                                                     {
                                                                       @Override
                                                                       public String process(final String pInput)
                                                                       {
                                                                         // System.out.println("Processor
                                                                         // A
                                                                         // received:"
                                                                         // +
                                                                         // pInput);
                                                                         return "A"
                                                                                + pInput;
                                                                       }
                                                                     };

    final AsynchronousProcessorInterface<String, String> lProcessorB =
                                                                     new AsynchronousProcessorBase<String, String>("B",
                                                                                                                   10)
                                                                     {
                                                                       @Override
                                                                       public String process(final String pInput)
                                                                       {
                                                                         // System.out.println("Processor
                                                                         // B
                                                                         // received:"
                                                                         // +
                                                                         // pInput);
                                                                         return "B"
                                                                                + pInput;
                                                                       }
                                                                     };

    lProcessorA.connectToReceiver(lProcessorB);
    assertTrue(lProcessorA.start());
    assertTrue(lProcessorB.start());

    boolean hasFailed = false;
    for (int i = 0; i < 100; i++)
    {
      hasFailed |= lProcessorA.passOrFail("test" + i);
      // if(i>50) assertFalse();
    }
    assertTrue(hasFailed);
    ThreadSleep.sleep(100, TimeUnit.MILLISECONDS);
    for (int i = 0; i < 100; i++)
    {
      assertTrue(lProcessorA.passOrFail("test" + i));
      ThreadSleep.sleep(10, TimeUnit.MILLISECONDS);
    }

    assertTrue(lProcessorB.waitToFinish(1, TimeUnit.SECONDS));
    assertEquals(0, lProcessorB.getInputQueueLength());
    assertTrue(lProcessorB.stop(1, TimeUnit.SECONDS));

    assertTrue(lProcessorA.waitToFinish(1, TimeUnit.SECONDS));
    assertEquals(0, lProcessorA.getInputQueueLength());
    assertTrue(lProcessorA.stop(1, TimeUnit.SECONDS));

    assertTrue(lProcessorA.start());
    assertTrue(lProcessorB.start());

    for (int i = 0; i < 100; i++)
    {
      hasFailed |= lProcessorA.passOrFail("test" + i);
      // if(i>50) assertFalse();
    }
    assertTrue(hasFailed);
    ThreadSleep.sleep(100, TimeUnit.MILLISECONDS);
    for (int i = 0; i < 100; i++)
    {
      assertTrue(lProcessorA.passOrFail("test" + i));
      ThreadSleep.sleep(10, TimeUnit.MILLISECONDS);
    }

    assertTrue(lProcessorB.waitToFinish(1, TimeUnit.SECONDS));
    assertEquals(0, lProcessorB.getInputQueueLength());
    assertTrue(lProcessorB.stop(1, TimeUnit.SECONDS));

    assertTrue(lProcessorA.waitToFinish(1, TimeUnit.SECONDS));
    assertEquals(0, lProcessorA.getInputQueueLength());
    assertTrue(lProcessorA.stop(1, TimeUnit.SECONDS));

  }

  /**
   * tests Long queue
   * 
   * @throws IOException
   *           exception
   */
  @Test
  public void testLongQueue() throws IOException
  {
    final AsynchronousProcessorInterface<String, String> lProcessorA =
                                                                     new AsynchronousProcessorBase<String, String>("A",
                                                                                                                   1000)
                                                                     {
                                                                       @Override
                                                                       public String process(final String pInput)
                                                                       {
                                                                         ThreadSleep.sleep(1,
                                                                                           TimeUnit.MILLISECONDS);
                                                                         return "A"
                                                                                + pInput;
                                                                       }
                                                                     };

    final AsynchronousProcessorInterface<String, String> lProcessorB =
                                                                     new AsynchronousProcessorBase<String, String>("B",
                                                                                                                   1000)
                                                                     {
                                                                       @Override
                                                                       public String process(final String pInput)
                                                                       {
                                                                         ThreadSleep.sleep(1,
                                                                                           TimeUnit.MILLISECONDS);
                                                                         return "B"
                                                                                + pInput;
                                                                       }
                                                                     };

    lProcessorA.connectToReceiver(lProcessorB);
    assertTrue(lProcessorA.start());
    assertTrue(lProcessorB.start());

    for (int i = 0; i < 1000; i++)
    {
      lProcessorA.passOrFail("test" + i);
    }

    assertTrue(lProcessorA.getInputQueueLength() > 0);
    assertTrue(lProcessorA.waitToFinish(2, TimeUnit.SECONDS));
    assertEquals(0, lProcessorA.getInputQueueLength());
    assertTrue(lProcessorA.stop(1, TimeUnit.SECONDS));

    assertTrue(lProcessorB.waitToFinish(2, TimeUnit.SECONDS));
    assertEquals(0, lProcessorB.getInputQueueLength());
    assertTrue(lProcessorB.stop(1, TimeUnit.SECONDS));

  }

  /**
   * Tests simple 2 processor pipeline with pooled processor
   * 
   * @throws InterruptedException
   *           N/A
   * @throws IOException
   *           N/A
   */
  @Test
  public void testSimple2ProcessorPipelineWithPooledProcessor() throws InterruptedException,
                                                                IOException
  {
    final AsynchronousProcessorInterface<Integer, Integer> lProcessorA =
                                                                       new AsynchronousProcessorBase<Integer, Integer>("A",
                                                                                                                       10)
                                                                       {
                                                                         @Override
                                                                         public Integer process(final Integer pInput)
                                                                         {
                                                                           ThreadSleep.sleep((long) (Math.random()
                                                                                                     * 1000000),
                                                                                             TimeUnit.NANOSECONDS);
                                                                           return pInput;
                                                                         }
                                                                       };

    final ProcessorInterface<Integer, Integer> lProcessor =
                                                          (input) -> {

                                                            ThreadSleep.sleep((long) (Math.random()
                                                                                      * 1000000),
                                                                              TimeUnit.NANOSECONDS);
                                                            return input;
                                                          };

    final AsynchronousProcessorPool<Integer, Integer> lProcessorB =
                                                                  new AsynchronousProcessorPool<>("B",
                                                                                                  10,
                                                                                                  2,
                                                                                                  lProcessor);

    final ConcurrentLinkedQueue<Integer> lIntList =
                                                  new ConcurrentLinkedQueue<>();

    final AsynchronousProcessorInterface<Integer, Integer> lProcessorC =
                                                                       new AsynchronousProcessorBase<Integer, Integer>("C",
                                                                                                                       10)
                                                                       {
                                                                         @Override
                                                                         public Integer process(final Integer pInput)
                                                                         {
                                                                           ThreadSleep.sleep((long) (Math.random()
                                                                                                     * 1000000),
                                                                                             TimeUnit.NANOSECONDS);
                                                                           if (pInput > 0)
                                                                             lIntList.add(pInput);
                                                                           return pInput;
                                                                         }
                                                                       };

    // lProcessorA.connectToReceiver(lProcessorC);
    lProcessorA.connectToReceiver(lProcessorB);
    lProcessorB.connectToReceiver(lProcessorC);
    assertTrue(lProcessorA.start());
    assertTrue(lProcessorB.start());
    assertTrue(lProcessorC.start());

    for (int i = 1; i <= 1000; i++)
    {
      lProcessorA.passOrWait(i);
      ThreadSleep.sleep(1, TimeUnit.MILLISECONDS);
    }

    // This really makes sure that all the 'jobs' have gone through the entire
    // pipeline. There is no other way to do this.
    while (lIntList.size() < 1000)
      ThreadSleep.sleep(1, TimeUnit.MILLISECONDS);

    // We wait for the process to finish the jobs they have _received_ that's
    // why we need the line above...
    assertTrue(lProcessorA.waitToFinish(10, TimeUnit.SECONDS));
    assertTrue(lProcessorB.waitToFinish(10, TimeUnit.SECONDS));
    assertTrue(lProcessorC.waitToFinish(10, TimeUnit.SECONDS));

    assertEquals(0, lProcessorA.getInputQueueLength());
    assertEquals(0, lProcessorB.getInputQueueLength());
    assertEquals(0, lProcessorC.getInputQueueLength());

    assertTrue(lProcessorA.stop(1, TimeUnit.SECONDS));
    assertTrue(lProcessorB.stop(1, TimeUnit.SECONDS));
    assertTrue(lProcessorC.stop(1, TimeUnit.SECONDS));

    for (int i = 1; i <= 1000; i++)
    {
      final Integer lPoll = lIntList.poll();
      assertNotNull(lPoll);
      assertEquals(i, lPoll, 0);
    }

  }
}
