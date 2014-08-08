package rtlib.ao.dms.demo;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import rtlib.ao.dms.Mirao52eDevice;
import rtlib.gui.video.video2d.jogl.VideoWindow;
import rtlib.kam.memory.impl.direct.NDArrayDirect;
import rtlib.kam.memory.ndarray.NDArray;
import rtlib.kam.memory.ndarray.NDArrayTyped;

public class Mirao52eDeviceDemo
{

	/**
	 * First start the Mirao52 UDP server on the localhost and then fire this
	 * demo.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void demo() throws IOException, InterruptedException
	{
		NDArrayTyped<Double> lNDArray = NDArrayDirect.allocateTXYZ(	Double.TYPE,
																									8,
																									8,
																									1);

		final VideoWindow lVideoWindow = new VideoWindow(	"VideoWindow test",
																											lNDArray.getSizeAlongDimension(1),
																											lNDArray.getSizeAlongDimension(2));
		lVideoWindow.setDisplayOn(true);
		lVideoWindow.setSourceBuffer(lNDArray);
		lVideoWindow.setVisible(true);

		Mirao52eDevice lMirao52eDevice = new Mirao52eDevice(1);

		assertTrue(lMirao52eDevice.open());

		long lStartValueForLastNumberOfShapes = (long) lMirao52eDevice.getNumberOfReceivedShapesVariable()
																																	.getValue();



		lVideoWindow.setSourceBuffer(lNDArray);
		for (int i = 1; i <= 10000; i++)
		{
			generateRandomVector(lNDArray);
			lMirao52eDevice.getMatrixReference().set(lNDArray);
			assertTrue(((long) lMirao52eDevice.getNumberOfReceivedShapesVariable()
																				.getValue()) == lStartValueForLastNumberOfShapes + i);

			lVideoWindow.notifyNewFrame();
			lVideoWindow.display();/**/
			Thread.sleep(10);
		}


		assertTrue(lMirao52eDevice.close());

		lVideoWindow.close();

	}

	private void generateRandomVector(NDArray pNDArray)
	{
		for (int i = 0; i < pNDArray.getVolume(); i++)
			pNDArray.getRAM()
							.setDoubleAligned(i, 0.001 * (2 * Math.random() - 1));
	}

}
