package clearcontrol.microscope.lightsheet.signalgen.staves;

import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.devices.signalgen.movement.Movement;
import clearcontrol.devices.signalgen.staves.ConstantStave;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetOpticalSwitch;

/**
 * Light sheet microscope optical switch staves. These staves are used when
 * controlling a lightsheeet microscope using digital signals to switch
 * lightsheets on and off.
 *
 * @author royer
 */
public class LightSheetOpticalSwitchStaves
{
  private LightSheetOpticalSwitch mLightSheetOpticalSwitch;

  private final ConstantStave[] mBitStave;

  private int[] mStaveIndex;

  /**
   * Instanciates given a lightsheet optical switch device and default stave
   * index.
   * 
   * @param pLightSheetOpticalSwitch
   *          lightsheet optical switch device
   * @param pDefaultStaveIndex
   *          default stave index
   */
  public LightSheetOpticalSwitchStaves(LightSheetOpticalSwitch pLightSheetOpticalSwitch,
                                       int pDefaultStaveIndex)
  {
    super();
    mLightSheetOpticalSwitch = pLightSheetOpticalSwitch;
    int lNumberOfBits =
                      (int) Math.ceil(Math.log(mLightSheetOpticalSwitch.getNumberOfSwitches())
                                      / Math.log(2));
    mBitStave = new ConstantStave[lNumberOfBits];
    mStaveIndex = new int[lNumberOfBits];

    for (int i = 0; i < mBitStave.length; i++)
    {
      mStaveIndex[i] =
                     MachineConfiguration.getCurrentMachineConfiguration()
                                         .getIntegerProperty("device.lsm.switch."
                                                             + mLightSheetOpticalSwitch.getName()
                                                             + i
                                                             + ".index",
                                                             pDefaultStaveIndex);
      mBitStave[i] = new ConstantStave("lightsheet.s." + i, 0);

    }
  }

  /**
   * Adds staves to staging movements.
   * 
   * @param pBeforeExposureMovement
   *          before exposure movement
   * @param pExposureMovement
   *          exposure movement
   */
  public void addStavesToMovements(Movement pBeforeExposureMovement,
                                   Movement pExposureMovement)
  {
    // Analog outputs before exposure:
    for (int i = 0; i < mBitStave.length; i++)
    {
      pBeforeExposureMovement.setStave(mStaveIndex[i], mBitStave[i]);
      pExposureMovement.setStave(mStaveIndex[i], mBitStave[i]);
    }
  }

  /**
   * Updates staves
   */
  public void update()
  {
    synchronized (this)
    {
      for (int i = 0; i < mBitStave.length; i++)
      {
        mBitStave[i].setValue(mLightSheetOpticalSwitch.getSwitchVariable(i)
                                                      .get() ? 1 : 0);
      }
    }
  }
}
