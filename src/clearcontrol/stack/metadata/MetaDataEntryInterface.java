package clearcontrol.stack.metadata;

/**
 * Interface for individual metadata entries
 *
 * @author royer
 * @param <T>
 *          entry type
 */
public interface MetaDataEntryInterface<T>
{
  /**
   * Returns the meta data value class
   * 
   * @return meta data value class
   */
  Class<T> getMetaDataClass();

}
