package rtlib.hardware.ao.slms.devices.alpao.demo;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.ejml.data.DenseMatrix64F;
import org.junit.Test;

import rtlib.hardware.ao.slms.demo.DeformableMirrorDeviceDemoHelper;
import rtlib.hardware.ao.slms.devices.alpao.AlpaoDMDevice;
import rtlib.hardware.ao.zernike.TransformMatrices;

public class AlpaoDeviceDemo
{
	@Test
	public void demoZernicke() throws IOException, InterruptedException
	{
		final AlpaoDMDevice lAlpaoDMDevice = new AlpaoDMDevice(1);
		final DenseMatrix64F lZernickeTransformMatrix = TransformMatrices.computeZernickeTransformMatrix(lAlpaoDMDevice.getMatrixWidth());

		assertTrue(lAlpaoDMDevice.open());

		DeformableMirrorDeviceDemoHelper.sweepModes(lAlpaoDMDevice,
																								lZernickeTransformMatrix);

		assertTrue(lAlpaoDMDevice.close());
	}

	@Test
	public void demoCosine() throws IOException, InterruptedException
	{
		final AlpaoDMDevice lAlpaoDMDevice = new AlpaoDMDevice(1);
		final DenseMatrix64F lCosineTransformMatrix = TransformMatrices.computeCosineTransformMatrix(lAlpaoDMDevice.getMatrixWidth());

		assertTrue(lAlpaoDMDevice.open());

		DeformableMirrorDeviceDemoHelper.sweepModes(lAlpaoDMDevice,
																								lCosineTransformMatrix);

		assertTrue(lAlpaoDMDevice.close());
	}

	@Test
	public void demoRandom() throws IOException, InterruptedException
	{
		final AlpaoDMDevice lAlpaoDMDevice = new AlpaoDMDevice(1);
		final DenseMatrix64F lCosineTransformMatrix = TransformMatrices.computeCosineTransformMatrix(lAlpaoDMDevice.getMatrixWidth());

		assertTrue(lAlpaoDMDevice.open());

		DeformableMirrorDeviceDemoHelper.playRandomShapes(lAlpaoDMDevice,
																											lCosineTransformMatrix,
																											10000);

		assertTrue(lAlpaoDMDevice.close());
	}

}
