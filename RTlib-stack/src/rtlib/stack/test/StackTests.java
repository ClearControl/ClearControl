package rtlib.stack.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.type.numeric.integer.UnsignedShortType;

import org.junit.Test;

import rtlib.core.concurrent.executors.RTlibExecutors;
import rtlib.stack.ContiguousOffHeapPlanarStackFactory;
import rtlib.stack.OffHeapPlanarStack;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import coremem.ContiguousMemoryInterface;
import coremem.offheap.OffHeapMemory;
import coremem.offheap.OffHeapMemoryAccess;
import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclerInterface;
import coremem.util.Size;

public class StackTests
{

	private static final int cMaximumNumberOfObjects = 1024;
	private static final long cMaximumLiveMemoryInBytes = 2L * 1024L * 1024L * 1024L;
	private static final long cBytesPerPixel = Size.of(short.class);
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

		final ContiguousMemoryInterface lContiguousMemory = OffHeapMemory.allocateShorts(cSizeX * cSizeY
																																								* cSizeZ);
		@SuppressWarnings("unchecked")
		final OffHeapPlanarStack<UnsignedShortType, ShortOffHeapAccess> lStack = (OffHeapPlanarStack<UnsignedShortType, ShortOffHeapAccess>) OffHeapPlanarStack.createStack(lContiguousMemory,
																																																																																				new UnsignedShortType(),
																																																																																				cSizeX,
																																																																																				cSizeY,
																																																																																				cSizeZ);

		assertEquals(1, lStack.getVoxelSizeInRealUnits(0), 0);

		lStack.setVoxelSizeInRealUnits(1, 0.5);
		lStack.setVoxelSizeInRealUnits(2, 1);
		lStack.setVoxelSizeInRealUnits(3, 3);

		assertEquals(0.5, lStack.getVoxelSizeInRealUnits(1), 0);
		assertEquals(1, lStack.getVoxelSizeInRealUnits(2), 0);
		assertEquals(3, lStack.getVoxelSizeInRealUnits(3), 0);

		assertEquals(0, lStack.getIndex());
		assertEquals(0, lStack.getTimeStampInNanoseconds());

		assertEquals(cLengthInBytes, lStack.getSizeInBytes());

		assertEquals(3, lStack.getNumberOfDimensions());

		assertEquals(cBytesPerPixel, lStack.getBytesPerVoxel());
		assertEquals(cSizeX, lStack.getWidth());
		assertEquals(cSizeY, lStack.getHeight());
		assertEquals(cSizeZ, lStack.getDepth());

		assertEquals(cSizeX, lStack.getDimensions()[0]);
		assertEquals(cSizeY, lStack.getDimensions()[1]);
		assertEquals(cSizeZ, lStack.getDimensions()[2]);

		assertEquals(cSizeX, lStack.getDimension(0));
		assertEquals(cSizeY, lStack.getDimension(1));
		assertEquals(cSizeZ, lStack.getDimension(2));

		lStack.free();

		assertTrue(lStack.isFree());

	}

	@Test
	public void testRecycling() throws InterruptedException
	{
		final long lStartTotalAllocatedMemory = OffHeapMemoryAccess.getTotalAllocatedMemory();

		final ContiguousOffHeapPlanarStackFactory<UnsignedShortType, ShortOffHeapAccess> lOffHeapPlanarStackFactory = new ContiguousOffHeapPlanarStackFactory<UnsignedShortType, ShortOffHeapAccess>();

		final RecyclerInterface<StackInterface<UnsignedShortType, ShortOffHeapAccess>, StackRequest<UnsignedShortType>> lRecycler = new BasicRecycler<StackInterface<UnsignedShortType, ShortOffHeapAccess>, StackRequest<UnsignedShortType>>(lOffHeapPlanarStackFactory,
																																																																																																																					cMaximumNumberOfObjects);

		final ThreadPoolExecutor lThreadPoolExecutor = RTlibExecutors.getOrCreateThreadPoolExecutor(this,
																																																Thread.NORM_PRIORITY,
																																																1,
																																																1,
																																																100);

		for (int i = 0; i < 100; i++)
		{
			// System.out.println(i);
			final StackInterface<UnsignedShortType, ShortOffHeapAccess> lStack;
			if ((i % 100) < 50)
			{

				lStack = OffHeapPlanarStack.getOrWaitWithRecycler(lRecycler,
																															10,
																															TimeUnit.SECONDS,
																															new UnsignedShortType(),
																															cSizeX * cBig,
																															cSizeY * cBig,
																															cSizeZ * cBig);
				assertEquals(	cLengthInBytes * Math.pow(cBig, 3),
											lStack.getSizeInBytes(),
											0);
			}
			else
			{
				lStack = OffHeapPlanarStack.getOrWaitWithRecycler(lRecycler,
																															10,
																															TimeUnit.SECONDS,
																															new UnsignedShortType(),
																															cSizeX,
																															cSizeY,
																															cSizeZ);
				assertEquals(cLengthInBytes, lStack.getSizeInBytes());
			}

			assertNotNull(lStack);

			final ContiguousMemoryInterface lContiguousMemory = lStack.getContiguousMemory((int) (cSizeZ / 2));

			for (int k = 0; k < lContiguousMemory.getSizeInBytes(); k++)
			{
				lContiguousMemory.setByteAligned(k, (byte) k);
			}

			final Runnable lRunnable2 = () -> {

				final ContiguousMemoryInterface lContiguousMemory2 = lStack.getContiguousMemory((int) (cSizeZ / 2));
				for (int k = 0; k < lContiguousMemory2.getSizeInBytes(); k++)
				{
					final byte lByte = lContiguousMemory2.getByteAligned(k);
					assertEquals((byte) k, lByte);
				}
				lStack.release();
				// System.out.println("released!");
			};

			lThreadPoolExecutor.execute(lRunnable2);

			final long lLiveObjectCount = lRecycler.getNumberOfLiveObjects();
			/*System.out.format("count=%d mem=%d \n",
												lLiveObjectCount,
												lLiveMemoryInBytes);/**/
			assertTrue(lLiveObjectCount > 0);

			final long lTotalAllocatedMemory = OffHeapMemoryAccess.getTotalAllocatedMemory();
			// System.out.println("lTotalAllocatedMemory=" + lTotalAllocatedMemory);
			assertTrue(lTotalAllocatedMemory > 0);


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

		final long lLiveObjectCount = lRecycler.getNumberOfLiveObjects();
		assertEquals(0, lLiveObjectCount);


		final long lEndTotalAllocatedMemory = OffHeapMemoryAccess.getTotalAllocatedMemory();
		assertTrue(lEndTotalAllocatedMemory < lStartTotalAllocatedMemory + 10);

	}
}
