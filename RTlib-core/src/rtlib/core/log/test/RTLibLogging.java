package rtlib.core.log.test;

import org.junit.Test;

import rtlib.core.log.Loggable;
import rtlib.core.log.gui.LogWindowHandler;

public class RTLibLogging implements Loggable
{

	@Test
	public void test() throws InterruptedException
	{
		for(int i=0; i<100; i++)
			info("test", "bla");

		final LogWindowHandler lLogWindowHandler = LogWindowHandler.getInstance("test",
																																						768,
																																						320);

		getLogger("test").addHandler(lLogWindowHandler);

		for (int i = 0; i < 100; i++)
			info("test", "blu");

		Thread.sleep(20000);

	}

}
