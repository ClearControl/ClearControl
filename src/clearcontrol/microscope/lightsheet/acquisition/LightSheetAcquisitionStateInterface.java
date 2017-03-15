package clearcontrol.microscope.lightsheet.acquisition;

import clearcontrol.microscope.lightsheet.acquisition.tables.InterpolationTables;

/**
 * Created by moon on 8/22/16.
 */
public interface LightSheetAcquisitionStateInterface
{
  InterpolationTables getCurrentState();

  void applyStateAtControlPlane(int i);

  int getBestDetectionArm(int czi);

  void setCurrentState(InterpolationTables newAcquisitionState);
}
