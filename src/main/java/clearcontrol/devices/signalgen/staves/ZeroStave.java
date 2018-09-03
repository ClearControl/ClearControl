package clearcontrol.devices.signalgen.staves;

public class ZeroStave extends ConstantStave implements StaveInterface
{
  public ZeroStave()
  {
    super("Zero", 0);
  }

  @Override
  public StaveInterface duplicate()
  {
    return new ZeroStave();
  }

}
