package clearcontrol.devices.cameras.devices.hamamatsu;

import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.devices.cameras.StackCameraQueue;

/**
 * Real time queue for stack camera simulators
 *
 * @author royer
 */
public class HamStackCameraQueue extends
                                 StackCameraQueue<HamStackCameraQueue>

{
  /**
   * Instantiates a queue given a stack camera simulator
   * 
   * 
   */
  public HamStackCameraQueue()
  {
    super();

    mStackWidthVariable = new BoundedVariable<Long>("FrameWidth",
                                                    2048L,
                                                    0L,
                                                    2048L)
    {
      @Override
      public Long setEventHook(final Long pOldValue,
                               final Long pNewValue)
      {

        long lAdjustedValue =
                            ((HamStackCamera) getStackCamera()).getDcamDevice()
                                                               .adjustWidthHeight(pNewValue,
                                                                                  4);

        return super.setEventHook(pOldValue, lAdjustedValue);
      }

    };

    mStackHeightVariable = new BoundedVariable<Long>("FrameHeight",
                                                     2048L,
                                                     0L,
                                                     2048L)
    {
      @Override
      public Long setEventHook(final Long pOldValue,
                               final Long pNewValue)
      {
        long lAdjustedValue =
                            ((HamStackCamera) getStackCamera()).getDcamDevice()
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
    setStackCamera(pHamStackCameraQueue.getStackCamera());
  }

}
