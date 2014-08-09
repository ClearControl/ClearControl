package rtlib.kam.kernel.impl.gpu.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import rtlib.core.memory.SizeOf;
import rtlib.kam.context.impl.gpu.ContextGPU;
import rtlib.kam.kernel.NDRangeUtils;
import rtlib.kam.kernel.impl.gpu.GPUProgramNDRange;
import rtlib.kam.memory.impl.direct.RAMDirect;
import rtlib.kam.memory.impl.gpu.Image3DGPU;
import rtlib.kam.memory.impl.gpu.NDArrayGPU;

public class GPUProgramTests
{
	static final long cSizeX = 128;
	static final long cSizeY = 128;
	static final long cBigSizeX = 4096;
	static final long cBigSizeY = 4096;
	static final long cBigSizeZ = 4096;

	static final long cImage3DSizeX = 64;
	static final long cImage3DSizeY = cImage3DSizeX;
	static final long cImage3DSizeZ = cImage3DSizeX;

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

			final RAMDirect lRAMDirect = new RAMDirect(cBigSizeX * cBigSizeY
																									* SizeOf.sizeOfShort());

			for (long i = 0; i < cBigSizeX * cBigSizeY; i++)
				lRAMDirect.setShortAligned(i, (short) i);

			lIn.mapAndReadFrom(lRAMDirect);
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

			lOut.writeTo(lRAMDirect);

			lOut.getCurrentQueue().waitForCompletion();

			for (long i = 0; i < cBigSizeX * cBigSizeY; i++)
			{
				final short lShort = lRAMDirect.getShortAligned(i);
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

			final RAMDirect lRAMDirect = new RAMDirect(cImage3DSizeX * cImage3DSizeY
																									* cImage3DSizeZ
																									* SizeOf.sizeOfFloat());

			for (long i = 0; i < cImage3DSizeX * cImage3DSizeY
														* cImage3DSizeZ; i++)
				lRAMDirect.setFloatAligned(i, i);

			for (long i = 0; i < cImage3DSizeX * cImage3DSizeY
														* cImage3DSizeZ; i++)
			{
				final float lFloat = lRAMDirect.getFloatAligned(i);
				assertEquals(i, lFloat, 0);
			}

			lIn.readFrom(lRAMDirect);
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

			lOut.writeTo(lRAMDirect);
			lOut.getCurrentQueue().waitForCompletion();

			for (long i = 0; i < cImage3DSizeX * cImage3DSizeY
														* cImage3DSizeZ; i++)
			{

				final float lFloat = lRAMDirect.getFloatAligned(i);
				// System.out.println(lFloat);
				assertEquals(i + 3, lFloat, 0);
			}

			lIn.free();
			lOut.free();
			lGPUprogram.free();
			lRAMDirect.free();
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
