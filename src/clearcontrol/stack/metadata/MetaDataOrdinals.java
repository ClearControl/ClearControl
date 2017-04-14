package clearcontrol.stack.metadata;

/**
 * Basic stack meta data entries
 *
 * @author royer
 */
@SuppressWarnings("javadoc")
public enum MetaDataOrdinals implements MetaDataEntryInterface<Long>
{
 TimeStampInNanoSeconds(Long.class),
 Index(Long.class),
 TimePoint(Long.class),
 Channel(Long.class);

  private final Class<Long> mClass;

  private MetaDataOrdinals(Class<Long> pClass)
  {
    mClass = pClass;
  }

  @Override
  public Class<Long> getMetaDataClass()
  {
    return mClass;
  }

}
