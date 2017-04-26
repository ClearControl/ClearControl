package clearcontrol.stack.metadata;

/**
 * Basic stack meta data entries
 *
 * @author royer
 */
@SuppressWarnings("javadoc")
public enum MetaDataChannel implements MetaDataEntryInterface<String>
{
 Channel(String.class);

  private final Class<String> mClass;

  private MetaDataChannel(Class<String> pClass)
  {
    mClass = pClass;
  }

  @Override
  public Class<String> getMetaDataClass()
  {
    return mClass;
  }

}
