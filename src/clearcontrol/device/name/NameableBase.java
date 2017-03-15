package clearcontrol.device.name;

public abstract class NameableBase implements NameableInterface
{
  private String mName;

  @SuppressWarnings("unused")
  private NameableBase()
  {
    super();
  }

  public NameableBase(final String pName)
  {
    super();
    mName = pName;
  }

  /* (non-Javadoc)
   * @see rtlib.core.device.NameableInterface#getName()
   */
  @Override
  public String getName()
  {
    return mName;
  }

  /* (non-Javadoc)
   * @see rtlib.core.device.NameableInterface#setName(java.lang.String)
   */
  @Override
  public void setName(final String name)
  {
    mName = name;
  }

  @Override
  public String toString()
  {
    return String.format("NameableAbstract [mName=%s]", mName);
  }
}
