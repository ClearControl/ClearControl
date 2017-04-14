package clearcontrol.core.device.name;

/**
 * Base class for all classes that require a name
 *
 * @author royer
 */
public abstract class NameableBase implements NameableInterface
{
  private String mName;

  @SuppressWarnings("unused")
  private NameableBase()
  {
    super();
  }

  /**
   * Instanciates an object given a name
   * 
   * @param pName
   *          name
   */
  public NameableBase(final String pName)
  {
    super();
    mName = pName;
  }

  @Override
  public String getName()
  {
    return mName;
  }

  @Override
  public void setName(final String name)
  {
    mName = name;
  }

  @Override
  public String toString()
  {
    return mName;
  }
}
