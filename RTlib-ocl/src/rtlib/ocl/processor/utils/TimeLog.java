package rtlib.ocl.processor.utils;

public class TimeLog
{
	public static double startT = 0;
	public static double elapsedT = 0;

	public static void start()
	{
		startT = System.nanoTime() / 1.e6;

	}

	public static double stop()
	{
		final double tmpT = startT;
		startT = System.nanoTime() / 1.e6;
		elapsedT += startT - tmpT;
		return startT - tmpT;
	}
}
