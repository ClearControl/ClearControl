package rtlib.core.concurrent.executors.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import rtlib.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import rtlib.core.concurrent.executors.AsynchronousSchedulerServiceAccess;
import rtlib.core.concurrent.executors.LimitedExecutionsRunnable;
import rtlib.core.concurrent.thread.ThreadUtils;

public class ExecutorServiceTests
{
	private static final int cNumberOfTasks = 1000;
	AtomicInteger mCounter = new AtomicInteger(0);

	private class ExecutorServiceTest	implements
																		AsynchronousExecutorServiceAccess,
																		AsynchronousSchedulerServiceAccess
	{

		public void doSomething() throws InterruptedException
		{
			for (int i = 0; i < cNumberOfTasks; i++)
			{
				final int j = i;
				Runnable lTask = () -> {
					// System.out.println("task-" + j);
					try
					{
						ThreadUtils.sleep(4, TimeUnit.MILLISECONDS);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					mCounter.incrementAndGet();
				};
				// System.out.println("submitting : " + j);
				Future<?> lFuture = executeAsynchronously(lTask);

				// System.out.println(" done.");

			}
		}

		public void scheduleSomething() throws InterruptedException
		{

			Runnable lTask = () -> {
				// System.out.println("scheduled task-" + j);
				mCounter.incrementAndGet();
				try
				{
					ThreadUtils.sleep(10, TimeUnit.MILLISECONDS);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

			};
			// System.out.println("submitting : " + j);

			LimitedExecutionsRunnable lLimitedExecutionsRunnable = LimitedExecutionsRunnable.wrap(lTask,
																																														100);

			lLimitedExecutionsRunnable.runNTimes(	this,
																						10,
																						TimeUnit.MILLISECONDS);

		}

	}

	@Test
	public void testAsynhronousExecution() throws InterruptedException,
																				ExecutionException,
																				TimeoutException
	{

		ExecutorServiceTest lExecutorServiceTest = new ExecutorServiceTest();

		mCounter.set(0);
		lExecutorServiceTest.doSomething();
		// System.out.print("WAITING");
		assertTrue(lExecutorServiceTest.waitForCompletion(10,
																											TimeUnit.SECONDS));
		assertEquals(cNumberOfTasks, mCounter.get());
		// System.out.println("...done");

		mCounter.set(0);
		lExecutorServiceTest.doSomething();
		// System.out.print("WAITING");
		assertFalse(lExecutorServiceTest.waitForCompletion(	10,
																												TimeUnit.MILLISECONDS));
		if (cNumberOfTasks <= mCounter.get())
			System.out.println("mCounter.get()=" + mCounter.get());
		assertTrue(cNumberOfTasks > mCounter.get());
		// System.out.println("...done");

	}

	@Test
	public void testPeriodicScheduling() throws InterruptedException,
																			ExecutionException,
																			TimeoutException
	{

		ExecutorServiceTest lExecutorServiceTest = new ExecutorServiceTest();

		mCounter.set(0);
		lExecutorServiceTest.scheduleSomething();
		// System.out.print("WAITING");
		// lExecutorServiceTest.waitForCompletion(1, TimeUnit.SECONDS);
		lExecutorServiceTest.waitForScheduleCompletion(	2000,
																										TimeUnit.MILLISECONDS);
		assertEquals(10 * 10, mCounter.get());
		// System.out.println("...done");

		mCounter.set(0);
		lExecutorServiceTest.scheduleSomething();
		// System.out.print("WAITING");
		Thread.sleep(250);
		lExecutorServiceTest.stopScheduledThreadPoolAndWaitForCompletion(	750,
																																			TimeUnit.MILLISECONDS);
		// System.out.println(mCounter.get());
		assertTrue(mCounter.get() > 10);
		assertTrue(10 * 10 / 2 > mCounter.get());
		// System.out.println("...done");

	}

}
