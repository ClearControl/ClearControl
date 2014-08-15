package rtlib.stack.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rtlib.core.concurrent.executors.RTlibExecutors;
import rtlib.core.memory.NativeMemoryAccess;
import rtlib.core.memory.SizeOf;
import rtlib.core.recycling.Recycler;
import rtlib.kam.memory.impl.direct.NDArrayTypedDirect;
import rtlib.stack.Stack;
import rtlib.stack.StackRequest;

public class StackTests
{

	private static final long cMAXIMUM_LIVE_MEMORY_IN_BYTES = 2L * 1024L * 1024L * 1024L;
	private static final long cBytesPerPixel = SizeOf.sizeOf(short.class);
	private static final long cSizeX = 320;
	private static final long cSizeY = 321;
	private static final long cSizeZ = 100;
	private static final long cBig = 2;

	private static final long cLengthInBytes = cSizeX * cSizeY
																							* cSizeZ
																							* cBytesPerPixel;

	@Test
	public void testLifeCycle()
	{
		final Stack<Short> lStack = new Stack<Short>(	1,
																									2,
																									short.class,
																									cSizeX,
																									cSizeY,
																									cSizeZ);

		assertEquals(1, lStack.getVolumePhysicalDimension(0), 0);

		lStack.setVolumePhysicalDimension(1, 0.5);
		lStack.setVolumePhysicalDimension(2, 1);
		lStack.setVolumePhysicalDimension(3, 3);

		assertEquals(0.5, lStack.getVolumePhysicalDimension(1), 0);
		assertEquals(1, lStack.getVolumePhysicalDimension(2), 0);
		assertEquals(3, lStack.getVolumePhysicalDimension(3), 0);

		assertEquals(1, lStack.getIndex());
		assertEquals(2, lStack.getTimeStampInNanoseconds());

		assertEquals(cLengthInBytes, 2 * lStack.getLengthInElements());
		assertEquals(cLengthInBytes, lStack.getSizeInBytes());

		assertEquals(3, lStack.getDimension());

		assertEquals(cBytesPerPixel, lStack.getBytesPerVoxel());
		assertEquals(cSizeX, lStack.getWidth());
		assertEquals(cSizeY, lStack.getHeight());
		assertEquals(cSizeZ, lStack.getDepth());

		assertEquals(1, lStack.getDimensions()[0]);
		assertEquals(cSizeX, lStack.getDimensions()[1]);
		assertEquals(cSizeY, lStack.getDimensions()[2]);
		assertEquals(cSizeZ, lStack.getDimensions()[3]);

		assertEquals(1, lStack.getSizeAlongDimension(0));
		assertEquals(cSizeX, lStack.getSizeAlongDimension(1));
		assertEquals(cSizeY, lStack.getSizeAlongDimension(2));
		assertEquals(cSizeZ, lStack.getSizeAlongDimension(3));

		lStack.free();

		assertTrue(lStack.isFree());

	}

	@Test
	public void testRecycling() throws InterruptedException
	{
		final long lStartTotalAllocatedMemory = NativeMemoryAccess.getTotalAllocatedMemory();

		@SuppressWarnings("rawtypes")
		final Recycler<Stack<Short>, StackRequest<Stack<Short>>> lRecycler = new Recycler(Stack.class,
																																											cMAXIMUM_LIVE_MEMORY_IN_BYTES);

		final ThreadPoolExecutor lThreadPoolExecutor = RTlibExecutors.getOrCreateThreadPoolExecutor(this,
																																																Thread.NORM_PRIORITY,
																																																1,
																																																1,
																																																100);

		for (int i = 0; i < 100; i++)
		{
			// System.out.println(i);
			final Stack<?> lStack;
			if ((i % 100) < 50)
			{

				lStack = Stack.requestOrWaitWithRecycler(	lRecycler,
																									10,
																									TimeUnit.SECONDS,
																									short.class,
																									cSizeX * cBig,
																									cSizeY * cBig,
																									cSizeZ * cBig);
				assertEquals(	cLengthInBytes * Math.pow(cBig, 3),
											lStack.getSizeInBytes(),
											0);
			}
			else
			{
				lStack = Stack.requestOrWaitWithRecycler(	lRecycler,
																									10,
																									TimeUnit.SECONDS,
																									short.class,
																									cSizeX,
																									cSizeY,
																									cSizeZ);
				assertEquals(cLengthInBytes, lStack.getSizeInBytes());
			}

			assertNotNull(lStack);

			final NDArrayTypedDirect<?> lNdArray = lStack.getNDArray();
			for (int k = 0; k < lStack.getSizeInBytes(); k += 1000)
			{
				lNdArray.setByteAligned(k, (byte) k);
			}

			final Runnable lRunnable2 = () -> {
				final NDArrayTypedDirect<?> lNdArray2 = lStack.getNDArray();
				for (int k = 0; k < lStack.getSizeInBytes(); k += 1000)
				{
					final byte lByte = lNdArray2.getByteAligned(k);
					assertEquals((byte) k, lByte);
				}
				lStack.releaseStack();
				// System.out.println("released!");
			};

			lThreadPoolExecutor.execute(lRunnable2);

			final long lLiveObjectCount = lRecycler.getLiveObjectCount();
			final long lLiveMemoryInBytes = lRecycler.getLiveMemoryInBytes();
			/*System.out.format("count=%d mem=%d \n",
												lLiveObjectCount,
												lLiveMemoryInBytes);/**/
			assertTrue(lLiveObjectCount > 0);
			assertTrue(lLiveMemoryInBytes > 0);

			final long lTotalAllocatedMemory = NativeMemoryAccess.getTotalAllocatedMemory();
			// System.out.println("lTotalAllocatedMemory=" + lTotalAllocatedMemory);
			assertTrue(lTotalAllocatedMemory > 0);

			assertTrue(lLiveMemoryInBytes < 2.5 * cMAXIMUM_LIVE_MEMORY_IN_BYTES);

			Thread.sleep(1);

			System.gc();
			/*
						System.out.println("totalMemory=" + Runtime.getRuntime()
																											.totalMemory());
						System.out.println("freeMemory=" + Runtime.getRuntime()
																											.freeMemory());
						System.out.println("maxMemory=" + Runtime.getRuntime()
																											.maxMemory());/**/

		}

		lThreadPoolExecutor.shutdown();
		lThreadPoolExecutor.awaitTermination(1, TimeUnit.SECONDS);

		lRecycler.free();

		final long lLiveObjectCount = lRecycler.getLiveObjectCount();
		assertEquals(0, lLiveObjectCount);

		final long lLiveMemoryInBytes = lRecycler.getLiveMemoryInBytes();
		assertEquals(0, lLiveMemoryInBytes);

		final long lEndTotalAllocatedMemory = NativeMemoryAccess.getTotalAllocatedMemory();
		assertEquals(lStartTotalAllocatedMemory, lEndTotalAllocatedMemory);

	}
}
