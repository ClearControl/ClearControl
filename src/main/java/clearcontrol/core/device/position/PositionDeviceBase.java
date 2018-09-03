package clearcontrol.core.device.position;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.device.VirtualDevice;
import clearcontrol.core.variable.Variable;

/**
 * Base class for position devices
 *
 * @author royer
 */
public abstract class PositionDeviceBase extends VirtualDevice
                                         implements
                                         PositionDeviceInterface
{
  protected Variable<Integer> mPositionVariable = null;
  protected int[] mValidPositions;
  private ConcurrentHashMap<Integer, String> mPositionToNameMap;

  /**
   * Instanciates a position device given a device name and valid positions
   * 
   * @param pDeviceName
   *          device name
   * @param pValidPositions
   *          valid position
   */
  public PositionDeviceBase(String pDeviceName, int[] pValidPositions)
  {
    super(pDeviceName);
    mValidPositions = pValidPositions;
    mPositionVariable = new Variable<Integer>("Position",
                                              pValidPositions[0]);
    mPositionToNameMap = new ConcurrentHashMap<>();

    for (int lPosition : mValidPositions)
    {
      mPositionToNameMap.put(lPosition, "" + lPosition);
    }
  }

  /**
   * Instantiates a position device given a device path, name and index.
   * 
   * @param pDevicePath
   *          device path
   * @param pDeviceName
   *          device name
   * @param pDeviceIndex
   *          device index
   */
  public PositionDeviceBase(String pDevicePath,
                            String pDeviceName,
                            int pDeviceIndex)
  {
    super(pDeviceName);
    ArrayList<String> lList = MachineConfiguration.get().getList(
                                                                 pDevicePath
                                                                 + "."
                                                                 + pDeviceName.toLowerCase());

    mValidPositions = new int[lList.size()];
    for (int i = 0; i < mValidPositions.length; i++)
    {
      mValidPositions[i] = i;
      mPositionToNameMap.put(i, lList.get(i));
    }

    mPositionVariable = new Variable<Integer>("Position",
                                              mValidPositions[0]);

  }

  @Override
  public void setPositionName(int pPositionIndex,
                              String pPositionName)
  {
    mPositionToNameMap.put(pPositionIndex, pPositionName);
  }

  @Override
  public String getPositionName(int pPositionIndex)
  {
    return mPositionToNameMap.get(pPositionIndex);
  }

  @Override
  public final Variable<Integer> getPositionVariable()
  {
    return mPositionVariable;
  }

  @Override
  public int getPosition()
  {
    return mPositionVariable.get();
  }

  @Override
  public void setPosition(final int pPosition)
  {
    mPositionVariable.set(pPosition);
  }

  @Override
  public int[] getValidPositions()
  {
    return mValidPositions;
  }

}
