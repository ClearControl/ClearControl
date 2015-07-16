package rtlib.microscope.lightsheet.illumination.si;

public abstract class StructuredIlluminatioPatternBase
{

	protected static final double clamp01(final double x)
	{
		return Math.max(0, Math.min(1, x));
	}
}
