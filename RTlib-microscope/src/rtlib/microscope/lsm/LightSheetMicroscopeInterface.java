package rtlib.microscope.lsm;

import java.util.concurrent.TimeUnit;

import rtlib.microscope.lsm.component.lightsheet.si.StructuredIlluminationPatternInterface;

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
	public void setCameraWidthHeight(int pWidth, int pHeight);

	/**
	 * Sets image acquisition exposure in
	 * 
	 * @param pValue
	 *          time
	 * @param pTimeUnit
	 *          time unit
	 * 
	 */
	public void setExposure(long pValue, TimeUnit pTimeUnit);

	/**
	 * Selects _one_ light sheet to direct light to:
	 * 
	 * @param pLightSheetIndex
	 *          light sheet index
	 * 
	 */
	public void selectI(int pLightSheetIndex);

	/**
	 * Directs light to one or several light sheets:
	 * 
	 * @param pLightSheetIndex
	 *          light sheet index
	 * @param pOnOff
	 *          true is on, false is off
	 */
	public void switchI(int pLightSheetIndex, boolean pOnOff);

	/**
	 * Switches on/off a given laser.
	 * 
	 * @param pLaserIndex
	 *          index of the laser device
	 * @param pLaserOnOff
	 *          true for on, false otherwise
	 */
	public void setLO(int pLaserIndex, boolean pLaserOnOff);

	/**
	 * Sets a the laser power (mW) for a given laser.
	 * 
	 * @param pLaserIndex
	 *          index of the laser device
	 * @param pLaserPowerInmW
	 *          laser power in mW
	 */
	public void setLP(int pLaserIndex, double pLaserPowerInmW);

	/**
	 * Sets a flag that determines whether the camera should acquire (or keep) an
	 * image.
	 * 
	 * @param pCameraIndex
	 *          index of the stack camera device
	 * @param pKeepImage
	 *          true if image should be acquired (or kept), false otherwise
	 */
	public void setC(int pCameraIndex, boolean pKeepImage);

	/**
	 * Sets a flag that determines whether all cameras should acquire (or keep) an
	 * image.
	 * 
	 * @param pKeepImage
	 *          true if image should be acquired (or kept), false otherwise
	 */
	public void setC(boolean pKeepImage);

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
	 * Sets the lightsheet's X position.
	 * 
	 * @param pLightSheetIndex
	 *          lightsheet index
	 * @param pPositionX
	 *          lightsheet's X position
	 */
	public void setIX(int pLightSheetIndex, double pPositionX);

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
	 * Sets the lightsheet's Z position.
	 * 
	 * @param pLightSheetIndex
	 *          lightsheet index
	 * @param pPositionZ
	 *          Z position of lightsheet
	 */
	public void setIZ(int pLightSheetIndex, double pPositionZ);

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
	 * Sets the lightsheet's analog laser modulation level.
	 * 
	 * @param pLightSheetIndex
	 *          lightsheet index
	 * @param pValue
	 *          lightsheet's analog modulation level
	 */
	public void setIP(int pLightSheetIndex, double pValue);

	/**
	 * Sets a flag that determines whether the laser power should be adapted to
	 * the height and with of the lightsheets.
	 * 
	 * @param pAdapt
	 *          true if power should be adapted
	 */
	public void setIPA(boolean pAdapt);

	/**
	 * Sets a flag that determines whether the laser power should be adapted to
	 * the height and with of a given lightsheet.
	 * 
	 * @param pLightSheetIndex
	 *          lightshet index
	 * 
	 * @param pAdapt
	 *          true if power should be adapted
	 */
	void setIPA(int pLightSheetIndex, boolean pAdapt);

	/**
	 * Sets the state (on/off) of all laser lines of all lightsheets.
	 * 
	 * @param pOn
	 *          state (true= on, false= off)
	 */
	void setILO(boolean pOn);
	
	/**
	 * Sets the state (on/off) of all laser lines of a given lightsheet.
	 * 
	 * @param pLightSheetIndex
	 *          lightsheet index
	 * @param pOn
	 *          state (true= on, false= off)
	 */
	void setILO(int pLightSheetIndex, boolean pOn);

	/**
	 * Sets the state (on/off) of a specific digital trigger of laser line for a
	 * given lightsheet.
	 * 
	 * @param pLightSheetIndex
	 *          lightsheet index
	 * @param pLaserIndex
	 *          laser line index
	 * @param pOn
	 *          state (true= on, false= off)
	 */
	void setILO(int pLightSheetIndex, int pLaserIndex, boolean pOn);

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
	public void setIPattern(int pLightSheetIndex,
													int pLaserIndex,
													StructuredIlluminationPatternInterface pPattern);

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
