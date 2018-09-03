package clearcontrol.microscope;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Microscope devie lists. Instnces of this class mannage the list of devices in
 * a microscope.
 *
 * @author royer
 */
public class MicroscopeDeviceLists
{
  private final ArrayList<Object> mAllDeviceList =
                                                 new ArrayList<Object>();
  private final ConcurrentHashMap<Object, Integer> mDeviceIndexMap =
                                                                   new ConcurrentHashMap<>();

  /**
   * Instanciates a microscope device list
   */
  public MicroscopeDeviceLists()
  {
  }

  /**
   * Adds a device to this list. There can only be only one device for a given
   * device class and index.
   * 
   * @param pDeviceIndex
   *          device index. All devices have an index, for example if the
   *          microscope has two lasers, the indices will be 0 and 1
   * @param pDevice
   *          device
   */
  public <T> void addDevice(int pDeviceIndex, T pDevice)
  {
    mDeviceIndexMap.put(pDevice, pDeviceIndex);
    mAllDeviceList.add(pDevice);
  }

  /**
   * Returns the device for a given class and index.
   * 
   * @param pClass
   *          class
   * @param pIndex
   *          index
   * @return device of given class and index (there is only one)
   */
  @SuppressWarnings("unchecked")
  public <T> T getDevice(Class<T> pClass, int pIndex)
  {
    for (Object lDevice : mAllDeviceList)
      if (pClass.isInstance(lDevice))
        if (mDeviceIndexMap.get(lDevice).equals(pIndex))
          return (T) lDevice;
    return null;
  }

  /**
   * Returns all devices of a given class.
   * 
   * @param pClass
   *          class
   * @return list of devices of given class
   */
  @SuppressWarnings("unchecked")
  public <T> ArrayList<T> getDevices(Class<T> pClass)
  {
    ArrayList<T> lFoundDevices = new ArrayList<>();
    for (Object lDevice : mAllDeviceList)
      if (pClass.isInstance(lDevice))
        lFoundDevices.add((T) lDevice);

    return lFoundDevices;
  }

  /**
   * Returns the number of devices of a given class
   * 
   * @param pClass
   *          class
   * @return number of devices of given class
   */
  public <T> int getNumberOfDevices(Class<T> pClass)
  {
    int lCount = 0;
    for (Object lDevice : mAllDeviceList)
      if (pClass.isInstance(lDevice))
        lCount++;

    return lCount;
  }

  /**
   * Returns one consolidated list for all devices managed by this microscope
   * device list.
   * 
   * @return single flat list for all devices
   */
  public ArrayList<Object> getAllDeviceList()
  {
    return mAllDeviceList;
  }

  /**
   * Returns a set of all classes of devices in this microscope device list
   * 
   * @return set of classes
   */
  public HashSet<Class<?>> getAllDeviceClassesList()
  {
    HashSet<Class<?>> lAllDeviceClassesList = new HashSet<>();
    for (Object lDevice : mAllDeviceList)
      lAllDeviceClassesList.add(lDevice.getClass());
    return lAllDeviceClassesList;
  }

  @Override
  public String toString()
  {
    final StringBuilder lBuilder = new StringBuilder();
    for (final Object lDevice : mAllDeviceList)
    {
      lBuilder.append(lDevice.toString());
      lBuilder.append("\n");
    }
    return lBuilder.toString();
  }

}
