package clearcontrol.microscope.lightsheet;

import clearcontrol.microscope.lightsheet.component.lightsheet.si.StructuredIlluminationPatternInterface;

/**
 * Interface implemented by lightsheet microscope queues to control the
 * different DOFs
 *
 * @author royer
 */
public interface LightSheetMicroscopeParameterInterface
{

  /**
   * Sets full ROI for all cameras.
   */
  void setFullROI();

  /**
   * Sets the image centered ROI for all cameras.
   * 
   * @param pWidth
   *          width
   * @param pHeight
   *          height
   */
  void setCenteredROI(int pWidth, int pHeight);

  /**
   * Sets exposure in seconds
   * 
   * @param pExposureISeconds
   *          exposure in seconds
   */
  void setExp(double pExposureISeconds);

  /**
   * Sets the stack finalisation time in seconds.
   * 
   * @param pFinalisationTimeInSeconds
   *          stack finalisation time in seconds
   */
  void setFinalisationTime(double pFinalisationTimeInSeconds);

  /**
   * Sets the transition time in seconds
   * 
   * @param pTransitionTimeInSeconds
   *          transition time in seconds
   */
  void setTransitionTime(double pTransitionTimeInSeconds);

  /**
   * Selects _one_ light sheet to direct light to:
   * 
   * @param pLightSheetIndex
   *          light sheet index
   * 
   */
  public void setI(int pLightSheetIndex);

  /**
   * Returns true if a light sheet is 'on'.
   * 
   * @param pLightSheetIndex
   *          light sheet device index
   * @return true if on, false if off.
   */
  public boolean getI(int pLightSheetIndex);

  /**
   * Directs light to one or several light sheets:
   * 
   * @param pLightSheetIndex
   *          light sheet index
   * @param pOnOff
   *          true is on, false is off
   */
  public void setI(int pLightSheetIndex, boolean pOnOff);

  /**
   * Sets the on/off state of all lightsheets.
   * 
   * @param pOnOff
   *          true: all lightsheets receive light, false: no lightsheet receives
   *          light.
   */
  public void setI(boolean pOnOff);

  /**
   * Sets a flag that determines whether all cameras should acquire (or keep) an
   * image.
   * 
   * @param pKeepImage
   *          true if image should be acquired (or kept), false otherwise
   */
  public void setC(boolean pKeepImage);

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
   * Returns whether the given camera is set to acquire/keep an image.
   * 
   * @param pCameraIndex
   *          camera device index
   * @return true if acquiring
   */
  boolean getC(int pCameraIndex);

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
   * Sets all detection objective to a given position
   * 
   * @param pPositionZ
   *          to set objective
   */
  void setDZ(double pPositionZ);

  /**
   * Returns the detection objective Z position
   * 
   * @param pDetectionArmIndex
   *          detection arm index
   * @return Z position
   */
  double getDZ(int pDetectionArmIndex);

  /**
   * Sets the lightsheet's X position (illumination objective).
   * 
   * @param pLightSheetIndex
   *          lightsheet device index
   * @param pPositionX
   *          lightsheet's X position
   */
  public void setIX(int pLightSheetIndex, double pPositionX);

  /**
   * Returns the lighsheet's X position (illumination objective).
   * 
   * @param pLightSheetIndex
   *          lightsheet device index
   * @return lightsheet's X position
   */
  double getIX(int pLightSheetIndex);

  /**
   * Sets the lightsheet's Y position (vertical lightsheet scanning direction).
   * 
   * @param pLightSheetIndex
   *          lightsheet index
   * @param pPositionY
   *          lightsheet's Y position
   */
  public void setIY(int pLightSheetIndex, double pPositionY);

  /**
   * Returns the lighsheet's Y position (vertical lightsheet scanning
   * direction).
   * 
   * @param pLightSheetIndex
   *          lightsheet device index
   * @return lightsheet's Y position
   */
  double getIY(int pLightSheetIndex);

  /**
   * Sets the lightsheet's Z position (stack scanning direction).
   * 
   * @param pLightSheetIndex
   *          lightsheet device index
   * @param pPositionZ
   *          Z position of lightsheet
   */
  public void setIZ(int pLightSheetIndex, double pPositionZ);

  /**
   * Sets all lightsheet's Z position (stack scanning direction).
   * 
   * @param pPositionZ
   *          Z position of lightsheet
   */
  public void setIZ(double pPositionZ);

  /**
   * Returns the lightsheet's Z position (stack scanning direction).
   * 
   * @param pLightSheetIndex
   *          lightsheet device index
   * @return Z position of lightsheet
   */
  double getIZ(int pLightSheetIndex);

  /**
   * Sets the lightsheet's angle alpha.
   * 
   * @param pLightSheetIndex
   *          lightsheet device index
   * @param pAngleAlpha
   *          lightsheet's alpha angle
   */
  public void setIA(int pLightSheetIndex, double pAngleAlpha);

  /**
   * Returns the lightsheet's angle alpha.
   * 
   * @param pLightSheetIndex
   *          lightsheet device index
   * @return lightsheet's alpha angle
   */
  double getIA(int pLightSheetIndex);

  /**
   * Sets the lightsheet's angle beta.
   * 
   * @param pLightSheetIndex
   *          lightsheet device index
   * @param pAngleBeta
   *          lightsheet's beta angle
   */
  public void setIB(int pLightSheetIndex, double pAngleBeta);

  /**
   * Returns the lightsheet's angle beta.
   * 
   * @param pLightSheetIndex
   *          lightsheet device index
   * @return lightsheet's beta angle
   */
  double getIB(int pLightSheetIndex);

  /**
   * Sets the lightsheet's width - i.e. its dimension along the propagation
   * axis.
   * 
   * @param pLightSheetIndex
   *          lightsheet device index
   * @param pRange
   *          lightsheet's width
   */
  public void setIW(int pLightSheetIndex, double pRange);

  /**
   * Returns the lightsheet's width - i.e. its dimension along the propagation
   * axis.
   * 
   * @param pLightSheetIndex
   *          lightsheet device index
   * @return lightsheet's width
   */
  double getIW(int pLightSheetIndex);

  /**
   * Sets the lightsheet's height - i.e. its dimension along the scanning
   * direction.
   * 
   * @param pLightSheetIndex
   *          lightsheet device index
   * @param pHeight
   *          lightsheet's height
   */
  public void setIH(int pLightSheetIndex, double pHeight);

  /**
   * Returns the lighsheet's height - i.e. its dimension along the scanning
   * direction.
   * 
   * @param pLightSheetIndex
   *          lightsheet device index
   * @return lightsheet's height
   */
  double getIH(int pLightSheetIndex);

  /**
   * Sets the lightsheet's analog laser modulation level (common to all lasers).
   * 
   * @param pLightSheetIndex
   *          lightsheet index
   * @param pValue
   *          lightsheet's analog modulation level
   */
  public void setIP(int pLightSheetIndex, double pValue);

  /**
   * Returns the lightsheet's analog laser modulation level (common to all
   * lasers)
   * 
   * @param pLightSheetIndex
   * @return analog laser modulation level
   */
  double getIP(int pLightSheetIndex);

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
   *          lightsheet device index
   * 
   * @param pAdapt
   *          true if power should be adapted, false if not
   */
  void setIPA(int pLightSheetIndex, boolean pAdapt);

  /**
   * Returns the state of the lightsheet's flag that determines whether the
   * intensity of the laser should be modulated to compensate for changes in the
   * lightsheet's height and width.
   * 
   * @param pLightSheetIndex
   *          lightsheet device index
   * @return true if power should be adapted, false if not
   */
  boolean getIPA(int pLightSheetIndex);

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
   *          lightsheet device index
   * @param pLaserIndex
   *          laser device index
   * @param pOn
   *          state (true= on, false= off)
   */
  void setILO(int pLightSheetIndex, int pLaserIndex, boolean pOn);

  /**
   * Returns the state (on/off) of a specific digital trigger for a given laser
   * line of a given lightsheet.
   * 
   * @param pLightSheetIndex
   *          lightsheet device index
   * @param pLaserIndex
   *          laser device index
   * @return state (true= on, false= off)
   */
  boolean getILO(int pLightSheetIndex, int pLaserIndex);

  /**
   * Sets the structured illumination pattern for a given lightsheet and laser
   * line.
   * 
   * @param pLightSheetIndex
   *          lightsheet device index
   * @param pLaserIndex
   *          laser device index
   * @param pPattern
   *          SI Pattern
   */
  public void setIPattern(int pLightSheetIndex,
                          int pLaserIndex,
                          StructuredIlluminationPatternInterface pPattern);

  /**
   * Returns the SI Pattern in use for a given lighsheet and laser line.
   * 
   * @param pLightSheetIndex
   *          lightsheet device index
   * @param pLaserIndex
   *          laser device index
   * @return SI Pattern
   */
  StructuredIlluminationPatternInterface getIPattern(int pLightSheetIndex,
                                                     int pLaserIndex);

  /**
   * Sets whether the structured illumination pattern for a given lightsheet and
   * laser line should be active or not.
   * 
   * @param pLightSheetIndex
   *          lightsheet device index
   * @param pLaserIndex
   *          laser device index
   * @param pOnOff
   *          true for on, false for off.
   */
  public void setIPatternOnOff(int pLightSheetIndex,
                               int pLaserIndex,
                               boolean pOnOff);

  /**
   * Returns whether the currently set SI pattern should be used or not.
   * 
   * @param pLightSheetIndex
   *          lightsheet device index
   * @param pLaserIndex
   *          laser device index
   * @return illumination SI pattern flag (true or false)
   */
  public boolean getIPatternOnOff(int pLightSheetIndex,
                                  int pLaserIndex);

}
