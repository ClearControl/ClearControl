package net.imglib2.img.planar;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.basictypeaccess.offheap.AbstractOffHeapAccess;
import net.imglib2.type.NativeType;
import net.imglib2.util.Fraction;
import coremem.ContiguousMemoryInterface;
import coremem.exceptions.FreedException;
import coremem.fragmented.FragmentedMemory;
import coremem.fragmented.FragmentedMemoryInterface;
import coremem.interfaces.SizedInBytes;
import coremem.rgc.Freeable;

import org.bridj.Pointer;

/**
 * Off heap planar image
 *
 * @param <T>
 *          data type
 * @param <A>
 *          access type
 * @author royer
 */
public class OffHeapPlanarImg<T extends NativeType<T>, A extends ArrayDataAccess<A>>
                             extends PlanarImg<T, A>
                             implements Freeable, SizedInBytes
{

  private ContiguousMemoryInterface mContiguousMemory;

  /**
   * Off heap planar image
   * 
   * @param pDim
   *          dimension
   * @param pEntitiesPerPixel
   *          entities per pixel
   */
  public OffHeapPlanarImg(long[] pDim, Fraction pEntitiesPerPixel)
  {
    super(null, pDim, pEntitiesPerPixel);
  }

  /**
   * Off heap planar image
   * 
   * @param pCreator
   *          creator
   * @param pDim
   *          dimension
   * @param pEntitiesPerPixel
   *          entities per pixel
   */
  public OffHeapPlanarImg(A pCreator,
                          long[] pDim,
                          Fraction pEntitiesPerPixel)
  {
    super(pCreator, pDim, pEntitiesPerPixel);
  }

  @Override
  public void free()
  {
    for (int i = 0; i < numSlices(); i++)
      getPlaneContiguousMemory(i).free();
    mContiguousMemory.free();
  }

  @Override
  public boolean isFree()
  {
    boolean lIsFree = mContiguousMemory.isFree();
    for (int i = 0; i < numSlices(); i++)
      lIsFree |= getPlaneContiguousMemory(i).isFree();
    return lIsFree;
  }

  @Override
  public void complainIfFreed() throws FreedException
  {
    for (int i = 0; i < numSlices(); i++)
      getPlaneContiguousMemory(i).complainIfFreed();
  }

  @Override
  public long getSizeInBytes()
  {
    if (isFree())
      return 0;
    long lSizeInBytes = 0;
    for (int i = 0; i < numSlices(); i++)
      lSizeInBytes += getPlaneContiguousMemory(i).getSizeInBytes();
    return lSizeInBytes;
  }

  /**
   * Returns the pointer for a given plane
   * 
   * @param pPlaneIndex
   *          plane index
   * @return plane pointer
   */
  public Pointer<Byte> getPlanePointer(int pPlaneIndex)
  {
    final ContiguousMemoryInterface lContiguousMemory =
                                                      getPlaneContiguousMemory(pPlaneIndex);
    final Pointer<Byte> lBridJPointer =
                                      lContiguousMemory.getBridJPointer(Byte.class);
    return lBridJPointer;
  }

  /**
   * Returns the contiguous memory for a given plane
   * 
   * @param pPlaneIndex
   *          plane index
   * @return contiguous memory
   */
  public ContiguousMemoryInterface getPlaneContiguousMemory(int pPlaneIndex)
  {
    final ContiguousMemoryInterface lContiguousMemory =
                                                      ((AbstractOffHeapAccess) getPlane(pPlaneIndex)).getContiguousMemory();
    return lContiguousMemory;
  }

  /**
   * Returns a fragmented memory object for this image
   * 
   * @return fragmented memory
   */
  public FragmentedMemoryInterface getFragmentedMemory()
  {
    final FragmentedMemoryInterface lFragmentedMemory =
                                                      new FragmentedMemory();
    for (int i = 0; i < numSlices(); i++)
      lFragmentedMemory.add(getPlaneContiguousMemory(i));

    return lFragmentedMemory;
  }

  /**
   * Returns the contiguous memory
   * 
   * @return contiguous memory
   */
  public ContiguousMemoryInterface getContiguousMemory()
  {
    return mContiguousMemory;
  }

  /**
   * Sets the contiguous memory for this image
   * 
   * @param pContiguousMemory
   *          contiguous memory
   */
  public void setContiguousMemory(ContiguousMemoryInterface pContiguousMemory)
  {
    mContiguousMemory = pContiguousMemory;
  }

}
