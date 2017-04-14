package clearcontrol.microscope.lightsheet;

import clearcontrol.microscope.MicroscopeInterface;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;

/**
 * Interface implemented by all lightsheet microscope implementations
 *
 * @author royer
 */
public interface LightSheetMicroscopeInterface extends
                                               MicroscopeInterface<LightSheetMicroscopeQueue>

{

  /**
   * Returns the number of detection arms
   * 
   * @return number of detection arms
   */
  public int getNumberOfDetectionArms();

  /**
   * Returns the number of lightsheets
   * 
   * @return number of lightsheets
   */
  public int getNumberOfLightSheets();

  /**
   * Returns the number of lightsheets
   * 
   * @return number of lightsheets
   */
  public int getNumberOfLaserLines();

  /**
   * Returns the detection arm given its index
   * 
   * @param pDeviceIndex
   *          detection arm index
   * @return detection arm for index
   */
  public DetectionArmInterface getDetectionArm(int pDeviceIndex);

  /**
   * Returns the lightsheet given its index
   * 
   * @param pDeviceIndex
   *          lightsheet index
   * @return lightsheet for index
   */
  public LightSheetInterface getLightSheet(int pDeviceIndex);

  /**
   * Sets with and height of camera image
   * 
   * @param pWidth
   *          width
   * @param pHeight
   *          height
   */
  public void setCameraWidthHeight(long pWidth, long pHeight);

  /**
   * Returns the camera image width.
   * 
   * @param pCameraDeviceIndex
   *          camera device index
   * @return width in pixels
   */
  int getCameraWidth(int pCameraDeviceIndex);

  /**
   * Returns the camera image height.
   * 
   * @param pCameraDeviceIndex
   *          camera device index
   * @return height in pixels
   */
  int getCameraHeight(int pCameraDeviceIndex);

  /**
   * Sets image acquisition exposure in seconds
   * 
   * @param pExposureInSeconds
   *          exposure in seconds
   */
  public void setExposure(double pExposureInSeconds);

  /**
   * Returns the camera exposure time in seconds.
   * 
   * @param pCameraDeviceIndex
   *          camera device index
   * @return camera exposure time in seconds
   */
  double getExposure(int pCameraDeviceIndex);

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
   * Returns whether a given laser is on or off.
   * 
   * @param pLaserIndex
   *          laser device index
   * @return true if on, false if off
   */
  boolean getLO(int pLaserIndex);

  /**
   * Sets a the laser power (mW) for a given laser device.
   * 
   * @param pLaserIndex
   *          index of the laser device
   * @param pLaserPowerInmW
   *          laser power in mW
   */
  public void setLP(int pLaserIndex, double pLaserPowerInmW);

  /**
   * Returns the laser power in mW for a given laser device
   * 
   * @param pLaserIndex
   *          laser device index
   * @return laser power in mW
   */
  double getLP(int pLaserIndex);

}
