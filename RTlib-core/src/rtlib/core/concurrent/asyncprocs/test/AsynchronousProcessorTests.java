package rtlib.core.concurrent.asyncprocs.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rtlib.core.concurrent.asyncprocs.AsynchronousProcessorBase;
import rtlib.core.concurrent.asyncprocs.AsynchronousProcessorInterface;
import rtlib.core.concurrent.asyncprocs.AsynchronousProcessorPool;
import rtlib.core.concurrent.asyncprocs.ProcessorInterface;
import rtlib.core.concurrent.thread.EnhancedThread;

public class AsynchronousProcessorTests
{

	@Test
	public void testSimple2ProcessorPipeline()
	{
		final AsynchronousProcessorInterface<String, String> lProcessorA = new AsynchronousProcessorBase<String, String>(	"A",
																																																											10)
		{
			@Override
			public String process(final String pInput)
			{
				// System.out.println("Processor A received:" + pInput);
				return "A" + pInput;
			}
		};

		final AsynchronousProcessorInterface<String, String> lProcessorB = new AsynchronousProcessorBase<String, String>(	"B",
																																																											10)
		{
			@Override
			public String process(final String pInput)
			{
				// System.out.println("Processor B received:" + pInput);
				return "B" + pInput;
			}
		};

		lProcessorA.connectToReceiver(lProcessorB);
		lProcessorA.start();
		lProcessorB.start();

		boolean hasFailed = false;
		for (int i = 0; i < 100; i++)
		{
			hasFailed |= lProcessorA.passOrFail("test" + i);
			// if(i>50) assertFalse();
		}
		assertTrue(hasFailed);
		EnhancedThread.sleep(1);
		for (int i = 0; i < 100; i++)
		{
			assertTrue(lProcessorA.passOrFail("test" + i));
			EnhancedThread.sleep(1);
		}

		lProcessorB.stop();
		lProcessorB.waitToFinish(1, TimeUnit.SECONDS);
		lProcessorA.stop();
		lProcessorA.waitToFinish(1, TimeUnit.SECONDS);



	}

	@Test
	public void testSimple2ProcessorPipelineWithPooledProcessor() throws InterruptedException
	{
		final AsynchronousProcessorInterface<Integer, Integer> lProcessorA = new AsynchronousProcessorBase<Integer, Integer>(	"A",
																																																													10)
		{
			@Override
			public Integer process(final Integer pInput)
			{
				EnhancedThread.sleepNanos((long) (Math.random() * 1000000));
				return pInput;
			}
		};

		final ProcessorInterface<Integer, Integer> lProcessor = new ProcessorInterface<Integer, Integer>()
		{

			@Override
			public Integer process(final Integer pInput)
			{
				// System.out.println("Processor B received:"+pInput);
				EnhancedThread.sleepNanos((long) (Math.random() * 1000000));
				return pInput;
			}

			@Override
			public void close() throws IOException
			{

			}
		};

		final AsynchronousProcessorPool<Integer, Integer> lProcessorB = new AsynchronousProcessorPool<>("B",
																																																		10,
																																																		2,
																																																		lProcessor);

		ConcurrentLinkedQueue<Integer> lIntList = new ConcurrentLinkedQueue<>();

		final AsynchronousProcessorInterface<Integer, Integer> lProcessorC = new AsynchronousProcessorBase<Integer, Integer>(	"C",
																																																													10)
		{
			@Override
			public Integer process(final Integer pInput)
			{
				EnhancedThread.sleepNanos((long) (Math.random() * 1000000));
				if (pInput > 0)
					lIntList.add(pInput);
				return pInput;
			}
		};

		lProcessorA.connectToReceiver(lProcessorC);
		// lProcessorA.connectToReceiver(lProcessorB);
		// lProcessorB.connectToReceiver(lProcessorC);
		lProcessorA.start();
		lProcessorB.start();
		lProcessorC.start();

		boolean hasFailed = false;
		for (int i = 0; i < 100; i++)
		{
			hasFailed |= lProcessorA.passOrFail(0);
		}
		assertTrue(hasFailed);

		EnhancedThread.sleep(10);
		for (int i = 1; i <= 1000; i++)
		{
			lProcessorA.passOrWait(i);
		}

		lProcessorA.waitToFinish(10, TimeUnit.SECONDS);
		lProcessorB.waitToFinish(10, TimeUnit.SECONDS);
		lProcessorC.waitToFinish(10, TimeUnit.SECONDS);

		lProcessorA.stop();
		lProcessorB.stop();
		lProcessorC.stop();

		for (int i = 1; i <= 1000; i++)
		{
			Integer lPoll = lIntList.poll();
			assertEquals(i, lPoll, 0);
		}

	}
}
