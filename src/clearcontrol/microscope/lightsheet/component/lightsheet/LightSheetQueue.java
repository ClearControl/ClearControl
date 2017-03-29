package clearcontrol.microscope.lightsheet.component.lightsheet;

import clearcontrol.core.device.queue.RealTimeQueueInterface;
import clearcontrol.core.device.queue.VariableQueueBase;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.microscope.lightsheet.component.lightsheet.si.BinaryStructuredIlluminationPattern;
import clearcontrol.microscope.lightsheet.component.lightsheet.si.StructuredIlluminationPatternInterface;

/**
 * lightsheet queue
 *
 * @author royer
 */
public class LightSheetQueue extends VariableQueueBase implements
                             RealTimeQueueInterface,
                             LightSheetParameterInterface
{

  private LightSheet mLightSheet;

  private final BoundedVariable<Number> mEffectiveExposureInMicrosecondsVariable =
                                                                                 new BoundedVariable<Number>("EffectiveExposureInMicroseconds",
                                                                                                             5000.0);

  private final BoundedVariable<Long> mImageHeightVariable =
                                                           new BoundedVariable<Long>("ImageHeight",
                                                                                     2 * 1024L);
  private final BoundedVariable<Number> mReadoutTimeInMicrosecondsPerLineVariable =
                                                                                  new BoundedVariable<Number>("ReadoutTimeInMicrosecondsPerLine",
                                                                                                              9.74);
  private final BoundedVariable<Number> mOverScanVariable =
                                                          new BoundedVariable<Number>("OverScan",
                                                                                      1.3);

  private final BoundedVariable<Number> mXVariable =
                                                   new BoundedVariable<Number>("LightSheetX",
                                                                               0.0);
  private final BoundedVariable<Number> mYVariable =
                                                   new BoundedVariable<Number>("LightSheetY",
                                                                               0.0);
  private final BoundedVariable<Number> mZVariable =
                                                   new BoundedVariable<Number>("LightSheetZ",
                                                                               0.0);

  private final BoundedVariable<Number> mAlphaInDegreesVariable =
                                                                new BoundedVariable<Number>("LightSheetAlphaInDegrees",
                                                                                            0.0);
  private final BoundedVariable<Number> mBetaInDegreesVariable =
                                                               new BoundedVariable<Number>("LightSheetBetaInDegrees",
                                                                                           0.0);
  private final BoundedVariable<Number> mWidthVariable =
                                                       new BoundedVariable<Number>("LightSheetRange",
                                                                                   0.0);
  private final BoundedVariable<Number> mHeightVariable =
                                                        new BoundedVariable<Number>("LightSheetLength",
                                                                                    0.0);
  private final BoundedVariable<Number> mPowerVariable =
                                                       new BoundedVariable<Number>("LightSheetLengthPower",
                                                                                   1.0);
  private final Variable<Boolean> mAdaptPowerToWidthHeightVariable =
                                                                   new Variable<Boolean>("AdaptLightSheetPowerToWidthHeight",
                                                                                         false);

  private final Variable<Boolean>[] mLaserOnOffVariableArray;

  private final Variable<Boolean>[] mSIPatternOnOffVariableArray;

  private final Variable<StructuredIlluminationPatternInterface>[] mSIPatternVariableArray;

  private int mNumberOfLaserDigitalControls;

  /**
   * Instanciates a lightsheet queue
   * 
   * @param pLightSheet
   *          light sheet
   */
  @SuppressWarnings("unchecked")
  public LightSheetQueue(LightSheet pLightSheet)
  {
    mLightSheet = pLightSheet;
    mNumberOfLaserDigitalControls =
                                  mLightSheet.getNumberOfLaserDigitalControls();

    registerVariables(mReadoutTimeInMicrosecondsPerLineVariable,
                      mOverScanVariable,
                      mImageHeightVariable);

    registerVariables(getXVariable(),
                      getYVariable(),
                      getZVariable(),
                      getBetaInDegreesVariable(),
                      getAlphaInDegreesVariable(),
                      getHeightVariable(),
                      getWidthVariable(),
                      getPowerVariable(),
                      getAdaptPowerToWidthHeightVariable());

    mLaserOnOffVariableArray =
                             new Variable[mNumberOfLaserDigitalControls];

    mSIPatternOnOffVariableArray =
                                 new Variable[mNumberOfLaserDigitalControls];

    mSIPatternVariableArray =
                            new Variable[mNumberOfLaserDigitalControls];

    for (int i = 0; i < mNumberOfLaserDigitalControls; i++)
    {
      final String lLaserName = "Laser" + i + ".exposure.trig";

      mSIPatternVariableArray[i] =
                                 new Variable<StructuredIlluminationPatternInterface>("StructuredIlluminationPattern",
                                                                                      new BinaryStructuredIlluminationPattern());

      mLaserOnOffVariableArray[i] = new Variable<Boolean>(lLaserName,
                                                          false);
      mSIPatternOnOffVariableArray[i] =
                                      new Variable<Boolean>(lLaserName
                                                            + "SIPatternOnOff",
                                                            false);
      registerVariables(mSIPatternVariableArray[i],
                        mLaserOnOffVariableArray[i],
                        mSIPatternOnOffVariableArray[i]);
    }
  }

  /**
   * Instanciates a lightsheet queue based on a existing template
   * 
   * @param pLightSheetQueueTemplate
   *          template
   */
  public LightSheetQueue(LightSheetQueue pLightSheetQueueTemplate)
  {
    this(pLightSheetQueueTemplate.getLightSheet());

    getOverScanVariable().set(pLightSheetQueueTemplate.getOverScanVariable());
    getReadoutTimeInMicrosecondsPerLineVariable().set(pLightSheetQueueTemplate.getReadoutTimeInMicrosecondsPerLineVariable());
    getImageHeightVariable().set(pLightSheetQueueTemplate.getImageHeightVariable());

    getXVariable().set(pLightSheetQueueTemplate.getXVariable());
    getYVariable().set(pLightSheetQueueTemplate.getYVariable());
    getZVariable().set(pLightSheetQueueTemplate.getZVariable());

    getWidthVariable().set(pLightSheetQueueTemplate.getWidthVariable());
    getHeightVariable().set(pLightSheetQueueTemplate.getHeightVariable());

    getAlphaInDegreesVariable().set(pLightSheetQueueTemplate.getAlphaInDegreesVariable());
    getBetaInDegreesVariable().set(pLightSheetQueueTemplate.getBetaInDegreesVariable());

    getPowerVariable().set(pLightSheetQueueTemplate.getPowerVariable());
    getAdaptPowerToWidthHeightVariable().set(pLightSheetQueueTemplate.getAdaptPowerToWidthHeightVariable()
                                                                     .get());

    for (int i =
               0; i < pLightSheetQueueTemplate.getNumberOfLaserDigitalControls(); i++)
    {
      mSIPatternVariableArray[i].set(pLightSheetQueueTemplate.getSIPatternVariable(i)
                                                             .get());

      mLaserOnOffVariableArray[i].set(pLightSheetQueueTemplate.getLaserOnOffArrayVariable(i)
                                                              .get());
      mSIPatternOnOffVariableArray[i].set(pLightSheetQueueTemplate.getSIPatternOnOffVariable(i)
                                                                  .get());
    }

  }

  /**
   * Returns the parent lightsheet
   * 
   * @return parent lightsheet
   */
  public LightSheet getLightSheet()
  {
    return mLightSheet;
  }

  /**
   * Returns the number of laser digital controls
   * 
   * @return number of laser digital controls
   */
  public int getNumberOfLaserDigitalControls()
  {
    return mNumberOfLaserDigitalControls;
  }

  /**
   * Returns the number of phases
   * 
   * @param pLaserIndex
   *          laser index
   * @return number of phases
   */
  public int getNumberOfPhases(int pLaserIndex)
  {
    return mSIPatternVariableArray[pLaserIndex].get()
                                               .getNumberOfPhases();
  }

  public BoundedVariable<Number> getEffectiveExposureInMicrosecondsVariable()
  {
    return mEffectiveExposureInMicrosecondsVariable;
  }

  public BoundedVariable<Long> getImageHeightVariable()
  {
    return mImageHeightVariable;
  }

  public BoundedVariable<Number> getOverScanVariable()
  {
    return mOverScanVariable;
  }

  public BoundedVariable<Number> getReadoutTimeInMicrosecondsPerLineVariable()
  {
    return mReadoutTimeInMicrosecondsPerLineVariable;
  }

  @Override
  public BoundedVariable<Number> getXVariable()
  {
    return mXVariable;
  }

  @Override
  public BoundedVariable<Number> getYVariable()
  {
    return mYVariable;
  }

  @Override
  public BoundedVariable<Number> getZVariable()
  {
    return mZVariable;
  }

  @Override
  public BoundedVariable<Number> getAlphaInDegreesVariable()
  {
    return mAlphaInDegreesVariable;
  }

  @Override
  public BoundedVariable<Number> getBetaInDegreesVariable()
  {
    return mBetaInDegreesVariable;
  }

  @Override
  public BoundedVariable<Number> getWidthVariable()
  {
    return mWidthVariable;
  }

  @Override
  public BoundedVariable<Number> getHeightVariable()
  {
    return mHeightVariable;
  }

  @Override
  public BoundedVariable<Number> getPowerVariable()
  {
    return mPowerVariable;
  }

  @Override
  public Variable<Boolean> getAdaptPowerToWidthHeightVariable()
  {
    return mAdaptPowerToWidthHeightVariable;
  }

  @Override
  public Variable<StructuredIlluminationPatternInterface> getSIPatternVariable(int pLaserIndex)
  {
    return mSIPatternVariableArray[pLaserIndex];
  }

  @Override
  public Variable<Boolean> getSIPatternOnOffVariable(int pLaserIndex)
  {
    return mSIPatternOnOffVariableArray[pLaserIndex];
  }

  @Override
  public Variable<Boolean> getLaserOnOffArrayVariable(int pLaserIndex)
  {
    return mLaserOnOffVariableArray[pLaserIndex];
  }

}
