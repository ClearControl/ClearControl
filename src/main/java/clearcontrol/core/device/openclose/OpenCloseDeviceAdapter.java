package clearcontrol.core.device.openclose;

/**
 * Adapter class that provides default implementations of the open and close
 * methods
 * 
 * @author royer
 */
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
