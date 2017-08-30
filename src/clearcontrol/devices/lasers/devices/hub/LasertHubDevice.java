package clearcontrol.devices.lasers.devices.hub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import clearcontrol.core.device.VirtualDevice;
import clearcontrol.core.device.startstop.StartStopDeviceInterface;
import clearcontrol.core.log.LoggingFeature;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.lasers.LaserDeviceInterface;

/**
 * Laser hub device. A laser hub device brings together several lasers.
 *
 * @author royer
 */
public class LasertHubDevice extends VirtualDevice implements
                             StartStopDeviceInterface,
                             LoggingFeature
{

  ArrayList<LaserDeviceInterface> mAddedLaserDeviceList =
                                                        new ArrayList<LaserDeviceInterface>();
  HashMap<Integer, LaserDeviceInterface> mWavelengthToOpenedLaserDeviceMap =
                                                                           new HashMap<Integer, LaserDeviceInterface>();

  /**
   * Instanciates a laser hub device.
   */
  public LasertHubDevice()
  {
    super("LasertHubDevice");
  }

  /**
   * Adds a laser to this hub.
   * 
   * @param pLaserDevice
   *          laser device to add
   */
  public void addLaser(final LaserDeviceInterface pLaserDevice)
  {
    mAddedLaserDeviceList.add(pLaserDevice);
  }

  /**
   * Returns list of laser devices
   * 
   * @return laser device list
   */
  public Collection<LaserDeviceInterface> getLaserDeviceList()
  {
    return mWavelengthToOpenedLaserDeviceMap.values();
  }

  /**
   * Returns laser device for a given wavelength
   * 
   * @param pWavelengthInNanometer
   *          wavelength
   * @return laser device of given wavelength
   */
  public LaserDeviceInterface getLaserDeviceByWavelength(final int pWavelengthInNanometer)
  {
    return mWavelengthToOpenedLaserDeviceMap.get(pWavelengthInNanometer);
  }

  /**
   * Returns laser on/off variable for a given wavelength
   * 
   * @param pWavelengthInNanometer
   *          wavelength in nanometer
   * @return laser on/off variable
   */
  public Variable<Boolean> getOnVariableByWavelength(final int pWavelengthInNanometer)
  {
    final LaserDeviceInterface lLaserDeviceByWavelength =
                                                        getLaserDeviceByWavelength(pWavelengthInNanometer);
    if (lLaserDeviceByWavelength == null)
      return null;
    return lLaserDeviceByWavelength.getLaserOnVariable();
  }

  /**
   * Returns laser target power variable for a given wavelength
   * 
   * @param pWavelengthInNanometer
   *          wavelength in nanometer
   * @return laser target power variable
   */
  public Variable<Number> getTargetPowerInMilliWattVariableByWavelength(final int pWavelengthInNanometer)
  {
    final LaserDeviceInterface lLaserDeviceByWavelength =
                                                        getLaserDeviceByWavelength(pWavelengthInNanometer);
    if (lLaserDeviceByWavelength == null)
      return null;
    return lLaserDeviceByWavelength.getTargetPowerInMilliWattVariable();
  }

  /**
   * Returns laser current power variable for a given wavelength
   * 
   * @param pWavelengthInNanometer
   *          wavelength in nanometer
   * @return laser current power variable
   */
  public Variable<Number> getCurrentPowerInMilliWattVariableByWavelength(final int pWavelengthInNanometer)
  {
    final LaserDeviceInterface lLaserDeviceByWavelength =
                                                        getLaserDeviceByWavelength(pWavelengthInNanometer);
    if (lLaserDeviceByWavelength == null)
      return null;
    return lLaserDeviceByWavelength.getCurrentPowerInMilliWattVariable();
  }

  @Override
  public boolean open()
  {
    boolean lAllLasersOpen = true;
    // Parallel
    for (final LaserDeviceInterface lLaserDevice : mAddedLaserDeviceList)
    {
      final boolean lLaserDeviceOpened = lLaserDevice.open();
      lAllLasersOpen &= lLaserDeviceOpened;
      if (lLaserDeviceOpened)
      {
        final int lWavelengthInNanoMeter =
                                         lLaserDevice.getWavelengthInNanoMeter();
        mWavelengthToOpenedLaserDeviceMap.put(lWavelengthInNanoMeter,
                                              lLaserDevice);
      }
      else
      {
        warning(LasertHubDevice.class.getSimpleName()
                + ": could not open: " + lLaserDevice.getName());
      }
    }
    return lAllLasersOpen;
  }

  @Override
  public boolean start()
  {
    boolean lAllLasersStarted = true;
    for (final Entry<Integer, LaserDeviceInterface> lEntry : mWavelengthToOpenedLaserDeviceMap.entrySet())
    {
      final LaserDeviceInterface lLaserDevice = lEntry.getValue();
      lAllLasersStarted &= lLaserDevice.start();
    }
    return lAllLasersStarted;
  }

  @Override
  public boolean stop()
  {
    boolean lAllLasersStopped = true;
    for (final Entry<Integer, LaserDeviceInterface> lEntry : mWavelengthToOpenedLaserDeviceMap.entrySet())
    {
      final LaserDeviceInterface lLaserDevice = lEntry.getValue();
      lAllLasersStopped &= lLaserDevice.stop();
    }
    return lAllLasersStopped;
  }

  @Override
  public boolean close()
  {
    boolean lAllLasersClosed = true;
    for (final Entry<Integer, LaserDeviceInterface> lEntry : mWavelengthToOpenedLaserDeviceMap.entrySet())
    {
      final LaserDeviceInterface lLaserDevice = lEntry.getValue();
      lAllLasersClosed &= lLaserDevice.close();
    }
    return lAllLasersClosed;
  }

  /**
   * Sets laser target power for all lasers.
   * 
   * @param pTargetPowerInMilliWat
   *          target power in milliwatt
   */
  public void setTargetPowerInMilliWatt(final double pTargetPowerInMilliWat)
  {
    for (final Entry<Integer, LaserDeviceInterface> lEntry : mWavelengthToOpenedLaserDeviceMap.entrySet())
    {
      final LaserDeviceInterface lLaserDevice = lEntry.getValue();
      lLaserDevice.setTargetPowerInMilliWatt(pTargetPowerInMilliWat);
    }
  }

  /**
   * Sets laser target power for all lasers.
   * 
   * @param pTargetPowerInPercent
   *          target power in percent of the max power
   */
  public void setTargetPowerInPercent(final double pTargetPowerInPercent)
  {
    for (final Entry<Integer, LaserDeviceInterface> lEntry : mWavelengthToOpenedLaserDeviceMap.entrySet())
    {
      final LaserDeviceInterface lLaserDevice = lEntry.getValue();
      lLaserDevice.setTargetPowerInPercent(pTargetPowerInPercent);
    }
  }

  /**
   * Returns the current laser power for all lasers in this hub.
   * 
   * @return an array of doubles containing the current power of all lasers.
   *         (the order is the same in which the laser devices were added)
   */
  public double[] getCurrentPowersInMilliWatt()
  {
    final double[] lCurrentPowersInMilliWatt =
                                             new double[mWavelengthToOpenedLaserDeviceMap.size()];
    int i = 0;
    for (final Entry<Integer, LaserDeviceInterface> lEntry : mWavelengthToOpenedLaserDeviceMap.entrySet())
    {
      final LaserDeviceInterface lLaserDevice = lEntry.getValue();
      lCurrentPowersInMilliWatt[i++] =
                                     lLaserDevice.getCurrentPowerInMilliWatt();
    }
    return lCurrentPowersInMilliWatt;
  }

}
