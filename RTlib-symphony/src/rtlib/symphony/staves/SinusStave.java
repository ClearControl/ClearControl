package rtlib.symphony.staves;

import rtlib.symphony.functions.SinusPattern;
import rtlib.symphony.interfaces.StaveInterface;

public class SinusStave extends StaveAbstract implements
																								StaveInterface
{
	public volatile double mSinusPeriod;
	public volatile double mSinusPhase;
	public volatile double mSinusAmplitude;

	public SinusStave(final String pName,
										final double pSinusPeriod,
										final double pSinusPhase,
										final double pSinusAmplitude)
	{
		super(pName);
		mSinusPeriod = pSinusPeriod;
		mSinusPhase = pSinusPhase;
		mSinusAmplitude = pSinusAmplitude;
	}

	@Override
	public void updateStaveBuffer()
	{
		SinusPattern.add(this, mSinusPeriod, mSinusPhase, mSinusAmplitude);
	}

}
