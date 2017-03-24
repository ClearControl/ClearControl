package clearcontrol.devices.sensors.devices.tc01;

public enum NIThermoCoupleType
{
 B(10047), // B-type thermocouple.
 E(10055), // E-type thermocouple.
 J(10072), // J-type thermocouple.
 K(10073), // K-type thermocouple.
 N(10077), // N-type thermocouple.
 R(10082), // R-type thermocouple.
 S(10085), // S-type thermocouple.
 T(10086); // T-type thermocouple.

  private final int value;

  private NIThermoCoupleType(final int newValue)
  {
    value = newValue;
  }

  public int getValue()
  {
    return value;
  }

}
