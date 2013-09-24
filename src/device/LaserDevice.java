package device;

public interface LaserDevice extends NamedDeviceInterface,VirtualDeviceInterface
{

	public int getWavelengthInNanoMeter();

	public void setTargetPowerInMilliWatt(double pTargetPowerinMilliWatt);
	
	public void setTargetPowerInPercent(double pTargetPowerInPercent);

	public double getCurrentPowerInMilliWatt();
}
