package rtlib.core.rgc.test;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import rtlib.core.concurrent.thread.ThreadUtils;
import rtlib.core.rgc.Cleanable;
import rtlib.core.rgc.Cleaner;
import rtlib.core.rgc.Freeable;
import rtlib.core.rgc.RessourceGarbageCollector;

public class RessourceGarbageCollectorTests
{
	static AtomicInteger sCounter = new AtomicInteger(0);

	public static final void freeRessource(long pResourceId)
	{
		sCounter.incrementAndGet();
		// System.out.println("freeing: " + pResourceId);
	}

	private static class ClassWithRessource	implements
																					Freeable,
																					Cleanable
	{
		long mSomeRessource = (long) (1000 * Math.random());
		AtomicBoolean mFree = new AtomicBoolean(false);

		{
			ClassWithRessource lClassWithRessource = this;
			RessourceGarbageCollector.register(lClassWithRessource);

			// double[] lGarbage = new double[10000000];
			// lGarbage[12345] = 1;
		}

		static class MyCleaner implements Cleaner
		{
			private long mSomeRessource2;

			public MyCleaner(long pSomeRessource)
			{
				mSomeRessource2 = pSomeRessource;
			}

			@Override
			public void run()
			{
				freeRessource(mSomeRessource2);
			}

		}

		@Override
		public Cleaner getCleaner()
		{
			return new MyCleaner(mSomeRessource);
		}

		@Override
		public void free()
		{
			mFree.set(false);
			freeRessource(mSomeRessource);
		}

		@Override
		public boolean isFree()
		{
			return mFree.get();
		}

	}

	@Test
	public void testRessourceCollection() throws InterruptedException
	{

		for (int i = 0; i < 100; i++)
		{
			ClassWithRessource a = new ClassWithRessource();
			RessourceGarbageCollector.collectNow();
			ThreadUtils.sleep(1, TimeUnit.MILLISECONDS);
		}

		for (int i = 0; i < 1000; i++)
		{
			ThreadUtils.sleep(1, TimeUnit.MILLISECONDS);
			System.gc();
			RessourceGarbageCollector.collectNow();
			// System.out.println(i);
		}

		int lNumberOfRegisteredObjects = RessourceGarbageCollector.getNumberOfRegisteredObjects();
		assertEquals(0, lNumberOfRegisteredObjects);
		// System.out.println("lNumberOfRegisteredObjects=" +
		// lNumberOfRegisteredObjects);

		// System.out.println(sCounter.get());
		assertEquals(100, sCounter.get());
	}
}
