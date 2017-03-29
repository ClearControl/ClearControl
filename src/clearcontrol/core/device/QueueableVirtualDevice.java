package clearcontrol.core.device;

import java.util.concurrent.Future;

import clearcontrol.core.device.queue.RealTimeQueueDeviceInterface;
import clearcontrol.core.device.queue.RealTimeQueueInterface;

/**
 * Queuable virtual device base class. Devices deriving from this base class
 * have the built-in machinery to handle state queues for variables
 *
 * @author royer
 * @param <Q>
 *          queue type
 */
public abstract class QueueableVirtualDevice<Q extends RealTimeQueueInterface>
                                            extends VirtualDevice
                                            implements
                                            RealTimeQueueDeviceInterface<Q>
{

  /**
   * Instanciates a queueable virtual device given a name.
   * 
   * @param pDeviceName
   *          device name
   */
  public QueueableVirtualDevice(final String pDeviceName)
  {
    super(pDeviceName);
  }

  @Override
  public boolean open()
  {
    return true;
  }

  @Override
  public boolean close()
  {
    return true;
  }

  @Override
  public abstract Q requestQueue();

  @Override
  public abstract Future<Boolean> playQueue(Q pQueue);
}
