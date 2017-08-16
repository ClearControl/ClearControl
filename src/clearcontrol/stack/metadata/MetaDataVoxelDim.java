package clearcontrol.stack.metadata;

/**
 * Basic stack meta data entries
 *
 * @author royer
 */
@SuppressWarnings("javadoc")
public enum MetaDataVoxelDim implements MetaDataEntryInterface<Double>
{
 VoxelDimX, VoxelDimY, VoxelDimZ;

  @Override
  public Class<Double> getMetaDataClass()
  {
    return Double.class;
  }

}
