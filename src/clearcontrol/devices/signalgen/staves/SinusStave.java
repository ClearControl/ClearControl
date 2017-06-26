package clearcontrol.devices.signalgen.staves;

import static java.lang.Math.PI;
import static java.lang.Math.sin;

public class SinusStave extends StaveAbstract
                        implements StaveInterface
{
  private volatile float mSinusPeriod;
  private volatile float mSinusPhase;
  private volatile float mSinusAmplitude;

  private volatile float mOmega;

  public SinusStave(final String pName,
                    final float pSinusPeriod,
                    final float pSinusPhase,
                    final float pSinusAmplitude)
  {
    super(pName);
    setSinusPeriod(pSinusPeriod);
    setSinusPhase(pSinusPhase);
    setSinusAmplitude(pSinusAmplitude);
  }

  @Override
  public StaveInterface duplicate()
  {

    return new SinusStave(getName(),
                          getSinusPeriod(),
                          getSinusPhase(),
                          getSinusAmplitude());
  }

  @Override
  public float getValue(float pNormalizedTime)
  {
    final float lValue =
                       (float) (getSinusAmplitude() * sin(
                                                          (pNormalizedTime
                                                           + getSinusPhase())
                                                          * mOmega));

    return lValue;
  }

  public float getSinusPeriod()
  {
    return mSinusPeriod;
  }

  public void setSinusPeriod(float pSinusPeriod)
  {
    mSinusPeriod = pSinusPeriod;
    mOmega = (float) ((2 * PI) / getSinusPeriod());
  }

  public float getSinusPhase()
  {
    return mSinusPhase;
  }

  public void setSinusPhase(float pSinusPhase)
  {
    mSinusPhase = pSinusPhase;
  }

  public float getSinusAmplitude()
  {
    return mSinusAmplitude;
  }

  public void setSinusAmplitude(float pSinusAmplitude)
  {
    mSinusAmplitude = pSinusAmplitude;
  }

}
