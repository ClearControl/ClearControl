package rtlib.core.concurrent.queues.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Test;

import rtlib.core.concurrent.queues.BestBlockingQueue;
import rtlib.core.concurrent.queues.ConcurrentLinkedBlockingQueue;

public class ConcurrentLinkedBlockingQueueTests
{

	@Test
	public void testBasics() throws InterruptedException
	{
		ConcurrentLinkedBlockingQueue<String> lConcurrentLinkedBlockingQueue = new ConcurrentLinkedBlockingQueue<>(4);

		lConcurrentLinkedBlockingQueue.put("1");
		assertEquals(	3,
									lConcurrentLinkedBlockingQueue.remainingCapacity());

		lConcurrentLinkedBlockingQueue.put("2");
		assertEquals(	2,
									lConcurrentLinkedBlockingQueue.remainingCapacity());

		lConcurrentLinkedBlockingQueue.put("3");
		assertEquals(	1,
									lConcurrentLinkedBlockingQueue.remainingCapacity());

		lConcurrentLinkedBlockingQueue.put("4");
		assertEquals(	0,
									lConcurrentLinkedBlockingQueue.remainingCapacity());

		assertEquals("1", lConcurrentLinkedBlockingQueue.take());
		assertEquals("2", lConcurrentLinkedBlockingQueue.take());
		assertEquals("3", lConcurrentLinkedBlockingQueue.take());
		assertEquals("4", lConcurrentLinkedBlockingQueue.take());
	}

	private static final int cQueuesCapacity = BestBlockingQueue.cQueuesCapacity;
	private static final int cNumberOfPuts = BestBlockingQueue.cNumberOfPuts;

	@Test
	public void testPerformance() throws InterruptedException
	{
		try
		{
			double lBenchmarkConcurrentLinkedBlockingQueue = 0;
			double lBenchmarkLinkedBlockingQueue = 0;
			double lBenchmarkArrayBlockingQueue = 0;

			int lAvailableProcessors = Runtime.getRuntime().availableProcessors();
			for (int i = 1; i < lAvailableProcessors+1 ; i++)
			{
				lBenchmarkConcurrentLinkedBlockingQueue += BestBlockingQueue.benchmarkQueue(i,
																																										cNumberOfPuts,
																																										new ConcurrentLinkedBlockingQueue<String>(cQueuesCapacity));
				lBenchmarkLinkedBlockingQueue += BestBlockingQueue.benchmarkQueue(i,
																																					cNumberOfPuts,
																																					new LinkedBlockingQueue<String>(cQueuesCapacity));
				lBenchmarkArrayBlockingQueue += BestBlockingQueue.benchmarkQueue(	i,
																																				cNumberOfPuts,
																																				new ArrayBlockingQueue<String>(cQueuesCapacity));
			}
			
			lBenchmarkConcurrentLinkedBlockingQueue = lBenchmarkConcurrentLinkedBlockingQueue/lAvailableProcessors;
			lBenchmarkLinkedBlockingQueue = lBenchmarkLinkedBlockingQueue/lAvailableProcessors;
			lBenchmarkArrayBlockingQueue = lBenchmarkArrayBlockingQueue/lAvailableProcessors;
			

			System.out.println("ConcurrentLinkedBlockingQueue -> " + lBenchmarkConcurrentLinkedBlockingQueue
													+ " ms");

			System.out.println("LinkedBlockingQueue -> " + lBenchmarkLinkedBlockingQueue
													+ " ms");

			System.out.println("ArrayBlockingQueue -> " + lBenchmarkArrayBlockingQueue
													+ " ms");

			assertTrue(lBenchmarkConcurrentLinkedBlockingQueue < 30);
			assertTrue(lBenchmarkLinkedBlockingQueue < 30);
			assertTrue(lBenchmarkArrayBlockingQueue < 30);

			//assertTrue(lBenchmarkConcurrentLinkedBlockingQueue < lBenchmarkLinkedBlockingQueue);
			//assertTrue(lBenchmarkConcurrentLinkedBlockingQueue < lBenchmarkArrayBlockingQueue);
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			fail();
		}

	}



	@Test
	public void testBestBlockingQueue() throws InterruptedException
	{
		BlockingQueue<String> lNewQueue = BestBlockingQueue.<String> newQueue(cQueuesCapacity);

		assertNotNull(lNewQueue);

		BestBlockingQueue.benchmarkQueue(2, cNumberOfPuts, lNewQueue);


	}
}

/**/