package clearcontrol.stack;

import clearcontrol.stack.metadata.StackMetaData;
import coremem.ContiguousMemoryInterface;
import coremem.enums.NativeTypeEnum;
import coremem.fragmented.FragmentedMemoryInterface;
import coremem.interfaces.SizedInBytes;
import coremem.recycling.RecyclableInterface;
import coremem.rgc.Freeable;

/**
 * Stack interface
 *
 * @author royer
 */
public interface StackInterface extends
                                RecyclableInterface<StackInterface, StackRequest>,
                                SizedInBytes,
                                Freeable
{

  /**
   * Returns the number of dimensions
   * 
   * @return number of dimensions
   */
  int getNumberOfDimensions();

  /**
   * Returns the dimensions array
   * 
   * @return dimensions array
   */
  long[] getDimensions();

  /**
   * Returns the dimension for a given index
   * 
   * @param pIndex
   *          index
   * @return dimension (0 -> width, 1 -> height, 2 -> depth, ... )
   */
  long getDimension(int pIndex);

  /**
   * Returns the stacks 'volume' - that is the total number of voxels
   * 
   * @return total number of voxels
   */
  long getVolume();

  /**
   * Returns stack width
   * 
   * @return stack width
   */
  long getWidth();

  /**
   * Returns the stack height
   * 
   * @return stack height
   */
  long getHeight();

  /**
   * Returns the stack depth
   * 
   * @return stack depth
   */
  long getDepth();

  /**
   * Returns the number of channels per voxel
   *
   * @return number of channels
   */
  long getNumberOfChannels();

  /**
   * Returns the number of bytes per voxel
   * 
   * @return number of bytes per voxel
   */
  long getBytesPerVoxel();

  /**
   * Sets metadata
   * 
   * @param pMetaData
   *          metadata
   */
  void setMetaData(StackMetaData pMetaData);

  /**
   * Returns the metadata object.
   * 
   * @return stack meta data interface
   */
  StackMetaData getMetaData();

  /**
   * Copies the meta data from the given stack
   * 
   * @param pStack
   */
  void copyMetaDataFrom(StackInterface pStack);

  /**
   * Returns the contiguous memory object
   * 
   * @return contiguous memory
   */
  ContiguousMemoryInterface getContiguousMemory();

  /**
   * Returns the contiguous memory object for a given plane index
   * 
   * @param pPlaneIndex
   *          plane index
   * @return corresponding memory for plane
   */
  ContiguousMemoryInterface getContiguousMemory(int pPlaneIndex);

  /**
   * Returns a fragmented memory object - one fragment per plane
   * 
   * @return fragmented memory object
   */
  FragmentedMemoryInterface getFragmentedMemory();

  /**
   * Returns a duplicate of this stack
   * 
   * @return duplicate stack
   */
  StackInterface duplicate();

  /**
   * Allocates a stack with identical dimensions and storage.
   * 
   * @return stack of same size
   */
  StackInterface allocateSameSize();

  /**
   * Returns the stack's data type
   *
   * @return data type
   */
  NativeTypeEnum getDataType();
}
