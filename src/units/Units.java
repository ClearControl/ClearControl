package units;

public class Units
{

	public static final double nano2unit(final double x)
	{
		return 0.001 * 0.001 * 0.001 * x;
	}

	public static final double unit2nano(final double x)
	{
		return 1000 * 1000 * 1000 * x;
	}

	public static final double pico2milli(final double x)
	{
		return 0.001 * 0.001 * 0.001 * x;
	}

	public static final double milli2pico(final double x)
	{
		return 1000 * 1000 * 1000 * x;
	}

	public static final double nano2milli(final double x)
	{
		return 0.001 * 0.001 * x;
	}

	public static final double milli2nano(final double x)
	{
		return 1000 * 1000 * x;
	}

	public static double micro2unit(final double x)
	{
		return 0.001 * 0.001 * x;
	}

	public static double unit2micro(final double x)
	{
		return 1000 * 1000 * x;
	}

	public static final double micro2nano(final double x)
	{
		return 1000 * x;
	}

	public static final double nano2micro(final double x)
	{
		return 0.001 * x;
	}

	public static final double milli2micro(final double x)
	{
		return 1000 * x;
	}

	public static final double micro2milli(final double x)
	{
		return 0.001 * x;
	}

	public static double milli2unit(final double x)
	{
		return 0.001 * x;
	}
	
	public static double unit2milli(final double x)
	{
		return 1000 * x;
	}

}
