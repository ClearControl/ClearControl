package clearcontrol.microscope.lightsheet;

import java.util.concurrent.TimeUnit;

import clearcontrol.microscope.MicroscopeInterface;

/**
 * Interface implemented by all lightsheet microscope implementations
 *
 * @author royer
 */
public interface LightSheetMicroscopeInterface extends
                                               MicroscopeInterface<LightSheetMicroscopeQueue>

{

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
   * Returns the camera exposure time.
   * 
   * @param pCameraDeviceIndex
   *          camera device index
   * @param pTimeUnit
   *          time unit in which to return the exposure
   * @return camera exposure time in the given unit
   */
  long getExposure(int pCameraDeviceIndex, TimeUnit pTimeUnit);

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
