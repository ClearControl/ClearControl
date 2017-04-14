package clearcontrol.core.device.name;

/**
 * Interface for classes that need to set their name
 *
 * @author royer
 */
public interface NameableInterface extends ReadOnlyNameableInterface
{

  /**
   * Sets the name of this object
   * 
   * @param pName
   *          name
   */
  public abstract void setName(String pName);

}