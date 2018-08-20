package clearcontrol.stack.metadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Stack meta data
 *
 * @author royer
 */
public class StackMetaData
{
  private static final ObjectMapper cMapper = new ObjectMapper();

  HashMap<String, Object> mMetaDataMap;

  /**
   * Instantiates an empty meta data object
   */
  public StackMetaData()
  {
    super();
    mMetaDataMap = new HashMap<>(10);
  }

  /**
   * Instantiates a meta data object
   * 
   * @param pStackMetaData
   *          meta data object
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
    mMetaDataMap.put(pEntryKey.toString(), pValue);
  }

  /**
   * Sets the meta data value for a given key
   * 
   * @param pEntryKey
   *          entry key
   * @param pValue
   *          value
   */

  public <T> void addEntry(String pEntryKey, T pValue)
  {
    mMetaDataMap.put(pEntryKey, pValue);
  }

  /**
   * Removes all meta data entries of a given type
   * 
   * @param pEntriesClass
   *          class of entries to remove
   */
  public <T> void removeAllEntries(Class<T> pEntriesClass)
  {

    for (Entry<String, Object> lEntry : new ArrayList<>(mMetaDataMap.entrySet()))
    {
      if (pEntriesClass.isInstance(lEntry.getValue()))
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
    mMetaDataMap.remove(pEntryKey.toString());
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
    return mMetaDataMap.containsKey(pEntryKey.toString());
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
    T lT = (T) mMetaDataMap.get(pEntryKey.toString());

    if (lT != null && !(pEntryKey.getMetaDataClass().isInstance(lT)))
    {
      if (lT.getClass() == Long.class
          && pEntryKey.getMetaDataClass() == Integer.class)
        return (T) lT;
      else if (lT.getClass() == Integer.class
               && pEntryKey.getMetaDataClass() == Long.class)
        return (T) lT;

      throw new IllegalArgumentException(String.format("Value of metadata '%s' value must be of type %s and not %s.",
                                                       pEntryKey,
                                                       pEntryKey.getMetaDataClass(),
                                                       lT.getClass()));
    }

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
    Number lValue = getValue(MetaDataOrdinals.Index);
    if (lValue == null)
      return null;
    return lValue.longValue();
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
    Number lValue = getValue(MetaDataOrdinals.TimeStampInNanoSeconds);
    if (lValue == null)
      return null;
    return lValue.longValue();
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
    try
    {

      String lString = cMapper.writeValueAsString(mMetaDataMap);
      return lString;
    }
    catch (JsonProcessingException e)
    {
      return toString();
    }
  }

  /**
   * Adds to this meta data object the entries contained in the given string (as
   * produced by toString())
   * 
   * @param pString
   *          string
   * @return values
   */
  public boolean fromString(String pString)
  {
    pString = backwardsCompatibilityFilter(pString);

    try
    {
      @SuppressWarnings("unchecked")
      HashMap<String, Object> lMapRead =
                                       (HashMap<String, Object>) cMapper.readValue(pString,
                                                                                   HashMap.class);
      mMetaDataMap.putAll(lMapRead);
      return true;
    }
    catch (IOException e)
    {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Applies transformations to the string to convert it t the new JSON format.
   * This is to make it possible to load older datasets
   * 
   * @param pString
   * @return transformed string normalized from old format to new JSOn format
   */
  protected String backwardsCompatibilityFilter(String pString)
  {
    if (!pString.contains("\"") && pString.contains("="))
    {
      pString = pString.replaceAll("=", ":");
      pString = pString.replaceAll(":", "\":");
      pString = pString.replaceAll(", ", ", \"");
      pString = pString.replaceAll("\\{", "\\{\"");
      pString = pString.replaceAll("TimeLapse", "\"TimeLapse\"");
      for (int l = 0; l < 4; l++)
      {
        for (int c = 0; c < 2; c++)
        {
          pString = pString.replaceAll("C" + c
                                       + "L"
                                       + l,
                                       "\"C" + c + "L" + l + "\"");
        }
      }
    }
    return pString;
  }

}
