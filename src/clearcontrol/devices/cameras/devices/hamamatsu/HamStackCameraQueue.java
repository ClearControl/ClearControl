package clearcontrol.devices.cameras.devices.hamamatsu;

import clearcontrol.core.variable.Variable;
import clearcontrol.devices.cameras.StackCameraQueue;

/**
 * Real time queue for stack camera simulators
 *
 * @author royer
 */
public class HamStackCameraQueue extends
                                 StackCameraQueue<HamStackCameraQueue>

{
  private HamStackCamera mHamStackCamera;

  /**
   * Instantiates a queue given a stack camera simulator
   * 
   * @param pHamStackCamera
   *          parent stack camera
   * 
   */
  public HamStackCameraQueue(HamStackCamera pHamStackCamera)
  {
    super();
    mHamStackCamera = pHamStackCamera;

    mStackWidthVariable = new Variable<Long>("FrameWidth", 2048L)
    {
      @Override
      public Long setEventHook(final Long pOldValue,
                               final Long pNewValue)
      {

        long lAdjustedValue = mHamStackCamera.getDcamDevice()
                                             .adjustWidthHeight(pNewValue,
                                                                4);

        return super.setEventHook(pOldValue, lAdjustedValue);
      }

    };

    mStackHeightVariable = new Variable<Long>("FrameHeight", 2048L)
    {
      @Override
      public Long setEventHook(final Long pOldValue,
                               final Long pNewValue)
      {
        long lAdjustedValue = mHamStackCamera.getDcamDevice()
                                             .adjustWidthHeight(pNewValue,
                                                                4);

        return super.setEventHook(pOldValue, lAdjustedValue);
      }
    };
  }

  /**
   * Instantiates a queue given a template queue's current state
   * 
   * @param pHamStackCameraQueue
   *          template queue
   * 
   */
  public HamStackCameraQueue(HamStackCameraQueue pHamStackCameraQueue)
  {
    super(pHamStackCameraQueue);
    mHamStackCamera = pHamStackCameraQueue.mHamStackCamera;
  }

}
