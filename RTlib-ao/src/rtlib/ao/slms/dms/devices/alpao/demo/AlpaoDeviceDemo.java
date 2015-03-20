package rtlib.ao.slms.dms.devices.alpao.demo;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.ejml.data.DenseMatrix64F;
import org.junit.Test;

import rtlib.ao.slms.dms.demo.DeformableMirrorDeviceDemoHelper;
import rtlib.ao.slms.dms.devices.alpao.AlpaoDMDevice;
import rtlib.ao.zernike.TransformMatrices;

public class AlpaoDeviceDemo
{
	@Test
	public void demoZernicke() throws IOException, InterruptedException
	{
		final AlpaoDMDevice lAlpaoDMDevice = new AlpaoDMDevice(1);
		final DenseMatrix64F lZernickeTransformMatrix = TransformMatrices.computeZernickeTransformMatrix(8);

		assertTrue(lAlpaoDMDevice.open());

		DeformableMirrorDeviceDemoHelper.sweepModes(lAlpaoDMDevice,
																								lZernickeTransformMatrix);

		assertTrue(lAlpaoDMDevice.close());
	}

	@Test
	public void demoCosine() throws IOException, InterruptedException
	{
		final AlpaoDMDevice lAlpaoDMDevice = new AlpaoDMDevice(1);
		final DenseMatrix64F lCosineTransformMatrix = TransformMatrices.computeCosineTransformMatrix(8);

		assertTrue(lAlpaoDMDevice.open());

		DeformableMirrorDeviceDemoHelper.sweepModes(lAlpaoDMDevice,
																								lCosineTransformMatrix);

		assertTrue(lAlpaoDMDevice.close());
	}

	@Test
	public void demoRandom() throws IOException, InterruptedException
	{
		final AlpaoDMDevice lAlpaoDMDevice = new AlpaoDMDevice(1);
		final DenseMatrix64F lCosineTransformMatrix = TransformMatrices.computeCosineTransformMatrix(8);

		assertTrue(lAlpaoDMDevice.open());

		DeformableMirrorDeviceDemoHelper.playRandomShapes(lAlpaoDMDevice,
																											lCosineTransformMatrix,
																											10000);

		assertTrue(lAlpaoDMDevice.close());
	}

}
