package rtlib.device.update;

public interface UpdatableInterface
{

	public abstract void ensureIsUpToDate();

	public abstract boolean isUpToDate();

	public abstract void setUpToDate(boolean pIsUpToDate);

	public abstract void requestUpdate();

}