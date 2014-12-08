package rtlib.kam.kernel.impl.gpu.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import rtlib.kam.context.impl.gpu.ContextGPU;
import rtlib.kam.kernel.NDRangeUtils;
import rtlib.kam.kernel.impl.gpu.GPUProgramNDRange;
import rtlib.kam.memory.impl.gpu.Image3DGPU;
import rtlib.kam.memory.impl.gpu.NDArrayGPU;
import coremem.offheap.OffHeapMemoryRegion;
import coremem.util.SizeOf;


public class GPUProgramTests
{
	static final int cSizeX = 128;
	static final int cSizeY = 128;
	static final int cBigSizeX = 4096;
	static final int cBigSizeY = 4096;
	static final int cBigSizeZ = 4096;

	static final int cImage3DSizeX = 64;
	static final int cImage3DSizeY = cImage3DSizeX;
	static final int cImage3DSizeZ = cImage3DSizeX;

	@Test
	public void testUnlimitedAdd2DBuffer() throws IOException
	{
		try
		{
			final ContextGPU lBestOpenCLContext = ContextGPU.getBestOpenCLContext("GeForce");
			/*System.out.println(lBestOpenCLContext.getDefaultQueue()
																						.getPeer()
																						.getDevice()
																						.getName());/**/

			final GPUProgramNDRange lGPUProgram = new GPUProgramNDRange(lBestOpenCLContext);

			lGPUProgram.setProgramStringFromRessource(this.getClass(),
																								"kernels/add.cl");

			lGPUProgram.setType("short");

			assertTrue(lGPUProgram.ensureKernelsAreCompiled());

			assertTrue(lGPUProgram.isUpToDate());

			final NDArrayGPU<Short> lIn = new NDArrayGPU<Short>(lBestOpenCLContext,
																													short.class,
																													false,
																													true,
																													cBigSizeX,
																													cBigSizeY);

			final OffHeapMemoryRegion lOffHeapMemoryRegion = new OffHeapMemoryRegion(cBigSizeX * cBigSizeY
																									* SizeOf.sizeOfShort());

			for (long i = 0; i < cBigSizeX * cBigSizeY; i++)
				lOffHeapMemoryRegion.setShortAligned(i, (short) i);

			lIn.mapAndReadFrom(lOffHeapMemoryRegion);
			lIn.getCurrentQueue().waitForCompletion();

			final NDArrayGPU<Short> lOut = new NDArrayGPU<Short>(	lBestOpenCLContext,
																														short.class,
																														true,
																														false,
																														cBigSizeX,
																														cBigSizeY);

			lGPUProgram.execute("add_buffer",
													NDRangeUtils.range(cBigSizeX, cBigSizeY),
													lIn,
													lOut,
													1);

			lOut.writeTo(lOffHeapMemoryRegion);

			lOut.getCurrentQueue().waitForCompletion();

			for (long i = 0; i < cBigSizeX * cBigSizeY; i++)
			{
				final short lShort = lOffHeapMemoryRegion.getShortAligned(i);
				assertEquals((short) (i + 1), lShort);
			}

		}
		catch (final java.lang.AssertionError etest)
		{
			throw etest;
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}

	}

	@Test
	public void testUnlimitedAdd3DImage() throws IOException
	{
		try
		{
			final ContextGPU lBestOpenCLContext = ContextGPU.getBestOpenCLContext("GeForce");
			/*System.out.println(lBestOpenCLContext.getDefaultQueue()
																						.getPeer()
																						.getDevice()
																						.getName());/**/

			final GPUProgramNDRange lGPUprogram = new GPUProgramNDRange(lBestOpenCLContext);

			lGPUprogram.setProgramStringFromRessource(this.getClass(),
																								"kernels/add.cl");

			lGPUprogram.setType("float");

			assertTrue(lGPUprogram.ensureKernelsAreCompiled());

			assertTrue(lGPUprogram.isUpToDate());

			final Image3DGPU<Float> lIn = new Image3DGPU<Float>(lBestOpenCLContext,
																													float.class,
																													false,
																													true,
																													cImage3DSizeX,
																													cImage3DSizeY,
																													cImage3DSizeZ);

			final OffHeapMemoryRegion lOffHeapMemoryRegion = new OffHeapMemoryRegion(cImage3DSizeX * cImage3DSizeY
																									* cImage3DSizeZ
																									* SizeOf.sizeOfFloat());

			for (long i = 0; i < cImage3DSizeX * cImage3DSizeY
														* cImage3DSizeZ; i++)
				lOffHeapMemoryRegion.setFloatAligned(i, i);

			for (long i = 0; i < cImage3DSizeX * cImage3DSizeY
														* cImage3DSizeZ; i++)
			{
				final float lFloat = lOffHeapMemoryRegion.getFloatAligned(i);
				assertEquals(i, lFloat, 0);
			}

			lIn.readFrom(lOffHeapMemoryRegion);
			lIn.getCurrentQueue().waitForCompletion();

			final Image3DGPU<Float> lOut = new Image3DGPU<Float>(	lBestOpenCLContext,
																														float.class,
																														true,
																														false,
																														cImage3DSizeX,
																														cImage3DSizeY,
																														cImage3DSizeZ);

			lGPUprogram.execute("add_image",
													NDRangeUtils.range(	cImage3DSizeX,
																							cImage3DSizeY,
																							cImage3DSizeZ),
													lIn,
													lOut,
													3.0f);

			lOut.writeTo(lOffHeapMemoryRegion);
			lOut.getCurrentQueue().waitForCompletion();

			for (long i = 0; i < cImage3DSizeX * cImage3DSizeY
														* cImage3DSizeZ; i++)
			{

				final float lFloat = lOffHeapMemoryRegion.getFloatAligned(i);
				// System.out.println(lFloat);
				assertEquals(i + 3, lFloat, 0);
			}

			lIn.free();
			lOut.free();
			lGPUprogram.free();
			lOffHeapMemoryRegion.free();
			lBestOpenCLContext.free();

		}
		catch (final java.lang.AssertionError etest)
		{
			throw etest;
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}

	}

}
