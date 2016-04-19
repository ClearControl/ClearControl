package rtlib.hardware.signalgen.staves;

import rtlib.device.name.NameableAbstract;

public abstract class StaveAbstract extends NameableAbstract implements
																														StaveInterface

{
	private volatile boolean mEnabled = false;
	private volatile long mDurationInNanoseconds;

	/**
	 * Constructor
	 * 
	 * @param pName
	 *          stave name
	 */
	public StaveAbstract(final String pName)
	{
		super(pName);
	}

	@Override
	public boolean isEnabled()
	{
		return mEnabled;
	}

	@Override
	public void setEnabled(boolean pEnabled)
	{
		mEnabled = pEnabled;
	}

	@Override
	public String toString()
	{
		return String.format(	"Stave [getName()=%s, mEnabled=%s, mDurationInNanoseconds=%s, ]",
													getName(),
													mEnabled,
													mDurationInNanoseconds);
	}

}
