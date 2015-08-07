package rtlib.stack;

import org.bridj.Pointer;

import coremem.ContiguousMemoryInterface;
import coremem.fragmented.FragmentedMemoryInterface;
import coremem.interfaces.SizedInBytes;
import coremem.interfaces.Typed;
import coremem.recycling.RecyclableInterface;
import coremem.rgc.Freeable;
import net.imglib2.img.NativeImg;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;

public interface StackInterface<T extends NativeType<T>, A extends ArrayDataAccess<A>>	extends
																						RecyclableInterface<StackInterface<T, A>, StackRequest<T>>,
																						Typed<T>,
																						SizedInBytes,
																						Freeable
{
	NativeImg<T, A> getImage();

	long getTimeStampInNanoseconds();

	void setTimeStampInNanoseconds(long pUnit2nano);

	long getIndex();

	void setIndex(long pStackIndex);

	long getBytesPerVoxel();

	long getNumberOfImagesPerPlane();

	void setNumberOfImagesPerPlane(long pNumberOfImagesPerPlane);

	int getNumberOfDimensions();

	long[] getDimensions();

	long getDimension(int pIndex);

	long getWidth();

	long getHeight();

	long getDepth();

	double getVoxelSizeInRealUnits(int pIndex);

	void setVoxelSizeInRealUnits(	int pIndex,
									double pVoxelSizeInRealUnits);

	double[] getVoxelSizeInRealUnits();

	void setChannel(int pChannel);

	int getChannel();

	Pointer<Byte> getPointer(int pPlaneIndex);

	ContiguousMemoryInterface getContiguousMemory();

	ContiguousMemoryInterface getContiguousMemory(int pPlaneIndex);

	FragmentedMemoryInterface getFragmentedMemory();

	void copyMetaDataFrom(StackInterface<T, A> pStack);

	StackInterface<T, A> duplicate();

}
