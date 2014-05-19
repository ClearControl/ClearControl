package rtlib.core.parallel;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Parallel
{
	static int iCPU = Runtime.getRuntime().availableProcessors();

	/**
	 * Parallel Loop Body Interface
	 */
	public static interface LoopBody<T>
	{
		void run(T p);
	}

	/**
	 * Parallel Loop Body Interface
	 */
	public static interface LoopBodyInt
	{
		void run(int p);
	}

	/**
	 * Parallel Task Interface
	 */
	public static interface Task
	{
		void run();
	}

	/**
	 * Partition Parallel.For into Parallel.ForEach
	 */
	public static class Partition
	{
		public int start; // inclusive start point
		public int end; // exclusive ending point
	}

	public static void runTasks(final Task[] tasks)
	{
		final ExecutorService executor = Executors.newFixedThreadPool(iCPU);
		final ArrayList<Future<?>> futures = new ArrayList<Future<?>>();

		for (final Task task : tasks)
		{
			final Future<?> future = executor.submit(new Runnable()
			{
				@Override
				public void run()
				{
					task.run();
				}
			});
			futures.add(future);
		}

		for (final Future<?> f : futures)
		{
			try
			{
				f.get();
			}
			catch (final InterruptedException e)
			{
			}
			catch (final ExecutionException e)
			{
			}
		}

		executor.shutdown();
	}

	/**
	 * Parallel.ForEach
	 */
	public static <T> void forEach(	final Iterable<T> parameters,
																	final LoopBody<T> loopBody)
	{
		final ExecutorService executor = Executors.newFixedThreadPool(iCPU);
		final ArrayList<Future<?>> futures = new ArrayList<Future<?>>();

		for (final T param : parameters)
		{
			final Future<?> future = executor.submit(new Runnable()
			{
				@Override
				public void run()
				{
					loopBody.run(param);
				}
			});
			futures.add(future);
		}

		for (final Future<?> f : futures)
		{
			try
			{
				f.get();
			}
			catch (final InterruptedException e)
			{
			}
			catch (final ExecutionException e)
			{
			}
		}

		executor.shutdown();
	}

	/**
	 * Parallel.For
	 */
	public static void forInt(final int start,
														final int end,
														final LoopBodyInt loopBody)
	{
		final ExecutorService executor = Executors.newFixedThreadPool(iCPU);
		final ArrayList<Future<?>> futures = new ArrayList<Future<?>>();
		final ArrayList<Partition> partitions = create(start, end, iCPU);

		for (final Partition p : partitions)
		{
			final Future<?> future = executor.submit(new Runnable()
			{
				@Override
				public void run()
				{
					for (int i = p.start; i < p.end; i++)
					{
						loopBody.run(i);
					}
				}
			});
			futures.add(future);
		}

		for (final Future<?> f : futures)
		{
			try
			{
				f.get();
			}
			catch (final InterruptedException e)
			{
			}
			catch (final ExecutionException e)
			{
			}
		}

		executor.shutdown();
	}

	/**
	 * Create Partitions To Turn Parallel.For To Parallel.ForEach
	 */
	public static ArrayList<Partition> create(final int inclusiveStart,
																						final int exclusiveEnd)
	{
		return create(inclusiveStart, exclusiveEnd, iCPU);
	}

	public static ArrayList<Partition> create(final int inclusiveStart,
																						final int exclusiveEnd,
																						final int cores)
	{
		// increment
		final int total = exclusiveEnd - inclusiveStart;
		final double dc = (double) total / cores;
		int ic = (int) dc;

		if (ic <= 0)
		{
			ic = 1;
		}
		if (dc > ic)
		{
			ic++;
		}

		// partitions
		final ArrayList<Partition> partitions = new ArrayList<Partition>();
		if (total <= cores)
		{
			for (int i = 0; i < total; i++)
			{
				final Partition p = new Partition();
				p.start = i;
				p.end = i + 1;
				partitions.add(p);
			}
			return partitions;
		}

		int count = inclusiveStart;
		while (count < exclusiveEnd)
		{
			final Partition p = new Partition();
			p.start = count;
			p.end = count + ic;

			partitions.add(p);
			count += ic;

			// boundary check
			if (p.end >= exclusiveEnd)
			{
				p.end = exclusiveEnd;
				break;
			}
		}

		return partitions;
	}

	/** End of Parallel class */
}
