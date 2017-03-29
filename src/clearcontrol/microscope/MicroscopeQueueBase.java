package clearcontrol.microscope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import clearcontrol.core.device.queue.RealTimeQueueDeviceInterface;
import clearcontrol.core.device.queue.RealTimeQueueInterface;

/**
 * Base class providing common fields and methods for all microscope queues
 *
 * @param <Q>
 *          queue type
 * @author royer
 */
public class MicroscopeQueueBase<Q extends MicroscopeQueueBase<Q>>
                                implements RealTimeQueueInterface
{

  private MicroscopeBase<Q> mMicroscope;

  private volatile int mNumberOfEnqueuedStates;

  private ArrayList<RealTimeQueueDeviceInterface<?>> mDeviceList =
                                                                 new ArrayList<>();
  private HashMap<RealTimeQueueDeviceInterface<?>, RealTimeQueueInterface> mDeviceToQueueMap =
                                                                                             new HashMap<>();

  /**
   * Instanciates a microscope queue
   * 
   * @param pMicroscope
   *          parent microscope
   */
  public MicroscopeQueueBase(MicroscopeBase<Q> pMicroscope)
  {
    super();
    mMicroscope = pMicroscope;

    @SuppressWarnings("rawtypes")
    ArrayList<RealTimeQueueDeviceInterface> lQueueableDevices =
                                                              mMicroscope.getDeviceLists()
                                                                         .getDevices(RealTimeQueueDeviceInterface.class);

    for (RealTimeQueueDeviceInterface<?> lQueueableDevice : lQueueableDevices)
    {
      RealTimeQueueInterface lRequestQueue =
                                           lQueueableDevice.requestQueue();

      mDeviceList.add(lQueueableDevice);
      mDeviceToQueueMap.put(lQueueableDevice, lRequestQueue);
    }

  }

  /**
   * Returns parent microcope.
   * 
   * @return parent microscope.
   */
  public MicroscopeBase<Q> getMicroscope()
  {
    return mMicroscope;
  }

  /**
   * Returns device list
   * 
   * @return device list
   */
  public ArrayList<RealTimeQueueDeviceInterface<?>> getDeviceList()
  {
    return mDeviceList;
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
  public RealTimeQueueInterface getDeviceQueue(Class<?> pClass,
                                               int pDeviceIndex)
  {
    Object lDevice = getMicroscope().getDevice(pClass, pDeviceIndex);
    if (lDevice == null)
      throw new IllegalArgumentException("Device not found for class: "
                                         + pClass.getSimpleName());

    if (!(lDevice instanceof RealTimeQueueDeviceInterface))
      throw new IllegalArgumentException("Should be an instance of "
                                         + RealTimeQueueDeviceInterface.class.getSimpleName());
    @SuppressWarnings("rawtypes")
    RealTimeQueueDeviceInterface<?> lQueueableDevice =
                                                     (RealTimeQueueDeviceInterface) lDevice;
    RealTimeQueueInterface lDeviceQueue =
                                        getDeviceQueue(lQueueableDevice);
    return lDeviceQueue;

  }

  /**
   * Returns the queue for a given device.
   * 
   * @param pDevice
   *          device
   * @return corresponding queue
   */
  public RealTimeQueueInterface getDeviceQueue(RealTimeQueueDeviceInterface<?> pDevice)
  {
    return mDeviceToQueueMap.get(pDevice);
  }

  @Override
  public void clearQueue()
  {

    for (final Entry<RealTimeQueueDeviceInterface<?>, RealTimeQueueInterface> lEntry : mDeviceToQueueMap.entrySet())
    {
      RealTimeQueueDeviceInterface<?> lDevice = lEntry.getKey();
      RealTimeQueueInterface lQueue = lEntry.getValue();

      if (lDevice instanceof RealTimeQueueDeviceInterface)
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

    for (final Entry<RealTimeQueueDeviceInterface<?>, RealTimeQueueInterface> lEntry : mDeviceToQueueMap.entrySet())
    {
      RealTimeQueueDeviceInterface<?> lDevice = lEntry.getKey();
      RealTimeQueueInterface lQueue = lEntry.getValue();

      if (lDevice instanceof RealTimeQueueDeviceInterface)
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

    for (final Entry<RealTimeQueueDeviceInterface<?>, RealTimeQueueInterface> lEntry : mDeviceToQueueMap.entrySet())
    {
      RealTimeQueueDeviceInterface<?> lDevice = lEntry.getKey();
      RealTimeQueueInterface lQueue = lEntry.getValue();

      if (lDevice instanceof RealTimeQueueDeviceInterface)
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
