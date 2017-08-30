package clearcontrol.stack.metadata;

/**
 * Basic stack meta data entries
 *
 * @author royer
 */
@SuppressWarnings("javadoc")
public enum MetaDataOrdinals implements MetaDataEntryInterface<Long>
{
 TimeStampInNanoSeconds, Index, TimePoint, DisplayChannel;

  @Override
  public Class<Long> getMetaDataClass()
  {
    return Long.class;
  }

}
