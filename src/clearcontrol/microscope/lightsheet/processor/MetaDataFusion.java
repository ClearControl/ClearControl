package clearcontrol.microscope.lightsheet.processor;

import clearcontrol.stack.metadata.MetaDataEntryInterface;

/**
 * Stack metadata entries for fusion
 *
 * @author royer
 */
@SuppressWarnings("javadoc")
public enum MetaDataFusion implements MetaDataEntryInterface<Boolean>
{
 RequestFullFusion(Boolean.class),
 RequestPerCameraFusion(Boolean.class),
 Fused(Boolean.class);

  private final Class<Boolean> mClass;

  private MetaDataFusion(Class<Boolean> pClass)
  {
    mClass = pClass;
  }

  @Override
  public Class<Boolean> getMetaDataClass()
  {
    return mClass;
  }

}
