package rtlib.core.parallel.test;

import java.util.ArrayList;

import org.junit.Test;

import rtlib.core.parallel.Parallel;
import rtlib.core.parallel.Parallel.LoopBody;
import rtlib.core.parallel.Parallel.LoopBodyInt;
import rtlib.core.parallel.Parallel.Partition;
import rtlib.core.parallel.Parallel.Task;

public class ParallelTests
{

	@Test
	public void test()
	{

		// sample data
		final ArrayList<String> ss = new ArrayList<String>();

		final String[] s =
		{ "a", "b", "c", "d", "e", "f", "g" };
		for (final String z : s)
		{
			ss.add(z);
		}
		final int m = ss.size();

		// parallel-for loop
		System.out.println("Parallel.For loop:");
		Parallel.forInt(0, m, new LoopBodyInt()
		{
			@Override
			public void run(final int i)
			{
				System.out.println(i + "\t" + ss.get(i));
			}
		});

		// parallel for-each loop
		System.out.println("Parallel.ForEach loop:");
		Parallel.forEach(ss, new LoopBody<String>()
		{
			@Override
			public void run(final String p)
			{
				System.out.println(p);
			}
		});

		// partitioned parallel loop
		System.out.println("Partitioned Parallel loop:");
		Parallel.forEach(Parallel.create(0, m), new LoopBody<Partition>()
		{
			@Override
			public void run(final Partition p)
			{
				for (int i = p.start; i < p.end; i++)
				{
					System.out.println(i + "\t" + ss.get(i));
				}
			}
		});

		// parallel tasks
		System.out.println("Parallel Tasks:");
		Parallel.runTasks(new Task[]
		{
			// task-1
			new Task()
			{
				@Override
				public void run()
				{
					for (int i = 0; i < 3; i++)
					{
						System.out.println(i + "\t" + ss.get(i));
					}
				}
			},

			// task-2
			new Task()
			{
				@Override
				public void run()
				{
					for (int i = 3; i < 6; i++)
					{
						System.out.println(i + "\t" + ss.get(i));
					}
				}
			} });

	}

}
