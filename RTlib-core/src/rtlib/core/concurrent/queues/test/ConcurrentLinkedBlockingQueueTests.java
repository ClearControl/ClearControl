package rtlib.core.concurrent.queues.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Test;

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

	private static final int cQueuesCapacity = 100;
	private static final int cNumberOfPuts = 1000000;

	@Test
	public void testPerformance() throws InterruptedException
	{
		double lBenchmarkConcurrentLinkedBlockingQueue = 0;
		double lBenchmarkLinkedBlockingQueue = 0;
		double lBenchmarkArrayBlockingQueue = 0;

		for (int i = 0; i < 10; i++)
		{
			lBenchmarkConcurrentLinkedBlockingQueue = benchmarkQueue(new ConcurrentLinkedBlockingQueue<String>(cQueuesCapacity));
			lBenchmarkLinkedBlockingQueue = benchmarkQueue(new LinkedBlockingQueue<String>(cQueuesCapacity));
			lBenchmarkArrayBlockingQueue = benchmarkQueue(new ArrayBlockingQueue<String>(cQueuesCapacity));
		}

		assertTrue(lBenchmarkConcurrentLinkedBlockingQueue < lBenchmarkLinkedBlockingQueue);
		assertTrue(lBenchmarkConcurrentLinkedBlockingQueue < lBenchmarkArrayBlockingQueue);

	}

	private double benchmarkQueue(final BlockingQueue<String> pBlockingQueue) throws InterruptedException
	{
		Runnable lProducer1 = () -> {
			for (int i = 0; i < cNumberOfPuts; i++)
			{
				try
				{
					pBlockingQueue.put(new String("P1-" + i));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};

		Runnable lProducer2 = () -> {
			for (int i = 0; i < cNumberOfPuts; i++)
			{
				try
				{
					pBlockingQueue.put(new String("P2-" + i));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};

		Runnable lConsummer1 = () -> {
			for (int i = 0; i < cNumberOfPuts; i++)
			{
				try
				{
					String lTake = pBlockingQueue.take();
					// System.out.println(lTake);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};

		Runnable lConsummer2 = () -> {
			for (int i = 0; i < cNumberOfPuts; i++)
			{
				try
				{
					String lTake = pBlockingQueue.take();
					// System.out.println(lTake);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};

		Thread lThreadP1 = new Thread(lProducer1);
		Thread lThreadP2 = new Thread(lProducer2);
		Thread lThreadC1 = new Thread(lConsummer1);
		Thread lThreadC2 = new Thread(lConsummer2);

		long lNanoTimeStart = System.nanoTime();
		lThreadP1.start();
		lThreadP2.start();
		lThreadC1.start();
		lThreadC2.start();

		lThreadP1.join();
		lThreadP2.join();
		lThreadC1.join();
		lThreadC2.join();
		long lNanoTimeEnd = System.nanoTime();

		double lTimeElapsedInMilliseconds = (lNanoTimeEnd - lNanoTimeStart) / (1000.0 * 1000.0);

		System.out.format("time elapsed for %s : \t\t%g ms \n",
											pBlockingQueue.getClass().toString(),
											lTimeElapsedInMilliseconds);

		double lThroughputInPutsPerMicroseconds = (2 * cNumberOfPuts) / (1000.0 * lTimeElapsedInMilliseconds);

		System.out.format("throughtput for %s : \t\t%g ppus (puts per us) \n",
											pBlockingQueue.getClass().toString(),
											lThroughputInPutsPerMicroseconds);

		return lTimeElapsedInMilliseconds;
	}
}

/*

package misc_2011_10_26_01;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import misc_2011_10_26_01.BlockingQueuesPerf.MyRBBlockingQueue;
import misc_2011_10_26_01.BlockingQueuesPerf.MyRBBlockingQueueAS;

import com.lmax.disruptor.ClaimStrategy;
import com.lmax.disruptor.ClaimStrategy.Option;
import com.lmax.disruptor.WaitStrategy;

public class GeneralBlockingQueueTest {

	private static final com.lmax.disruptor.WaitStrategy.Option PUB_WAITING = WaitStrategy.Option.YIELDING;
	private static final Option PUB_THREADING = ClaimStrategy.Option.SINGLE_THREADED;
	
	private static class HackedConcQueue<E> extends ConcurrentLinkedQueue<E> implements BlockingQueue<E>{

		@Override
		public void put(E event) throws InterruptedException {
			do {
				if (offer(event)) {
					return;
				}
				// Note interruption check after offer as spec says only done when blocking
				if (Thread.currentThread().isInterrupted()) {
					throw new InterruptedException();
				}
				Thread.yield();
			} while (true);
		}

		@Override
		public E take() throws InterruptedException {
			E result;
			do {
				result = poll();
				if (result != null) {
					return result;
				}
				// Note interruption check after poll as spec says only done when blocking
				if (Thread.currentThread().isInterrupted()) {
					throw new InterruptedException();
				}
				Thread.yield();
			} while (true);
		}

		@Override
		public int drainTo(Collection<? super E> arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int drainTo(Collection<? super E> arg0, int arg1) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean offer(E arg0, long arg1, TimeUnit arg2)
				throws InterruptedException {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public E poll(long arg0, TimeUnit arg1) throws InterruptedException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int remainingCapacity() {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}
	
	private static BlockingQueue<Integer> createHackedConcQueue(int size) {
		return new HackedConcQueue<Integer>();
	}

	private static BlockingQueue<Integer> createArrayBlockingQueue(int size) {
		return new ArrayBlockingQueue<Integer>(size);
	}

	private static BlockingQueue<Integer> createLinkedBlockingQueue(int size) {
		return new LinkedBlockingQueue<Integer>(size);
	}

	private static BlockingQueue<Integer> createDisruptorQueue(int size) {
		return new DisruptorQueue<Integer>(size, PUB_THREADING, PUB_WAITING);
	}
	
	private static BlockingQueue<Integer> createMyRBBlockingQueue(int size) {
		return new MyRBBlockingQueue<Integer>(size, PUB_THREADING, PUB_WAITING);
	}

	private static BlockingQueue<Integer> createMyRBBlockingQueueAS(int size) {
		return new MyRBBlockingQueueAS<Integer>(size, PUB_THREADING, PUB_WAITING);
	}

	private static void testSinglePublisherNConsumersQueue(final BlockingQueue<Integer> queue, int threads, final int expected) throws Exception {

	    // Stabalize env
        Thread.sleep(2000); // TODO jeff For faster benches
	    System.gc();

	    // Start up consumers
	    final CountDownLatch latch = new CountDownLatch(1);
	    final AtomicLong al = new AtomicLong(0);
	    Thread runThreads[] = new Thread[threads];
	    for (int i=0; i<threads; i++) {
	        runThreads[i] = new Thread(new Runnable() {
	            @Override
	            public void run() {

	                while (true)
	                {
	                    try {
	                        queue.take();
	                        if (al.incrementAndGet() == expected) {
	                            latch.countDown();
	                        }
	                    } catch (final InterruptedException ex) {
	                        break;
	                    }
	                }

	            }
	        });
	        runThreads[i].start();
	    }

	    // Let consumers settle
	    Thread.sleep(2000); // TODO jeff For faster benches

	    // Publish data
	    long start = System.nanoTime();
	    for (int i=0; i<expected; i++) {
	        queue.put(i);
	    }

	    // Wait for results
	    latch.await();
	    long deltaNano = System.nanoTime() - start;

	    // Stop consumers
	    // TODO jeff Using it as stopper, since wait strategy might not check interruption status
	    queue.clear();
	    for (int i=0; i<threads; i++) {
	        runThreads[i].interrupt();
	        runThreads[i].join();
	    }

	    long opsPerSecond = 1000000000 / (deltaNano / expected);
	    System.out.println("Operations/sec  = " + opsPerSecond + " for " + queue.getClass().getSimpleName());
	}

	public static void main(String[] args) throws Exception {

	    // TODO jeff Sized figures down for my usual programming mule (silent but few weak cores ;).
		int size = (int)1024 * 16/4; // TODO jeff Smaller arrays help a lot with some CPUs.
		int threads = 4;
		int events = size*10000;

		System.out.println("Threads         = " + threads);
		System.out.println("Events          = " + events);
		System.out.println("QueueSize       = " + size);

        // TODO jeff multiple runs (can't do loop in method, for DisruptorQueue would block due to old threads-local sequences)
		testSinglePublisherNConsumersQueue(createHackedConcQueue(size), threads, events);
		testSinglePublisherNConsumersQueue(createDisruptorQueue(size), threads, events);
		testSinglePublisherNConsumersQueue(createMyRBBlockingQueue(size), threads, events);
		testSinglePublisherNConsumersQueue(createMyRBBlockingQueueAS(size), threads, events);
//		testSinglePublisherNConsumersQueue(createArrayBlockingQueue(size), threads, events);
//		testSinglePublisherNConsumersQueue(createLinkedBlockingQueue(size), threads, events);
	}

}

/**/