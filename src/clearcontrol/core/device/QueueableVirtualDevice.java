package clearcontrol.core.device;

import java.util.concurrent.Future;

import clearcontrol.core.device.queue.HasVariableStateQueues;
import clearcontrol.core.device.queue.StateQueueDeviceInterface;
import clearcontrol.core.variable.queue.VariableStateQueues;

/**
 * Queuable virtual device base class. Devices deriving from this base class
 * have the built-in machinery to handle state queues for variables
 *
 * @author royer
 */
public abstract class QueueableVirtualDevice extends VirtualDevice
                                             implements
                                             StateQueueDeviceInterface,
                                             HasVariableStateQueues
{

  protected VariableStateQueues mVariableStateQueues =
                                                     new VariableStateQueues();

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
  public VariableStateQueues getVariableStateQueues()
  {
    return mVariableStateQueues;
  }

  @Override
  public void clearQueue()
  {
    mVariableStateQueues.clearQueue();
  }

  @Override
  public void addCurrentStateToQueue()
  {
    mVariableStateQueues.addCurrentStateToQueue();
  }

  @Override
  public void finalizeQueue()
  {
    mVariableStateQueues.finalizeQueue();
  }

  @Override
  public int getQueueLength()
  {
    return mVariableStateQueues.getQueueLength();
  }

  @Override
  public abstract Future<Boolean> playQueue();
}
