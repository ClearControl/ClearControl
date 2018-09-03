package clearcontrol.stack;

import coremem.ContiguousMemoryInterface;
import coremem.enums.NativeTypeEnum;
import coremem.fragmented.FragmentedMemoryInterface;

/**
 * Emtpy stack of null dimensions
 *
 * @author royer
 */
public class EmptyStack extends StackBase implements StackInterface
{

  /**
   * Instanciates an empty stack
   */
  public EmptyStack()
  {
    super(NativeTypeEnum.Byte, 0);
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
  public long getVolume()
  {
    return 0;
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
    return new EmptyStack();
  }

  @Override
  public StackInterface duplicate()
  {
    return new EmptyStack();
  }

}
