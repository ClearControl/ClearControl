package rtlib.ao.dms.demo;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;
import org.junit.Test;

import rtlib.ao.DeformableMirrorDevice;
import rtlib.ao.dms.Mirao52eDevice;
import rtlib.ao.utils.MatrixConversions;
import rtlib.ao.zernike.TransformMatrices;
import rtlib.cameras.StackCamera;
import rtlib.cameras.devices.orcaflash4.OrcaFlash4StackCamera;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.gui.video.video2d.jogl.VideoWindow;
import rtlib.kam.memory.impl.direct.NDArrayDirect;
import rtlib.kam.memory.ndarray.NDArrayTyped;
import rtlib.kam.memory.ram.RAM;
import rtlib.stack.Stack;

public class Mirao52eDeviceDemo
{
	private volatile boolean mReceivedStack = false;
	private volatile Stack<Short> mNewStack;

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
		final OrcaFlash4StackCamera lOrcaFlash4StackCamera = OrcaFlash4StackCamera.buildWithSoftwareTriggering(0);
		Mirao52eDevice lMirao52eDevice = new Mirao52eDevice(1);

		optimizePSF(lOrcaFlash4StackCamera, lMirao52eDevice);

	}

	private void optimizePSF(	final StackCamera pStackCamera,
														DeformableMirrorDevice pDeformableMirrorDevice)	throws InterruptedException,
																																						IOException
	{
		final NDArrayDirect<Short> lNDArrayDirect = NDArrayDirect.allocateTXYZ(	Short.class,
																																						128,
																																						128,
																																						1);

		final VideoWindow lCameraVideoWindow = new VideoWindow(	"Camera image",
																														(int) lNDArrayDirect.getSizeAlongDimension(1),
																														(int) lNDArrayDirect.getSizeAlongDimension(2));
		lCameraVideoWindow.setDisplayOn(true);
		lCameraVideoWindow.setSourceBuffer(lNDArrayDirect);
		lCameraVideoWindow.setVisible(true);
		lCameraVideoWindow.setManualMinMax(false);

		pStackCamera.getStackReferenceVariable()
								.sendUpdatesTo(new ObjectVariable<Stack<Short>>("Receiver")
								{

									@Override
									public Stack<Short> setEventHook(	final Stack<Short> pOldStack,
																										final Stack<Short> pNewStack)
									{
										mReceivedStack = true;
										mNewStack = pNewStack;
										lCameraVideoWindow.setSourceBuffer(pNewStack.getNDArray());
										lCameraVideoWindow.notifyNewFrame();
										lCameraVideoWindow.display();/**/

										return super.setEventHook(pOldStack, pNewStack);
									}

								});

		assertTrue(pStackCamera.open());

		pStackCamera.getExposureInMicrosecondsVariable().setValue(100000);
		pStackCamera.getFrameWidthVariable()
								.setValue(lNDArrayDirect.getSizeAlongDimension(1));
		pStackCamera.getFrameHeightVariable()
								.setValue(lNDArrayDirect.getSizeAlongDimension(2));
		pStackCamera.getFrameDepthVariable().setValue(1);
		pStackCamera.getStackModeVariable().setValue(false);

		//

		DenseMatrix64F lTransformMatrix = TransformMatrices.computeCosineTransformMatrix(8);

		DenseMatrix64F lZernikeVector = new DenseMatrix64F(64, 1);
		NDArrayTyped<Double> lNDArray = NDArrayDirect.allocateTXYZ(	Double.TYPE,
																																8,
																																8,
																																1);
		generateRandomVector(lZernikeVector);
		MatrixConversions.convertMatrixToNDArray(lZernikeVector, lNDArray);

		final VideoWindow lDMShapeVideoWindow = new VideoWindow(	"Deformable mirror shape",
																														8,
																														8);
		lDMShapeVideoWindow.setDisplayOn(true);
		lDMShapeVideoWindow.setSourceBuffer(lNDArray);
		lDMShapeVideoWindow.setVisible(true);
		lDMShapeVideoWindow.setManualMinMax(true);
		lDMShapeVideoWindow.setMinIntensity(-0.1);
		lDMShapeVideoWindow.setMaxIntensity(0.1);

		assertTrue(pDeformableMirrorDevice.open());

		long lStartValueForLastNumberOfShapes = (long) pDeformableMirrorDevice.getNumberOfReceivedShapesVariable()
																																					.getValue();

		assertTrue(pStackCamera.start());
		lDMShapeVideoWindow.setSourceBuffer(lNDArray);
		for (int i = 1; i <= 1000000; i++)
		{
			// generateRandomVector(lVector);

			lZernikeVector.set(7, 0.5 * cos(2 * PI * i / 100));

			DenseMatrix64F lShapeVector = new DenseMatrix64F(64, 1);
			CommonOps.mult(lTransformMatrix, lZernikeVector, lShapeVector);
			MatrixConversions.convertMatrixToNDArray(lShapeVector, lNDArray);
			pDeformableMirrorDevice.getMatrixReference().set(lNDArray);
			// assertTrue(((long) lMirao52eDevice.getNumberOfReceivedShapesVariable()
			// .getValue()) == lStartValueForLastNumberOfShapes + i);

			lDMShapeVideoWindow.notifyNewFrame();
			lDMShapeVideoWindow.display();/**/
			Thread.sleep(5);
			pStackCamera.trigger();
			while (!mReceivedStack)
				Thread.sleep(1);

			long lVolume = mNewStack.getNDArray().getVolume();
			RAM lRAM = mNewStack.getNDArray().getRAM();
			long lMax = Long.MIN_VALUE;
			for (long j = 0; j < lVolume; j++)
			{
				int lCharAligned = lRAM.getCharAligned(j);
				lMax = Math.max(lMax, lCharAligned);
			}

			System.out.println(lZernikeVector);
			System.out.println("lMax=" + lMax);

			Thread.sleep(100);
		}
		pStackCamera.stop();

		assertTrue(pDeformableMirrorDevice.close());

		lDMShapeVideoWindow.close();
		pStackCamera.close();

		lCameraVideoWindow.close();
	}

	private void generateRandomNDArrayVector(NDArrayTyped<Double> pNDArray)
	{
		for (int i = 0; i < pNDArray.getVolume(); i++)
			pNDArray.getRAM()
							.setDoubleAligned(i, 0.001 * (2 * Math.random() - 1));
	}

	private void generateRandomVector(DenseMatrix64F pMatrix)
	{
		for (int i = 0; i < pMatrix.getNumElements(); i++)
			pMatrix.set(i, 0.001 * (2 * Math.random() - 1));
	}

}
