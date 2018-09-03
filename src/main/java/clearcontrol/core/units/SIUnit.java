package clearcontrol.core.units;

/**
 * SI units
 *
 * @author royer
 */
@SuppressWarnings("javadoc")
public enum SIUnit
{
 ArbitraryUnit(OrderOfMagnitude.Unit, "AU"),
 Meter(OrderOfMagnitude.Unit, "m"),
 Gram(OrderOfMagnitude.Unit, "g"),
 Second(OrderOfMagnitude.Unit, "s"),
 Kelvin(OrderOfMagnitude.Unit, "m"),
 Mole(OrderOfMagnitude.Kilo, "mol"),
 Candela(OrderOfMagnitude.Unit, "cd"),
 Ampere(OrderOfMagnitude.Unit, "A"),

 MilliMeter(OrderOfMagnitude.Milli, "mm"),
 MicroMeter(OrderOfMagnitude.Micro, "um"),
 NanoMeter(OrderOfMagnitude.Nano, "nm"),
 Kilogram(OrderOfMagnitude.Kilo, "Kg"),
 MilliSecond(OrderOfMagnitude.Milli, "ms"),
 MicroSecond(OrderOfMagnitude.Micro, "us");

  private final OrderOfMagnitude mMagnitude;
  private final String mAbbreviation;

  /**
   * Instantiates a SI unit given a magnitude and abbreviation
   * 
   * @param pMagnitude
   *          magnitiude
   * @param pAbbreviation
   *          abbreviation
   */
  SIUnit(OrderOfMagnitude pMagnitude, String pAbbreviation)
  {
    mMagnitude = pMagnitude;
    mAbbreviation = pAbbreviation;
  }

  /**
   * Returns magnitude
   * 
   * @return magnitude
   */
  public OrderOfMagnitude getMagnitude()
  {
    return mMagnitude;
  }

  /**
   * Returns the abbreviation
   * 
   * @return abbreviation
   */
  public String getAbbreviation()
  {
    return mAbbreviation;
  }

  /**
   * Returns true if this unit is a base unit.
   * 
   * @return true if base unit
   */
  public boolean isBaseUnit()
  {
    return mMagnitude == OrderOfMagnitude.Unit;
  }
}
