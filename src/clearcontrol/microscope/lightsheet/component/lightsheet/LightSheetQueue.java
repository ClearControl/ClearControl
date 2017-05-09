package clearcontrol.microscope.lightsheet.component.lightsheet;

import clearcontrol.core.device.queue.QueueInterface;
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
                             QueueInterface,
                             LightSheetParameterInterface
{

  private LightSheet mLightSheet;

  private final BoundedVariable<Long> mImageHeightVariable;
  private final BoundedVariable<Number> mEffectiveExposureInSecondsVariable,
      mFinalisationTimeInSecondsVariable,
      mReadoutTimeInMicrosecondsPerLineVariable, mOverScanVariable,
      mXVariable, mYVariable, mZVariable, mAlphaInDegreesVariable,
      mBetaInDegreesVariable, mWidthVariable, mHeightVariable,
      mPowerVariable;

  private final Variable<Boolean> mAdaptPowerToWidthHeightVariable;

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

    String lNamePrefix = mLightSheet.getName();

    mEffectiveExposureInSecondsVariable =
                                        new BoundedVariable<Number>(lNamePrefix
                                                                    + "-EffectiveExposureInSeconds",
                                                                    0.005);

    mFinalisationTimeInSecondsVariable =
                                       new BoundedVariable<Number>(lNamePrefix
                                                                   + "-FinalizationTimeInSeconds",
                                                                   0.000005);

    mImageHeightVariable = new BoundedVariable<Long>(lNamePrefix
                                                     + "-ImageHeight",
                                                     2 * 1024L);
    mReadoutTimeInMicrosecondsPerLineVariable =
                                              new BoundedVariable<Number>(lNamePrefix
                                                                          + "-ReadoutTimeInMicrosecondsPerLine",
                                                                          9.74);
    mOverScanVariable =
                      new BoundedVariable<Number>(lNamePrefix
                                                  + "-OverScan", 1.3);

    mXVariable = new BoundedVariable<Number>(lNamePrefix
                                             + "-LightSheetX", 0.0);
    mYVariable = new BoundedVariable<Number>(lNamePrefix
                                             + "-LightSheetY", 0.0);
    mZVariable = new BoundedVariable<Number>(lNamePrefix
                                             + "-LightSheetZ", 0.0);

    mAlphaInDegreesVariable = new BoundedVariable<Number>(lNamePrefix
                                                          + "-LightSheetAlphaInDegrees",
                                                          0.0);
    mBetaInDegreesVariable = new BoundedVariable<Number>(lNamePrefix
                                                         + "-LightSheetBetaInDegrees",
                                                         0.0);
    mWidthVariable = new BoundedVariable<Number>(lNamePrefix
                                                 + "-LightSheetRange",
                                                 0.0);
    mHeightVariable = new BoundedVariable<Number>(lNamePrefix
                                                  + "-LightSheetLength",
                                                  0.0);
    mPowerVariable = new BoundedVariable<Number>(lNamePrefix
                                                 + "-LightSheetPower",
                                                 1.0);
    mAdaptPowerToWidthHeightVariable =
                                     new Variable<Boolean>(lNamePrefix
                                                           + "-AdaptLightSheetPowerToWidthHeight",
                                                           false);

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
      final String lLaserName = lNamePrefix + "-Laser"
                                + i
                                + ".exposure.trig";

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

    getEffectiveExposureInSecondsVariable().set(pLightSheetQueueTemplate.getEffectiveExposureInSecondsVariable());
    getFinalisationTimeInSecondsVariable().set(pLightSheetQueueTemplate.getFinalisationTimeInSecondsVariable());

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

  @Override
  public BoundedVariable<Number> getEffectiveExposureInSecondsVariable()
  {
    return mEffectiveExposureInSecondsVariable;
  }

  @Override
  public BoundedVariable<Number> getFinalisationTimeInSecondsVariable()
  {
    return mFinalisationTimeInSecondsVariable;
  }

  @Override
  public BoundedVariable<Long> getImageHeightVariable()
  {
    return mImageHeightVariable;
  }

  @Override
  public BoundedVariable<Number> getOverScanVariable()
  {
    return mOverScanVariable;
  }

  @Override
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
