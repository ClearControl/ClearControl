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
import clearcontrol.microscope.lightsheet.component.lightsheet.si.StructuredIlluminationPatternInterface;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

/**
 * Light sheet device. This device abstracts the parameters of a light sheet
 * with a set of variables and functions.
 *
 * @author royer
 */
public class LightSheet extends
                        QueueableVirtualDevice<LightSheetQueue>
                        implements
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

  private LightSheetQueue mLightSheetQueueTemplate;

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

    mLightSheetQueueTemplate = new LightSheetQueue(this);

    @SuppressWarnings("rawtypes")
    final VariableSetListener lVariableListener = (o, n) -> {
      notifyListeners(this);
      // System.out.format("LightSheet.java > lightsheet y=%g \n",
      // mLightSheetQueueTemplate.getYVariable().get().doubleValue());
    };

    mLightSheetQueueTemplate.getReadoutTimeInMicrosecondsPerLineVariable()
                            .set(pReadoutTimeInMicrosecondsPerLine);
    mLightSheetQueueTemplate.getImageHeightVariable()
                            .set(pNumberOfLines);
    mLightSheetQueueTemplate.getOverScanVariable().setMinMax(1.001,
                                                             2);

    mLightSheetQueueTemplate.getReadoutTimeInMicrosecondsPerLineVariable()
                            .addSetListener(lVariableListener);
    mLightSheetQueueTemplate.getImageHeightVariable()
                            .addSetListener(lVariableListener);
    mLightSheetQueueTemplate.getOverScanVariable()
                            .addSetListener(lVariableListener);

    for (int i = 0; i < mNumberOfLaserDigitalControls; i++)
    {
      mLightSheetQueueTemplate.getSIPatternVariable(i)
                              .addSetListener(lVariableListener);
      mLightSheetQueueTemplate.getLaserOnOffArrayVariable(i)
                              .addSetListener(lVariableListener);
      mLightSheetQueueTemplate.getSIPatternOnOffVariable(i)
                              .addSetListener(lVariableListener);
    }

    mLightSheetQueueTemplate.getXVariable()
                            .addSetListener(lVariableListener);
    mLightSheetQueueTemplate.getYVariable()
                            .addSetListener(lVariableListener);
    mLightSheetQueueTemplate.getZVariable()
                            .addSetListener(lVariableListener);
    mLightSheetQueueTemplate.getBetaInDegreesVariable()
                            .addSetListener(lVariableListener);
    mLightSheetQueueTemplate.getAlphaInDegreesVariable()
                            .addSetListener(lVariableListener);
    mLightSheetQueueTemplate.getHeightVariable()
                            .addSetListener(lVariableListener);
    mLightSheetQueueTemplate.getWidthVariable()
                            .addSetListener(lVariableListener);
    mLightSheetQueueTemplate.getPowerVariable()
                            .addSetListener(lVariableListener);
    mLightSheetQueueTemplate.getAdaptPowerToWidthHeightVariable()
                            .addSetListener(lVariableListener);

    final VariableSetListener<?> lFunctionVariableListener =
                                                           (o, n) -> {
                                                             info("new function: "
                                                                  + n);
                                                             resetBounds();
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
                                              mLightSheetQueueTemplate.getXVariable(),
                                              mXFunction.get());
    MachineConfiguration.getCurrentMachineConfiguration()
                        .getBoundsForVariable("device.lsm.lighsheet."
                                              + getName()
                                              + ".y.bounds",
                                              mLightSheetQueueTemplate.getYVariable(),
                                              mYFunction.get());
    MachineConfiguration.getCurrentMachineConfiguration()
                        .getBoundsForVariable("device.lsm.lighsheet."
                                              + getName()
                                              + ".z.bounds",
                                              mLightSheetQueueTemplate.getZVariable(),
                                              mZFunction.get());

    MachineConfiguration.getCurrentMachineConfiguration()
                        .getBoundsForVariable("device.lsm.lighsheet."
                                              + getName()
                                              + ".w.bounds",
                                              mLightSheetQueueTemplate.getWidthVariable(),
                                              mWidthFunction.get());
    MachineConfiguration.getCurrentMachineConfiguration()
                        .getBoundsForVariable("device.lsm.lighsheet."
                                              + getName()
                                              + ".h.bounds",
                                              mLightSheetQueueTemplate.getHeightVariable(),
                                              mHeightFunction.get());

    MachineConfiguration.getCurrentMachineConfiguration()
                        .getBoundsForVariable("device.lsm.lighsheet."
                                              + getName()
                                              + ".a.bounds",
                                              mLightSheetQueueTemplate.getAlphaInDegreesVariable(),
                                              mAlphaFunction.get());
    MachineConfiguration.getCurrentMachineConfiguration()
                        .getBoundsForVariable("device.lsm.lighsheet."
                                              + getName()
                                              + ".b.bounds",
                                              mLightSheetQueueTemplate.getBetaInDegreesVariable(),
                                              mBetaFunction.get());

    MachineConfiguration.getCurrentMachineConfiguration()
                        .getBoundsForVariable("device.lsm.lighsheet."
                                              + getName()
                                              + ".p.bounds",
                                              mLightSheetQueueTemplate.getPowerVariable(),
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
    return mLightSheetQueueTemplate.getEffectiveExposureInMicrosecondsVariable();
  }

  @Override
  public BoundedVariable<Long> getImageHeightVariable()
  {
    return mLightSheetQueueTemplate.getImageHeightVariable();
  }

  @Override
  public BoundedVariable<Number> getOverScanVariable()
  {
    return mLightSheetQueueTemplate.getOverScanVariable();
  }

  @Override
  public BoundedVariable<Number> getReadoutTimeInMicrosecondsPerLineVariable()
  {
    return mLightSheetQueueTemplate.getReadoutTimeInMicrosecondsPerLineVariable();
  }

  @Override
  public BoundedVariable<Number> getXVariable()
  {
    return mLightSheetQueueTemplate.getXVariable();
  }

  @Override
  public BoundedVariable<Number> getYVariable()
  {
    return mLightSheetQueueTemplate.getYVariable();
  }

  @Override
  public BoundedVariable<Number> getZVariable()
  {
    return mLightSheetQueueTemplate.getZVariable();
  }

  @Override
  public BoundedVariable<Number> getAlphaInDegreesVariable()
  {
    return mLightSheetQueueTemplate.getAlphaInDegreesVariable();
  }

  @Override
  public BoundedVariable<Number> getBetaInDegreesVariable()
  {
    return mLightSheetQueueTemplate.getBetaInDegreesVariable();
  }

  @Override
  public BoundedVariable<Number> getWidthVariable()
  {
    return mLightSheetQueueTemplate.getWidthVariable();
  }

  @Override
  public BoundedVariable<Number> getHeightVariable()
  {
    return mLightSheetQueueTemplate.getHeightVariable();
  }

  @Override
  public BoundedVariable<Number> getPowerVariable()
  {
    return mLightSheetQueueTemplate.getPowerVariable();
  }

  @Override
  public Variable<Boolean> getAdaptPowerToWidthHeightVariable()
  {
    return mLightSheetQueueTemplate.getAdaptPowerToWidthHeightVariable();
  }

  @Override
  public Variable<StructuredIlluminationPatternInterface> getSIPatternVariable(int pLaserIndex)
  {
    return mLightSheetQueueTemplate.getSIPatternVariable(pLaserIndex);
  }

  @Override
  public int getNumberOfPhases(int pLaserIndex)
  {
    return mLightSheetQueueTemplate.getNumberOfPhases(pLaserIndex);
  }

  @Override
  public Variable<Boolean> getSIPatternOnOffVariable(int pLaserIndex)
  {
    return mLightSheetQueueTemplate.getSIPatternOnOffVariable(pLaserIndex);
  }

  @Override
  public Variable<Boolean> getLaserOnOffArrayVariable(int pLaserIndex)
  {
    return mLightSheetQueueTemplate.getLaserOnOffArrayVariable(pLaserIndex);
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
  public LightSheetQueue requestQueue()
  {
    return new LightSheetQueue(mLightSheetQueueTemplate);
  }

  @Override
  public Future<Boolean> playQueue(LightSheetQueue pLightSheetQueue)
  {
    // Nothing to play here
    return null;
  }

}
