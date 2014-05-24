package rtlib.core.concurrent.queues;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import rtlib.core.log.Loggable;

public class BestBlockingQueue implements Loggable
{
	public static final int cNumberOfCycles = 10;
	public static final int cQueuesCapacity = 100;
	public static final int cNumberOfPuts = 1000;

	public static double benchmarkQueue(final int pNumberOfProducers,
																			final int pNumberOfPuts,
																			final BlockingQueue<String> pBlockingQueue)
	{
		Runnable lProducer = () -> {
			for (int i = 0; i < pNumberOfPuts; i++)
			{
				try
				{
					pBlockingQueue.put(new String("P-" + i));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};

		Runnable lConsummer = () -> {
			for (int i = 0; i < pNumberOfPuts; i++)
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

		Runnable lParasite = () -> {
			double[] lArray = new double[pNumberOfPuts];
			for (int i = 0; i < pNumberOfPuts; i++)
			{
				lArray[i] = lArray[(i + (pNumberOfPuts / 2)) % pNumberOfPuts] + i;
			}
		};

		ArrayList<Thread> lProducers = new ArrayList<>();
		for (int i = 0; i < pNumberOfProducers; i++)
		{
			Thread lThreadProducer = new Thread(lProducer);
			lProducers.add(lThreadProducer);
		}

		ArrayList<Thread> lParasites = new ArrayList<>();
		for (int i = 0; i < pNumberOfProducers; i++)
		{
			Thread lThreadParasite = new Thread(lParasite);
			lParasites.add(lThreadParasite);
		}

		ArrayList<Thread> lConsumers = new ArrayList<>();
		for (int i = 0; i < pNumberOfProducers; i++)
		{
			Thread lThreadConsumer = new Thread(lConsummer);
			lConsumers.add(lThreadConsumer);
		}

		long lNanoTimeStart = System.nanoTime();

		for (Thread lProducerThread : lProducers)
			lProducerThread.start();
		for (Thread lThreadParasite : lParasites)
			lThreadParasite.start();
		for (Thread lThreadConsumer : lConsumers)
			lThreadConsumer.start();

		try
		{
			for (Thread lProducerThread : lProducers)
				lProducerThread.join();
			for (Thread lThreadConsumer : lConsumers)
				lThreadConsumer.join();

			long lNanoTimeEnd = System.nanoTime();

			double lTimeElapsedInMilliseconds = (lNanoTimeEnd - lNanoTimeStart) / (1000.0 * 1000.0);

			/*System.out.format("time elapsed for %s : \t\t%g ms \n",
												pBlockingQueue.getClass().toString(),
												lTimeElapsedInMilliseconds);/**/

			double lThroughputInPutsPerMicroseconds = (pNumberOfProducers * pNumberOfPuts) / (1000.0 * lTimeElapsedInMilliseconds);

			/*System.out.format("throughtput for %s : \t\t%g ppus (puts per us) \n",
												pBlockingQueue.getClass().toString(),
												lThroughputInPutsPerMicroseconds);/**/

			return lTimeElapsedInMilliseconds;
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		return -1;
	}

	public static final <T> Class<?> benchmarkQueues(	int pNumberOfCycles,
																										int pNumberOfPuts,
																										int pQueueCapacity)
	{
		double lBenchmarkConcurrentLinkedBlockingQueue = 0;
		double lBenchmarkLinkedBlockingQueue = 0;
		double lBenchmarkArrayBlockingQueue = 0;

		int lNumberOfProducers = Runtime.getRuntime()
																		.availableProcessors();

		for (int i = 0; i < pNumberOfCycles; i++)
		{
			// System.out.println(i);
			lBenchmarkConcurrentLinkedBlockingQueue += BestBlockingQueue.benchmarkQueue(lNumberOfProducers,
																																									pNumberOfPuts,
																																									new ConcurrentLinkedBlockingQueue<String>(pQueueCapacity));
			lBenchmarkLinkedBlockingQueue += BestBlockingQueue.benchmarkQueue(lNumberOfProducers,
																																				pNumberOfPuts,
																																				new LinkedBlockingQueue<String>(pQueueCapacity));
			lBenchmarkArrayBlockingQueue += BestBlockingQueue.benchmarkQueue(	lNumberOfProducers,
																																				pNumberOfPuts,
																																				new ArrayBlockingQueue<String>(pQueueCapacity));
		}

		BestBlockingQueue lBestBlockingQueue = new BestBlockingQueue();
		lBestBlockingQueue.info("Concurrent",
														"ConcurrentLinkedBlockingQueue -> " + lBenchmarkConcurrentLinkedBlockingQueue
																+ " ms");

		lBestBlockingQueue.info("Concurrent",
														"LinkedBlockingQueue -> " + lBenchmarkLinkedBlockingQueue
																+ " ms");

		lBestBlockingQueue.info("Concurrent",
														"ArrayBlockingQueue -> " + lBenchmarkArrayBlockingQueue
																+ " ms");

		/*if (lBenchmarkConcurrentLinkedBlockingQueue < lBenchmarkLinkedBlockingQueue && lBenchmarkConcurrentLinkedBlockingQueue < lBenchmarkArrayBlockingQueue)
			return ConcurrentLinkedBlockingQueue.class;/**/
		if (lBenchmarkLinkedBlockingQueue < lBenchmarkArrayBlockingQueue)
			return LinkedBlockingQueue.class;
		else
			return ArrayBlockingQueue.class;

	}

	private static AtomicReference<Class<?>> sBestQueueClass = new AtomicReference<>();

	static void ensureBestBlockingQueueDetermined()
	{
		if (sBestQueueClass.get() == null)
		{
			sBestQueueClass.set(benchmarkQueues(cNumberOfCycles,
																					cNumberOfPuts,
																					cQueuesCapacity));
		}
	}/**/

	public static <T> BlockingQueue<T> newBoundedQueue(int pQueuesCapacity)
	{
		ensureBestBlockingQueueDetermined();

		try
		{
			if (sBestQueueClass.get() == null && Integer.MIN_VALUE != pQueuesCapacity)
				return new ArrayBlockingQueue<T>(pQueuesCapacity);

			Constructor<?> lDeclaredConstructors = sBestQueueClass.get()
																														.getConstructor(new Class[]
																														{ int.class });

			@SuppressWarnings("unchecked")
			BlockingQueue<T> lNewInstance = (BlockingQueue<T>) lDeclaredConstructors.newInstance(pQueuesCapacity);

			return lNewInstance;
		}
		catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e)
		{
			throw new RuntimeException(	"Cannot instantiate a 'best' bloking queue",
																	e);
		}
	}

}
