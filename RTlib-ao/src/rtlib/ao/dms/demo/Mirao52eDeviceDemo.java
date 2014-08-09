package rtlib.ao.dms.demo;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;
import org.junit.Test;

import rtlib.ao.dms.Mirao52eDevice;
import rtlib.ao.utils.MatrixConversions;
import rtlib.ao.zernike.TransformMatrices;
import rtlib.cameras.orcaflash4.OrcaFlash4StackCamera;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.gui.video.video2d.jogl.VideoWindow;
import rtlib.kam.memory.impl.direct.NDArrayDirect;
import rtlib.kam.memory.ndarray.NDArrayTyped;
import rtlib.stack.Stack;

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
		final NDArrayDirect<Short> lNDArrayDirect = NDArrayDirect.allocateTXYZ(	Short.class,
																																						128,
																																						128,
																																						1);

		final VideoWindow lOrcaVideoWindow = new VideoWindow(	"Camera image",
																											lNDArrayDirect.getSizeAlongDimension(1),
																											lNDArrayDirect.getSizeAlongDimension(2));
		lOrcaVideoWindow.setDisplayOn(true);
		lOrcaVideoWindow.setSourceBuffer(lNDArrayDirect);
		lOrcaVideoWindow.setVisible(true);
		lOrcaVideoWindow.setManualMinMax(false);

		final OrcaFlash4StackCamera lOrcaFlash4StackCamera = new OrcaFlash4StackCamera(	0,
																																										true);

		lOrcaFlash4StackCamera.getStackReferenceVariable()
													.sendUpdatesTo(new ObjectVariable<Stack<Short>>("Receiver")
													{

														@Override
														public Stack<Short> setEventHook(	final Stack<Short> pOldStack,
																															final Stack<Short> pNewStack)
														{

															lOrcaVideoWindow.setSourceBuffer(pNewStack.getNDArray());
															lOrcaVideoWindow.notifyNewFrame();
															lOrcaVideoWindow.display();/**/

															return super.setEventHook(pOldStack,
																												pNewStack);
														}

													});

		assertTrue(lOrcaFlash4StackCamera.open());

		lOrcaFlash4StackCamera.getExposureInMicrosecondsVariable()
													.setValue(100000);
		lOrcaFlash4StackCamera.getFrameWidthVariable()
													.setValue(lNDArrayDirect.getSizeAlongDimension(1));
		lOrcaFlash4StackCamera.getFrameHeightVariable()
													.setValue(lNDArrayDirect.getSizeAlongDimension(2));
		lOrcaFlash4StackCamera.getFrameDepthVariable().setValue(1);
		lOrcaFlash4StackCamera.getStackModeVariable().setValue(false);
		lOrcaFlash4StackCamera.ensureEnough2DFramesAreAvailable(100);

		//

		DenseMatrix64F lTransformMatrix = TransformMatrices.computeCosineTransformMatrix(8);

		DenseMatrix64F lZernikeVector = new DenseMatrix64F(64, 1);
		NDArrayTyped<Double> lNDArray = NDArrayDirect.allocateTXYZ(	Double.TYPE,
																																8,
																																8,
																																1);
		generateRandomVector(lZernikeVector);
		MatrixConversions.convertMatrixToNDArray(lZernikeVector,lNDArray);

		final VideoWindow lMirrorVideoWindow = new VideoWindow(	"Deformable mirror shape",
																											8,
																											8);
		lMirrorVideoWindow.setDisplayOn(true);
		lMirrorVideoWindow.setSourceBuffer(lNDArray);
		lMirrorVideoWindow.setVisible(true);
		lMirrorVideoWindow.setManualMinMax(true);
		lMirrorVideoWindow.setMinIntensity(-0.1);
		lMirrorVideoWindow.setMaxIntensity(0.1);

		Mirao52eDevice lMirao52eDevice = new Mirao52eDevice(1);

		assertTrue(lMirao52eDevice.open());

		long lStartValueForLastNumberOfShapes = (long) lMirao52eDevice.getNumberOfReceivedShapesVariable()
																																	.getValue();

		assertTrue(lOrcaFlash4StackCamera.start());
		lMirrorVideoWindow.setSourceBuffer(lNDArray);
		for (int i = 1; i <= 1000000; i++)
		{
			// generateRandomVector(lVector);

			lZernikeVector.set(7, 0.5 * cos(2 * PI * i / 100));

			DenseMatrix64F lShapeVector = new DenseMatrix64F(64, 1);
			CommonOps.mult(lTransformMatrix, lZernikeVector, lShapeVector);
			MatrixConversions.convertMatrixToNDArray(lShapeVector, lNDArray);
			lMirao52eDevice.getMatrixReference().set(lNDArray);
			// assertTrue(((long) lMirao52eDevice.getNumberOfReceivedShapesVariable()
			// .getValue()) == lStartValueForLastNumberOfShapes + i);

			lMirrorVideoWindow.notifyNewFrame();
			lMirrorVideoWindow.display();/**/
			Thread.sleep(100);
		}
		lOrcaFlash4StackCamera.stop();

		assertTrue(lMirao52eDevice.close());

		lMirrorVideoWindow.close();
		lOrcaFlash4StackCamera.close();

		lOrcaVideoWindow.close();

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
