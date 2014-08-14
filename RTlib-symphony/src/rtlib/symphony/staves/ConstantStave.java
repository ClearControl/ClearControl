package rtlib.symphony.staves;

import rtlib.symphony.functions.Set;
import rtlib.symphony.interfaces.StaveInterface;

public class ConstantStave extends StaveAbstract implements
																								StaveInterface
{
	public volatile double mValue;

	public ConstantStave(final String pName, final double pValue)
	{
		super(pName + "(value=" + pValue + ")");
		mValue = pValue;
	}

	@Override
	public void updateStaveBuffer()
	{
		Set.write(this, mValue);
	}

}
