package clearcontrol.device.update;

public interface UpdatableInterface 
{
	
	public void ensureIsUpToDate();

	public boolean isUpToDate();

	public void setUpToDate(boolean pIsUpToDate);

	public void requestUpdate();

}