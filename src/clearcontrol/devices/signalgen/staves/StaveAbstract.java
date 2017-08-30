package clearcontrol.devices.signalgen.staves;

import clearcontrol.core.device.name.NameableBase;

public abstract class StaveAbstract extends NameableBase
                                    implements StaveInterface

{
  private volatile boolean mEnabled = true;
  private volatile long mDurationInNanoseconds;

  /**
   * Constructor
   * 
   * @param pName
   *          stave name
   */
  public StaveAbstract(final String pName)
  {
    super(pName);
  }

  @Override
  public boolean isEnabled()
  {
    return mEnabled;
  }

  @Override
  public void setEnabled(boolean pEnabled)
  {
    mEnabled = pEnabled;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result =
           prime * result + (int) (mDurationInNanoseconds
                                   ^ (mDurationInNanoseconds >>> 32));
    result = prime * result + (mEnabled ? 1231 : 1237);
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    StaveAbstract other = (StaveAbstract) obj;
    if (mDurationInNanoseconds != other.mDurationInNanoseconds)
      return false;
    if (mEnabled != other.mEnabled)
      return false;
    return true;
  }
  /**/

  @Override
  public String toString()
  {
    return String.format("Stave [getName()=%s, mEnabled=%s, mDurationInNanoseconds=%s, ]",
                         getName(),
                         mEnabled,
                         mDurationInNanoseconds);
  }

}
