package rtlib.microscope.lsm.calibrator;

import jama.Matrix;
import rtlib.microscope.lsm.lightsheet.LightSheetInterface;

public class LightSheetPositioner
{

	private LightSheetInterface mLightSheetDevice;
	private Matrix mTransformMatrix,mInverseTransformMatrix;

	public LightSheetPositioner(LightSheetInterface pLightSheetDevice,
								Matrix pTransformMatrix)
	{
		mLightSheetDevice = pLightSheetDevice;
		mTransformMatrix = pTransformMatrix;
		mInverseTransformMatrix = mTransformMatrix.inverse();
	}

	public void setLightSheetAt(double pPixelX, double pPixelY)
	{
		Matrix lControlVector = mInverseTransformMatrix.timesColumnVector(new double[]{pPixelX, pPixelY});
		double lLightSheetX = lControlVector.get(0,0);
		double lLightSheetY = lControlVector.get(1,0);
		
		mLightSheetDevice.getXVariable().set(lLightSheetX);
		mLightSheetDevice.getYVariable().set(lLightSheetY);
	}

}
