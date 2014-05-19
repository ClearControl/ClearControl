package rtlib.core.rgc;

@FunctionalInterface
public interface Cleaner extends Runnable
{
	@Override
	public void run();
}
