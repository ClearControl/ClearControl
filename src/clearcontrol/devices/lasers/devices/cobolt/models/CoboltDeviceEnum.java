package clearcontrol.devices.lasers.devices.cobolt.models;

/**
 * List of Cobolt devices.
 *
 * @author royer
 */
@SuppressWarnings("javadoc")
public enum CoboltDeviceEnum
{

 Zouk(355),
 Calypso(491),
 Samba(532),
 Jive(561),
 Mambo(594),
 Flamenco(660),
 Rumba(1064);

  int mWavelengthInNanometer;

  private CoboltDeviceEnum(int pWavelengthInNanometer)
  {
    mWavelengthInNanometer = pWavelengthInNanometer;
  }

  /**
   * Returns the wavelength of this model.
   * 
   * @return wavelegth in nanometer
   */
  public int getWavelengthInNanoMeter()
  {
    return mWavelengthInNanometer;
  }

}
