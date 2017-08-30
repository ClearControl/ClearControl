package clearcontrol.microscope.stacks.metadata;

import clearcontrol.microscope.state.AcquisitionType;
import clearcontrol.stack.metadata.MetaDataEntryInterface;

/**
 * Stack metadata entries for acquisition types
 *
 * @author royer
 */
@SuppressWarnings("javadoc")
public enum MetaDataAcquisitionType implements
                                    MetaDataEntryInterface<AcquisitionType>
{

 AcquisitionType(AcquisitionType.class);

  private final Class<AcquisitionType> mClass;

  private MetaDataAcquisitionType(Class<AcquisitionType> pClass)
  {
    mClass = pClass;
  }

  @Override
  public Class<AcquisitionType> getMetaDataClass()
  {
    return mClass;
  }

}
