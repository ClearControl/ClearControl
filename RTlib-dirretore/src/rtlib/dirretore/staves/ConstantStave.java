package rtlib.dirretore.staves;

import rtlib.dirretore.functions.Set;
import rtlib.dirretore.interfaces.StaveInterface;

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
