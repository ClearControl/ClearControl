package clearcontrol.microscope.lightsheet.acquisition;

import clearcontrol.microscope.lightsheet.acquisition.tables.InterpolationTables;

/**
 * Lightsheet acquisition state interface
 *
 * @author royer
 */
public interface LightSheetAcquisitionStateInterface
{
  /**
   * Returns curent interpolationtables
   * 
   * @return current Interpolation tables
   */
  InterpolationTables getCurrentState();

  /**
   * Applies state at a given control plane index
   * 
   * @param pControlPlaneIndex
   *          control plane index
   */
  void applyStateAtControlPlane(int pControlPlaneIndex);

  /**
   * Returns control plane index
   * 
   * @param pControlPlaneIndex
   *          control plane index
   * @return best detection arm index
   */
  int getBestDetectionArm(int pControlPlaneIndex);

  /**
   * Sets the current state to a given set of interpolation tables
   * 
   * @param pInterpolationTables
   *          interpolation tables
   */
  void setCurrentState(InterpolationTables pInterpolationTables);
}
