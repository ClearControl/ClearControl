package clearcontrol.core.log.demo;

import org.junit.Test;

import clearcontrol.core.log.Loggable;
import clearcontrol.core.log.gui.LogWindowHandler;

public class LoggingDemo implements Loggable
{

	@Test
	public void demo() throws InterruptedException
	{
		for (int i = 0; i < 100; i++)
			info("test", "bla");

		final LogWindowHandler lLogWindowHandler = LogWindowHandler.getInstance("test",
																																						768,
																																						320);

		getLogger("test").addHandler(lLogWindowHandler);

		for (int i = 0; i < 100; i++)
			info("test", "blu");

		Thread.sleep(4000);

	}

}
