package clearcontrol.stack;

import org.bridj.Pointer;

import coremem.ContiguousMemoryInterface;
import coremem.fragmented.FragmentedMemoryInterface;
import net.imglib2.img.NativeImg;
import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.type.numeric.integer.UnsignedShortType;

public class EmptyStack extends StackBase implements StackInterface
{

  public EmptyStack()
  {
    super();
  }

  @Override
  public boolean isCompatible(StackRequest pParameters)
  {
    return false;
  }

  @Override
  public void recycle(StackRequest pParameters)
  {

  }

  @Override
  public long getSizeInBytes()
  {
    return 0;
  }

  @Override
  public void free()
  {

  }

  @Override
  public boolean isFree()
  {
    return false;
  }

  @Override
  public NativeImg<UnsignedShortType, ShortOffHeapAccess> getImage()
  {
    return null;
  }

  @Override
  public long getBytesPerVoxel()
  {
    return 0;
  }

  @Override
  public int getNumberOfDimensions()
  {
    return 0;
  }

  @Override
  public long[] getDimensions()
  {
    return null;
  }

  @Override
  public long getDimension(int pIndex)
  {
    return 0;
  }

  @Override
  public long getWidth()
  {
    return 0;
  }

  @Override
  public long getHeight()
  {
    return 0;
  }

  @Override
  public long getDepth()
  {
    return 0;
  }

  @Override
  public Pointer<Byte> getPointer(int pPlaneIndex)
  {
    return null;
  }

  @Override
  public ContiguousMemoryInterface getContiguousMemory()
  {
    return null;
  }

  @Override
  public ContiguousMemoryInterface getContiguousMemory(int pPlaneIndex)
  {
    return null;
  }

  @Override
  public FragmentedMemoryInterface getFragmentedMemory()
  {
    return null;
  }

  @Override
  public StackInterface allocateSameSize()
  {
    return null;
  }

  @Override
  public StackInterface duplicate()
  {
    return new EmptyStack();
  }

}
