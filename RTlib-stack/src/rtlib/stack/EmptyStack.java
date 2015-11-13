package rtlib.stack;

import net.imglib2.img.NativeImg;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;

import org.bridj.Pointer;

import coremem.ContiguousMemoryInterface;
import coremem.fragmented.FragmentedMemoryInterface;

public class EmptyStack<T extends NativeType<T>, A extends ArrayDataAccess<A>>	extends
																																								StackBase<T, A>	implements
																																																StackInterface<T, A>
{

	public EmptyStack()
	{
		super();
	}

	@Override
	public boolean isCompatible(StackRequest<T> pParameters)
	{
		return false;
	}

	@Override
	public void recycle(StackRequest<T> pParameters)
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
	public NativeImg<T, A> getImage()
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
	public StackInterface<T, A> allocateSameSize()
	{
		return null;
	}

	@Override
	public StackInterface<T, A> duplicate()
	{
		return new EmptyStack<T, A>();
	}

}
