package rtlib.microscope.lightsheet.calibrator.modules;

import static java.lang.Math.abs;
import static java.lang.Math.min;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.stat.StatUtils;

import gnu.trove.list.array.TDoubleArrayList;
import jama.Matrix;
import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.img.planar.OffHeapPlanarImg;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import rtlib.core.math.functions.UnivariateAffineComposableFunction;
import rtlib.core.math.functions.UnivariateAffineFunction;
import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.microscope.lightsheet.LightSheetMicroscope;
import rtlib.microscope.lightsheet.calibrator.utils.ImageAnalysisUtils;
import rtlib.microscope.lightsheet.illumination.LightSheetInterface;
import rtlib.scripting.engine.ScriptingEngine;
import rtlib.stack.StackInterface;

public class CalibrationXY
{

	private final LightSheetMicroscope mLightSheetMicroscope;

	private int mNumberOfDetectionArmDevices;
	private int mNumberOfLightSheetDevices;

	private MultiKeyMap<Integer, Vector2D> mOriginFromX,
			mUnitVectorFromX, mOriginFromY, mUnitVectorFromY;

	private MultiKeyMap<Integer, Matrix> mTransformMatrices;

	@SuppressWarnings("unchecked")
	public CalibrationXY(LightSheetMicroscope pLightSheetMicroscope)
	{
		super();
		mLightSheetMicroscope = pLightSheetMicroscope;

		mNumberOfDetectionArmDevices = mLightSheetMicroscope.getDeviceLists()
															.getNumberOfDetectionArmDevices();

		mNumberOfLightSheetDevices = mLightSheetMicroscope.getDeviceLists()
															.getNumberOfLightSheetDevices();

		mOriginFromX = new MultiKeyMap<>();
		mUnitVectorFromX = new MultiKeyMap<>();
		mOriginFromY = new MultiKeyMap<>();
		mUnitVectorFromY = new MultiKeyMap<>();

		mTransformMatrices = new MultiKeyMap<>();
	}

	public boolean calibrate(int pLightSheetIndex, int pNumberOfPoints)
	{
		boolean lResult = true;

		for (int d = 0; d < mNumberOfDetectionArmDevices; d++)
		{
			lResult &= calibrate(	pLightSheetIndex,
									d,
									pNumberOfPoints,
									true) && calibrate(	pLightSheetIndex,
														d,
														pNumberOfPoints,
														false);
			
			if(ScriptingEngine.isCancelRequestedStatic()) return false;
		}
		return lResult;
	}

	public boolean calibrate(	int pLightSheetIndex,
								int pDetectionArmIndex,
								int pNumberOfPoints)
	{
		return calibrate(	pLightSheetIndex,
							pDetectionArmIndex,
							pNumberOfPoints,
							true) && calibrate(	pLightSheetIndex,
												pDetectionArmIndex,
												pNumberOfPoints,
												false);
	}

	private boolean calibrate(	int pLightSheetIndex,
								int pDetectionArmIndex,
								int pNumberOfPoints,
								boolean pDoAxisX)
	{
		LightSheetInterface lLightSheet = mLightSheetMicroscope.getDeviceLists()
																.getLightSheetDevice(pLightSheetIndex);

		double lMin, lMax;

		if (pDoAxisX)
		{
			ObjectVariable<UnivariateAffineComposableFunction> lLightSheetXFunction = lLightSheet.getXFunction();
			lMin = lLightSheetXFunction.get().getMin();
			lMax = lLightSheetXFunction.get().getMax();
		}
		else
		{
			ObjectVariable<UnivariateAffineComposableFunction> lLightSheetYFunction = lLightSheet.getYFunction();
			lMin = lLightSheetYFunction.get().getMin();
			lMax = lLightSheetYFunction.get().getMax();
		}

		try
		{
			TDoubleArrayList lOriginXList = new TDoubleArrayList();
			TDoubleArrayList lOriginYList = new TDoubleArrayList();

			TDoubleArrayList lUnitVectorXList = new TDoubleArrayList();
			TDoubleArrayList lUnitVectorYList = new TDoubleArrayList();

			double lRange = min(abs(lMin), abs(lMax));
			for (double f = 0; f <= lRange; f += (2 * lRange / pNumberOfPoints))
			{
				Vector2D lCenterP, lCenterN;

				if (pDoAxisX)
				{
					lCenterP = lightSheetImageCenterWhenAt(	pLightSheetIndex,
															pDetectionArmIndex,
															f,
															0,
															4);

					lCenterN = lightSheetImageCenterWhenAt(	pLightSheetIndex,
															pDetectionArmIndex,
															-f,
															0,
															4);
				}
				else
				{
					lCenterP = lightSheetImageCenterWhenAt(	pLightSheetIndex,
															pDetectionArmIndex,
															0,
															f,
															4);

					lCenterN = lightSheetImageCenterWhenAt(	pLightSheetIndex,
															pDetectionArmIndex,
															0,
															-f,
															4);
				}

				System.out.format("center at %g: %s \n", f, lCenterP);
				System.out.format("center at %g: %s \n", -f, lCenterN);

				if (lCenterP == null && lCenterN == null)
					continue;

				lOriginXList.add(0.5 * (lCenterP.getX() + lCenterN.getX()));
				lOriginYList.add(0.5 * (lCenterP.getY() + lCenterN.getY()));

				if (f == 0)
				{
					lUnitVectorXList.add((lCenterP.getX() - lCenterN.getX()) / f);
					lUnitVectorYList.add((lCenterP.getY() - lCenterN.getY()) / f);
				}
			}

			double lOriginX = StatUtils.percentile(	lOriginXList.toArray(),
													50);
			double lOriginY = StatUtils.percentile(	lOriginYList.toArray(),
													50);

			double lUnitVectorX = StatUtils.percentile(	lUnitVectorXList.toArray(),
														50);
			double lUnitVectorY = StatUtils.percentile(	lUnitVectorYList.toArray(),
														50);

			if (pDoAxisX)
			{
				mOriginFromX.put(	pLightSheetIndex,
									pDetectionArmIndex,
									new Vector2D(lOriginX, lOriginY));
				mUnitVectorFromX.put(	pLightSheetIndex,
										pDetectionArmIndex,
										new Vector2D(	lUnitVectorX,
														lUnitVectorY));

				System.out.format("From X axis: \n");
				System.out.format("Origin : %s \n", mOriginFromX);
				System.out.format(	"Unit Vector : %s \n",
									mUnitVectorFromX);
			}
			else
			{
				mOriginFromY.put(	pLightSheetIndex,
									pDetectionArmIndex,
									new Vector2D(lOriginX, lOriginY));
				mUnitVectorFromY.put(	pLightSheetIndex,
										pDetectionArmIndex,
										new Vector2D(	lUnitVectorX,
														lUnitVectorY));

				System.out.format("From X axis: \n");
				System.out.format("Origin : %s \n", mOriginFromY);
				System.out.format("Unit Vector : %s \n", mOriginFromY);
			}

		}
		catch (InterruptedException | ExecutionException
				| TimeoutException e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
		}

		return true;
	}

	private Vector2D lightSheetImageCenterWhenAt(	int pLightSheetIndex,
													int pDetectionArmIndex,
													double pX,
													double pY,
													int pN)	throws InterruptedException,
															ExecutionException,
															TimeoutException
	{
		// Building queue start:
		mLightSheetMicroscope.clearQueue();
		mLightSheetMicroscope.zero();

		mLightSheetMicroscope.selectI(pLightSheetIndex);
		mLightSheetMicroscope.setIZ(pLightSheetIndex, 0);
		mLightSheetMicroscope.setIH(pLightSheetIndex, 0);
		mLightSheetMicroscope.setIW(pLightSheetIndex, 1);

		for (int i = 0; i < mNumberOfDetectionArmDevices; i++)
			mLightSheetMicroscope.setDZ(i, 0);

		for (int i = 1; i <= pN; i++)
		{
			mLightSheetMicroscope.setIX(pLightSheetIndex, pX);
			mLightSheetMicroscope.setIY(pLightSheetIndex, pY);
			for (int d = 0; d < mNumberOfDetectionArmDevices; d++)
				mLightSheetMicroscope.setC(d, i == pN);
			mLightSheetMicroscope.addCurrentStateToQueue();
		}
		mLightSheetMicroscope.finalizeQueue();
		// Building queue end.

		final Boolean lPlayQueueAndWait = mLightSheetMicroscope.playQueueAndWaitForStacks(	mLightSheetMicroscope.getQueueLength(),
																							TimeUnit.SECONDS);

		if (!lPlayQueueAndWait)
			return null;

		final StackInterface<UnsignedShortType, ShortOffHeapAccess> lStackInterface = mLightSheetMicroscope.getStackVariable(pDetectionArmIndex)
																											.get();

		OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> lImage = (OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>) lStackInterface.getImage();

		System.out.println("lImage=" + lImage);

		long lWidth = lImage.dimension(0);
		long lHeight = lImage.dimension(1);

		System.out.format(	"image: width=%d, height=%d \n",
							lWidth,
							lHeight);

		Vector2D lPoint = ImageAnalysisUtils.findBrightestPointsForEachPlane(lImage)[0];

		System.out.format(	"image: lightsheet center at: %s \n",
							lPoint);

		return lPoint;
	}

	public double apply(int pLightSheetIndex)
	{
		double lError = 0;
		for (int d = 0; d < mNumberOfDetectionArmDevices; d++)
			lError += apply(pLightSheetIndex, d);
		return lError;
	}

	public double apply(int pLightSheetIndex, int pDetectionArmIndex)
	{
		Vector2D lOriginFromX = mOriginFromX.get(	pLightSheetIndex,
													pDetectionArmIndex);
		Vector2D lOriginFromY = mOriginFromY.get(	pLightSheetIndex,
													pDetectionArmIndex);

		Vector2D lOrigin = new Vector2D(0, 0);
		lOrigin.add(lOriginFromX);
		lOrigin.add(lOriginFromY);
		lOrigin.scalarMultiply(0.5);

		Vector2D lUnitVectorU = mUnitVectorFromX.get(	pLightSheetIndex,
														pDetectionArmIndex);
		Vector2D lUnitVectorV = mUnitVectorFromY.get(	pLightSheetIndex,
														pDetectionArmIndex);

		Matrix lMatrix = new Matrix(2, 2);
		lMatrix.set(0, 0, lUnitVectorU.getX());
		lMatrix.set(1, 0, lUnitVectorU.getY());
		lMatrix.set(0, 1, lUnitVectorV.getX());
		lMatrix.set(1, 1, lUnitVectorV.getY());

		mTransformMatrices.put(	pLightSheetIndex,
								pDetectionArmIndex,
								lMatrix);

		Matrix lInverse = lMatrix.inverse();

		Matrix lOriginAsMatrix = new Matrix(2, 1);
		lOriginAsMatrix.set(0, 0, lOrigin.getX());
		lOriginAsMatrix.set(1, 0, lOrigin.getY());

		Matrix lNewOffsets = lInverse.times(lOriginAsMatrix);
		double lXOffset = lNewOffsets.get(0, 0);
		double lYOffset = lNewOffsets.get(0, 0);

		LightSheetInterface lLightSheetDevice = mLightSheetMicroscope.getDeviceLists()
																		.getLightSheetDevice(pLightSheetIndex);
		ObjectVariable<UnivariateAffineComposableFunction> lFunctionXVariable = lLightSheetDevice.getXFunction();
		ObjectVariable<UnivariateAffineComposableFunction> lFunctionYVariable = lLightSheetDevice.getYFunction();

		// TODO: use pixel calibration here...
		lFunctionXVariable.get()
							.composeWith(UnivariateAffineFunction.axplusb(	1,
																			lXOffset));
		lFunctionYVariable.get()
							.composeWith(UnivariateAffineFunction.axplusb(	1,
																			lYOffset));

		lFunctionXVariable.setCurrent();
		lFunctionXVariable.setCurrent();

		// TODO: use pixel calibration here...
		ObjectVariable<UnivariateAffineComposableFunction> lHeightFunctionVariable = lLightSheetDevice.getHeightFunction();
		lHeightFunctionVariable.set(UnivariateAffineFunction.axplusb(	1,
																		0));
		lHeightFunctionVariable.setCurrent();

		double lError = Vector2D.distance(lOriginFromX, lOriginFromY);

		return lError;
	}

	public void reset()
	{

	}

	public Matrix getTransformMatrix(	int pLightSheetIndex,
									int pDetectionArmIndex)
	{
		return mTransformMatrices.get(pLightSheetIndex,pDetectionArmIndex);
	}

}
