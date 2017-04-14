package clearcontrol.microscope.stacks.metadata;

import clearcontrol.stack.metadata.MetaDataEntryInterface;

/**
 * Basic stack meta data entries
 *
 * @author royer
 */
@SuppressWarnings("javadoc")
public enum MetaDataView implements MetaDataEntryInterface<Integer>
{

 Camera(Integer.class), LightSheet(Integer.class);

  private final Class<Integer> mClass;

  private MetaDataView(Class<Integer> pClass)
  {
    mClass = pClass;
  }

  @Override
  public Class<Integer> getMetaDataClass()
  {
    return mClass;
  }

}
