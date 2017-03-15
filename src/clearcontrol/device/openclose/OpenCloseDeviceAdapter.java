package clearcontrol.device.openclose;

public class OpenCloseDeviceAdapter implements
                                    OpenCloseDeviceInterface
{
  @Override
  public boolean open()
  {
    return true;
  }

  @Override
  public boolean close()
  {
    return true;
  }
}
