package clearcontrol.microscope.lightsheet.component.detection;

import java.util.concurrent.Future;

import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.device.QueueableVirtualDevice;
import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.math.functions.UnivariateAffineFunction;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableSetListener;
import clearcontrol.core.variable.bounded.BoundedVariable;

/**
 * Light sheet microscope detection arm
 *
 * @author royer
 */
public class DetectionArm extends
                          QueueableVirtualDevice<DetectionArmQueue>
                          implements
                          DetectionArmInterface,
                          LoggingInterface
{

  private final Variable<UnivariateAffineFunction> mZFunction =
                                                              new Variable<>("DetectionZFunction",
                                                                             new UnivariateAffineFunction());

  private final Variable<Double> mPixelSizeInMicrometerVariable =
                                                                new Variable<>("PixelSizeInMicrometers",
                                                                               MachineConfiguration.getCurrentMachineConfiguration()
                                                                                                   .getDoubleProperty("device.lsm.detection."
                                                                                                                      + getName()
                                                                                                                      + ".pixelsize",
                                                                                                                      0.406));

  DetectionArmQueue mTemplateQueue;

  /**
   * Instanciates a lightsheet microscope detection arm
   * 
   * @param pName
   *          detection arm name
   */
  @SuppressWarnings("unchecked")
  public DetectionArm(String pName)
  {
    super(pName);

    mTemplateQueue = new DetectionArmQueue(this);

    resetFunctions();
    resetBounds();

    @SuppressWarnings("rawtypes")
    final VariableSetListener lVariableListener = (o, n) -> {
      notifyListeners(this);
    };

    mTemplateQueue.getZVariable().addSetListener(lVariableListener);

    final VariableSetListener<UnivariateAffineFunction> lFunctionVariableListener =
                                                                                  (o,
                                                                                   n) -> {
                                                                                    info("new Z function: "
                                                                                         + n);
                                                                                    resetBounds();
                                                                                    notifyListeners(this);
                                                                                  };

    mZFunction.addSetListener(lFunctionVariableListener);

    notifyListeners(this);
  }

  @Override
  public void resetBounds()
  {
    MachineConfiguration.getCurrentMachineConfiguration()
                        .getBoundsForVariable("device.lsm.detection."
                                              + getName()
                                              + ".z.bounds",
                                              mTemplateQueue.getZVariable(),
                                              getZFunction().get(),
                                              -200,
                                              200);
  }

  @Override
  public void resetFunctions()
  {
    // TODO Auto-generated method stub

  }

  @Override
  public BoundedVariable<Number> getZVariable()
  {
    return mTemplateQueue.getZVariable();
  }

  @Override
  public Variable<UnivariateAffineFunction> getZFunction()
  {
    return mZFunction;
  }

  @Override
  public Variable<Double> getPixelSizeInMicrometerVariable()
  {
    return mPixelSizeInMicrometerVariable;
  }

  @Override
  public DetectionArmQueue requestQueue()
  {
    return new DetectionArmQueue(mTemplateQueue);
  }

  @Override
  public Future<Boolean> playQueue(DetectionArmQueue pDetectionArmQueue)
  {
    // do nothing
    return null;
  }

}
