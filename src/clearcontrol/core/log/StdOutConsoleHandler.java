package clearcontrol.core.log;

import java.util.logging.ConsoleHandler;

public class StdOutConsoleHandler extends ConsoleHandler
{

	public StdOutConsoleHandler()
	{
		super();
		setOutputStream(System.out);
	}

}
