package clearcontrol.devices.signalgen.staves;

public class ClosurePatternSteppingStave extends PatternSteppingStave
                                         implements StaveInterface
{

  private final SteppingFunction mSteppingFunction;

  public ClosurePatternSteppingStave(final String pName,
                                     SteppingFunction pSteppingFunction)
  {
    super(pName);
    mSteppingFunction = pSteppingFunction;
  }

  @Override
  public StaveInterface duplicate()
  {
    return new ClosurePatternSteppingStave(getName(),
                                           getSteppingFunction());
  }


  public SteppingFunction getSteppingFunction()
  {
    return mSteppingFunction;
  }

  @Override
  public float function(int pIndex)
  {
    return getSteppingFunction().function(pIndex);
  }


}
