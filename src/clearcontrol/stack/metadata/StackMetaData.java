package clearcontrol.stack.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Stack meta data
 *
 * @author royer
 */
public class StackMetaData
{
  HashMap<MetaDataEntryInterface<?>, Object> mMetaDataMap;

  /**
   * Instanciates an empty meta data object
   */
  public StackMetaData()
  {
    super();
    mMetaDataMap = new HashMap<>(10);
  }

  /**
   * Instanciates a meta data object
   * 
   * @param pStackMetaData
   *          met data object
   */
  public StackMetaData(StackMetaData pStackMetaData)
  {
    super();
    mMetaDataMap = new HashMap<>(pStackMetaData.mMetaDataMap);
  }

  /**
   * Sets the meta data value for a given key
   * 
   * @param pEntryKey
   *          entry key
   * @param pValue
   *          value
   */

  public <T> void addEntry(MetaDataEntryInterface<T> pEntryKey,
                           T pValue)
  {
    if (!(pEntryKey.getMetaDataClass().isInstance(pValue)))
      throw new IllegalArgumentException(String.format("Value of metadata '%s' value must be of type %s.",
                                                       pEntryKey,
                                                       pEntryKey.getMetaDataClass()));

    mMetaDataMap.put(pEntryKey, pValue);
  }

  /**
   * Removes all meta data entries of a given type
   * 
   * @param pEntriesClass
   *          type of entries to remove
   */
  public <T> void removeAllEntries(Class<T> pEntriesClass)
  {

    for (Entry<MetaDataEntryInterface<?>, Object> lEntry : new ArrayList<>(mMetaDataMap.entrySet()))
    {
      if (pEntriesClass.isInstance(lEntry.getKey()))
        mMetaDataMap.remove(lEntry.getKey());
    }
  }

  /**
   * Removed the given meta data entry
   * 
   * @param pEntryKey
   *          entry to remove
   */

  public <T> void removeEntry(MetaDataEntryInterface<T> pEntryKey)
  {
    mMetaDataMap.remove(pEntryKey);
  }

  /**
   * Returns true if this metadata object contains the given entry key
   * 
   * @param pEntryKey
   *          entry
   * @return true -> entry(key) present
   */
  public <T> boolean hasEntry(MetaDataEntryInterface<T> pEntryKey)
  {
    return mMetaDataMap.containsKey(pEntryKey);
  }

  /**
   * Returns true if this metadata object contains the given value
   * 
   * @param pValue
   *          value
   * @return true -> value present
   */
  public <T> boolean hasValue(Object pValue)
  {
    return mMetaDataMap.containsValue(pValue);
  }

  /**
   * Returns a given meta data entry
   * 
   * 
   * @param pEntryKey
   *          key
   * @return value
   */
  @SuppressWarnings("unchecked")
  public <T> T getValue(MetaDataEntryInterface<T> pEntryKey)
  {
    T lT = (T) mMetaDataMap.get(pEntryKey);

    if (lT != null && !(pEntryKey.getMetaDataClass().isInstance(lT)))
      throw new IllegalArgumentException(String.format("Value of metadata '%s' value must be of type %s.",
                                                       pEntryKey,
                                                       pEntryKey.getMetaDataClass()));

    return lT;
  }

  /**
   * Clears all entries in this metadata object.
   */
  public void clear()
  {
    mMetaDataMap.clear();
  }

  /**
   * Adds all the entries from the provided metadata object to this metadata
   * object
   * 
   * @param pMetaData
   *          metadata to copy entries from
   */
  public void addAll(StackMetaData pMetaData)
  {
    mMetaDataMap.putAll(pMetaData.mMetaDataMap);
  }

  /**
   * Clones this meta data
   * 
   * @return cloned meta data
   */

  @Override
  public StackMetaData clone()
  {
    return new StackMetaData(this);
  }

  /**
   * Returns stack's index
   * 
   * @return stack's index
   */

  public Long getIndex()
  {
    return getValue(MetaDataOrdinals.Index);
  }

  /**
   * Sets the stack's index
   * 
   * @param pStackIndex
   *          stack's index
   */

  public void setIndex(final long pStackIndex)
  {
    addEntry(MetaDataOrdinals.Index, pStackIndex);
  }

  /**
   * Returns the time stamp in nanoseconds
   * 
   * @return time stamp in nanoseconds
   */

  public Long getTimeStampInNanoseconds()
  {
    return getValue(MetaDataOrdinals.TimeStampInNanoSeconds);
  }

  /**
   * Sets the time stamp in nanoseconds
   * 
   * @param pTimeStampInNanoseconds
   *          time stamp in nanoseconds
   */

  public void setTimeStampInNanoseconds(final long pTimeStampInNanoseconds)
  {
    addEntry(MetaDataOrdinals.TimeStampInNanoSeconds,
             pTimeStampInNanoseconds);
  }

  /**
   * Returns the voxel dimension along x axis
   * 
   * @return voxel dimension along x axis
   */

  public Double getVoxelDimX()
  {
    return getValue(MetaDataVoxelDim.VoxelDimX);
  }

  /**
   * Sets voxel dimensions along the x axis
   * 
   * @param pVoxelDimX
   *          voxel dimensions along the x axis
   */

  public void setVoxelDimX(final double pVoxelDimX)
  {
    addEntry(MetaDataVoxelDim.VoxelDimX, pVoxelDimX);
  }

  /**
   * Returns the voxel dimension along y axis
   * 
   * @return voxel dimension along y axis
   */

  public Double getVoxelDimY()
  {
    return getValue(MetaDataVoxelDim.VoxelDimY);
  }

  /**
   * Sets voxel dimensions along the y axis
   * 
   * @param pVoxelDimY
   *          voxel dimensions along the y axis
   */

  public void setVoxelDimY(final double pVoxelDimY)
  {
    addEntry(MetaDataVoxelDim.VoxelDimY, pVoxelDimY);
  }

  /**
   * Returns the voxel dimension along z axis
   * 
   * @return voxel dimension along z axis
   */

  public Double getVoxelDimZ()
  {
    return getValue(MetaDataVoxelDim.VoxelDimZ);
  }

  /**
   * Sets voxel dimensions along the z axis
   * 
   * @param pVoxelDimZ
   *          voxel dimensions along the z axis
   */

  public void setVoxelDimZ(final double pVoxelDimZ)
  {
    addEntry(MetaDataVoxelDim.VoxelDimZ, pVoxelDimZ);
  }

  @Override
  public String toString()
  {
    return mMetaDataMap.toString();
  }

}
