package clearcontrol.devices.cameras;

import clearcontrol.core.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.core.device.openclose.ReOpenDeviceInterface;
import clearcontrol.core.variable.Variable;

/**
 * Interface implemented by all cameras.
 *
 * @author royer
 */
public interface CameraDeviceInterface extends
                                       OpenCloseDeviceInterface,
                                       ReOpenDeviceInterface
{

  /**
   * This is a convenience method that can (software) trigger the camera.
   */
  void trigger();

  /**
   * Convenience method for setting the exposure time in seconds
   * 
   * @param pExposureInSeconds
   *          exposure in seconds
   */
  void setExposureInSeconds(double pExposureInSeconds);

  /**
   * Convenience method that returns the exposure in seconds.
   * 
   * @return exposure in seconds
   */
  double getExposureInSeconds();

  /**
   * Returns the variable that holds the camera's maximal width (limited by the
   * cameras hardware)
   * 
   * @return stack max width variable
   */
  Variable<Long> getMaxWidthVariable();

  /**
   * Returns the variable that holds the camera's maximal height (limited by the
   * cameras hardware)
   * 
   * @return stack max height variable
   */
  Variable<Long> getMaxHeightVariable();

  /**
   * Returns the variable holding the line readout time in microseconds
   * 
   * @return line readout time variable
   */
  Variable<Double> getLineReadOutTimeInMicrosecondsVariable();

  /**
   * Returns the variable holding the pixel size in micrometers. This is the
   * physical pixel size on the detector.
   * 
   * @return variable holding the pixel size in micrometers
   */
  Variable<Double> getPixelSizeInMicrometersVariable();

  /**
   * Returns the variable holding the number of bytes per pixel
   * 
   * @return bytes per pixel variable
   */
  Variable<Long> getBytesPerPixelVariable();

  /**
   * Returns the variable holding the exposure time in seconds
   * 
   * @return exposure time variable
   */
  Variable<Number> getExposureInSecondsVariable();

  /**
   * Returns the variable holding the current acquisition index.
   * 
   * @return acquisition index variable
   */
  Variable<Long> getCurrentIndexVariable();

  /**
   * Returns variable holding the boolean flag indicating whether the camera is
   * currently acquiring
   * 
   * @return acquiring flag variable
   */
  Variable<Boolean> getIsAcquiringVariable();

  /**
   * Returns the variable holding the trigger signal. Sending an edge (false
   * then true) triggers the camera (single image)
   * 
   * @return trigger variable
   */
  Variable<Boolean> getTriggerVariable();

}
