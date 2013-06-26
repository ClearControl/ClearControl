package thread.test;

import org.junit.Test;

import thread.EnhancedThread;

public class EnhancedThreadTest
{

	@Test
	public void testEnhancedThread()
	{
		final EnhancedThread lEnhancedThread = new EnhancedThread("test")
		{
			int i = 0;

			@Override
			public boolean initiate()
			{
				System.out.println("initiate");
				return true;
			}

			@Override
			public boolean loop()
			{
				i++;
				System.out.println(i);
				return i < 100000;
			}

			@Override
			public boolean terminate()
			{
				System.out.println("terminate");
				return true;
			}
		};

		lEnhancedThread.start();
		EnhancedThread.sleep(1);
		lEnhancedThread.pause();
		System.out.println("Suspended");
		EnhancedThread.sleep(100);
		System.out.println("Resuming");
		lEnhancedThread.resume();
		EnhancedThread.sleep(1);
		lEnhancedThread.stop();
		lEnhancedThread.start();
		lEnhancedThread.start();
		lEnhancedThread.waitToFinish();

	}

}
