package rtlib.core.score.staves;

import rtlib.core.score.StaveAbstract;
import rtlib.core.score.functions.Set;
import rtlib.core.score.interfaces.StaveInterface;

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
