package rtlib.ao.slms.dms.devices.mirao52e.demo;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.ejml.data.DenseMatrix64F;
import org.junit.Test;

import rtlib.ao.slms.dms.demo.DeformableMirrorDeviceDemoHelper;
import rtlib.ao.slms.dms.devices.mirao52e.Mirao52eDevice;
import rtlib.ao.zernike.TransformMatrices;

public class Mirao52eDeviceDemo
{

	@Test
	public void demoZernicke() throws IOException, InterruptedException
	{
		final Mirao52eDevice lMirao52eDevice = new Mirao52eDevice(1);
		final DenseMatrix64F lZernickeTransformMatrix = TransformMatrices.computeZernickeTransformMatrix(8);

		assertTrue(lMirao52eDevice.open());

		DeformableMirrorDeviceDemoHelper.sweepModes(lMirao52eDevice,
																								lZernickeTransformMatrix);

		assertTrue(lMirao52eDevice.close());
	}

	@Test
	public void demoCosine() throws IOException, InterruptedException
	{
		final Mirao52eDevice lMirao52eDevice = new Mirao52eDevice(1);
		final DenseMatrix64F lCosineTransformMatrix = TransformMatrices.computeCosineTransformMatrix(8);

		assertTrue(lMirao52eDevice.open());

		DeformableMirrorDeviceDemoHelper.sweepModes(lMirao52eDevice,
																								lCosineTransformMatrix);

		assertTrue(lMirao52eDevice.close());
	}

	@Test
	public void demoRandom() throws IOException, InterruptedException
	{
		final Mirao52eDevice lMirao52eDevice = new Mirao52eDevice(1);
		final DenseMatrix64F lCosineTransformMatrix = TransformMatrices.computeCosineTransformMatrix(8);

		assertTrue(lMirao52eDevice.open());

		DeformableMirrorDeviceDemoHelper.playRandomShapes(lMirao52eDevice,
																											lCosineTransformMatrix,
																											10000);

		assertTrue(lMirao52eDevice.close());
	}

}
