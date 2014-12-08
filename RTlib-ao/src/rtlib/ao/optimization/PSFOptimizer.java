package rtlib.ao.optimization;

import static java.lang.Math.min;

import java.io.IOException;

import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;
import org.jtransforms.dct.FloatDCT_2D;

import coremem.MemoryRegionInterface;
import rtlib.ao.utils.MatrixConversions;
import rtlib.ao.zernike.TransformMatrices;
import rtlib.cameras.StackCameraDeviceBase;
import rtlib.core.device.VirtualDeviceInterface;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.gui.plots.MultiPlot;
import rtlib.gui.plots.PlotTab;
import rtlib.gui.video.video2d.jogl.VideoWindow;
import rtlib.kam.memory.impl.direct.NDArrayTypedDirect;
import rtlib.kam.memory.ndarray.NDArrayTyped;
import rtlib.slms.dms.DeformableMirrorDevice;
import rtlib.stack.Stack;

public class PSFOptimizer implements VirtualDeviceInterface
{
	private volatile boolean mReceivedStack = false;
	private volatile Stack<Character> mNewStack;
	private StackCameraDeviceBase<Character, Character> mStackCamera;
	private DeformableMirrorDevice mDeformableMirrorDevice;
	private VideoWindow<Character> mCameraVideoWindow;
	private DenseMatrix64F mTransformMatrix;
	private VideoWindow<Double> mDMShapeVideoWindow;
	private NDArrayTyped<Double> mNDArray;
	private int mMatrixWidth;
	private int mMatrixHeight;

	public PSFOptimizer(final StackCameraDeviceBase pStackCamera,
											DeformableMirrorDevice pDeformableMirrorDevice)
	{
		super();
		mStackCamera = pStackCamera;
		mDeformableMirrorDevice = pDeformableMirrorDevice;

		mStackCamera.getStackReferenceVariable()
								.sendUpdatesTo(new ObjectVariable<Stack<Character>>("Receiver")
								{

									@Override
									public Stack<Character> setEventHook(	final Stack<Character> pOldStack,
																										final Stack<Character> pNewStack)
									{
										mReceivedStack = true;
										mNewStack = pNewStack;
										mCameraVideoWindow.setSourceBuffer(pNewStack.getNDArray());
										mCameraVideoWindow.notifyNewFrame();
										mCameraVideoWindow.requestDisplay();/**/

										return super.setEventHook(pOldStack, pNewStack);
									}

								});

		mMatrixWidth = (int) mDeformableMirrorDevice.getMatrixWidthVariable()
																								.getValue();
		mMatrixHeight = (int) mDeformableMirrorDevice.getMatrixHeightVariable()
																									.getValue();

		mCameraVideoWindow = new VideoWindow<Character>("Camera image",
																										Character.class,
																										(int) (pStackCamera.getFrameWidthVariable().getValue()),
																										(int) (pStackCamera.getFrameWidthVariable().getValue()));
		mCameraVideoWindow.setDisplayOn(true);
		mCameraVideoWindow.setVisible(true);
		mCameraVideoWindow.setManualMinMax(false);

		mDMShapeVideoWindow = new VideoWindow<Double>("Deformable mirror shape",
																									Double.class,
																					mMatrixWidth,
																					mMatrixHeight);
		mDMShapeVideoWindow.setDisplayOn(true);
		mDMShapeVideoWindow.setVisible(true);
		mDMShapeVideoWindow.setManualMinMax(false);
		// mDMShapeVideoWindow.setMinIntensity(-0.1);
		// mDMShapeVideoWindow.setMaxIntensity(0.1);

		mTransformMatrix = TransformMatrices.computeCosineTransformMatrix(mMatrixWidth);
	}

	public DenseMatrix64F optimize(	DenseMatrix64F pInitialGuess,
																	int pMaxNumberOfBasisElements,
																	int pNumberOfEvaluations,
																	double pSearchRadius)	throws InterruptedException,
																												IOException
	{
		mStackCamera.getFrameDepthVariable().setValue(1);
		mStackCamera.getStackModeVariable().setValue(false);

		DenseMatrix64F lCurrentBestVector = pInitialGuess;
		if (lCurrentBestVector == null)
			lCurrentBestVector = new DenseMatrix64F(mMatrixWidth * mMatrixHeight,
																							1);

		getObjectiveValueForShape(lCurrentBestVector);

		for (int bv = 0; bv < min(mMatrixHeight,
															pMaxNumberOfBasisElements); bv++)
			for (int bu = 0; bu < min(pMaxNumberOfBasisElements,
																mMatrixWidth); bu++)
			{
				System.out.println("basis=(" + bu + "," + bv + ")");

				double lCurrentBasisElementValue = lCurrentBestVector.get(bv * mMatrixWidth
																																	+ bu);

				PlotTab lPlot = MultiPlot.getMultiPlot("Curves")
																	.getPlot("(" + bu + "," + bv + ")");

				WeightedObservedPoints lWeightedObservedPoints = new WeightedObservedPoints();

				for (int i = -pNumberOfEvaluations / 2; i <= pNumberOfEvaluations / 2; i++)
				{
					double lBasisElementValue = lCurrentBasisElementValue + i
																			* (2 * pSearchRadius / pNumberOfEvaluations);

					lCurrentBestVector.set(	bv * mMatrixWidth + bu,
																	lBasisElementValue);

					// System.out.println("lBasisElementValue=" + lBasisElementValue);
					double lObjectiveValueForShape = getObjectiveValueForShapeTenengrad(lCurrentBestVector);
					lWeightedObservedPoints.add(lBasisElementValue,
																			lObjectiveValueForShape);

					lPlot.addPoint(	"max intensity",
													lBasisElementValue,
													lObjectiveValueForShape);
					// System.out.println("lObjectiveValueForShape=" +
					// lObjectiveValueForShape);

				}

				/*
												double[] parameters = GaussianCurveFitter.create()
																																	.fit(lWeightedObservedPoints.toList());
												final double lNorm = parameters[0];
												final double lMean = parameters[1];
												final double lSigma = parameters[2];
												Gaussian lGaussian = new Gaussian(lNorm, lMean, lSigma);
												for (int i = -pNumberOfEvaluations / 2; i <= pNumberOfEvaluations / 2; i++)
												{
													double lBasisElementValue = lCurrentBasisElementValue + i
																											* (pSearchRadius / pNumberOfEvaluations);
													lPlot.addPoint(	"max intensity (gaussian fit)",
																					lBasisElementValue,
																					lGaussian.value(lBasisElementValue));
												}/**/
				lPlot.ensureUpToDate();

				double lArgMax = argmax(lWeightedObservedPoints);

				lCurrentBestVector.set(bv * mMatrixWidth + bu, lArgMax);
				System.gc();
			}

		getObjectiveValueForShape(lCurrentBestVector);
		return lCurrentBestVector;
	}

	private double argmax(WeightedObservedPoints pWeightedObservedPoints)
	{
		double lMaxY = Double.NEGATIVE_INFINITY;
		double lArgMax = 0;

		for (WeightedObservedPoint lWeightedObservedPoint : pWeightedObservedPoints.toList())
		{
			double lX = lWeightedObservedPoint.getX();
			double lY = lWeightedObservedPoint.getY();

			if (lY > lMaxY)
			{
				lMaxY = lY;
				lArgMax = lX;
			}
		}
		return lArgMax;
	}

	private double getObjectiveValueForShape(DenseMatrix64F pBasisVector) throws InterruptedException
	{
		if (mNDArray == null)
		{
			mNDArray = NDArrayTypedDirect.allocateTXYZ(	Double.TYPE,
																									(int) mDeformableMirrorDevice.getMatrixWidthVariable()
																																								.getValue(),
																									(int) mDeformableMirrorDevice.getMatrixHeightVariable()
																																								.getValue(),
																									1);
			mDMShapeVideoWindow.setSourceBuffer(mNDArray);
		}

		DenseMatrix64F lShapeVector = new DenseMatrix64F(64, 1);
		CommonOps.mult(mTransformMatrix, pBasisVector, lShapeVector);
		MatrixConversions.convertMatrixToNDArray(lShapeVector, mNDArray);
		mDeformableMirrorDevice.getMatrixReference().set(mNDArray);
		// assertTrue(((long)
		// lMirao52eDevice.getNumberOfReceivedShapesVariable()
		// .getValue()) == lStartValueForLastNumberOfShapes + i);

		mDMShapeVideoWindow.notifyNewFrame();
		mDMShapeVideoWindow.requestDisplay();/**/
		Thread.sleep(5);
		mStackCamera.trigger();
		while (!mReceivedStack)
			Thread.sleep(1);
		mReceivedStack = false;

		long lVolume = mNewStack.getNDArray().getVolume();
		MemoryRegionInterface lMemoryRegionInterface = mNewStack.getNDArray().getMemoryRegionInterface();
		double lMax = Long.MIN_VALUE;
		long lMaxIndex = 0;
		for (long j = 0; j < lVolume; j++)
		{
			double lValue = Math.log(1 + lMemoryRegionInterface.getCharAligned(j));
			if (lValue > lMax)
			{
				lMaxIndex = j;
				lMax = lValue;
			}
		}
		double lNonMax = 0;
		for (long j = 0; j < lVolume; j++)
			if (j != lMaxIndex)
			{
				double lValue = Math.log(1 + lMemoryRegionInterface.getCharAligned(j));
				lNonMax += lValue;
			}

		return lMax; // - (lNonMax / (lVolume - 1))
	}

	FloatDCT_2D mFloatDCT_2D;

	private double getObjectiveValueForShapeTenengrad(DenseMatrix64F pBasisVector) throws InterruptedException
	{
		if (mNDArray == null)
		{
			mNDArray = NDArrayTypedDirect.allocateTXYZ(	Double.TYPE,
																									(int) mDeformableMirrorDevice.getMatrixWidthVariable()
																																								.getValue(),
																									(int) mDeformableMirrorDevice.getMatrixHeightVariable()
																																								.getValue(),
																									1);
			mDMShapeVideoWindow.setSourceBuffer(mNDArray);
		}

		DenseMatrix64F lShapeVector = new DenseMatrix64F(64, 1);
		CommonOps.mult(mTransformMatrix, pBasisVector, lShapeVector);
		MatrixConversions.convertMatrixToNDArray(lShapeVector, mNDArray);
		mDeformableMirrorDevice.getMatrixReference().set(mNDArray);
		// assertTrue(((long)
		// lMirao52eDevice.getNumberOfReceivedShapesVariable()
		// .getValue()) == lStartValueForLastNumberOfShapes + i);

		mDMShapeVideoWindow.notifyNewFrame();
		mDMShapeVideoWindow.requestDisplay();/**/
		Thread.sleep(5);
		mStackCamera.trigger();
		while (!mReceivedStack)
			Thread.sleep(1);
		mReceivedStack = false;

		long lVolume = mNewStack.getNDArray().getVolume();
		NDArrayTypedDirect<Character> lNdArray = mNewStack.getNDArray();

		final long lWidth = lNdArray.getWidth();
		final long lHeight = lNdArray.getHeight();

		MemoryRegionInterface lMemoryRegionInterface = mNewStack.getNDArray().getMemoryRegionInterface();

		float lSum = 0;
		for (long y = 1; y < lHeight - 1; y++)
		{

			for (long x = 1; x < lWidth - 1; x++)
			{

				short a = lMemoryRegionInterface.getShortAligned(x - 1 + lWidth * (y - 1));
				short b = lMemoryRegionInterface.getShortAligned(x + lWidth * (y - 1));
				short c = lMemoryRegionInterface.getShortAligned(x + 1 + lWidth * (y - 1));
				short d = lMemoryRegionInterface.getShortAligned(x - 1 + lWidth * y);
				short e = lMemoryRegionInterface.getShortAligned(x + lWidth * y);
				short f = lMemoryRegionInterface.getShortAligned(x + 1 + lWidth * y);
				short g = lMemoryRegionInterface.getShortAligned(x - 1 + lWidth * (y + 1));
				short h = lMemoryRegionInterface.getShortAligned(x + lWidth * (y + 1));
				short i = lMemoryRegionInterface.getShortAligned(x + 1 + lWidth * (y + 1));

				float dx = c + 2 * f + i - a - 2 * d - g;
				float dy = a + 2 * b + c - g - 2 * h - i;

				float dx2dy2 = dx * dx + dy * dy;

				lSum += dx2dy2;
			}

		}

		return lSum / (lHeight * lHeight); // - (lNonMax / (lVolume - 1))
	}

	@Override
	public boolean open()
	{
		boolean lResult = true;
		lResult &= mStackCamera.open();
		lResult &= mDeformableMirrorDevice.open();

		return lResult;
	}

	@Override
	public boolean start()
	{
		boolean lResult = true;
		lResult &= mStackCamera.start();
		lResult &= mDeformableMirrorDevice.start();
		return lResult;
	}

	@Override
	public boolean stop()
	{
		boolean lResult = true;
		lResult &= mStackCamera.stop();
		lResult &= mDeformableMirrorDevice.stop();

		return lResult;
	}

	@Override
	public boolean close()
	{
		boolean lResult = true;

		try
		{
			lResult &= mDeformableMirrorDevice.close();
			lResult &= mStackCamera.close();
			mDMShapeVideoWindow.close();
			mCameraVideoWindow.close();
			return lResult;
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			return false;
		}
	}
}
