package clearcontrol.microscope.lightsheet.component.lightsheet;

import java.util.concurrent.Future;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.device.QueueableVirtualDevice;
import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.math.functions.UnivariateAffineFunction;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableSetListener;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.microscope.lightsheet.component.lightsheet.si.BinaryStructuredIlluminationPattern;
import clearcontrol.microscope.lightsheet.component.lightsheet.si.StructuredIlluminationPatternInterface;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

/**
 * Light sheet device. This device abstracts the parameters of a light sheet
 * with a set of variables and functions.
 *
 * @author royer
 */
public class LightSheet extends QueueableVirtualDevice implements
                        LightSheetInterface,
                        AsynchronousExecutorServiceAccess,
                        LoggingInterface
{

  private final Variable<UnivariateAffineFunction> mXFunction =
                                                              new Variable<>("LightSheetXFunction",
                                                                             new UnivariateAffineFunction());
  private final Variable<UnivariateAffineFunction> mYFunction =
                                                              new Variable<>("LightSheetYFunction",
                                                                             new UnivariateAffineFunction());
  private final Variable<UnivariateAffineFunction> mZFunction =
                                                              new Variable<>("LightSheetZFunction",
                                                                             new UnivariateAffineFunction());

  private final Variable<UnivariateAffineFunction> mWidthFunction =
                                                                  new Variable<>("LightSheetWidthFunction",
                                                                                 new UnivariateAffineFunction());
  private final Variable<UnivariateAffineFunction> mHeightFunction =
                                                                   new Variable<>("LightSheetHeightFunction",
                                                                                  new UnivariateAffineFunction());

  private final Variable<UnivariateAffineFunction> mAlphaFunction =
                                                                  new Variable<>("LightSheetAlphaFunction",
                                                                                 new UnivariateAffineFunction());
  private final Variable<UnivariateAffineFunction> mBetaFunction =
                                                                 new Variable<>("LightSheetBetaFunction",
                                                                                new UnivariateAffineFunction());

  private final Variable<UnivariateAffineFunction> mPowerFunction =
                                                                  new Variable<>("LightSheetPowerFunction",
                                                                                 new UnivariateAffineFunction());

  private final Variable<PolynomialFunction> mWidthPowerFunction =
                                                                 new Variable<>("LightSheetWidthPowerFunction",
                                                                                new PolynomialFunction(new double[]
                                                                                { 1, 0 }));

  private final Variable<PolynomialFunction> mHeightPowerFunction =
                                                                  new Variable<>("LightSheetHeightPowerFunction",
                                                                                 new PolynomialFunction(new double[]
                                                                                 { 1, 0 }));

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

  private final Variable<StructuredIlluminationPatternInterface>[] mStructuredIlluminationPatternVariableArray;

  private final int mNumberOfLaserDigitalControls;

  /**
   * Instanciates a Light Sheet device
   * 
   * @param pName
   *          light sheet name
   * @param pReadoutTimeInMicrosecondsPerLine
   *          readout tie in microseconds per line
   * @param pNumberOfLines
   *          number of lines
   * @param pNumberOfLaserDigitalControls
   *          number of digital controls
   */
  @SuppressWarnings("unchecked")
  public LightSheet(String pName,
                    final double pReadoutTimeInMicrosecondsPerLine,
                    final long pNumberOfLines,
                    final int pNumberOfLaserDigitalControls)
  {
    super(pName);

    mNumberOfLaserDigitalControls = pNumberOfLaserDigitalControls;

    @SuppressWarnings("rawtypes")
    final VariableSetListener lVariableListener = (o, n) -> {
      // System.out.println(getName() + ": new variable value: " + n);
      update();
      notifyListeners(this);
    };

    mReadoutTimeInMicrosecondsPerLineVariable.set(pReadoutTimeInMicrosecondsPerLine);
    mImageHeightVariable.set(pNumberOfLines);
    mOverScanVariable.setMinMax(1.001, 2);

    mReadoutTimeInMicrosecondsPerLineVariable.addSetListener(lVariableListener);
    mOverScanVariable.addSetListener(lVariableListener);
    mImageHeightVariable.addSetListener(lVariableListener);

    getVariableStateQueues().registerConstantVariables(mReadoutTimeInMicrosecondsPerLineVariable,
                                                       mOverScanVariable,
                                                       mImageHeightVariable);

    mLaserOnOffVariableArray =
                             new Variable[mNumberOfLaserDigitalControls];

    mSIPatternOnOffVariableArray =
                                 new Variable[mNumberOfLaserDigitalControls];

    mStructuredIlluminationPatternVariableArray =
                                                new Variable[mNumberOfLaserDigitalControls];

    for (int i = 0; i < mLaserOnOffVariableArray.length; i++)
    {
      final String lLaserName = "Laser" + i + ".exposure.trig";

      mStructuredIlluminationPatternVariableArray[i] =
                                                     new Variable<StructuredIlluminationPatternInterface>("StructuredIlluminationPattern",
                                                                                                          new BinaryStructuredIlluminationPattern());

      mStructuredIlluminationPatternVariableArray[i].addSetListener(lVariableListener);

      mLaserOnOffVariableArray[i] = new Variable<Boolean>(lLaserName,
                                                          false);
      mLaserOnOffVariableArray[i].addSetListener(lVariableListener);

      mSIPatternOnOffVariableArray[i] =
                                      new Variable<Boolean>(lLaserName
                                                            + "SIPatternOnOff",
                                                            false);
      mSIPatternOnOffVariableArray[i].addSetListener(lVariableListener);

      getVariableStateQueues().registerVariables(mStructuredIlluminationPatternVariableArray[i],
                                                 mLaserOnOffVariableArray[i],
                                                 mSIPatternOnOffVariableArray[i]);
    }

    mXVariable.addSetListener(lVariableListener);
    mYVariable.addSetListener(lVariableListener);
    mZVariable.addSetListener(lVariableListener);
    mBetaInDegreesVariable.addSetListener(lVariableListener);
    mAlphaInDegreesVariable.addSetListener(lVariableListener);
    mHeightVariable.addSetListener(lVariableListener);
    mWidthVariable.addSetListener(lVariableListener);
    mPowerVariable.addSetListener(lVariableListener);
    mOverScanVariable.addSetListener(lVariableListener);
    mAdaptPowerToWidthHeightVariable.addSetListener(lVariableListener);

    getVariableStateQueues().registerConstantVariables(mXVariable,
                                                       mYVariable,
                                                       mZVariable,
                                                       mBetaInDegreesVariable,
                                                       mAlphaInDegreesVariable,
                                                       mHeightVariable,
                                                       mWidthVariable,
                                                       mPowerVariable,
                                                       mOverScanVariable,
                                                       mAdaptPowerToWidthHeightVariable);

    final VariableSetListener<?> lFunctionVariableListener =
                                                           (o, n) -> {
                                                             info("new function: "
                                                                  + n);
                                                             resetBounds();
                                                             update();
                                                             notifyListeners(this);
                                                           };

    resetFunctions();
    resetBounds();

    mXFunction.addSetListener((VariableSetListener<UnivariateAffineFunction>) lFunctionVariableListener);
    mYFunction.addSetListener((VariableSetListener<UnivariateAffineFunction>) lFunctionVariableListener);
    mZFunction.addSetListener((VariableSetListener<UnivariateAffineFunction>) lFunctionVariableListener);

    mAlphaFunction.addSetListener((VariableSetListener<UnivariateAffineFunction>) lFunctionVariableListener);
    mBetaFunction.addSetListener((VariableSetListener<UnivariateAffineFunction>) lFunctionVariableListener);
    mWidthFunction.addSetListener((VariableSetListener<UnivariateAffineFunction>) lFunctionVariableListener);
    mHeightFunction.addSetListener((VariableSetListener<UnivariateAffineFunction>) lFunctionVariableListener);
    mPowerFunction.addSetListener((VariableSetListener<UnivariateAffineFunction>) lFunctionVariableListener);

    mWidthPowerFunction.addSetListener((VariableSetListener<PolynomialFunction>) lFunctionVariableListener);
    mHeightPowerFunction.addSetListener((VariableSetListener<PolynomialFunction>) lFunctionVariableListener);

    update();
    notifyListeners(this);
  }

  @Override
  public void resetFunctions()
  {
    mXFunction.set(MachineConfiguration.getCurrentMachineConfiguration()
                                       .getUnivariateAffineFunction("device.lsm.lighsheet."
                                                                    + getName()
                                                                    + ".x.f"));

    mYFunction.set(MachineConfiguration.getCurrentMachineConfiguration()
                                       .getUnivariateAffineFunction("device.lsm.lighsheet."
                                                                    + getName()
                                                                    + ".y.f"));

    mZFunction.set(MachineConfiguration.getCurrentMachineConfiguration()
                                       .getUnivariateAffineFunction("device.lsm.lighsheet."
                                                                    + getName()
                                                                    + ".z.f"));

    mWidthFunction.set(MachineConfiguration.getCurrentMachineConfiguration()
                                           .getUnivariateAffineFunction("device.lsm.lighsheet."
                                                                        + getName()
                                                                        + ".w.f"));

    mHeightFunction.set(MachineConfiguration.getCurrentMachineConfiguration()
                                            .getUnivariateAffineFunction("device.lsm.lighsheet."
                                                                         + getName()
                                                                         + ".h.f"));

    mAlphaFunction.set(MachineConfiguration.getCurrentMachineConfiguration()
                                           .getUnivariateAffineFunction("device.lsm.lighsheet."
                                                                        + getName()
                                                                        + ".a.f"));

    mBetaFunction.set(MachineConfiguration.getCurrentMachineConfiguration()
                                          .getUnivariateAffineFunction("device.lsm.lighsheet."
                                                                       + getName()
                                                                       + ".b.f"));

    mPowerFunction.set(MachineConfiguration.getCurrentMachineConfiguration()
                                           .getUnivariateAffineFunction("device.lsm.lighsheet."
                                                                        + getName()
                                                                        + ".p.f"));

    // TODO: load a polynomial:
    mWidthPowerFunction.set(new PolynomialFunction(new double[]
    { 1 }));

    mHeightPowerFunction.set(new PolynomialFunction(new double[]
    { 1 }));/**/
  }

  @Override
  public void resetBounds()
  {

    MachineConfiguration.getCurrentMachineConfiguration()
                        .getBoundsForVariable("device.lsm.lighsheet."
                                              + getName()
                                              + ".x.bounds",
                                              mXVariable,
                                              mXFunction.get());
    MachineConfiguration.getCurrentMachineConfiguration()
                        .getBoundsForVariable("device.lsm.lighsheet."
                                              + getName()
                                              + ".y.bounds",
                                              mYVariable,
                                              mYFunction.get());
    MachineConfiguration.getCurrentMachineConfiguration()
                        .getBoundsForVariable("device.lsm.lighsheet."
                                              + getName()
                                              + ".z.bounds",
                                              mZVariable,
                                              mZFunction.get());

    MachineConfiguration.getCurrentMachineConfiguration()
                        .getBoundsForVariable("device.lsm.lighsheet."
                                              + getName()
                                              + ".w.bounds",
                                              mWidthVariable,
                                              mWidthFunction.get());
    MachineConfiguration.getCurrentMachineConfiguration()
                        .getBoundsForVariable("device.lsm.lighsheet."
                                              + getName()
                                              + ".h.bounds",
                                              mHeightVariable,
                                              mHeightFunction.get());

    MachineConfiguration.getCurrentMachineConfiguration()
                        .getBoundsForVariable("device.lsm.lighsheet."
                                              + getName()
                                              + ".a.bounds",
                                              mAlphaInDegreesVariable,
                                              mAlphaFunction.get());
    MachineConfiguration.getCurrentMachineConfiguration()
                        .getBoundsForVariable("device.lsm.lighsheet."
                                              + getName()
                                              + ".b.bounds",
                                              mBetaInDegreesVariable,
                                              mBetaFunction.get());

    MachineConfiguration.getCurrentMachineConfiguration()
                        .getBoundsForVariable("device.lsm.lighsheet."
                                              + getName()
                                              + ".p.bounds",
                                              mPowerVariable,
                                              mPowerFunction.get());

  }

  @Override
  public int getNumberOfLaserDigitalControls()
  {
    return mNumberOfLaserDigitalControls;
  }

  @Override
  public BoundedVariable<Number> getEffectiveExposureInMicrosecondsVariable()
  {
    return mEffectiveExposureInMicrosecondsVariable;
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
    return mStructuredIlluminationPatternVariableArray[pLaserIndex];
  }

  @Override
  public int getNumberOfPhases(int pLaserIndex)
  {
    return mStructuredIlluminationPatternVariableArray[pLaserIndex].get()
                                                                   .getNumberOfPhases();
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

  @Override
  public Variable<UnivariateAffineFunction> getXFunction()
  {
    return mXFunction;
  }

  @Override
  public Variable<UnivariateAffineFunction> getYFunction()
  {
    return mYFunction;
  }

  @Override
  public Variable<UnivariateAffineFunction> getZFunction()
  {
    return mZFunction;
  }

  @Override
  public Variable<UnivariateAffineFunction> getWidthFunction()
  {
    return mWidthFunction;
  }

  @Override
  public Variable<UnivariateAffineFunction> getHeightFunction()
  {
    return mHeightFunction;
  }

  @Override
  public Variable<UnivariateAffineFunction> getAlphaFunction()
  {
    return mAlphaFunction;
  }

  @Override
  public Variable<UnivariateAffineFunction> getBetaFunction()
  {
    return mBetaFunction;
  }

  @Override
  public Variable<UnivariateAffineFunction> getPowerFunction()
  {
    return mPowerFunction;
  }

  @Override
  public Variable<PolynomialFunction> getWidthPowerFunction()
  {
    return mWidthPowerFunction;
  }

  @Override
  public Variable<PolynomialFunction> getHeightPowerFunction()
  {
    return mHeightPowerFunction;
  }

  @Override
  public Future<Boolean> playQueue()
  {
    // Nothing to play here
    return null;
  }

  @Override
  public void update()
  {
    // TODO Auto-generated method stub

  }

}
