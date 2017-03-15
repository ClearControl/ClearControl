package clearcontrol.microscope.lightsheet.component.lightsheet.si;

public abstract class StructuredIlluminationPatternBase
{

  protected static final double clamp01(final double x)
  {
    return Math.max(0, Math.min(1, x));
  }
}
