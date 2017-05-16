package clearcontrol.microscope.lightsheet.state;

import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeInterface;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.state.tables.InterpolationTables;
import clearcontrol.microscope.state.AcquisitionStateInterface;

/**
 * Lightsheet acquisition state interface
 *
 * @author royer
 * @param <S>
 *          reflexive state type
 */
public interface LightSheetAcquisitionStateInterface<S extends LightSheetAcquisitionStateInterface<S>>
                                                    extends
                                                    AcquisitionStateInterface<LightSheetMicroscopeInterface, LightSheetMicroscopeQueue>,
                                                    Cloneable
{

  /**
   * Returns a queue with a given range of cameras, lightsheets and laser lines.
   * 
   * @param pCameraIndexMin
   *          lower camera index (inclusive)
   * @param pCameraIndexMax
   *          higher camera index (exclusive)
   * @param pLightSheetIndexMin
   *          lower lightsheet index (inclusive)
   * @param pLightSheetIndexMax
   *          higher lightsheet index (exclusive)
   * @param pLaserLineIndexMin
   *          lower laser line index (inclusive)
   * @param pLaserLineIndexMax
   *          higher laser line index (exclusive)
   * @return queue
   */
  LightSheetMicroscopeQueue getQueue(int pCameraIndexMin,
                                     int pCameraIndexMax,
                                     int pLightSheetIndexMin,
                                     int pLightSheetIndexMax,
                                     int pLaserLineIndexMin,
                                     int pLaserLineIndexMax);

  /**
   * Returns current interpolation tables
   * 
   * @return current Interpolation tables
   */
  InterpolationTables getInterpolationTables();

  /**
   * Applies state at a given control plane index
   * 
   * @param pQueue
   *          queue to append to
   * @param pControlPlaneIndex
   *          control plane index
   */
  void applyStateAtControlPlane(LightSheetMicroscopeQueue pQueue,
                                int pControlPlaneIndex);

  /**
   * Returns state variable x
   * 
   * @return stage variable x
   */
  BoundedVariable<Number> getStageXVariable();

  /**
   * Returns state variable y
   * 
   * @return stage variable y
   */
  BoundedVariable<Number> getStageYVariable();

  /**
   * Returns state variable z
   * 
   * @return stage variable z
   */
  BoundedVariable<Number> getStageZVariable();

  /**
   * Returns the On/Off variable for a given camera index
   * 
   * @param pCameraIndex
   *          camera index
   * @return on/off variable.
   */
  Variable<Boolean> getCameraOnOffVariable(int pCameraIndex);

  /**
   * Returns the On/Off variable for a given lightsheet index
   * 
   * @param pLightSheetIndex
   *          lightsheet index
   * @return on/off variable.
   */
  Variable<Boolean> getLightSheetOnOffVariable(int pLightSheetIndex);

  /**
   * Returns the On/Off variable for a given laser index
   * 
   * @param pLaserLineIndex
   *          laser line index
   * @return on/off variable.
   */
  Variable<Boolean> getLaserOnOffVariable(int pLaserLineIndex);

}
