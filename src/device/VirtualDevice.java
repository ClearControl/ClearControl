package device;

public interface VirtualDevice
{
	public boolean open();

	public boolean start();

	public boolean stop();

	public boolean close();
}
