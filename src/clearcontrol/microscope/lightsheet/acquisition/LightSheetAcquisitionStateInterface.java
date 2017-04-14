package clearcontrol.microscope.lightsheet.acquisition;

import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeInterface;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.acquisition.tables.InterpolationTables;
import clearcontrol.microscope.state.AcquisitionStateInterface;

/**
 * Lightsheet acquisition state interface
 *
 * @author royer
 */
public interface LightSheetAcquisitionStateInterface extends
                                                     AcquisitionStateInterface<LightSheetMicroscopeInterface, LightSheetMicroscopeQueue>
{
  /**
   * Returns curent interpolationtables
   * 
   * @return current Interpolation tables
   */
  InterpolationTables getInterpolationTables();

  /**
   * Returns control plane index
   * 
   * @param pControlPlaneIndex
   *          control plane index
   * @return best detection arm index
   */
  int getBestDetectionArm(int pControlPlaneIndex);

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
   * Applies new stage position
   * 
   * @param pLightSheetMicroscopeInterface
   */
  void applyStagePosition();

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
   * Returns the On/Off variable for a given lightsheet index
   * 
   * @param pLightSheetIndex
   *          lightsheet index
   * @return on/off variable.
   */
  Variable<Boolean> getLightSheetOnOffVariable(int pLightSheetIndex);

}
