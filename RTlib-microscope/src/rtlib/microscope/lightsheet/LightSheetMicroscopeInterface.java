package rtlib.microscope.lightsheet;

import java.util.concurrent.TimeUnit;

import rtlib.microscope.lightsheet.illumination.si.StructuredIlluminatioPatternInterface;

public interface LightSheetMicroscopeInterface
{

	/**
	 * Sets with and height of camera image
	 * 
	 * @param pWidth
	 *          width
	 * @param pHeight
	 *          height
	 */
	public void setWidthHeight(int pWidth, int pHeight);

	/**
	 * Sets image aquisition exposure in
	 * 
	 * @param pValue
	 *          time
	 * @param pTimeUnit
	 *          time unit
	 * 
	 */
	public void setExposure(long pValue, TimeUnit pTimeUnit);

	/**
	 * Sets a detection objective to a given position.
	 * 
	 * @param pDetectionIndex
	 *          index of detection objective
	 * @param pPositionZ
	 *          position to set objective
	 */
	public void setDZ(int pDetectionIndex, double pPositionZ);

	/**
	 * Sets the lightsheet's Z position.
	 * 
	 * @param pLightSheetIndex
	 *          lightsheet index
	 * @param pPositionZ
	 *          Z position of lightsheet
	 */
	public void setIZ(int pLightSheetIndex, double pPositionZ);

	/**
	 * Sets the lightsheet's Y position.
	 * 
	 * @param pLightSheetIndex
	 *          lightsheet index
	 * @param pPositionY
	 *          lightsheet's Y position
	 */
	public void setIY(int pLightSheetIndex, double pPositionY);

	/**
	 * Sets the lightsheet's angle alpha.
	 * 
	 * @param pLightSheetIndex
	 *          lightsheet index
	 * @param pAngleAlpha
	 *          lightsheet's alpha angle
	 */
	public void setIA(int pLightSheetIndex, double pAngleAlpha);

	/**
	 * Sets the lightsheet's angle beta.
	 * 
	 * @param pLightSheetIndex
	 *          lightsheet index
	 * @param pAngleBeta
	 *          lightsheet's beta angle
	 */
	public void setIB(int pLightSheetIndex, double pAngleBeta);

	/**
	 * Sets the lightsheet's width - i.e. its dimension along the propagation
	 * axis.
	 * 
	 * @param pLightSheetIndex
	 *          lightsheet index
	 * @param pRange
	 *          lightsheet's range
	 */
	public void setIW(int pLightSheetIndex, double pRange);

	/**
	 * Sets the lightsheet's height - i.e. its dimension along the scanning
	 * direction.
	 * 
	 * @param pLightSheetIndex
	 *          lightsheet index
	 * @param pLength
	 *          lightsheet's length
	 */
	public void setIH(int pLightSheetIndex, double pLength);

	/**
	 * Sets the lightsheet's analog laser power level.
	 * 
	 * @param pLightSheetIndex
	 *          lightsheet index
	 * @param pValue
	 *          lightsheet's analog modulation level
	 */
	public void setIP(int pLightSheetIndex, double pValue);

	/**
	 * Sets the structured illumination pattern for a given lightsheet and laser
	 * line.
	 * 
	 * @param pLightSheetIndex
	 *          lightsheet index
	 * @param pLaserIndex
	 *          laser index
	 * @param pPattern
	 *          pattern
	 */
	void setIPattern(	int pLightSheetIndex,
										int pLaserIndex,
										StructuredIlluminatioPatternInterface pPattern);

	/**
	 * Sets whether the structured illumination pattern for a given lightsheet and
	 * laser line should be active or not.
	 * 
	 * @param pLightSheetIndex
	 *          lightsheet index
	 * @param pLaserIndex
	 *          laser index
	 * @param pOnOff
	 *          true for on, false for off.
	 */
	public void setIPatternOnOff(	int pLightSheetIndex,
																int pLaserIndex,
																boolean pOnOff);



}
