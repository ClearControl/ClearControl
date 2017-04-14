package clearcontrol.stack.metadata;

/**
 * Basic stack meta data entries
 *
 * @author royer
 */
@SuppressWarnings("javadoc")
public enum MetaDataVoxelDim implements MetaDataEntryInterface<Double>
{
 VoxelDimX(Double.class),
 VoxelDimY(Double.class),
 VoxelDimZ(Double.class);

  private final Class<Double> mClass;

  private MetaDataVoxelDim(Class<Double> pClass)
  {
    mClass = pClass;
  }

  @Override
  public Class<Double> getMetaDataClass()
  {
    return mClass;
  }

}
