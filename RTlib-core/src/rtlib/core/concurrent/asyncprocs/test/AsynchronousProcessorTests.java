package rtlib.core.concurrent.asyncprocs.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

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
				//System.out.println("Processor A received:" + pInput);
				return "A" + pInput;
			}
		};

		final AsynchronousProcessorInterface<String, String> lProcessorB = new AsynchronousProcessorBase<String, String>(	"B",
																																																											10)
		{
			@Override
			public String process(final String pInput)
			{
				//System.out.println("Processor B received:" + pInput);
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
		EnhancedThread.sleep(10);
		for (int i = 0; i < 100; i++)
		{
			assertTrue(lProcessorA.passOrFail("test" + i));
			EnhancedThread.sleep(10);
		}

	}

	@Test
	public void testSimple2ProcessorPipelineWithPooledProcessor()
	{
		final AsynchronousProcessorInterface<String, String> lProcessorA = new AsynchronousProcessorBase<String, String>(	"A",
																																																											10)
		{
			@Override
			public String process(final String pInput)
			{
				// System.out.println("Processor A received:"+pInput);
				return "A" + pInput;
			}
		};

		final ProcessorInterface<String, String> lProcessor = new ProcessorInterface<String, String>()
		{

			@Override
			public String process(final String pInput)
			{
				// System.out.println("Processor B received:"+pInput);
				EnhancedThread.sleepNanos(100);
				return "B" + pInput;
			}

			@Override
			public void close() throws IOException
			{

			}
		};

		final AsynchronousProcessorPool<String, String> lProcessorB = new AsynchronousProcessorPool<String, String>("B",
																																																								10,
																																																								2,
																																																								lProcessor);

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
		EnhancedThread.sleep(10);
		for (int i = 0; i < 1000; i++)
		{
			if (lProcessorA.passOrWait("test" + i) && i % 1000 == 0)
			{
				//System.out.println(".");
			}

			if (i % 1000 == 0)
			{
				//System.out.format("Load: %g \n", lProcessorB.getLoad());
				// EnhancedThread.sleepnanos(1);
			}
		}

	}
}
