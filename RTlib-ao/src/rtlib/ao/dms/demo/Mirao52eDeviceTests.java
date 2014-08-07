package rtlib.ao.dms.demo;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import rtlib.ao.dms.Mirao52eDevice;
import rtlib.kam.memory.impl.direct.NDArrayDirect;
import rtlib.kam.memory.ndarray.NDArray;

public class Mirao52eDeviceTests
{

	/**
	 * Start the Mirao52 UDP server on the localhost and fire this demo.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void test() throws IOException, InterruptedException
	{
		Mirao52eDevice lMirao52eDevice = new Mirao52eDevice(1);

		assertTrue(lMirao52eDevice.open());

		long lStartValueForLastNumberOfShapes = (long) lMirao52eDevice.getNumberOfReceivedShapesVariable()
																																	.getValue();

		NDArray lNDArray = NDArrayDirect.allocateSXYZ(8, 8, 8, 1);

		for (int i = 1; i <= 10000; i++)
		{
			generateRandomVector(lNDArray);
			lMirao52eDevice.getMatrixReference().set(lNDArray);
			assertTrue(((long) lMirao52eDevice.getNumberOfReceivedShapesVariable()
																				.getValue()) == lStartValueForLastNumberOfShapes + i);
			Thread.sleep(10);
		}

		assertTrue(lMirao52eDevice.close());

	}

	private void generateRandomVector(NDArray pNDArray)
	{
		for (int i = 0; i < pNDArray.getVolume(); i++)
			pNDArray.getRAM()
							.setDoubleAligned(i, 0.001 * (2 * Math.random() - 1));

	}

}
