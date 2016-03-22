package rtlib.lasers.devices.cobolt.models;

public enum CoboltDeviceEnum
{
	Zouk, Calypso, Samba, Jive, Mambo, Flamenco, Rumba;

	public static int getWavelengthInNanoMeter(final CoboltDeviceEnum pCoboltDeviceEnum)
	{
		switch (pCoboltDeviceEnum)
		{
		case Zouk:
			return 355;
		case Calypso:
			return 491;
		case Samba:
			return 532;
		case Jive:
			return 561;
		case Mambo:
			return 594;
		case Flamenco:
			return 660;
		case Rumba:
			return 1064;
		default:
			return 0;
		}
	}

}
