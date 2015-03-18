package rtlib.gui.video.video3d.demo;

import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.type.numeric.integer.UnsignedShortType;

import org.junit.Test;

import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.gui.video.video3d.Stack3DDisplay;
import rtlib.stack.OffHeapPlanarStack;
import rtlib.stack.StackInterface;
import clearcuda.CudaContext;
import clearcuda.CudaDevice;
import clearcuda.CudaHostPointer;
import clearcuda.memory.CudaMemory;
import coremem.ContiguousMemoryInterface;
import coremem.buffers.ContiguousBuffer;
import coremem.offheap.OffHeapMemory;

public class VideoFrame3DDisplayDemos
{

	@Test
	public void demoContiguousStack() throws InterruptedException
	{

		final long lResolutionX = 320;
		final long lResolutionY = lResolutionX + 1;
		final long lResolutionZ = lResolutionX / 2;

		final ContiguousMemoryInterface lContiguousMemory = OffHeapMemory.allocateShorts(lResolutionX * lResolutionY
																																											* lResolutionZ);

		final ContiguousBuffer lContiguousBuffer = new ContiguousBuffer(lContiguousMemory);

		final OffHeapPlanarStack<UnsignedShortType, ShortOffHeapAccess> lStack = OffHeapPlanarStack.createUnsignedShortStack(	0,
																																																													0,
																																																													lContiguousMemory,
																																																													lResolutionX,
																																																													lResolutionY,
																																																													lResolutionZ);

		final Stack3DDisplay<UnsignedShortType> lVideoFrame3DDisplay = new Stack3DDisplay<UnsignedShortType>(	"Test",
																																																					new UnsignedShortType());

		final ObjectVariable<StackInterface<UnsignedShortType, ?>> lFrameReferenceVariable = lVideoFrame3DDisplay.getOffHeapPlanarStackReferenceVariable();

		lVideoFrame3DDisplay.open();

		lVideoFrame3DDisplay.start();

		for (int i = 0; i < 32000 && lVideoFrame3DDisplay.isShowing(); i++)
		{

			lContiguousBuffer.rewind();
			for (int z = 0; z < lResolutionZ; z++)
			{
				for (int y = 0; y < lResolutionY; y++)
				{
					for (int x = 0; x < lResolutionX; x++)
					{
						final short lValue = (short) (i + x ^ y ^ z);
						lContiguousBuffer.writeShort(lValue);
					}
				}
			}

			lFrameReferenceVariable.setReference(lStack);
			Thread.sleep(10);
		}

		lVideoFrame3DDisplay.stop();

		lVideoFrame3DDisplay.close();

	}

	@Test
	public void demoPinnedMemoryStack() throws InterruptedException
	{

		final CudaDevice lCudaDevice = new CudaDevice(0);
		final CudaContext lCudaContext = new CudaContext(	lCudaDevice,
																											false);

		final long lResolutionX = 320;
		final long lResolutionY = lResolutionX + 1;
		final long lResolutionZ = lResolutionX / 2;

		final CudaHostPointer lCudaHostPointer = CudaHostPointer.mallocPinned(2		* lResolutionX
																																							* lResolutionY
																																							* lResolutionZ,
																																					false,
																																					false);
		final ContiguousMemoryInterface lContiguousMemory = new CudaMemory(lCudaHostPointer);

		final ContiguousBuffer lContiguousBuffer = new ContiguousBuffer(lContiguousMemory);

		final OffHeapPlanarStack<UnsignedShortType, ShortOffHeapAccess> lStack = OffHeapPlanarStack.createUnsignedShortStack(	0,
																																																													0,
																																																													lContiguousMemory,
																																																													lResolutionX,
																																																													lResolutionY,
																																																													lResolutionZ);

		final Stack3DDisplay<UnsignedShortType> lVideoFrame3DDisplay = new Stack3DDisplay<UnsignedShortType>(	"Test",
																																																					new UnsignedShortType());

		final ObjectVariable<StackInterface<UnsignedShortType, ?>> lFrameReferenceVariable = lVideoFrame3DDisplay.getOffHeapPlanarStackReferenceVariable();

		lVideoFrame3DDisplay.open();

		lVideoFrame3DDisplay.start();

		for (int i = 0; i < 32000 && lVideoFrame3DDisplay.isShowing(); i++)
		{

			lContiguousBuffer.rewind();
			for (int z = 0; z < lResolutionZ; z++)
			{
				for (int y = 0; y < lResolutionY; y++)
				{
					for (int x = 0; x < lResolutionX; x++)
					{
						final short lValue = (short) (i + x ^ y ^ z);
						lContiguousBuffer.writeShort(lValue);
					}
				}
			}/**/

			lFrameReferenceVariable.setReference(lStack);
			Thread.sleep(1);
		}

		lVideoFrame3DDisplay.stop();

		lVideoFrame3DDisplay.close();

		lCudaContext.close();

		lCudaDevice.close();

	}

}
