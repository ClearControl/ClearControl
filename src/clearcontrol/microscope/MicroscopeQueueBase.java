package clearcontrol.microscope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import clearcontrol.core.device.queue.QueueDeviceInterface;
import clearcontrol.core.device.queue.QueueInterface;
import clearcontrol.devices.cameras.StackCameraDeviceInterface;
import clearcontrol.devices.cameras.StackCameraQueue;

/**
 * Base class providing common fields and methods for all microscope queues
 * 
 * @param <M>
 *          microscope type
 * @param <Q>
 *          queue type
 * @author royer
 * 
 */
public class MicroscopeQueueBase<M extends MicroscopeBase<M, Q>, Q extends MicroscopeQueueBase<M, Q>>
                                implements QueueInterface
{

  private M mMicroscope;

  private volatile int mNumberOfEnqueuedStates;

  ArrayList<QueueDeviceInterface<?>> mDeviceList = new ArrayList<>();
  ArrayList<QueueInterface> mQueueList = new ArrayList<>();
  HashMap<QueueDeviceInterface<?>, QueueInterface> mDeviceToQueueMap =
                                                                     new HashMap<>();

  /**
   * Instantiates a microscope queue
   * 
   * @param pMicroscope
   *          parent microscope
   */
  public MicroscopeQueueBase(M pMicroscope)
  {
    super();
    mMicroscope = pMicroscope;

    @SuppressWarnings("rawtypes")
    ArrayList<QueueDeviceInterface> lQueueableDevices =
                                                      mMicroscope.getDeviceLists()
                                                                 .getDevices(QueueDeviceInterface.class);

    for (QueueDeviceInterface<?> lQueueableDevice : lQueueableDevices)
    {
      QueueInterface lRequestedQueue =
                                     lQueueableDevice.requestQueue();

      mDeviceList.add(lQueueableDevice);
      mQueueList.add(lRequestedQueue);
      mDeviceToQueueMap.put(lQueueableDevice, lRequestedQueue);
    }

  }

  /**
   * Returns parent microscope.
   * 
   * @return parent microscope.
   */
  public M getMicroscope()
  {
    return mMicroscope;
  }

  /**
   * Returns device list
   * 
   * @return device list
   */
  public ArrayList<QueueDeviceInterface<?>> getDeviceList()
  {
    return mDeviceList;
  }

  /**
   * Returns the queue for a given stacj camera device.
   * 
   * @param pCameraIndex
   *          device index
   * @return queue
   */
  public StackCameraQueue<?> getCameraDeviceQueue(int pCameraIndex)
  {
    return (StackCameraQueue<?>) getDeviceQueue(StackCameraDeviceInterface.class,
                                                pCameraIndex);

  }

  /**
   * Returns the queue for a given device class and index.
   * 
   * @param pClass
   *          class
   * @param pDeviceIndex
   *          device index
   * @return queue
   */
  public QueueInterface getDeviceQueue(Class<?> pClass,
                                       int pDeviceIndex)
  {
    Object lDevice = getMicroscope().getDevice(pClass, pDeviceIndex);
    if (lDevice == null)
      throw new IllegalArgumentException("Device not found for class: "
                                         + pClass.getSimpleName());

    if (!(lDevice instanceof QueueDeviceInterface))
      throw new IllegalArgumentException("Should be an instance of "
                                         + QueueDeviceInterface.class.getSimpleName());
    @SuppressWarnings("rawtypes")
    QueueDeviceInterface<?> lQueueableDevice =
                                             (QueueDeviceInterface) lDevice;
    QueueInterface lDeviceQueue = getDeviceQueue(lQueueableDevice);
    return lDeviceQueue;

  }

  /**
   * Returns the queue for a given device.
   * 
   * @param pDevice
   *          device
   * @return corresponding queue
   */
  public QueueInterface getDeviceQueue(QueueDeviceInterface<?> pDevice)
  {
    return mDeviceToQueueMap.get(pDevice);
  }

  @Override
  public void clearQueue()
  {

    for (final Entry<QueueDeviceInterface<?>, QueueInterface> lEntry : mDeviceToQueueMap.entrySet())
    {
      QueueDeviceInterface<?> lDevice = lEntry.getKey();
      QueueInterface lQueue = lEntry.getValue();

      if (lDevice instanceof QueueDeviceInterface)
        if (mMicroscope.isActiveDevice(lDevice))
        {
          lQueue.clearQueue();
        }
    }
    mNumberOfEnqueuedStates = 0;

  }

  @Override
  public void addCurrentStateToQueue()
  {

    for (final Entry<QueueDeviceInterface<?>, QueueInterface> lEntry : mDeviceToQueueMap.entrySet())
    {
      QueueDeviceInterface<?> lDevice = lEntry.getKey();
      QueueInterface lQueue = lEntry.getValue();

      if (lDevice instanceof QueueDeviceInterface)
        if (mMicroscope.isActiveDevice(lDevice))
        {
          lQueue.addCurrentStateToQueue();
        }
    }
    mNumberOfEnqueuedStates++;

  }

  @Override
  public void finalizeQueue()
  {

    for (final Entry<QueueDeviceInterface<?>, QueueInterface> lEntry : mDeviceToQueueMap.entrySet())
    {
      QueueDeviceInterface<?> lDevice = lEntry.getKey();
      QueueInterface lQueue = lEntry.getValue();

      if (lDevice instanceof QueueDeviceInterface)
        if (mMicroscope.isActiveDevice(lDevice))
        {
          lQueue.finalizeQueue();
        }
    }

  }

  @Override
  public int getQueueLength()
  {
    return mNumberOfEnqueuedStates;
  }

}
