package clearcontrol.core.device.task;

import clearcontrol.core.concurrent.timing.WaitingInterface;
import clearcontrol.core.log.LoggingFeature;

/**
 * Base class for loop task devices
 *
 * @author royer
 */
public abstract class LoopTaskDevice extends TaskDevice implements
                                     LoggingFeature,
                                     WaitingInterface
{

  /**
   * Instanciates a loop task device
   * 
   * @param pDeviceName
   *          device name
   */
  public LoopTaskDevice(final String pDeviceName)
  {
    super(pDeviceName);
  }

  @Override
  public void run()
  {
    while (!getStopSignalVariable().get())
    {
      boolean lResult = loop();

      if (!lResult)
        stopTask();
    }
  };

  /**
   * loop to execute
   * 
   * @return true -> continue looping, false -> stop loop
   */
  public abstract boolean loop();

}
