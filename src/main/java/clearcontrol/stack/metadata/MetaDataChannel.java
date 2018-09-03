package clearcontrol.stack.metadata;

/**
 * Basic stack meta data entries
 *
 * @author royer
 */
@SuppressWarnings("javadoc")
public enum MetaDataChannel implements MetaDataEntryInterface<String>

{
 Channel;

  @Override
  public Class<String> getMetaDataClass()
  {
    return String.class;
  }

}
