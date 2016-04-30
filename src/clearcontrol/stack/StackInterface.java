package clearcontrol.stack;

import org.bridj.Pointer;

import coremem.ContiguousMemoryInterface;
import coremem.fragmented.FragmentedMemoryInterface;
import coremem.interfaces.SizedInBytes;
import coremem.recycling.RecyclableInterface;
import coremem.rgc.Freeable;
import net.imglib2.img.NativeImg;
import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.type.numeric.integer.UnsignedShortType;

public interface StackInterface	extends
																RecyclableInterface<StackInterface, StackRequest>,
																SizedInBytes,
																Freeable
{
	NativeImg<UnsignedShortType, ShortOffHeapAccess> getImage();

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

	void copyMetaDataFrom(StackInterface pStack);

	StackInterface allocateSameSize();

	StackInterface duplicate();

}
