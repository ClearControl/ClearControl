package clearcontrol.microscope.lightsheet.calibrator;

import static java.lang.Math.max;
import static java.lang.Math.min;

import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;

import org.ejml.data.DenseMatrix64F;
import org.ejml.simple.SimpleMatrix;

/**
 * Lightsheet positioner
 *
 * @author royer
 */
public class LightSheetPositioner
{

  private DenseMatrix64F mTransformMatrix, mInverseTransformMatrix;

  /**
   * Instanciates a lightsheet positioner
   */
  public LightSheetPositioner()
  {
  }

  /**
   * Instanciates a lightsheet positioner given a transform matrix
   * 
   * @param pTransformMatrix
   *          transform matrix
   */
  public LightSheetPositioner(SimpleMatrix pTransformMatrix)
  {
    setTransformMatrix(pTransformMatrix.getMatrix());
    setInverseTransformMatrix(pTransformMatrix.invert().getMatrix());
  }

  /**
   * Sets the center (X,Y) in pixel coordinates of a given lightsheet
   * 
   * @param pLightSheetMicroscope
   *          lightsheet microscope
   * @param pLightSheetIndex
   *          lightsheet index
   * @param pPixelX
   *          pixel X coordinate
   * @param pPixelY
   *          pixel Y coordinate
   */
  public void setAt(LightSheetMicroscope pLightSheetMicroscope,
                    int pLightSheetIndex,
                    double pPixelX,
                    double pPixelY)
  {
    LightSheetInterface lLightSheetDevice =
                                          pLightSheetMicroscope.getDeviceLists()
                                                               .getDevice(LightSheetInterface.class,
                                                                          pLightSheetIndex);

    setAt(lLightSheetDevice, pPixelX, pPixelY);
  }

  /**
   * Sets the center (X,Y) in pixel coordinates of a given lightsheet
   * 
   * @param pLightSheetDevice
   *          lightsheet device
   * @param pPixelX
   *          pixel X coordinate
   * @param pPixelY
   *          pixel Y coordinate
   */
  public void setAt(LightSheetInterface pLightSheetDevice,
                    double pPixelX,
                    double pPixelY)
  {

    SimpleMatrix lControlVector = getControlVector(pPixelX, pPixelY);

    double lLightSheetX = lControlVector.get(0, 0);
    double lLightSheetY = lControlVector.get(1, 0);

    pLightSheetDevice.getXVariable().set(lLightSheetX);
    pLightSheetDevice.getYVariable().set(lLightSheetY);
  }

  /**
   * Illuminates a given box in pixel coordiantes [[minx,miny],[maxx,maxy]]
   * 
   * @param pLightSheetDevice
   *          lightsheet device
   * @param pMinX
   *          min x
   * @param pMinY
   *          min y
   * @param pMaxX
   *          max x
   * @param pMaxY
   *          max y
   */
  public void illuminateBox(LightSheetInterface pLightSheetDevice,
                            double pMinX,
                            double pMinY,
                            double pMaxX,
                            double pMaxY)
  {

    SimpleMatrix lControlVectorA = getControlVector(pMinX, pMinY);
    SimpleMatrix lControlVectorB = getControlVector(pMaxX, pMaxY);
    SimpleMatrix lControlVectorC = getControlVector(pMinX, pMaxY);
    SimpleMatrix lControlVectorD = getControlVector(pMaxX, pMinY);

    double lAX = lControlVectorA.get(0, 0);
    double lAY = lControlVectorA.get(1, 0);

    double lBX = lControlVectorB.get(0, 0);
    double lBY = lControlVectorB.get(1, 0);

    double lCX = lControlVectorC.get(0, 0);
    double lCY = lControlVectorC.get(1, 0);

    double lDX = lControlVectorD.get(0, 0);
    double lDY = lControlVectorD.get(1, 0);

    double lMinLSX = min(min(lAX, lBX), min(lCX, lDX));
    double lMinLSY = min(min(lAY, lBY), min(lCY, lDY));

    double lMaxLSX = max(max(lAX, lBX), max(lCX, lDX));
    double lMaxLSY = max(max(lAY, lBY), max(lCY, lDY));

    double lX = (lMaxLSX + lMinLSX) / 2;
    double lY = (lMaxLSY + lMinLSY) / 2;

    double lWidth = lMaxLSX - lMinLSX;
    double lHeight = lMaxLSY - lMinLSY;

    pLightSheetDevice.getXVariable().set(lX);
    pLightSheetDevice.getYVariable().set(lY);

    pLightSheetDevice.getWidthVariable().set(lWidth);
    pLightSheetDevice.getHeightVariable().set(lHeight);

  }

  private SimpleMatrix getControlVector(double pPixelX,
                                        double pPixelY)
  {
    SimpleMatrix lVector = new SimpleMatrix(2, 1);
    lVector.set(0, 0, pPixelX);
    lVector.set(1, 0, pPixelY);

    SimpleMatrix lControlVector =
                                SimpleMatrix.wrap(getInverseTransformMatrix())
                                            .mult(lVector);
    return lControlVector;
  }

  /**
   * Returns transform matrix
   * 
   * @return transform matrix
   */
  public DenseMatrix64F getTransformMatrix()
  {
    return mTransformMatrix;
  }

  /**
   * Sets transform matrix
   * 
   * @param pTransformMatrix
   *          transform
   */
  public void setTransformMatrix(DenseMatrix64F pTransformMatrix)
  {
    mTransformMatrix = pTransformMatrix;
  }

  /**
   * Returns the inverse trasnform matrix
   * 
   * @return inverse transform matrix
   */
  public DenseMatrix64F getInverseTransformMatrix()
  {
    return mInverseTransformMatrix;
  }

  /**
   * Sets the inverse transform matrix
   * 
   * @param pInverseTransformMatrix
   *          inverse transform matrix
   */
  public void setInverseTransformMatrix(DenseMatrix64F pInverseTransformMatrix)
  {
    mInverseTransformMatrix = pInverseTransformMatrix;
  }

}
